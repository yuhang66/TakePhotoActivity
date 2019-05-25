package com.example.administrator.taktphotoactivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.cjt2325.cameralibrary.JCameraView;
import com.cjt2325.cameralibrary.listener.ClickListener;
import com.cjt2325.cameralibrary.listener.ErrorListener;
import com.cjt2325.cameralibrary.listener.JCameraListener;
import com.cjt2325.cameralibrary.util.DeviceUtil;
import com.cjt2325.cameralibrary.util.FileUtil;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class Second extends AppCompatActivity {
    private JCameraView jCameraView;
    private boolean onlyPhotograph;
    public static final int RESULT_CODE_RETURN_PHOTO = 101;
    public static final int RESULT_CODE_RETURN_VIDEO = 102;
    public static final int RESULT_CODE_PERMISS_REJECT = 103;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_second);
        Intent intent = getIntent();
        if (intent != null){
            onlyPhotograph = intent.getBooleanExtra("onlyPhotograph", false);
        }
        jCameraView = (JCameraView) findViewById(R.id.jcameraview);
        //设置视频保存路径
        jCameraView.setSaveVideoPath(Environment.getExternalStorageDirectory().getPath() + File.separator + "JCamera");
        //是否只允许拍照
        if (onlyPhotograph){
            jCameraView.setTip("轻触拍照");
            jCameraView.setFeatures(JCameraView.BUTTON_STATE_ONLY_CAPTURE);//只给拍照
        }else {
            jCameraView.setTip("轻触拍照，长按摄像");
            jCameraView.setFeatures(JCameraView.BUTTON_STATE_BOTH);//同时拍照和摄像
        }
        jCameraView.setMediaQuality(JCameraView.MEDIA_QUALITY_MIDDLE);//设置视频质量
        jCameraView.setErrorLisenter(new ErrorListener() {
            @Override
            public void onError() {
                //错误监听，失败时回调
                Log.i("CJT", "camera error");
                Intent intent = new Intent();
                setResult(RESULT_CODE_PERMISS_REJECT, intent);
                finish();
            }

            @Override
            public void AudioPermissionError() {
                Toast.makeText(Second.this, "请检查是否开启录音权限", Toast.LENGTH_SHORT).show();
            }
        });

        //JCameraView监听
        jCameraView.setJCameraLisenter(new JCameraListener() {
            @Override
            public void captureSuccess(Bitmap bitmap) {
                //获取拍照图片bitmap
                Log.i("JCameraView", "bitmap = " + bitmap.getWidth());
                String path = FileUtil.saveBitmap("JCamera", bitmap);
                Intent intent = new Intent();
                intent.putExtra("path", path);
                Log.i("team",path+"000"+bitmap);
               final String image =  ChangeBase.bitmapToBase64(bitmap);
                final String address = "https://aip.baidubce.com/rest/2.0/face/v3/search?access_token=24.4e83c7fc343eeb9063273b998b8fa147.2592000.1561259470.282335-16323186";
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ChangeBase.sendOkhttp(address, image, new Callback() {
                            @Override
                            public void onFailure(Call call, final IOException e) {
                                Second.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(Second.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }

                            @Override
                            public void onResponse(Call call, final Response response) throws IOException {
                                Second.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Toast.makeText(Second.this,response.body().string(),Toast.LENGTH_SHORT).show();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        });
                    }
                }).start();





                setResult(RESULT_CODE_RETURN_PHOTO, intent);
                finish();
            }

            @Override
            public void recordSuccess(String url, Bitmap firstFrame) {//视频路径，首帧图
                //获取视频首帧图并转成路径
                String path = FileUtil.saveBitmap("JCamera", firstFrame);//FileUtil是本库自带的
                Log.i("CJT", "url = " + url + ", Bitmap = " + path);
                Intent intent = new Intent();
                intent.putExtra("path", path);
                intent.putExtra("videoUrl", url);
                setResult(RESULT_CODE_RETURN_VIDEO, intent);
                finish();
            }
        });

        jCameraView.setLeftClickListener(new ClickListener() {
            @Override
            public void onClick() {
                Second.this.finish();
            }
        });
        jCameraView.setRightClickListener(new ClickListener() {
            @Override
            public void onClick() {
                Toast.makeText(Second.this,"Right",Toast.LENGTH_SHORT).show();
            }
        });

        Log.i("CJT", DeviceUtil.getDeviceModel());
    }

    //JCameraView生命周期
    @Override
    protected void onStart() {
        super.onStart();
        //全屏显示
        if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(option);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        jCameraView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        jCameraView.onPause();
    }
}


