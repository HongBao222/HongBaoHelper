package com.hongbaohelper;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 绑定界面控件
        Button btnPermission = findViewById(R.id.btn_permission);
        Button btnOpenSettings = findViewById(R.id.btn_open_settings);
        Switch switchEnable = findViewById(R.id.switch_enable);
        TextView tvStatus = findViewById(R.id.tv_status);

        // 1. 请求悬浮窗权限按钮
        btnPermission.setOnClickListener(v -> {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 1);
                Toast.makeText(this, "请找到本应用并开启「允许显示在其他应用上层」权限", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "悬浮窗权限已开启", Toast.LENGTH_SHORT).show();
            }
        });

        // 2. 跳转无障碍设置按钮
        btnOpenSettings.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
            Toast.makeText(this, "请在无障碍服务列表中，找到「抢红包助手」并开启", Toast.LENGTH_LONG).show();
        });

        // 3. 主开关：控制服务的启动与停止
        switchEnable.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Intent serviceIntent = new Intent(MainActivity.this, HongBaoService.class);
            if (isChecked) {
                startService(serviceIntent);
                tvStatus.setText("状态：服务已启动，返回微信即可");
                tvStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark)); // 已修正为绿色
                Toast.makeText(this, "抢红包服务已启动", Toast.LENGTH_SHORT).show();
            } else {
                stopService(serviceIntent);
                tvStatus.setText("状态：服务已停止");
                tvStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                Toast.makeText(this, "抢红包服务已停止", Toast.LENGTH_SHORT).show();
            }
        });

        // 初始化状态文本
        tvStatus.setText("状态：请先开启上方两项权限");
    }
}
