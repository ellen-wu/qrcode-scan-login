package com.learn.scan.utils;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkUtil {
    public static String doGet(String urlPath) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(urlPath).build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {

        }
        return "";
    }
    public static String doPost(String urlPath, HashMap<String, String> params) {
        OkHttpClient okHttpClient = new OkHttpClient();
        MultipartBody.Builder builder = new MultipartBody.Builder();
        // 设置为表单类型
        builder.setType(MultipartBody.FORM);
        for (String key : params.keySet()) {
            builder.addFormDataPart(key, params.get(key));
            // builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + key + "\""), RequestBody.create(null, params.get(key)));
        }
//        System.out.println(params);
        RequestBody requestBody = builder.build();

//        FormBody.Builder builder1 = new FormBody.Builder();
//        for (String key : params.keySet()) {
//            builder1.add(key, params.get(key));
//        }


        Request request = new Request.Builder().url(urlPath).post(requestBody).build();
        try {
            Call call = okHttpClient.newCall(request);
            Response response = call.execute();
//            System.out.println(response);
            if (response.isSuccessful()) {
                String body = response.body().string();
//                System.out.println(body);

                return body;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });*/
        return "";
    }

    /**
     * 压缩文件上传
     * @param urlPath
     * @param params
     * @param compressedFile
     * @return
     */
    public static String doPostUploadFile(String urlPath, HashMap<String, String> params, List<File> compressedFile) {
        OkHttpClient okHttpClient = new OkHttpClient();
        MultipartBody.Builder builder = new MultipartBody.Builder();
        // 设置为表单类型
        builder.setType(MultipartBody.FORM);
        for (String key : params.keySet()) {
            builder.addFormDataPart(key, params.get(key));
        }
        for (int i = 0; i < compressedFile.size(); i++) {
            System.out.println("compress type: " + compressedFile.get(i).getClass().toString());
            builder.addFormDataPart("img_" + i, compressedFile.get(i).getName(), RequestBody.create(MediaType.parse("image/jpeg"), compressedFile.get(i)));
        }
        RequestBody requestBody = builder.build();

        Request request = new Request.Builder().url(urlPath).post(requestBody).build();
        try {
            Call call = okHttpClient.newCall(request);
            Response response = call.execute();
            if (response.isSuccessful()) {
                String body = response.body().string();
                System.out.println(body);

                return body;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }
}
