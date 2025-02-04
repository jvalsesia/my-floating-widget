package com.example.myfloatingwidget;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RequestDoor {
    @GET("/api/door/state")
    Call<Door> getDoorState();
    
}
