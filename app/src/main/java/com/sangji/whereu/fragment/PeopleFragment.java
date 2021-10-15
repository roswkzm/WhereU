//친구추가 부분
package com.sangji.whereu.fragment;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sangji.whereu.R;
import com.sangji.whereu.UserAccount;
import com.sangji.whereu.chat.MessageActivity;

import java.util.ArrayList;
import java.util.List;

public class PeopleFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_people,container,false);
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.peoplefragment_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        recyclerView.setAdapter(new PeopleFragmentRecyclerViewAdapter());

        return view;
    }

    class PeopleFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        List<UserAccount> userAccounts;
        public PeopleFragmentRecyclerViewAdapter() {
            userAccounts = new ArrayList<>();
            final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseDatabase.getInstance().getReference().child("whereu").child("UserAccount").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    userAccounts.clear();

                    for(DataSnapshot snapshot :dataSnapshot.getChildren()){

                        UserAccount userAccount = snapshot.getValue(UserAccount.class);

                        if(userAccount.getIdToken().equals(myUid)){
                            continue;
                        }
                        userAccounts.add(userAccount);
                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError error) {

                }
            });

        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend,parent,false);

            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            Glide.with
                    (holder.itemView.getContext())
                    .load(userAccounts.get(position).getProfileImageUrl())
                    .apply(new RequestOptions().circleCrop())
                    .into(((CustomViewHolder)holder).imageView);
            ((CustomViewHolder)holder).textView.setText(userAccounts.get(position).getName());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), MessageActivity.class);
                    intent.putExtra("destinationUid",userAccounts.get(position).getIdToken());
                    ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(view.getContext(),R.anim.fromright,R.anim.toleft);    //애니메이션 효과
                    startActivity(intent,activityOptions.toBundle());   // 화면 전환과 동시에 윗줄의 애니메이션 실행구현
                }
            });
            //만약 상태메시지가 있을경우 바인딩 해주는 코드임
            if(userAccounts.get(position).getComment() != null){
                ((CustomViewHolder) holder).textView_commnet.setText(userAccounts.get(position).getComment());
            }

        }

        @Override
        public int getItemCount() {
            return userAccounts.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;     //친구의 프사
            public TextView textView;       //친구의 이름
            public TextView textView_commnet;   //친구의 상태메세지

            public CustomViewHolder(View view) {
                super(view);
                imageView = (ImageView) view.findViewById(R.id.frienditem_imageview);
                textView = (TextView) view.findViewById(R.id.frienditem_textview);
                textView_commnet = (TextView) view.findViewById(R.id.frienditem_textview_comment);
            }
        }
    }
}
