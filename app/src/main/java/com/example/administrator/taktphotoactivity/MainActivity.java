package com.example.administrator.taktphotoactivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.cjt2325.cameralibrary.JCameraView;

public class MainActivity extends AppCompatActivity {
    private Button btn;
    private ImageView picture;
    private Uri imageUri;
    public static final int  ConstantsREQUEST_CAMERA = 1;
    public static final int SyncStateContractConstantsREQUESTCODE_CAPTURE_CAMEIA=2;
    public static final int RESULT_CODE_RETURN_PHOTO = 101;
    public static final int RESULT_CODE_RETURN_VIDEO = 102;
    public static final int RESULT_CODE_PERMISS_REJECT = 103;
    private JCameraView jCameraView;
    private boolean onlyPhotograph;
    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img = findViewById(R.id.img);
        btn = findViewById(R.id.take);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toCameraActivity();
            }
        });

    }

    private void toCameraActivity() {
       // 6.0 以上权限动态处理
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)) {
                //不具有获取权限，需要进行权限申请
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.CAMERA}, ConstantsREQUEST_CAMERA);//请自定义最后的参数：常量
                return;
            }
        }
        //根据业务需要添加

        // 跳转到 拍摄界面
        startActivityForResult(new Intent(MainActivity.this, Second.class).putExtra("onlyPhotograph", onlyPhotograph), SyncStateContractConstantsREQUESTCODE_CAPTURE_CAMEIA);//请自定义最后一个参数：常量
    }

    //处理拍照／摄像的返回值
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (requestCode == SyncStateContractConstantsREQUESTCODE_CAPTURE_CAMEIA) {
            if(resultCode == RESULT_CODE_RETURN_PHOTO){//拍照
                //照片路径
                String photoPath = data.getStringExtra("path");
              Log.i("lala",photoPath);
            }else if(resultCode == RESULT_CODE_RETURN_VIDEO){//摄像
                //视频第一帧图片路径
                String firstVideoPicture = data.getStringExtra("path");
                //视频路径，该路径为已压缩过的视频路径
                String videoPath = data.getStringExtra("videoUrl");
            }
        }
    }

    //处理权限
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,  int[] grantResults) {
        switch (requestCode){
            case ConstantsREQUEST_CAMERA:
                int size = 0;
                if (grantResults.length >= 1) {
                    int writeResult = grantResults[0];
                    //读写内存权限
                    boolean writeGranted = writeResult == PackageManager.PERMISSION_GRANTED;
                    if (!writeGranted) {
                        size++;
                    }
                    //录音权限
                    int recordPermissionResult = grantResults[1];
                    boolean recordPermissionGranted = recordPermissionResult == PackageManager.PERMISSION_GRANTED;
                    if (!recordPermissionGranted) {
                        size++;
                    }
                    //相机权限
                    int cameraPermissionResult = grantResults[2];
                    boolean cameraPermissionGranted = cameraPermissionResult == PackageManager.PERMISSION_GRANTED;
                    if (!cameraPermissionGranted) {
                        size++;
                    }
                    if (size == 0) {
                        startActivityForResult(new Intent(MainActivity.this, Second.class), 100);
                    } else {
                        Toast.makeText(this, "请到设置-权限管理中开启", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                break;
        }
    }

    }





