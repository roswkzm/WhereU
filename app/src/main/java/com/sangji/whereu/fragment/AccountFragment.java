package com.sangji.whereu.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sangji.whereu.R;
import com.sangji.whereu.UserAccount;

import java.util.HashMap;
import java.util.Map;

//상태메세지 부분(3개의 바텀네비게이션 중 제일 오른쪽꺼)
public class AccountFragment extends Fragment {

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef; // 실시간 데이터베이스
    UserAccount userAccount;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account,container,false);

        Button button = view.findViewById(R.id.accountFragment_button_comment);
        TextView userComment = view.findViewById(R.id.now_userComment);

        FirebaseAuth myFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = myFirebaseAuth.getCurrentUser();
        DatabaseReference myDatabaseRef = FirebaseDatabase.getInstance().getReference("whereu").child("UserAccount").child(firebaseUser.getUid());

        myDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userAccount = snapshot.getValue(UserAccount.class);
                userComment.setText(userAccount.getComment());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(view.getContext());
            }
        });

        return view;
    }
    void showDialog(Context context){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_comment,null);
        final EditText editText = (EditText) view.findViewById(R.id.commentDialog_edittext);
        editText.setText(userAccount.getComment());
        builder.setView(view).setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Map<String,Object> stringObjectMap = new HashMap<>();
                //확인 버튼을 눌렀을 때 상태메시지가 데이터베이스에 저장하게 하는 부분
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                stringObjectMap.put("comment",editText.getText().toString());
                FirebaseDatabase.getInstance().getReference().child("whereu").child("UserAccount").child(uid).updateChildren(stringObjectMap);

            }
        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }
}