package com.test;

import android.Manifest;
import android.content.Intent;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.tbruyelle.rxpermissions3.RxPermissions;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import lib.zxing.activity.CaptureActivity;

/**
 * Created by 宋文超
 */
public class ScanUtil {

    /**
     * 打开扫描二维码界面
     */
    public static void scan(final FragmentActivity activity, final int code) {
        RxPermissions rxPermissions = new RxPermissions(activity);
        rxPermissions
                .request(Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Boolean granted) {
                        if (granted) {
                            Intent intent = new Intent(activity, CaptureActivity.class);
                            intent.putExtra("statusBarColor", 0xffffffff);
                            intent.putExtra("dark", true);
                            intent.putExtra("arrowRes", R.mipmap.icon_arrow_left_black);
                            intent.putExtra("cropRes", R.mipmap.image_crop);
                            intent.putExtra("picRes", R.mipmap.image_crop);
                            intent.putExtra("titleText", "标题的描述");
                            intent.putExtra("titleTextColor", 0xff000000);
                            intent.putExtra("desText", "下面扫描的描述");
                            intent.putExtra("lightText", "手电筒的描述");
                            activity.startActivityForResult(intent, code);
                        } else {
                            Toast.makeText(activity, "请设置权限", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
