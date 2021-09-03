package lib.zxing.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import lib.zxing.R;
import lib.zxing.util.CodeUtils;
import lib.zxing.view.QRCodeReaderView;

public class CaptureActivity extends Activity implements QRCodeReaderView.OnQRCodeReadListener {

    private QRCodeReaderView qrCodeReaderView;
    private boolean status = false;//记录手电筒状态

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        RelativeLayout rlTitle = findViewById(R.id.rl_title);
        RelativeLayout crop = findViewById(R.id.capture_crop_layout);
        qrCodeReaderView = findViewById(R.id.capture_preview);
        ImageView arrow = findViewById(R.id.iv_arrow);
        TextView title = findViewById(R.id.tv_title);
        TextView des = findViewById(R.id.tv_des);
        LinearLayout sdt = findViewById(R.id.ll_sdt);

        int barColor = getIntent().getIntExtra("statusBarColor", 0);
        int arrowRes = getIntent().getIntExtra("arrowRes", 0);
        int cropRes = getIntent().getIntExtra("cropRes", 0);
        int titleTextColor = getIntent().getIntExtra("titleTextColor", 0);
        String desText = getIntent().getStringExtra("desText");
        boolean dark = getIntent().getBooleanExtra("dark", false);
        if (barColor != 0) {
            rlTitle.setBackgroundColor(barColor);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(barColor);
        }
        View decor = getWindow().getDecorView();
        if (dark) {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
        if (arrowRes != 0) {
            arrow.setImageResource(arrowRes);
        }
        if (cropRes != 0) {
            crop.setBackgroundResource(cropRes);
        }
        if (titleTextColor != 0) {
            title.setTextColor(titleTextColor);
        }
        if (!TextUtils.isEmpty(desText)) {
            des.setText(desText);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            qrCodeReaderView.setAutofocusInterval(2000L);
            qrCodeReaderView.setOnQRCodeReadListener(this);
            qrCodeReaderView.setBackCamera();
            qrCodeReaderView.startCamera();
        }

        sdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status) { //如果已经是打开状态，不需要打开
                    close();
                } else {
                    open();
                }
            }
        });
    }

    //打开手电筒
    private void open() {
        if (status) { //如果已经是打开状态，不需要打开
            return;
        }
        qrCodeReaderView.setTorchEnabled(true);
        status = true;//记录手电筒状态为打开
    }

    //关闭手电筒
    private void close() {
        if (!status) { //如果已经是关闭状态，不需要打开
            return;
        }
        qrCodeReaderView.setTorchEnabled(false);
        status = false;//记录手电筒状态为关闭
    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        Intent intent = getIntent();
        if (!TextUtils.isEmpty(text)) {
            intent.putExtra(CodeUtils.RESULT_STRING, text);
            setResult(RESULT_OK, intent);
        } else {
            setResult(RESULT_CANCELED, intent);
        }
        finish();
    }

    public void back(View view) {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (qrCodeReaderView != null) {
            qrCodeReaderView.startCamera();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (qrCodeReaderView != null) {
            qrCodeReaderView.stopCamera();
        }
    }

}