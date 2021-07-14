package com.sangji.whereu;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.sangji.whereu.fragment.PeopleFragment;

public class TalkMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk_main);

        getFragmentManager().beginTransaction().replace(R.id.mainactivity_framelayout,new PeopleFragment()).commit();


    }
}