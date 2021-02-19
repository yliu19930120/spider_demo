package com.y;

import okhttp3.*;

import java.io.IOException;
import java.util.Map;

public class ReqUtils {


    private static final OkHttpClient CLIENT = new OkHttpClient();

    public static String get(String url) throws IOException{
        Request request = new Request.Builder().url(url).build();
        Response response = CLIENT.newCall(request).execute();
        return response.body().string();
    }

    public static String post(String params,String url) throws  IOException{
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody requestBody = RequestBody.create(params,mediaType);
        Request request = new Request.Builder().url(url).post(requestBody).build();
        Response response = CLIENT.newCall(request).execute();
        return response.body().string();
    }

    public static String post(MediaType mediaType,String params,String url) throws  IOException{
        RequestBody requestBody = RequestBody.create(params,mediaType);
        Request request = new Request.Builder().url(url).post(requestBody).build();
        Response response = CLIENT.newCall(request).execute();
        return response.body().string();
    }

    public static String post(Headers headers, RequestBody requestBody, String url) throws  IOException{
        Request request = new Request.Builder().url(url).headers(headers).post(requestBody).build();
        Response response = CLIENT.newCall(request).execute();
        return response.body().string();
    }
}
