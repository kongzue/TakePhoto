package com.kongzue.takephoto;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.kongzue.takephoto.util.FileUtils;
import com.kongzue.takephoto.util.imagechooser.api.ChooserType;
import com.kongzue.takephoto.util.imagechooser.api.ChosenImage;
import com.kongzue.takephoto.util.imagechooser.api.ChosenImages;
import com.kongzue.takephoto.util.imagechooser.api.ImageChooserListener;
import com.kongzue.takephoto.util.imagechooser.api.ImageChooserManager;
import com.kongzue.takephoto.util.imagezip.CompressHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class TakePhotoUtil {
    
    public static String CACHE_FOLDER_NAME = ".TakePhotoCache";             //缓存文件夹名称
    
    public static boolean COMPRESSED_PICS = true;       //是否压缩图片
    public static boolean ALLOW_MULTIPLE = false;       //是否允许多选
    public static int DEFAULT_QUALITY = 80;             //图片质量
    public static int DEFAULT_MAX_WIDTH = 1080;         //图片最大宽度
    public static int DEFAULT_MAX_HEIGHT = 1080;        //图片最大高度
    public static Bitmap.CompressFormat DEFAULT_PIC_TYPE = Bitmap.CompressFormat.JPEG;        //图片默认压缩格式
    
    private String[] cameraPermissions;
    private String[] galleryPermissions;
    private AppCompatActivity context;
    private int REQUEST_CODE_PERMISSION = 0x00099;
    
    private int chooserType;
    private static final int CODE_TAKE_PICTURE = 99;
    private ImageChooserManager imageChooserManager;
    private String filePath;
    private String originalFilePath;
    
    private static TakePhotoUtil takePhotoUtil;
    
    private TakePhotoUtil() {
        cameraPermissions = new String[]{
                Manifest.permission.CAMERA
        };
        
        galleryPermissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
    }
    
    public static TakePhotoUtil getInstance(AppCompatActivity appCompatActivity) {
        synchronized (TakePhotoUtil.class) {
            if (takePhotoUtil == null) takePhotoUtil = new TakePhotoUtil();
            takePhotoUtil.context = appCompatActivity;
            return takePhotoUtil;
        }
    }
    
    private ImageChooserListener imageChooserListener = new ImageChooserListener() {
        @Override
        public void onImageChosen(final ChosenImage image) {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    chooseImage(image);
                }
            });
        }
        
        @Override
        public void onError(String reason) {
            context.runOnUiThread(new Runnable() {
                
                @Override
                public void run() {
                    log("获取图片失败");
                }
            });
        }
        
        @Override
        public void onImagesChosen(final ChosenImages images) {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    chooseImages(images);
                }
            });
        }
    };
    
    private void chooseImage(final ChosenImage image) {
        context.runOnUiThread(new Runnable() {
            
            @Override
            public void run() {
                originalFilePath = image.getFilePathOriginal();
                if (image != null) {
                    String outfile = originalFilePath;
                    if (COMPRESSED_PICS) {
                        File newFile = new CompressHelper.Builder(context)
                                .setMaxWidth(DEFAULT_MAX_WIDTH)
                                .setMaxHeight(DEFAULT_MAX_HEIGHT)
                                .setQuality(DEFAULT_QUALITY)
                                .setCompressFormat(DEFAULT_PIC_TYPE) // 设置默认压缩为jpg格式
                                .setDestinationDirectoryPath(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + CACHE_FOLDER_NAME)
                                .setFileNamePrefix("temp_")
                                .build()
                                .compressToFile(new File(originalFilePath));
                        if (newFile != null) {
                            outfile = newFile.getAbsolutePath();
                        }
                    }
                    log("outfile:" + outfile);
                    if (returnPhoto != null)
                        returnPhoto.onGetPhotos(new String[]{outfile});
                } else {
                    Log.i("未选择图像", "Chosen Image: Is null");
                }
            }
        });
    }
    
    private void chooseImages(final ChosenImages chosenImages) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (chosenImages == null || chosenImages.getImages() == null || chosenImages.getImages().isEmpty()) {
                    Log.i("未选择图像", "Chosen Image: Is null");
                    return;
                }
                List<String> results = new ArrayList<>();
                List<ChosenImage> images = chosenImages.getImages();
                for (ChosenImage image : images) {
                    originalFilePath = image.getFilePathOriginal();
                    if (image != null) {
                        String outfile = originalFilePath;
                        if (COMPRESSED_PICS) {
                            File newFile = new CompressHelper.Builder(context)
                                    .setMaxWidth(DEFAULT_MAX_WIDTH)
                                    .setMaxHeight(DEFAULT_MAX_HEIGHT)
                                    .setQuality(DEFAULT_QUALITY)
                                    .setCompressFormat(DEFAULT_PIC_TYPE) // 设置默认压缩为jpg格式
                                    .setDestinationDirectoryPath(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + CACHE_FOLDER_NAME)
                                    .setFileNamePrefix("temp_")
                                    .build()
                                    .compressToFile(new File(originalFilePath));
                            if (newFile != null) {
                                outfile = newFile.getAbsolutePath();
                            }
                        }
                        results.add(outfile);
                    }
                }
                String[] resultStrs = new String[results.size()];
                for (int i = 0; i < results.size(); i++) {
                    resultStrs[i] = results.get(i);
                }
                if (returnPhoto != null)
                    returnPhoto.onGetPhotos(resultStrs);
            }
        });
    }
    
    private void log(final Object obj) {
        try {
            context.runOnUiThread(new Runnable() {
                
                @Override
                public void run() {
                    if (BuildConfig.DEBUG) {
                        Log.i(">>>", obj.toString());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public Bitmap getBitmapFromUri(String outfile) {
        try {
            outfile = outfile.replace("/storage/emulated/0/", "file:///sdcard/");
            Uri uri = Uri.parse(outfile);
            // 读取uri所在的图片
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    //拍照
    private File mTmpFile;
    
    public void doOpenCamera() {
        if (checkPermissions(cameraPermissions)) {
            try {
                mTmpFile = FileUtils.createTmpFile(context);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (mTmpFile != null && mTmpFile.exists()) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (android.os.Build.VERSION.SDK_INT < 24) {
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTmpFile));
                    context.startActivityForResult(intent, CODE_TAKE_PICTURE);
                } else {
                    ContentValues contentValues = new ContentValues(1);
                    contentValues.put(MediaStore.Images.Media.DATA, mTmpFile.getAbsolutePath());
                    Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    context.startActivityForResult(intent, CODE_TAKE_PICTURE);
                }
            } else {
                log("doOpenCamera：无法创建照片文件，请检查权限设置");
            }
        } else {
            Log.i("Error", "权限未处理，请确保已申请权限：Manifest.permission.CAMERA");
            requestPermission(cameraPermissions, 0x0001);
        }
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CODE_TAKE_PICTURE:
                log("resultCode:" + resultCode);
                if (resultCode == RESULT_OK) {
                    try {
                        String outfile = mTmpFile.getAbsolutePath();
                        if (COMPRESSED_PICS) {
                            File newFile = new CompressHelper.Builder(context)
                                    .setMaxWidth(DEFAULT_MAX_WIDTH)
                                    .setMaxHeight(DEFAULT_MAX_HEIGHT)
                                    .setQuality(DEFAULT_QUALITY)
                                    .setCompressFormat(DEFAULT_PIC_TYPE)
                                    .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                            Environment.DIRECTORY_PICTURES).getAbsolutePath())
                                    .build()
                                    .compressToFile(mTmpFile);
                            outfile = newFile.getAbsolutePath();
                        }
                        
                        log("outfile:" + outfile);
                        if (returnPhoto != null)
                            returnPhoto.onGetPhotos(new String[]{outfile});
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case ChooserType.REQUEST_PICK_PICTURE:
                if (imageChooserManager == null) {
                    reinitializeImageChooser();
                }
                imageChooserManager.submit(requestCode, data);
                break;
            case ChooserType.REQUEST_CAPTURE_PICTURE:
                if (imageChooserManager == null) {
                    reinitializeImageChooser();
                }
                imageChooserManager.submit(requestCode, data);
                break;
        }
    }
    
    private void reinitializeImageChooser() {
        chooserType = ChooserType.REQUEST_PICK_PICTURE;
        imageChooserManager = new ImageChooserManager(context, chooserType, true);
        Bundle bundle = new Bundle();
        bundle.putBoolean(Intent.EXTRA_ALLOW_MULTIPLE, ALLOW_MULTIPLE);
        imageChooserManager.setExtras(bundle);
        imageChooserManager.setImageChooserListener(imageChooserListener);
        imageChooserManager.reinitialize(filePath);
    }
    
    //相册
    public void doOpenGallery() {
        if (checkPermissions(galleryPermissions)) {
            imageChooserManager = new ImageChooserManager(context, ChooserType.REQUEST_PICK_PICTURE, true);
            Bundle bundle = new Bundle();
            bundle.putBoolean(Intent.EXTRA_ALLOW_MULTIPLE, ALLOW_MULTIPLE);
            imageChooserManager.setExtras(bundle);
            imageChooserManager.setImageChooserListener(imageChooserListener);
            imageChooserManager.clearOldFiles();
            try {
                filePath = imageChooserManager.choose();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.i("Error", "权限未处理，请确保已申请权限：Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE");
            requestPermission(galleryPermissions, 0x0001);
        }
    }
    
    //权限处理
    private boolean checkPermissions(String[] permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                log("未获取权限：" + permission);
                return false;
            }
        }
        return true;
    }
    
    private void requestPermission(String[] permissions, int requestCode) {
        this.REQUEST_CODE_PERMISSION = requestCode;
        if (checkPermissions(permissions)) {
            permissionSuccess(REQUEST_CODE_PERMISSION);
        } else {
            List<String> needPermissions = getDeniedPermissions(permissions);
            ActivityCompat.requestPermissions(context, needPermissions.toArray(new String[needPermissions.size()]), REQUEST_CODE_PERMISSION);
        }
    }
    
    private void permissionSuccess(int requestCode) {
        Log.d(">>>", "获取权限成功=" + requestCode);
    }
    
    private List<String> getDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissionList = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) !=
                    PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
                needRequestPermissionList.add(permission);
            }
        }
        return needRequestPermissionList;
    }
    
    //接口
    private ReturnPhoto returnPhoto;
    
    public ReturnPhoto getReturnPhoto() {
        return returnPhoto;
    }
    
    public TakePhotoUtil setReturnPhoto(ReturnPhoto returnPhoto) {
        this.returnPhoto = returnPhoto;
        return this;
    }
    
    public interface ReturnPhoto {
        void onGetPhotos(String[] selectImagePaths);
        
        void onError(Exception e);
    }
    
}
