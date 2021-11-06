package com.sangji.whereu.fragment;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.sangji.whereu.R;
import com.sangji.whereu.UserAccount;

import java.util.HashMap;
import java.util.Map;

//상태메세지 부분(3개의 바텀네비게이션 중 제일 오른쪽꺼)
public class AccountFragment extends Fragment {

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef; // 실시간 데이터베이스
    private ImageView show_img;
    private Button changeProfileImage_Btn;
    private Uri imageUri;
    private static final int PICK_FROM_ALBUM = 10;
    UserAccount userAccount;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account,container,false);

        Button button = view.findViewById(R.id.accountFragment_button_comment);
        TextView userComment = view.findViewById(R.id.now_userComment);
        show_img = view.findViewById(R.id.show_img);
        changeProfileImage_Btn = view.findViewById(R.id.changeProfileImage_Btn);
        TextView show_name = view.findViewById(R.id.show_name);
        TextView show_email = view.findViewById(R.id.show_email);
        TextView show_uid = view.findViewById(R.id.show_uid);
        Button btn = view.findViewById(R.id.btn);



        FirebaseAuth myFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = myFirebaseAuth.getCurrentUser();
        DatabaseReference myDatabaseRef = FirebaseDatabase.getInstance().getReference("whereu").child("UserAccount").child(firebaseUser.getUid());
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("whereu");

        myDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userAccount = snapshot.getValue(UserAccount.class);
                userComment.setText(userAccount.getComment());

                Glide.with(AccountFragment.this)
                        .load(userAccount.getProfileImageUrl())
                        .override(300,300)
                        .fitCenter().into(show_img);

                show_name.setText(userAccount.getName());
                show_email.setText(userAccount.getEmailId());
                show_uid.setText(userAccount.getIdToken());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        changeProfileImage_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent,PICK_FROM_ALBUM);
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(view.getContext());
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 전화 걸기
                Intent mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:119"));
                startActivity(mIntent);
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PICK_FROM_ALBUM && resultCode == RESULT_OK){
            show_img.setImageURI(data.getData());
            imageUri = data.getData();
        }
        FirebaseStorage.getInstance().getReference().child("whereu").child("UserAccount").child(mFirebaseAuth.getInstance().getCurrentUser().getUid())
                .putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        Task<Uri> imageUrl = task.getResult().getStorage().getDownloadUrl();
                        while(!imageUrl.isComplete());
                        String strImage = imageUrl.getResult().toString();
                        Log.d("strImage check", "onComplete: strImage" + strImage);
                        UserAccount account = new UserAccount();
                        Map<String, Object> taskMap = new HashMap<String, Object>();
                        taskMap.put("profileImageUrl", strImage);
                        account.setProfileImageUrl(strImage);   // 이미지 주소
                        Log.d("account check", "onComplete: strImage :" + account.getProfileImageUrl());
                        // setValue는 database에 insert하는 행위다.
                        mDatabaseRef.child("UserAccount").child(mFirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(taskMap);
                    }
                }
        );
    }
    /*

    FirebaseStorage.getInstance().getReference().child("whereu").child("UserAccount").child(mFirebaseAuth.getInstance().getCurrentUser().getUid()).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                Task<Uri> imageUrl = task.getResult().getStorage().getDownloadUrl();
                while(!imageUrl.isComplete());
                String strImage = imageUrl.getResult().toString();
                Log.d("strImage check", "onComplete: strImage" + strImage);
                UserAccount account = new UserAccount();
                account.setProfileImageUrl(strImage);   // 이미지 주소
                Log.d("account check", "onComplete: strImage :" + account.getProfileImageUrl());
                // setValue는 database에 insert하는 행위다.
                mDatabaseRef.child("UserAccount").child(mFirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(account);
            }
        }
        );
     */



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