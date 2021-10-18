package com.sangji.whereu.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sangji.whereu.R;
import com.sangji.whereu.UserAccount;

import java.util.HashMap;
import java.util.Map;

//단체 채팅방 틀 만들기
public class GroupMessageActivity extends AppCompatActivity {

    Map<String, UserAccount> users = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_message);

        FirebaseDatabase.getInstance().getReference().child("whereu").child("UserAccount").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                users = (Map<String, UserAccount>) dataSnapshot.getValue();
                System.out.println(users.size());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}