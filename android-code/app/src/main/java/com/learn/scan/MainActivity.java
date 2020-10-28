package com.learn.scan;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;

/**
 * scan kit
 * https://developer.huawei.com/consumer/cn/codelab/ScanKit-DefaultView/index.html#0
 */
public class MainActivity extends AppCompatActivity {
    public static final int DEFAULT_VIEW = 0x22;
    private static final int REQUEST_CODE_SCAN = 0X01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addScanClick();
    }

    public void addScanClick() {
        ImageView scanIv = (ImageView) findViewById(R.id.qrcode_scan_iv);
        scanIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 实际上使用的app 应先判断用户是都登录 登录有才调用  否则跳转登录页
                // 检查权限
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                            DEFAULT_VIEW);
                } else {
                    ScanUtil.startScan(MainActivity.this, REQUEST_CODE_SCAN, new HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScan.ALL_SCAN_TYPE).create());
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissions == null || grantResults == null || grantResults.length < 2 || grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // 调用扫码
        if (requestCode == DEFAULT_VIEW) {
            //start ScankitActivity for scanning barcode
            ScanUtil.startScan(this, REQUEST_CODE_SCAN, new HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScan.ALL_SCAN_TYPE).create());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        // 扫码成功
        if (requestCode == REQUEST_CODE_SCAN) {
            Object obj = data.getParcelableExtra(ScanUtil.RESULT);
            if (obj instanceof HmsScan) {
                if (!TextUtils.isEmpty(((HmsScan) obj).getOriginalValue())) {

                    if (((HmsScan) obj).getOriginalValue().indexOf("action=scankit&code=") == -1) {
                        Toast.makeText(this, "亲，请使用正确的登录二维码", Toast.LENGTH_LONG).show();
                        return;
                    }
                    Intent intent = new Intent(this, QrLoginActivity.class);
                    intent.putExtra("qrcodeString", ((HmsScan) obj).getOriginalValue());
                    startActivity(intent);
//                    Toast.makeText(this, ((HmsScan) obj).getOriginalValue(), Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
        return;
    }
}
