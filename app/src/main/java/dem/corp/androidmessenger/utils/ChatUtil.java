package dem.corp.androidmessenger.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

import dem.corp.androidmessenger.users.User;

public class ChatUtil {
    public static void createChat(User user){
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        FirebaseDatabase.getInstance().getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean flag=true;
                otto:
                for (DataSnapshot userSnapshot : snapshot.getChildren()){
                    if(userSnapshot.getKey().equals(uid)){
                        for (DataSnapshot userSnapshot2 : snapshot.getChildren()){
                            if(userSnapshot2.getKey().equals(user.uid)){
                                String[] chats2=userSnapshot2.child("chats").getValue().toString().split(",");
                                String[] chats1=userSnapshot.child("chats").getValue().toString().split(",");
                                her:
                                for(String chat1:chats1){
                                    for(String chat2:chats2){
                                        if(chat1.equals(chat2)){
                                            Log.i("dsgg","НЕЛЬЗЯЯЯЯЯЯЯ");
                                            flag=false;
                                            break her;
                                        }
                                    }
                                }
                                break otto;
                            }
                        }

                    }
                }
                if(flag){
                    HashMap<String, String> chatInfo = new HashMap<>();
                    chatInfo.put("user1", uid);
                    chatInfo.put("user2", user.uid);

                    String chatId = generateChatId(uid, user.uid);
                    FirebaseDatabase.getInstance().getReference().child("Chats").child(chatId)
                            .setValue(chatInfo);

                    addChatIdToUser(uid, chatId);
                    addChatIdToUser(user.uid, chatId);
                }
                else{
                    Log.i("dsgg","НЕЛЬЗЯЯЯЯЯЯЯ");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private static String generateChatId(String userId1, String userId2){
        String sumUser1User2 = userId1+userId2;
        char[] charArray = sumUser1User2.toCharArray();
        Arrays.sort(charArray);

        return new String(charArray);
    }

    private static void addChatIdToUser(String uid, String chatId){
        FirebaseDatabase.getInstance().getReference().child("Users").child(uid)
                .child("chats").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        String chats = task.getResult().getValue().toString();
                        String chatsUpd = addIdToStr(chats, chatId);

                        FirebaseDatabase.getInstance().getReference().child("Users").child(uid)
                                .child("chats").setValue(chatsUpd);
                    }
                });
    }

    private static String addIdToStr(String str, String chatId){
        str += (str.isEmpty()) ? chatId : (","+chatId);
        return str;
    }
}
