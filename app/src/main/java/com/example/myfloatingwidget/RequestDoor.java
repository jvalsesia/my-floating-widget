package com.example.myfloatingwidget;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface RequestDoor {
    @GET
    Call<String> getDoorState(@Url String url);
}
