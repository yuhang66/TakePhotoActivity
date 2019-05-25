package com.example.administrator.taktphotoactivity;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class ChangeBase {
    public static  String bitmapToBase64(Bitmap bitmap) {

        // 要返回的字符串
        String reslut = null;

        ByteArrayOutputStream baos = null;

        try {

            if (bitmap != null) {

                baos = new ByteArrayOutputStream();
                /**
                 * 压缩只对保存有效果bitmap还是原来的大小
                 */
                bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);

                try {
                    baos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                baos.close();
                // 转换为字节数组
                byte[] byteArray = baos.toByteArray();

                // 转换为字符串
                reslut = Base64.encodeToString(byteArray, Base64.DEFAULT);

                Log.i("sss",reslut);
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                if (baos != null) {
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return reslut;

    }

public static void sendOkhttp( String address,String image,okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
    FormBody formBody = new FormBody.Builder().add("image_type", "BASE64").add("group_id_list", "smartgen").add("image", image).build();

    Request request = new Request.Builder().url(address).
            header("Content-Type", "application/x-www-form-urlencoded").post(formBody).build();

    client.newCall(request).enqueue(callback);

}



}
