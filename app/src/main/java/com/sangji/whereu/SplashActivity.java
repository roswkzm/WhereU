package com.sangji.whereu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

public class SplashActivity extends  Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super .onCreate(savedInstanceState);

        try{
            Thread.sleep(500);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        startActivity(new Intent(this,LoginActivity.class));
        finish();
    }
}
