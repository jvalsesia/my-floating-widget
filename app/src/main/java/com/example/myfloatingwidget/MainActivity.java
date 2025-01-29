package com.example.myfloatingwidget;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    Button buttonWidget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//
//        if(!Settings.canDrawOverlays(MainActivity.this)) {
//            getPermission();
//        } else {
//            Intent intent = new Intent(MainActivity.this, WidgetService.class);
//            startService(intent);
//        }

        buttonWidget = findViewById(R.id.button);
        getPermission();
        buttonWidget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Settings.canDrawOverlays(MainActivity.this)) {
                    getPermission();
                } else {
                    Intent intent = new Intent(MainActivity.this, WidgetService.class);
                    startService(intent);
                }
            }
        });

//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_main);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
    }


    public void getPermission() {
        if(!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}