package lib.zxing.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import lib.zxing.R;
import lib.zxing.util.CodeUtils;
import lib.zxing.view.QRCodeReaderView;

public class CaptureActivity extends Activity implements QRCodeReaderView.OnQRCodeReadListener {

    private QRCodeReaderView qrCodeReaderView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        RelativeLayout rlTitle = findViewById(R.id.rl_title);
        qrCodeReaderView = findViewById(R.id.capture_preview);

        int titleColor = getIntent().getIntExtra("statusBarColor", 0);
        if (titleColor != 0) {
            rlTitle.setBackgroundColor(titleColor);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(titleColor);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            qrCodeReaderView.setAutofocusInterval(2000L);
            qrCodeReaderView.setOnQRCodeReadListener(this);
            qrCodeReaderView.setBackCamera();
            qrCodeReaderView.startCamera();
        }
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