package com.example.myfloatingwidget;

import static android.view.WindowManager.*;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WidgetService extends Service {
    int LAYOUT_FLAG;
    View myFloatingView;

    WindowManager windowManager;

    ImageView imageClose;
    TextView tvWidget, responseTV;
    Button helloButton;
    float height, width;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // insert onStartCommand
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = LayoutParams.TYPE_PHONE;
        }
        // inflate widget layout
        myFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_widget, null);
        WindowManager.LayoutParams layoutParams =  new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        // initial position
        layoutParams.gravity = Gravity.TOP | Gravity.END;
        layoutParams.x = 0;
        layoutParams.y = 400;

        // layout params for close button

        WindowManager.LayoutParams imageParams =  new LayoutParams(
                140,
                140,
                LAYOUT_FLAG,
                LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        // initial position
        imageParams.gravity = Gravity.BOTTOM | Gravity.CENTER;
        imageParams.x = 0;
        imageParams.y = 100;

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        imageClose = new ImageView(this);
        imageClose.setImageResource(R.drawable.close);
        imageClose.setVisibility(View.INVISIBLE);
        windowManager.addView(imageClose, imageParams);
        windowManager.addView(myFloatingView, layoutParams);
        myFloatingView.setVisibility(View.VISIBLE);

        height = windowManager.getDefaultDisplay().getHeight();
        width = windowManager.getDefaultDisplay().getWidth();

        tvWidget = myFloatingView.findViewById(R.id.text_widget);
        helloButton = myFloatingView.findViewById(R.id.button2);
        responseTV = myFloatingView.findViewById(R.id.responseTV);

        helloButton.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View view) {
                                               // generate random number
                                               int randomNumber = (int) (Math.random() * 100);
                                                // append random number to responseTV
                                                responseTV.setText("Hello World #" + randomNumber);
                                           }
                                       });

        //show current time
        final Handler handler =  new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tvWidget.setText(new SimpleDateFormat("HH:mm:ss").format(new Date()));
                handler.postDelayed(this, 1000);

            }
        }, 10);



        return START_STICKY;
    }

    // insert onDestroy
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
