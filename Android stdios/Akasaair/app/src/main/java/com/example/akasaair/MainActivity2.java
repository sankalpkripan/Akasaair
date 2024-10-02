package com.example.akasaair;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.GsonBuilder;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;




import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity2 extends AppCompatActivity {

    private static final String TAG = "GeminiAPI";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // Use the Future-based API for asynchronous content generation
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        // Prepare the content request
        Content content = new Content.Builder().addText("Write a story about a magic backpack.").build();

        // Use an executor for running the task
        Executor executor = Executors.newSingleThreadExecutor();

        // Asynchronous call to generate content
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(
                response,
                new FutureCallback<GenerateContentResponse>() {
                    @Override
                    public void onSuccess(GenerateContentResponse result) {
                        // Process the result when successful
                        String resultText = result.getText();
                        Log.d(TAG, "Generated content: " + resultText);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        // Handle any errors
                        Log.e(TAG, "Error generating content", t);
                    }
                },
                executor
        );
    }
}
