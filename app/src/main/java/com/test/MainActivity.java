package com.test;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import lib.zxing.util.CodeUtils;

public class MainActivity extends FragmentActivity {

    private TextView mResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mResult = findViewById(R.id.tv_result);
    }

    public void scan(View v) {
        ScanUtil.scan(MainActivity.this, 123);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == RESULT_OK) {
            if (data != null) {
                //返回的文本内容
                String result = data.getStringExtra(CodeUtils.RESULT_STRING);
                mResult.setText(result);
            }
        }
    }
}
