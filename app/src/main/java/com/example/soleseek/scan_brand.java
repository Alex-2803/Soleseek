package com.example.soleseek;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.widget.ImageView;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.soleseek.ml.SsdMobilenetV11Metadata1;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.List;

public class scan_brand extends AppCompatActivity {

    private List<String> labels;
    private List<Integer> colors = Arrays.asList(
            Color.BLUE, Color.GREEN, Color.RED, Color.CYAN, Color.GRAY, Color.BLACK,
            Color.DKGRAY, Color.MAGENTA, Color.YELLOW, Color.RED
    );
    private Paint paint = new Paint();
    private ImageProcessor imageProcessor;
    private Bitmap bitmap;
    private ImageView imageView;
    private CameraDevice cameraDevice;
    private Handler handler;
    private CameraManager cameraManager;
    private TextureView textureView;
    private SsdMobilenetV11Metadata1 model;
    private Interpreter tflite;
    private Interpreter.Options options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_brand);
        getPermission();
        try {
            labels = FileUtil.loadLabels(this, "labels.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        imageProcessor = new ImageProcessor.Builder()
                .add(new ResizeOp(300, 300, ResizeOp.ResizeMethod.BILINEAR))
                .build();

        try {
            model = SsdMobilenetV11Metadata1.newInstance(this);
            MappedByteBuffer tfliteModel = loadModelFile(this, "ssd_mobilenet_v1_1_metadata_1.tflite");
            options = new Interpreter.Options();
            tflite = new Interpreter(tfliteModel, options);
        } catch (IOException e) {
            e.printStackTrace();
        }

        HandlerThread handlerThread = new HandlerThread("videoThread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());

        imageView = findViewById(R.id.imageView);

        textureView = findViewById(R.id.textureView);
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
                openCamera();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                bitmap = textureView.getBitmap();
                TensorImage image = TensorImage.fromBitmap(bitmap);
                image = imageProcessor.process(image);

                // Create input and output buffers
                TensorBuffer inputBuffer = TensorBuffer.createFixedSize(new int[]{1, 300, 300, 3}, DataType.UINT8);
                TensorBuffer outputLocations = TensorBuffer.createFixedSize(new int[]{1, 10, 4}, DataType.FLOAT32);
                TensorBuffer outputClasses = TensorBuffer.createFixedSize(new int[]{1, 10}, DataType.FLOAT32);
                TensorBuffer outputScores = TensorBuffer.createFixedSize(new int[]{1, 10}, DataType.FLOAT32);
                TensorBuffer outputNumDetections = TensorBuffer.createFixedSize(new int[]{1, 1}, DataType.FLOAT32);

                // Preprocess input image
                inputBuffer.loadBuffer(image.getBuffer());

                // Run inference
                tflite.run(new Object[]{inputBuffer.getBuffer()},
                        new Object[]{outputLocations.getBuffer()}
                );

                // Process inference results and draw on bitmap
                Bitmap mutable = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                Canvas canvas = new Canvas(mutable);

                int h = mutable.getHeight();
                int w = mutable.getWidth();
                paint.setTextSize(h / 15f);
                paint.setStrokeWidth(h / 85f);
                int x;
                float[] locations = outputLocations.getFloatArray();
                float[] classes = outputClasses.getFloatArray();
                float[] scores = outputScores.getFloatArray();
                for (int index = 0; index < scores.length; index++) {
                    x = index * 4;
                    if (scores[index] > 0.5) {
                        paint.setColor(colors.get(index % colors.size()));
                        paint.setStyle(Paint.Style.STROKE);
                        canvas.drawRect(
                                new RectF(locations[x + 1] * w, locations[x] * h, locations[x + 3] * w, locations[x + 2] * h),
                                paint
                        );
                        paint.setStyle(Paint.Style.FILL);
                        canvas.drawText(
                                labels.get((int) classes[index]) + " " + scores[index],
                                locations[x + 1] * w,
                                locations[x] * h,
                                paint
                        );
                    }
                }

                imageView.setImageBitmap(mutable);
            }
        });

        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (model != null) {
            model.close();
        }
    }

    @SuppressLint("MissingPermission")
    private void openCamera() {
        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.openCamera(cameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice cameraDevice) {
                    scan_brand.this.cameraDevice = cameraDevice;
                    SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
                    assert surfaceTexture != null;
                    surfaceTexture.setDefaultBufferSize(1920, 1080);
                    Surface surface = new Surface(surfaceTexture);

                    try {
                        CameraCaptureSession.StateCallback callback = new CameraCaptureSession.StateCallback() {
                            @Override
                            public void onConfigured(@NonNull CameraCaptureSession session) {
                                try {
                                    CaptureRequest.Builder captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                                    captureRequestBuilder.addTarget(surface);
                                    CaptureRequest captureRequest = captureRequestBuilder.build();

                                    session.setRepeatingRequest(captureRequest, null, null);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                            }
                        };
                        List<Surface> surfaces = Arrays.asList(surface);
                        cameraDevice.createCaptureSession(surfaces, callback, handler);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice cameraDevice) {
                }

                @Override
                public void onError(@NonNull CameraDevice cameraDevice, int error) {
                }
            }, handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 101);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) { // Make sure the requestCode matches what you used for the permission request
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                // Permission was not granted, show a message to the user
                Toast.makeText(this, "Camera permission is required for object detection.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private MappedByteBuffer loadModelFile(Context context, String modelFileName) throws IOException {
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open("ml/" + modelFileName);
        File file = createFileFromInputStream(inputStream);
        FileInputStream fileInputStream = new FileInputStream(file);
        FileChannel fileChannel = fileInputStream.getChannel();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
    }

    private File createFileFromInputStream(InputStream inputStream) throws IOException {
        File file = File.createTempFile("temp", null);
        file.deleteOnExit();
        FileOutputStream outputStream = new FileOutputStream(file);
        byte[] buffer = new byte[4 * 1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();
        return file;
    }
}
