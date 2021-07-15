# 二维码扫描
[![](https://jitpack.io/v/wenchaosong/ZxingCode.svg)](https://jitpack.io/#wenchaosong/ZxingCode)

## 使用

```
dependencies{
       implementation 'com.github.wenchaosong:ZxingCode:1.1.0'
}
```

```
if (获取权限后) {
    Intent intent = new Intent(activity, CaptureActivity.class);
    intent.putExtra("statusBarColor", 0xffffff00);
    activity.startActivityForResult(intent, code);
}
```