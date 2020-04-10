# 二维码扫描

## 使用

```
dependencies{
    implementation 'com.ms:zxingcode:1.0.0'
}
```

```
if (获取权限后) {
    Intent intent = new Intent(activity, CaptureActivity.class);
    activity.startActivityForResult(intent, code);
}
```