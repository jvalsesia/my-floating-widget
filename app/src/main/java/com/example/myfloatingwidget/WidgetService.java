package com.example.myfloatingwidget;

import static android.view.WindowManager.*;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.util.Log;
public class WidgetService extends Service {

    private static final String PREFS_NAME = "DoorPrefs";
    private static final String DOOR_URL_PREF = "doorURLPref";
    private static final String DOOR_PATH_PREF = "doorPathPref";
    // Define a tag for your log messages
    private static final String TAG = "WidgetService";
    public static boolean isRunning;
    int LAYOUT_FLAG;
    View myFloatingView;

    WindowManager windowManager;

    ImageView imageClose;
    TextView responseTV;
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
        layoutParams.gravity = Gravity.TOP | Gravity.START;
        layoutParams.x = 25;
        layoutParams.y = 650;

        // layout params for close button
        WindowManager.LayoutParams imageParams =  new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        // initial position
        imageParams.gravity = Gravity.BOTTOM | Gravity.END;
        imageParams.x = 25;
        imageParams.y = 650;

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        imageClose = new ImageView(this);
        imageClose.setImageResource(R.drawable.close);
        imageClose.setVisibility(View.INVISIBLE);
        windowManager.addView(imageClose, imageParams);
        windowManager.addView(myFloatingView, layoutParams);
        myFloatingView.setVisibility(View.VISIBLE);

        height = windowManager.getDefaultDisplay().getHeight();
        width = windowManager.getDefaultDisplay().getWidth();

        helloButton = myFloatingView.findViewById(R.id.unlock_door_button);
        responseTV = myFloatingView.findViewById(R.id.responseTV);

        helloButton.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View view) {
                                               getDoorState();
                                           }
                                       });


        isRunning = true;
        return START_STICKY;
    }



    private void getDoorState() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String baseUrl = prefs.getString(DOOR_URL_PREF, "");
        String apiPath = prefs.getString(DOOR_PATH_PREF, "");

        // Log an info message
        Log.i(TAG, "Base URL: ".concat(baseUrl));
        Log.i(TAG, "API Path: ".concat(apiPath));
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl).addConverterFactory(JacksonConverterFactory.create()).build();
        RequestDoor requestDoor = retrofit.create(RequestDoor.class);
        requestDoor.getDoorState(baseUrl + apiPath).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                // get response code
                int status = response.code();

                Log.i(TAG, "door status: ".concat(String.valueOf(status)));
                responseTV.setText(String.valueOf(status));

            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e(TAG, "door error: " + t.getMessage());
                responseTV.setText(R.string.result_fail);
            }
        });
    }

    // insert onDestroy

    public void closeWidget() {
        isRunning = false;
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
        closeWidget();
    }
}
