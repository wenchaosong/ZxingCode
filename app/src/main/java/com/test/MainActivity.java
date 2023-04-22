package com.test;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import lib.zxing.util.CodeUtils;

public class MainActivity extends FragmentActivity {

    private TextView mResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mResult = findViewById(R.id.tv_result);
    }

    public void generate(View v) {
        EditText editText = findViewById(R.id.et);
        ImageView imageView = findViewById(R.id.iv);
        String content = editText.getText().toString().trim();
        Bitmap qrCode = CodeUtils.createQRCode(content, 300);
        imageView.setImageBitmap(qrCode);
    }

    public void scan(View v) {
        ScanUtil.scan(MainActivity.this, 123);
    }

    public void pic(View v) {
        ScanUtil.scan(MainActivity.this, 456);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 123) {
                if (data != null) {
                    //返回的文本内容
                    String result = data.getStringExtra(CodeUtils.RESULT_STRING);
                    mResult.setText(result);
                } else {
                    mResult.setText("二维码错误");
                }
            } else {
                if (data != null) {
                    //返回的文本内容
                    String result = data.getStringExtra(CodeUtils.RESULT_STRING);
                    mResult.setText(result);
                } else {
                    mResult.setText("二维码错误");
                }
            }
        } else {
            mResult.setText("二维码错误");
        }
    }
}
