package com.example.akasaair;


import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.content.Intent;
import android.view.View;
import retrofit2.http.Headers;

import androidx.annotation.OptIn;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.appcompat.app.AppCompatActivity; // For Activity
import androidx.fragment.app.Fragment;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import androidx.appcompat.widget.Toolbar;

import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity {

    // Declare the PreviewView
    private PreviewView previewView;
    private ImageCapture imageCapture;
    private FaceDetector faceDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Custom Toolbar");
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        previewView = findViewById(R.id.previewView);
        Button checkoutButton = findViewById(R.id.checkoutButton);
        ImageView frontCardImage = findViewById(R.id.frontCardImage);
        ImageView backCardImage = findViewById(R.id.backCardImage);

        // Initialize Face Detector with settings
        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
                .build();
        faceDetector = FaceDetection.getClient(options);

        // Start CameraX preview and facial analysis
        startCamera();
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the SecondActivity
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(intent);
            }
        });

    }

    public void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // Preview setup
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                // Select the front camera as the default
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                        .build();

                // Initialize ImageCapture
                imageCapture = new ImageCapture.Builder().build();

                // Bind use cases to the camera
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageCapture);

                // Start analyzing images for face detection
                analyzeImage(cameraProvider);

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @OptIn(markerClass = androidx.camera.core.ExperimentalGetImage.class)
    private void analyzeImage(ProcessCameraProvider cameraProvider) {
        // Image analysis for face detection
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder().build();
        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), imageProxy -> {
            // Opting into the experimental getImage() API
            if (imageProxy.getImage() != null) {
                InputImage image = InputImage.fromMediaImage(imageProxy.getImage(), imageProxy.getImageInfo().getRotationDegrees());

                faceDetector.process(image)
                        .addOnSuccessListener(faces -> {
                            if (faces.size() > 0) {
                                Face face = faces.get(0);
                                Log.d("FaceDetection", "Face detected: " + face.getBoundingBox());
                                // If a face is detected, take a picture automatically
                                takePicture();
                            }
                            imageProxy.close();
                        })
                        .addOnFailureListener(e -> {
                            Log.e("FaceDetection", "Face detection failed", e);
                            imageProxy.close();
                        });
            } else {
                imageProxy.close();
            }
        });

        cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_FRONT_CAMERA, imageAnalysis);
    }

    private void takePicture() {
        if (imageCapture == null) {
            return;
        }

        imageCapture.takePicture(ContextCompat.getMainExecutor(this), new ImageCapture.OnImageCapturedCallback() {
            @Override
            public void onCaptureSuccess(@NonNull ImageProxy image) {
                // Process the captured image using the ImageProxy
                // Here you can pass this ImageProxy to your face detection code
                processCapturedImage(image);
                image.close(); // Don't forget to close the image when done
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Log.e("ImageCapture", "Image capture failed", exception);
            }
        });
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    private void processCapturedImage(ImageProxy image) {
        // Convert the ImageProxy to an InputImage for ML Kit
        InputImage inputImage = InputImage.fromMediaImage(image.getImage(), image.getImageInfo().getRotationDegrees());

        // Perform face detection
        faceDetector.process(inputImage)
                .addOnSuccessListener(faces -> {
                    if (!faces.isEmpty()) {
                        Log.d("FaceDetection", "Face detected: " + faces.get(0).getBoundingBox());
                        Toast.makeText(MainActivity.this, "Face Detected!", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("FaceDetection", "No face detected");
                    }
                })
                .addOnFailureListener(e -> Log.e("FaceDetection", "Face detection failed", e));
    }
}
