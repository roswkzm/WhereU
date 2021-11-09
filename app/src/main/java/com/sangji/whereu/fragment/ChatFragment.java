package com.sangji.whereu.fragment;
import android.app.ActivityOptions;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sangji.whereu.ChatModel;
import com.sangji.whereu.R;
import com.sangji.whereu.UserAccount;
import com.sangji.whereu.chat.GroupMessageActivity;
import com.sangji.whereu.chat.MessageActivity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
public class ChatFragment extends Fragment {
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd hh:mm");
    private ChatRecyclerViewAdapter chatRecyclerViewAdapter = null;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat,container,false);
        RecyclerView recyclerView = view.findViewById(R.id.chatfragment_recyclerview);
        chatRecyclerViewAdapter = new ChatRecyclerViewAdapter();
        recyclerView.setAdapter(chatRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        return view;

    }

    class ChatRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private ArrayList<String> destinationUsers = new ArrayList<>();
        private List<ChatModel> chatModels = new ArrayList<>();
        private List<String> keys = new ArrayList<>();
        private String uid;
        public ChatRecyclerViewAdapter() {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseDatabase.getInstance().getReference().child("whereu").child("chatrooms").orderByChild("users/"+uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    chatModels.clear();
                    for (DataSnapshot item :dataSnapshot.getChildren()){
                        chatModels.add(item.getValue(ChatModel.class));
                        keys.add(item.getKey());
                    }
                    notifyDataSetChanged();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat,parent,false);
            return new CustomViewHolder(view);
        }
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            CustomViewHolder customViewHolder = (CustomViewHolder)holder;
            String destinationUid = null;
            //체팅방에 있는 유저를 체크함
            for(String user: chatModels.get(position).users.keySet()){
                if(!user.equals(uid)){
                    destinationUid = user;
                    destinationUsers.add(destinationUid);
                }
            }
            FirebaseDatabase.getInstance().getReference().child("whereu").child("UserAccount").child(destinationUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    UserAccount userAccount = dataSnapshot.getValue(UserAccount.class);
                    Glide.with(customViewHolder.itemView.getContext())
                            .load(userAccount.getProfileImageUrl())
                            .apply(new RequestOptions().circleCrop())
                            .into(customViewHolder.imageView);
                    customViewHolder.textView_title.setText(userAccount.getName());
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            //메세지를 내림 차순으로 정렬후 마지막 메세지의 키값을 가져옴
            Map<String,ChatModel.Comment> commentMap = new TreeMap<>(Collections.reverseOrder());
            commentMap.putAll(chatModels.get(position).comments);
            if(commentMap.keySet().toArray().length > 0) {
                String lastMessageKey = (String) commentMap.keySet().toArray()[0];
                Log.d("last item", chatModels.get(position).comments.get(lastMessageKey).message);
                customViewHolder.textView_last_message.setText(chatModels.get(position).comments.get(lastMessageKey).message);
                // 체팅방 목록의 타임스테프 출력부분
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
                long unixTime = (long) chatModels.get(position).comments.get(lastMessageKey).timestamp;
                Date date = new Date(unixTime);
                customViewHolder.textView_timestamp.setText(simpleDateFormat.format(date));
            }
            customViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = null;
                    //채팅방에 초대되는 인원이 2명 이상이면 GroupMessageActivity에서 로직처리 1대1 채팅방이면 MessageActivity에서 로직처리함
                    if(chatModels.get(position).users.size() > 2){
                        intent = new Intent(view.getContext(), GroupMessageActivity.class);
                        intent.putExtra("destinationRoom",keys.get(position));
                    }else {
                        intent = new Intent(view.getContext(), MessageActivity.class);
                        intent.putExtra("destinationUid", destinationUsers.get(position));
                    }
                    ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(view.getContext(), R.anim.fromright, R.anim.toleft);
                    startActivity(intent, activityOptions.toBundle());
                }
            });
        }
        @Override
        public int getItemCount() {
            return chatModels.size();
        }
        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public TextView textView_title;
            public TextView textView_last_message;
            public TextView textView_timestamp;
            public CustomViewHolder(View view) {
                super(view);
                imageView = view.findViewById(R.id.chatitem_imageview);
                textView_title = (TextView)view.findViewById(R.id.chatitem_textview_title);
                textView_last_message = (TextView)view.findViewById(R.id.chatitem_textview_lastMessage);
                textView_timestamp = (TextView)view.findViewById(R.id.chatitem_textview_timestamp);
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        chatRecyclerViewAdapter.notifyDataSetChanged();
    }
}