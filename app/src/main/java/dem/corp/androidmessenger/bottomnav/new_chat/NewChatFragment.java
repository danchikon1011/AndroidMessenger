package dem.corp.androidmessenger.bottomnav.new_chat;

import android.os.Bundle;
import android.text.BoringLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

import dem.corp.androidmessenger.databinding.FragmentNewChatBinding;
import dem.corp.androidmessenger.users.User;
import dem.corp.androidmessenger.users.UsersAdapter;

public class NewChatFragment extends Fragment {
    private FragmentNewChatBinding binding;
    public Boolean flagg;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNewChatBinding.inflate(inflater, container, false);
        binding.searchView.clearFocus();
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return false;
            }
        });
        loadUsers();

        return binding.getRoot();
    }
    private void filterList(String text){
        ArrayList<User> users = new ArrayList<User>();
        FirebaseDatabase.getInstance().getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String [] chats2=new String[]{""};
                for (DataSnapshot userSnapshot : snapshot.getChildren()){
                    if (userSnapshot.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){//проверка на самого себя
                        chats2=userSnapshot.child("chats").getValue().toString().split(",");
                        break;
                    }
                }
                for (DataSnapshot userSnapshot : snapshot.getChildren()){
                    //Log.i("dsgg",userSnapshot.getKey());
                    //Log.i("dsgg",FirebaseAuth.getInstance().getCurrentUser().getUid());
                    String [] chats1=userSnapshot.child("chats").getValue().toString().split(",");
                    flagg=false;
                    for(String one:chats1) {
                        for(String two:chats2) {
                            //Log.i("dsgg",one +"  "+two);
                            if(one.toString().equals(two.toString())){
                                Log.i("dsgg","Не нужен пользователь"+userSnapshot.child("username").getValue());
                                flagg=true;
                                break;
                            }
                        }
                        if(flagg){break;}
                    }

                    if (flagg || userSnapshot.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){//проверка на самого себя
                        //Log.i("dsgg","hereee");
                        continue;
                    }
                    if(flagg){continue;}
                    //Log.i("dsgg",FirebaseAuth.getInstance().getCurrentUser().;
                    String uid = userSnapshot.getKey();
                    String username = userSnapshot.child("username").getValue().toString();
                    String profileImage = userSnapshot.child("profileImage").getValue().toString();

                    users.add(new User(uid, username, profileImage));
                }
                ArrayList<User> filteredList =new ArrayList<User>();
                for(User item:users){
                    if(item.username.toLowerCase().contains(text.toLowerCase())){
                        filteredList.add(item);
                    }

                }
                binding.usersRv.setLayoutManager(new LinearLayoutManager(getContext()));
                binding.usersRv.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
                binding.usersRv.setAdapter(new UsersAdapter(filteredList));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //binding.usersRv.;
    }

    private void loadUsers(){
        ArrayList<User> users = new ArrayList<User>();
        FirebaseDatabase.getInstance().getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String [] chats2=new String[]{""};
                for (DataSnapshot userSnapshot : snapshot.getChildren()){
                    if (userSnapshot.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){//проверка на самого себя
                        chats2=userSnapshot.child("chats").getValue().toString().split(",");
                        break;
                    }
                }
                for (DataSnapshot userSnapshot : snapshot.getChildren()){
                    //Log.i("dsgg",userSnapshot.getKey());
                    //Log.i("dsgg",FirebaseAuth.getInstance().getCurrentUser().getUid());
                    String [] chats1=userSnapshot.child("chats").getValue().toString().split(",");
                    flagg=false;
                    for(String one:chats1) {
                        for(String two:chats2) {
                            //Log.i("dsgg",one +"  "+two);
                            if(one.toString().equals(two.toString())){
                                Log.i("dsgg","Не нужен пользователь"+userSnapshot.child("username").getValue());
                                flagg=true;
                                break;
                            }
                        }
                        if(flagg){break;}
                    }

                    if (flagg || userSnapshot.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){//проверка на самого себя
                        //Log.i("dsgg","hereee");
                        continue;
                    }
                    if(flagg){continue;}
                    //Log.i("dsgg",FirebaseAuth.getInstance().getCurrentUser().;
                    String uid = userSnapshot.getKey();
                    String username = userSnapshot.child("username").getValue().toString();
                    String profileImage = userSnapshot.child("profileImage").getValue().toString();

                    users.add(new User(uid, username, profileImage));
                }
                Collections.sort(users,(a, b) -> {
                    return a.username.compareTo(b.username);
                });
                binding.usersRv.setLayoutManager(new LinearLayoutManager(getContext()));
                binding.usersRv.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
                binding.usersRv.setAdapter(new UsersAdapter(users));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
