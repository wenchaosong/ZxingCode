# 二维码扫描
[![](https://jitpack.io/v/wenchaosong/ZxingCode.svg)](https://jitpack.io/#wenchaosong/ZxingCode)

## 使用

```
dependencies{
       implementation 'com.github.wenchaosong:ZxingCode:1.5.0'
}
```

```
if (获取权限后) {
    Intent intent = new Intent(activity, CaptureActivity.class);
    intent.putExtra("statusBarColor", 0xffffffff);
    intent.putExtra("dark", true);
    intent.putExtra("arrowRes", R.mipmap.icon_arrow_left_black);
    intent.putExtra("picRes", R.mipmap.image_crop);
    intent.putExtra("cropRes", R.mipmap.image_crop);
    intent.putExtra("titleText", "标题的描述");
    intent.putExtra("titleTextColor", 0xff000000);
    intent.putExtra("desText", "下面扫描的描述");
    intent.putExtra("lightText", "手电筒的描述");
    activity.startActivityForResult(intent, code);
}
```