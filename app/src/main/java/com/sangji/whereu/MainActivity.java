package com.sangji.whereu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef; // 실시간 데이터베이스

    private TextView tv_id, tv_pass, tv_name;
    private ImageView tv_img;
    String userName;
    UserAccount userAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_id = findViewById(R.id.tv_email);
        tv_pass = findViewById(R.id.tv_uid);
        tv_name = findViewById(R.id.tv_name);
        tv_img = findViewById(R.id.tv_img);



        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("whereu").child("UserAccount").child(firebaseUser.getUid());


        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //문제 부분
                userAccount = snapshot.getValue(UserAccount.class); // String이 아님 UserAccount 객체를 받아옴
                //바로 위의 userAccount와 데이터베이스의 UserAccount는 다른개념임
                tv_name.setText(userAccount.getName()); //getName()대신 다른걸넣으면 데베안의 다른게 가져와짐
                Glide.with(MainActivity.this).load(userAccount.getProfileImageUrl()).override(300,300).fitCenter().into(tv_img);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        String userEmail = mFirebaseAuth.getCurrentUser().getEmail();
        tv_id.setText(userEmail);
        String userUid = mFirebaseAuth.getCurrentUser().getUid();
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

        Button btn_talk = findViewById(R.id.btn_talk);
        btn_talk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,TalkMainActivity.class);
                startActivity(intent);
            }
        });


        Button btn_map = findViewById(R.id.btn_map);
        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,TalkMainActivity.class);
                startActivity(intent);
            }
        });

    }
}