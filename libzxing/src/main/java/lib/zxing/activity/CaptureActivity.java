package lib.zxing.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.media.ExifInterface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import lib.zxing.R;
import lib.zxing.util.CodeUtils;
import lib.zxing.view.BitmapLuminanceSource;
import lib.zxing.view.QRCodeReaderView;

public class CaptureActivity extends Activity implements QRCodeReaderView.OnQRCodeReadListener {

    private QRCodeReaderView qrCodeReaderView;
    private boolean status = false;//记录手电筒状态
    private static final int REQUEST_CODE = 11;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        RelativeLayout rlTitle = findViewById(R.id.rl_title);
        ImageView crop = findViewById(R.id.capture_crop_layout);
        qrCodeReaderView = findViewById(R.id.capture_preview);
        ImageView arrow = findViewById(R.id.iv_arrow);
        ImageView pic = findViewById(R.id.iv_pic);
        TextView title = findViewById(R.id.tv_title);
        TextView des = findViewById(R.id.tv_des);
        TextView light = findViewById(R.id.tv_light);
        LinearLayout sdt = findViewById(R.id.ll_sdt);

        int barColor = getIntent().getIntExtra("statusBarColor", 0);
        int arrowRes = getIntent().getIntExtra("arrowRes", 0);
        int cropRes = getIntent().getIntExtra("cropRes", 0);
        int picRes = getIntent().getIntExtra("picRes", 0);
        int titleTextColor = getIntent().getIntExtra("titleTextColor", 0);
        String desText = getIntent().getStringExtra("desText");
        String titleText = getIntent().getStringExtra("titleText");
        String lightText = getIntent().getStringExtra("lightText");
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
        if (picRes != 0) {
            pic.setImageResource(picRes);
        }
        if (cropRes != 0) {
            crop.setImageResource(cropRes);
        }
        if (titleTextColor != 0) {
            title.setTextColor(titleTextColor);
        }
        if (!TextUtils.isEmpty(desText)) {
            des.setText(desText);
        }
        if (!TextUtils.isEmpty(titleText)) {
            title.setText(titleText);
        }
        if (!TextUtils.isEmpty(lightText)) {
            light.setText(lightText);
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

    public void pic(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Intent intentFile = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intentFile, REQUEST_CODE);
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                if (data != null) {
                    String path = "";
                    Cursor cursor = getContentResolver().query(data.getData(),
                            new String[]{MediaStore.Images.Media.DATA}, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                    if (columnIndex > 0) {
                        path = cursor.getString(columnIndex);
                    }
                    cursor.close();
                    final String finalPath = path;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Bitmap bitmap = compressBitmap(finalPath, 480, 640);
                            MultiFormatReader multiFormatReader = new MultiFormatReader();
                            Hashtable<DecodeHintType, Object> hints = new Hashtable<>(2);
                            List<BarcodeFormat> list = new ArrayList<>();
                            list.add(BarcodeFormat.QR_CODE);
                            hints.put(DecodeHintType.POSSIBLE_FORMATS, list);
                            hints.put(DecodeHintType.CHARACTER_SET, "UTF8");
                            multiFormatReader.setHints(hints);
                            try {
                                Result result = multiFormatReader.decodeWithState(new BinaryBitmap(
                                        new HybridBinarizer(new BitmapLuminanceSource(bitmap))));
                                Intent intent = getIntent();
                                if (!TextUtils.isEmpty(result.getText())) {
                                    intent.putExtra(CodeUtils.RESULT_STRING, result.getText());
                                    setResult(RESULT_OK, intent);
                                } else {
                                    setResult(RESULT_CANCELED, intent);
                                }
                                finish();
                            } catch (NotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        }
    }

    /**
     * 压缩图片
     */
    private Bitmap compressBitmap(String filePath, int maxWidth, int maxHeight) {
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

        //by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
        //you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);
        if (bmp == null) {
            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream(filePath);
                BitmapFactory.decodeStream(inputStream, null, options);
                inputStream.close();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

        if (actualHeight == -1 || actualWidth == -1) {
            try {
                ExifInterface exifInterface = new ExifInterface(filePath);
                actualHeight = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, ExifInterface.ORIENTATION_NORMAL);// 获取图片的高度
                actualWidth = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, ExifInterface.ORIENTATION_NORMAL);// 获取图片的宽度
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (actualWidth <= 0 || actualHeight <= 0) {
            Bitmap bitmap2 = BitmapFactory.decodeFile(filePath);
            if (bitmap2 != null) {
                actualWidth = bitmap2.getWidth();
                actualHeight = bitmap2.getHeight();
            } else {
                return null;
            }
        }

        float imgRatio = (float) actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        //width and height values are set maintaining the aspect ratio of the image
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        }

        //setting inSampleSize value allows to load a scaled down version of the original image
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

        //inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

        //this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            // load the bitmap getTempFile its path
            bmp = BitmapFactory.decodeFile(filePath, options);
            if (bmp == null) {
                InputStream inputStream = null;
                try {
                    inputStream = new FileInputStream(filePath);
                    BitmapFactory.decodeStream(inputStream, null, options);
                    inputStream.close();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }
        if (actualHeight <= 0 || actualWidth <= 0) {
            return null;
        }

        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, 0, 0);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, 0, 0, new Paint(Paint.FILTER_BITMAP_FLAG));

        // 采用 ExitInterface 设置图片旋转方向
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(),
                    matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return scaledBitmap;
    }

    /**
     * 计算inSampleSize
     */
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;

        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }
}