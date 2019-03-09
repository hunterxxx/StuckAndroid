package com.microsoft.sdksample;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

interface PostWav {



    @Multipart
        @POST("v1?cid=48b1fa24-f21f-4b41-8c3f-be319d8b4b26")
        Call<ResponseBody> send (@Header("Content-Type") String contentType,
                                 @Part("myFile") RequestBody audioFile,
                                 @Header("Ocp-Apim-Subscription-Key") String lang);




    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://francecentral.stt.speech.microsoft.com/speech/recognition/dictation/cognitiveservices/")
            .build();

    PostWav service = retrofit.create(PostWav.class);
    }
    //PostWav.send("forHan", "6149675feef147f797f24802e78aafac");
