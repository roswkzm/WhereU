package com.sangji.whereu.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.sangji.whereu.ChatModel;
import com.sangji.whereu.R;
import com.sangji.whereu.UserAccount;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//단체 채팅방 틀 만들기
public class GroupMessageActivity extends AppCompatActivity {

    Map<String, UserAccount> users = new HashMap<>();
    String destinationRoom;
    String uid;
    EditText editText;


    private UserAccount destinationuserAccount;
    private DatabaseReference databaseReference;    //메세지를 확인했는지 알아보기 위함
    private  ValueEventListener valueEventListener; //메세지를 확인했는지 알아보기 위함

    private RecyclerView recyclerView;

    List<ChatModel.Comment> comments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_message);
        destinationRoom = getIntent().getStringExtra("destinationRoom");
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        editText = (EditText)findViewById(R.id.groupMessageActivity_editText);

        FirebaseDatabase.getInstance().getReference().child("whereu").child("UserAccount").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                users = (Map<String, UserAccount>) dataSnapshot.getValue();
                init();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        recyclerView = findViewById(R.id.groupMessageActivity_recyclerview);
        recyclerView.setAdapter(new GroupMessageRecyclerViewAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    void init(){
        Button button = (Button) findViewById(R.id.groupMessageActivity_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatModel.Comment comment = new ChatModel.Comment();
                comment.uid = uid;
                comment.message = editText.getText().toString();
                comment.timestamp = ServerValue.TIMESTAMP;
                FirebaseDatabase.getInstance().getReference().child("whereu").child("chatrooms").child(destinationRoom).child("comments")
                        .push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        editText.setText("");
                    }
                });
            }
        });
    }
    class GroupMessageRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        public GroupMessageRecyclerViewAdapter(){

            getMessageList();
        }
        //메세지를 읽어오는 코드
        void getMessageList(){
            databaseReference = FirebaseDatabase.getInstance().getReference().child("whereu").child("chatrooms").child(destinationRoom).child("comments");
            valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    comments.clear();
                    Map<String,Object> readUsersMap = new HashMap<>();
                    for(DataSnapshot item : dataSnapshot.getChildren()){
                        String key = item.getKey();
                        ChatModel.Comment comment_origin = item.getValue(ChatModel.Comment.class);
                        ChatModel.Comment comment_motify = item.getValue(ChatModel.Comment.class);
                        comment_motify.readUsers.put(uid,true);
                        // 메세지 읽었는지 안읽었는지 판별
                        readUsersMap.put(key,comment_motify);
                        comments.add(comment_origin);
                    }
                    if(comments.size() == 0){return;}
                    if(!comments.get(comments.size()-1).readUsers.containsKey(uid)) {

                        //만약 메세지를 읽은것을 확인하면
                        FirebaseDatabase.getInstance().getReference().child("whereu").child("chatrooms").child(destinationRoom).child("comments")
                                .updateChildren(readUsersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                // 메시지가 새로 갱신되도록 함
                                notifyDataSetChanged();
                                recyclerView.scrollToPosition(comments.size() - 1); // 메시지를 보낸후 대화방의 가장 하단으로 시점이 변경됨
                            }
                        });
                    }else{
                        notifyDataSetChanged();
                        recyclerView.scrollToPosition(comments.size() - 1); // 메시지를 보낸후 대화방의 가장 하단으로 시점이 변경됨
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message,parent,false);


            return new GroupMessageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return comments.size();
        }

        private class GroupMessageViewHolder extends RecyclerView.ViewHolder {
            public GroupMessageViewHolder(View view) {
                super(view);
            }
        }
    }
}