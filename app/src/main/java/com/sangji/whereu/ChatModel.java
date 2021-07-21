//체팅방 데이터베이스
package com.sangji.whereu;

import java.util.HashMap;
import java.util.Map;


    public class ChatModel{

        public Map<String,Boolean> users = new HashMap<>(); // 체팅방의 유저들
        public Map<String,Comment> comments = new HashMap<>(); //체팅방의 대화내용
        public static class Comment{

            public String uid;
            public String message;
            public Object timestamp;
        }
    }

