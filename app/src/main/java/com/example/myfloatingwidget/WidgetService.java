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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

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

                                               getDoorState();
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

    private void getDoorState() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.106:8080/api/door/state/").addConverterFactory(JacksonConverterFactory.create()).build();
        RequestDoor requestDoor = retrofit.create(RequestDoor.class);
        requestDoor.getDoorState().enqueue(new Callback<Door>() {
            @Override
            public void onResponse(@NonNull Call<Door> call, @NonNull Response<Door> response) {
                Door door = response.body();
                assert door != null;
                if(door.open) {
                    responseTV.setText(R.string.result_open);
                } else {
                    responseTV.setText(R.string.result_close);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Door> call, @NonNull Throwable t) {
                responseTV.setText(R.string.result_fail);
            }
        });
    }

    // insert onDestroy

    public void closeWidget() {
        if (myFloatingView != null) {
            windowManager.removeView(myFloatingView);
            myFloatingView = null;
        }
        if (imageClose != null) {
            windowManager.removeView(imageClose);
            imageClose = null;
        }
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (myFloatingView != null) {
            windowManager.removeView(myFloatingView);
        }
        if (imageClose != null) {
            windowManager.removeView(imageClose);
        }
    }
}
