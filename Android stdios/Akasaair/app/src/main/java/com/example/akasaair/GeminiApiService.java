package com.example.akasaair;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Headers;
public interface GeminiApiService {
    @Headers("Authorization: Bearer AIzaSyBJFIJagMpqatD18nTDOcqMzKpgEq3jyAs")
    @POST("/app/prompts/new_chat")  // Replace with actual API endpoint
    Call<GeminiResponse> getGeminiResponse(@Body GeminiRequest request);
}
