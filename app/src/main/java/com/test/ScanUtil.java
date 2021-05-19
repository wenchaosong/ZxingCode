package com.test;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.functions.Consumer;
import lib.zxing.activity.CaptureActivity;

/**
 * Created by 宋文超
 */
public class ScanUtil {

    /**
     * 打开扫描二维码界面
     */
    @SuppressLint("CheckResult")
    public static void scan(final FragmentActivity activity, final int code) {
        RxPermissions rxPermissions = new RxPermissions(activity);
        rxPermissions
                .request(Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean granted) throws Exception {
                        if (granted) {
                            Intent intent = new Intent(activity, CaptureActivity.class);
                            intent.putExtra("statusBarColor", 0xffffff00);
                            activity.startActivityForResult(intent, code);
                        } else {
                            Toast.makeText(activity, "请设置权限", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
