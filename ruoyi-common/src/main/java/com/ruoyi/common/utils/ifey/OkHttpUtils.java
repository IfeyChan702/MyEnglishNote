package com.ruoyi.common.utils.ifey;



import okhttp3.*;
import okhttp3.OkHttpClient;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Okhttp 访问网络的公共类
 */
public class OkHttpUtils {

    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");

    private static OkHttpClient client;

    private static OkHttpClient longClient;
    public static OkHttpClient getOkHttpClient() {
        if(client==null)
            client = new OkHttpClient();
        return client;
    }

    public static OkHttpClient getLoongConnectOkHttpClient() {
        if(longClient==null)
            longClient = new OkHttpClient.Builder()
                    .connectTimeout(300, TimeUnit.SECONDS) // 设置连接超时时间为300秒
                    .readTimeout(600, TimeUnit.SECONDS) // 设置读取超时时间为600秒
                    .build();
        return longClient;
    }

    /**
     * 同步执行
     * @param url
     * @param jsonBody
     * @return
     * @throws IOException
     */
    public static String doPost(String url, String jsonBody) throws IOException {
        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, jsonBody);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = getOkHttpClient().newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response);
            }
            return response.body().string();
        }
    }


    public static String sendPostRequest(String url, Map<String, String> parameters) throws IOException {
        FormBody.Builder formBuilder = new FormBody.Builder();

        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            formBuilder.add(entry.getKey(), entry.getValue());
        }

        RequestBody formBody = formBuilder.build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        try (Response response = getOkHttpClient().newCall(request).execute()) {
            return response.body().string();
        }
    }



    /**
     * 异步执行
     * @param url
     * @param jsonBody
     * @param callback
     */
    public static void doPostAsync(String url, String jsonBody, Callback callback) {
        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, jsonBody);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        getOkHttpClient().newCall(request).enqueue(callback);
    }

    public static void doPostAsync(String url, Map<String, String> parameters, Callback callback) {
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            formBodyBuilder.add(entry.getKey(), entry.getValue());
        }
        RequestBody body = formBodyBuilder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        getOkHttpClient().newCall(request).enqueue(callback);
    }
    public static void doPostAsync(String url, Callback callback) {
        RequestBody requestBody = RequestBody.create(null, new byte[0]);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        getOkHttpClient().newCall(request).enqueue(callback);
    }

    public static void doGetAsyncGet(String url, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        getOkHttpClient().newCall(request).enqueue(callback);
    }
    public static void doPostAsyncLong(String url, Callback callback) {
        RequestBody requestBody = RequestBody.create(null, new byte[0]);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        getLoongConnectOkHttpClient().newCall(request).enqueue(callback);
    }

    /**
     * do get
     * @param url
     * @param headers
     * @param callback
     */
    public static void doGetAsync(String url, Map<String, String> headers, Callback callback) {
        Request.Builder requestBuilder = new Request.Builder()
                .url(url);
        // 添加请求头
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Request request = requestBuilder.build();
        getOkHttpClient().newCall(request).enqueue(callback);
    }

    /**
     * do get
     * @param url

     */
    public static String doGetDirect(String url) {
        Request.Builder requestBuilder = new Request.Builder()
                .url(url);
        // 添加请求头
        Request request = requestBuilder.build();
        try (Response response = getOkHttpClient().newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            return "";
        }
    }

    /**
     * do get
     * @param url
     * @param headers
     * @param callback
     */
    public static void doGetAsyncLong(String url, Map<String, String> headers, Callback callback) {
        Request.Builder requestBuilder = new Request.Builder()
                .url(url);
        // 添加请求头
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Request request = requestBuilder.build();
        getLoongConnectOkHttpClient().newCall(request).enqueue(callback);
    }
}