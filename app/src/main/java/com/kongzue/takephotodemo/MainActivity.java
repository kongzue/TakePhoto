package com.kongzue.takephotodemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kongzue.takephoto.TakePhotoUtil;

public class MainActivity extends AppCompatActivity {
    
    private Button btnTakePhoto;
    private ImageView imgPhoto;
    private TextView txtPath;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //initViews
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        imgPhoto = findViewById(R.id.imgPhoto);
        txtPath = findViewById(R.id.txtPath);
        
        //初始化
        TakePhotoUtil.ALLOW_MULTIPLE = true;                               //是否允许多选图片
        TakePhotoUtil.COMPRESSED_PICS = true;                               //是否开启压缩
        TakePhotoUtil.DEFAULT_QUALITY = 90;                                 //压缩框架：图片质量
        TakePhotoUtil.DEFAULT_MAX_WIDTH = 1080;                             //压缩框架：图片最大宽度
        TakePhotoUtil.DEFAULT_MAX_HEIGHT = 1080;                            //压缩框架：图片最大高度
        TakePhotoUtil.DEFAULT_PIC_TYPE = Bitmap.CompressFormat.JPEG;        //压缩框架：默认压缩格式
        
        //回调逻辑
        TakePhotoUtil.getInstance(MainActivity.this).setReturnPhoto(new TakePhotoUtil.ReturnPhoto() {
            
            @Override
            public void onGetPhotos(String[] selectImagePaths) {
                imgPhoto.setImageBitmap(TakePhotoUtil.getInstance(MainActivity.this).getBitmapFromUri(selectImagePaths[0]));
                txtPath.setText(selectImagePaths[0]);
                
                Log.i(">>>", "onGetPhotos: ====================");
                for (String s : selectImagePaths) {
                    Log.i("+", s);
                }
                Log.i(">>>", "onGetPhotos: ====================");
            }
            
            @Override
            public void onError(Exception e) {
                Toast.makeText(MainActivity.this, "发生错误，请在Logcat查看详情", Toast.LENGTH_SHORT);
                e.printStackTrace();
            }
        });
        
        //执行方法
        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu();
            }
        });
    }
    
    private void showMenu() {
        String[] selectShunxuStr = new String[]{"使用相机拍摄", "从相册中选择", "删除"};
        new AlertDialog.Builder(this).setItems(selectShunxuStr, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        TakePhotoUtil.getInstance(MainActivity.this).doOpenCamera();
                        break;
                    case 1:
                        TakePhotoUtil.getInstance(MainActivity.this).doOpenGallery();
                        break;
                    case 2:
                        imgPhoto.setImageDrawable(null);
                        txtPath.setText("");
                        break;
                    default:
                        break;
                }
                dialog.dismiss();
            }
        }).show();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        TakePhotoUtil.getInstance(MainActivity.this).onActivityResult(requestCode, resultCode, data);
    }
}
