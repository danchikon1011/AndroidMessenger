package dem.corp.androidmessenger.bottomnav.profile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.content.SharedPreferences;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import dem.corp.androidmessenger.LoginActivity;
import dem.corp.androidmessenger.MetkaClass;
import dem.corp.androidmessenger.MyAdapterMetkaClass;
import dem.corp.androidmessenger.databinding.FragmentNewChatBinding;
import dem.corp.androidmessenger.databinding.FragmentProfileBinding;
import dem.corp.androidmessenger.users.User;
import dem.corp.androidmessenger.users.UsersAdapter;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private Uri filePath;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        loadUserInfo();

        binding.buttonBackToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.layoutwithmetkiss.setVisibility(View.INVISIBLE);
            }
        });

        binding.textViewFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<MetkaClass> metkis = new ArrayList<MetkaClass>();
                FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Object qqq_lll=snapshot.child("HasFind").getValue();
                                if(qqq_lll!=null) {
                                    for (String qqqs : qqq_lll.toString().split(",")) {
                                        FirebaseDatabase.getInstance().getReference().child("Metki").child(qqqs)//here error
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        String description = snapshot.child("description").getValue().toString();
                                                        String first_coord = snapshot.child("first_coord").getValue().toString();
                                                        String image = snapshot.child("image").getValue().toString();
                                                        String name = snapshot.child("name").getValue().toString();
                                                        String owner_id = snapshot.child("ownerId").getValue().toString();
                                                        String second_coord = snapshot.child("second_coord").getValue().toString();
                                                        String slognost = snapshot.child("slognost").getValue().toString();
                                                        String zagadka = snapshot.child("zagadka").getValue().toString();

                                                        metkis.add(new MetkaClass(description, first_coord, image, name, owner_id, second_coord, slognost, zagadka));
                                                        binding.recyclerrr.setLayoutManager(new LinearLayoutManager(getContext()));
                                                        binding.recyclerrr.setAdapter(new MyAdapterMetkaClass(getContext(), metkis));
                                                        binding.layoutwithmetkiss.setVisibility(View.VISIBLE);

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });


                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        });
        binding.textViewOwn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<MetkaClass> metkis = new ArrayList<MetkaClass>();
                FirebaseDatabase.getInstance().getReference().child("Metki").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            if(userSnapshot.child("ownerId").getValue().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid().toString())) {
                                //String uid = userSnapshot.getKey();
                                String description = userSnapshot.child("description").getValue().toString();
                                String first_coord = userSnapshot.child("first_coord").getValue().toString();
                                String image = userSnapshot.child("image").getValue().toString();
                                String name = userSnapshot.child("name").getValue().toString();
                                String owner_id = userSnapshot.child("ownerId").getValue().toString();
                                String second_coord = userSnapshot.child("second_coord").getValue().toString();
                                String slognost = userSnapshot.child("slognost").getValue().toString();
                                String zagadka = userSnapshot.child("zagadka").getValue().toString();

                                metkis.add(new MetkaClass(description, first_coord, image, name, owner_id, second_coord, slognost, zagadka));
                            }

                        }
                        binding.recyclerrr.setLayoutManager(new LinearLayoutManager(getContext()));
                        binding.recyclerrr.setAdapter(new MyAdapterMetkaClass(getContext(),metkis));
                        binding.layoutwithmetkiss.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        binding.profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences =getActivity().getSharedPreferences("loginnn", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.remove("pass");
                editor.remove("email");
                editor.apply();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), LoginActivity.class));
            }
        });

        return binding.getRoot();
    }

    ActivityResultLauncher<Intent> pickImageActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode()==Activity.RESULT_OK && result.getData()!=null && result.getData().getData()!=null){
                        filePath = result.getData().getData();

                        try{
                            Bitmap bitmap = MediaStore.Images.Media
                                    .getBitmap(
                                            requireContext().getContentResolver(),
                                            filePath
                                    );
                            binding.profileImageView.setImageBitmap(bitmap);
                        }catch(IOException e){
                            e.printStackTrace();
                        }

                        uploadImage();
                    }
                }
            }
    );

    private void loadUserInfo(){
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String username = snapshot.child("username").getValue().toString();
                        String profileImage = snapshot.child("profileImage").getValue().toString();
                        Object metki_finds=snapshot.child("HasFind").getValue();
                        if(metki_finds!=null) {
                            binding.textViewFind.setText(String.valueOf(metki_finds.toString().split(",").length));
                            if(metki_finds.toString().split(",").length<=5){
                                binding.textView2Ststus.setText("Новичок");
                            }
                            else if(metki_finds.toString().split(",").length<=15){
                                binding.textView2Ststus.setText("Опытный");
                            }else{
                                binding.textView2Ststus.setText("Мастер");
                            }
                        }else{
                            binding.textViewFind.setText("0");
                            binding.textView2Ststus.setText("Новичок");
                        }
                        binding.usernameTv.setText(username);

                        if (!profileImage.isEmpty()){
                            Glide.with(getContext()).load(profileImage).into(binding.profileImageView);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        FirebaseDatabase.getInstance().getReference().child("Metki").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int k=0;
                for (DataSnapshot userSnapshot : snapshot.getChildren()){
                    Log.i("sdfs",userSnapshot.child("ownerId").toString());
                    Log.i("sdfs",FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
                    if (userSnapshot.child("ownerId").getValue().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid().toString())){//проверка на самого себя
                        k+=1;
                    }
                }
                binding.textViewOwn.setText(String.valueOf(k));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void selectImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        pickImageActivityResultLauncher.launch(intent);
    }

    private void uploadImage(){
        if (filePath!=null){
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            FirebaseStorage.getInstance().getReference().child("images/"+uid)
                    .putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //Toast.makeText(getContext(), "Photo upload complete", Toast.LENGTH_SHORT).show();

                            FirebaseStorage.getInstance().getReference().child("images/"+uid).getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                    .child("profileImage").setValue(uri.toString());
                                        }
                                    });
                        }
                    });
        }
    }
}
