package com.sangji.whereu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private TextView tv_id, tv_pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mFirebaseAuth = FirebaseAuth.getInstance();
        String userEmail = mFirebaseAuth.getCurrentUser().getEmail();
        String userUid = mFirebaseAuth.getCurrentUser().getUid();
        tv_id = findViewById(R.id.tv_email);
        tv_pass = findViewById(R.id.tv_uid);
        tv_id.setText(userEmail);
        tv_pass.setText(userUid);

        Button btn_logout = findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 로그아웃 하기
                mFirebaseAuth.signOut();

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        /*
        // 회원탈퇴 기능
        mFirebaseAuth.getCurrentUser().delete();
         */
    }
}