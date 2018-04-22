# KongzueTakePhoto
Kongzue APP拍照&相册选择工具

### 更新日志：
暂无

### 说明
1) 在 Android 6.0 以上会自动申请权限，但依然需要您在您的项目中预先声明相机权限和存储读取、写入权限。申请权限的步骤会自动进行。因申请权限需要，您在调用本工具的 Activity 必须是继承自 AppCompatActivity 的，本工具采用单例方式进行使用，在 getInstance() 时必须传入 Activity extends AppCompatActivity.
2) 本工具仅提供默认的单图片拍摄以及相册中的单图片选择功能。
3) 本工具默认集成图片压缩的 Tiny 框架（ https://github.com/Sunzxyong/Tiny ） 感谢 @Sunzxyong 开源做出的贡献。
4) 本工具已经处理在 Android 7.0 以上时系统禁止 APP 互相传输 Uri 可能导致的无法正常调用相机拍摄照片存储在指定目录的问题。请勿担心此问题放心使用。
5) 本工具需要您提供的参数对照表如下：

图片压缩相关：

属性 | 含义 | 说明
---|---|---
DEFAULT_SIZE | 图片最大体积限制（KB） | 可选，默认值800（KB）
DEFAULT_QUALITY | 图片质量 | 可选，默认值80（%）

功能相关：

方法 | 含义 | 是否必须
---|---|---
doOpenCamera() | 调用相机拍照 | 可选
doOpenGallery() | 调用相册选择照片 | 可选
onActivityResult( requestCode, resultCode, data) | 请在您的Activity中重写onActivityResult方法并将相关参数传入本工具的此方法中 | 必须
setReturnPhoto(ReturnPhoto) | 回调监听器 | 可选

需要的权限：
```
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
```

### 准备
1) 修改 AndroidManifest.xml，添加上述权限。

然后初始化 TakePhoto：
```
TakePhotoUtil.getInstance(您的Activity).setReturnPhoto(new TakePhotoUtil.ReturnPhoto() {
            @Override
            public void onGetPhoto(String path, Bitmap bitmap) {

            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
```
请注意，在您第一次调用 getInstance() 方法时会触发权限申请。

此回调方法中，path 为返回的文件路径，bitmap 为已处理好的位图数据。若产生错误，会在 onError 中返回。

2) 请在您的 Activity 中重写 onActivityResult 方法，并将它的数据传入 TakePhotoUtil：

```
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    TakePhotoUtil.getInstance(MainActivity.this).onActivityResult(requestCode, resultCode, data);
}
```

3) 调用相应方法使用相机及相册：
```
//使用相机拍摄
TakePhotoUtil.getInstance(MainActivity.this).doOpenCamera();
//使用相册选择
TakePhotoUtil.getInstance(MainActivity.this).doOpenGallery();
```

### 其他
调整 Tiny 压缩选项：

```
TakePhotoUtil.DEFAULT_QUALITY = 90;
TakePhotoUtil.DEFAULT_SIZE = 900;
```

### 引入TakePhoto到您的项目

引入方法：
```
implementation 'com.kongzue.takephoto:takephoto:1.0.0'
```
