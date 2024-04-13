package dem.corp.androidmessenger;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.media.metrics.Event;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Circle;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.layers.ObjectEvent;
import com.yandex.mapkit.location.FilteringMode;
import com.yandex.mapkit.location.LocationListener;
import com.yandex.mapkit.location.LocationStatus;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CompositeIcon;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.InputListener;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.map.RotationType;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.mapkit.user_location.UserLocationObjectListener;
import com.yandex.mapkit.user_location.UserLocationView;
import com.yandex.runtime.image.ImageProvider;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import android.content.SharedPreferences;
import dem.corp.androidmessenger.databinding.FragmentMapsBinding;
import dem.corp.androidmessenger.databinding.FragmentProfileBinding;
import dem.corp.androidmessenger.users.User;
import dem.corp.androidmessenger.users.UsersAdapter;


public class MapFragment extends Fragment implements UserLocationObjectListener  {
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 1;
    SupportMapFragment supportMapFragment;
    private FragmentMapsBinding binding;
    private com.yandex.mapkit.location.LocationManager locationManager;
    private LocationListener myLocationListener;
    private Point myLocation;
    private UserLocationLayer userLocationLayer;
    MapObjectTapListener mapObjectTapListener;
    public Uri filePath;
    Point tap_point;
    InputListener inputListener;
    MetkaClass qq1;

    List<PlacemarkMapObject> mp=new ArrayList<>();
    Boolean ReadyLoadPhoto=false;
    ArrayList<MetkaClass> metkis = new ArrayList<MetkaClass>();

    String last_tap_metka;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //MapKitFactory.setApiKey("e3a26867-ca88-4822-96c2-87eeafa68da6");

        // Initialize view
        MapKitFactory.initialize(getContext());
        binding = FragmentMapsBinding.inflate(inflater, container, false);
        binding.buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.coordLayout.setVisibility(View.VISIBLE);
                ReadyLoadPhoto=false;
            }
        });
        binding.buttonBackMetkaLayput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.buttonFindMetkaLayout.setVisibility(View.VISIBLE);binding.metkaCheckLayout.setVisibility(View.INVISIBLE);
            }
        });
        binding.buttonFindMetkaLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ////
                FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                FirebaseDatabase.getInstance().getReference().child("Metki").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot userSnapshot : snapshot.getChildren()){
                                            Log.i("sdfsdfsd",userSnapshot.child("image").toString());
                                            Log.i("sdfsdfsd1",qq1.image);
                                            if(userSnapshot.child("image").getValue().toString().equals(qq1.image)){
                                                Object qqq_lll=snapshot1.child("HasFind").getValue();
                                                if(qqq_lll==null) {
                                                    FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                            .child("HasFind").setValue(userSnapshot.getKey().toString());//устанавливаем метку
                                                }else{
                                                    FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                            .child("HasFind").setValue(qqq_lll.toString()+","+userSnapshot.getKey().toString());//устанавливаем метку
                                                }
                                                DownloadMetkis();
                                                break;
                                            }

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });////

                binding.metkaCheckLayout.setVisibility(View.INVISIBLE);
            }
        });
        mapObjectTapListener = new MapObjectTapListener() {
            @Override
            public boolean onMapObjectTap(@NonNull MapObject mapObject, @NonNull Point point) {
                //сделать
                double min_rast=1000000000;
                for(MetkaClass mtkcl:metkis){
                    double rast=Double.valueOf(Math.sqrt(Math.pow(Double.valueOf(mtkcl.first_coord) - Double.valueOf(point.getLatitude()),2)+Math.pow(Double.valueOf(mtkcl.second_coord) - Double.valueOf(point.getLongitude()),2)));
                    if(rast<=min_rast){
                        qq1=mtkcl;
                        min_rast=rast;
                    }
                }
                FirebaseDatabase.getInstance().getReference().child("Metki").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot userSnapshot : snapshot.getChildren()){
                            if(userSnapshot.child("image").getValue().toString().equals(qq1.image)){
                                FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                                Boolean yes_gal=false;
                                                Object obj11 = snapshot1.child("HasFind").getValue();
                                                Log.i(userSnapshot.getKey(),obj11.toString());
                                                if(obj11!=null){
                                                    if(Arrays.asList(obj11.toString().split(",")).contains(userSnapshot.getKey())){
                                                        yes_gal=true;
                                                    }
                                                }
                                                Log.i("sdgsdgsdgsd", "dsgsdgsdgdsgsdgdsdgd" + qq1.image);
                                                binding.textViewMetkaLayoutName.setText(qq1.name);
                                                binding.textViewMetkaLayoutOpis.setText(qq1.description);
                                                binding.textViewMetkaLayoutSlog.setText("Сложность - "+qq1.slognost);
                                                try {
                                                    if (!qq1.image.isEmpty()) {
                                                        //Glide.with(getContext()).load(qq1.image).into(binding.imageViewMetkaLayoutIm);
                                                    }
                                                }
                                                catch(Exception e){
                                                    Toast.makeText(getContext(), "Failed to get profile image link", Toast.LENGTH_SHORT).show();

                                                    binding.metkaCheckLayout.setVisibility(View.VISIBLE);
                                                    //mapObject==

                                                }
                                                Glide.with(getActivity()).load(qq1.image).into(binding.imageViewMetkaLayoutIm);
                                                binding.metkaCheckLayout.setVisibility(View.VISIBLE);
                                                if(yes_gal){
                                                    Log.i("HJKJHJKHJK","HERERERE");
                                                    binding.buttonFindMetkaLayout.setVisibility(View.INVISIBLE);
                                                }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                break;
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                return false;
            }};

        binding.mapview.getMapWindow().getMap().move(
                new CameraPosition(
                        new Point(55.751225, 37.629540),
                        /* zoom = */ 17.0f,
                        /* azimuth = */ 150.0f,
                        /* tilt = */ 30.0f
                ),new Animation(Animation.Type.SMOOTH, 15f), null
        );
        requestLocationPermission();

        MapKit mapKit = MapKitFactory.getInstance();
        mapKit.resetLocationManagerToDefault();
        userLocationLayer = mapKit.createUserLocationLayer(binding.mapview.getMapWindow());
        userLocationLayer.setVisible(true);
        userLocationLayer.setHeadingEnabled(true);

        userLocationLayer.setObjectListener(this);
        binding.buttonFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getActivity().getSharedPreferences("filterr", MODE_PRIVATE);
                Boolean one_=preferences.getBoolean("one",true);
                Boolean two_=preferences.getBoolean("two",true);
                Boolean three_=preferences.getBoolean("three",true);
                Boolean four_=preferences.getBoolean("four",true);
                Boolean five_=preferences.getBoolean("five",true);
                Boolean six_=preferences.getBoolean("six",true);
                Boolean seven_=preferences.getBoolean("seven",true);
                Boolean eight_=preferences.getBoolean("eight",true);
                Boolean nine_=preferences.getBoolean("nine",true);

                Boolean type_zagadka_=preferences.getBoolean("type_zagadka",true);
                Boolean type_defoult_=preferences.getBoolean("type_defoult",true);

                Boolean own_=preferences.getBoolean("own",true);
                Boolean no_own_=preferences.getBoolean("no_own",true);
                if(one_){
                    binding.checkBoxDif1.setChecked(true);
                }else{binding.checkBoxDif1.setChecked(false);}
                if(two_){
                    binding.checkBoxDif2.setChecked(true);
                }else{binding.checkBoxDif2.setChecked(false);}
                if(three_){
                    binding.checkBoxDif3.setChecked(true);
                }else{binding.checkBoxDif3.setChecked(false);}
                if(four_){
                    binding.checkBoxDif4.setChecked(true);
                }else{binding.checkBoxDif4.setChecked(false);}
                if(five_){
                    binding.checkBoxDif5.setChecked(true);
                }else{binding.checkBoxDif5.setChecked(false);}
                if(six_){
                    binding.checkBoxDif6.setChecked(true);
                }else{binding.checkBoxDif6.setChecked(false);}
                if(seven_){
                    binding.checkBoxDif7.setChecked(true);
                }else{binding.checkBoxDif7.setChecked(false);}
                if(eight_){
                    binding.checkBoxDif8.setChecked(true);
                }else{binding.checkBoxDif8.setChecked(false);}
                if(nine_){
                    binding.checkBoxDif9.setChecked(true);
                }else{binding.checkBoxDif9.setChecked(false);}
                if(own_){
                    binding.checkBoxOwn.setChecked(true);
                }else{binding.checkBoxOwn.setChecked(false);}
                if(no_own_){
                    binding.checkBoxNoOwn.setChecked(true);
                }else{binding.checkBoxNoOwn.setChecked(false);}
                if(type_zagadka_){
                    binding.checkBoxTypeZagadka.setChecked(true);
                }else{binding.checkBoxTypeZagadka.setChecked(false);}
                if(type_defoult_){
                    binding.checkBoxTypeDefoult.setChecked(true);
                }else{binding.checkBoxTypeDefoult.setChecked(false);}
                binding.filterrLayout.setVisibility(View.VISIBLE);


            }
        });
        binding.buttonFilterComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getActivity().getSharedPreferences("filterr", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("one", binding.checkBoxDif1.isChecked());
                editor.putBoolean("two", binding.checkBoxDif2.isChecked());
                editor.putBoolean("three", binding.checkBoxDif3.isChecked());
                editor.putBoolean("four", binding.checkBoxDif4.isChecked());
                editor.putBoolean("five", binding.checkBoxDif5.isChecked());
                editor.putBoolean("six", binding.checkBoxDif6.isChecked());
                editor.putBoolean("seven", binding.checkBoxDif7.isChecked());
                editor.putBoolean("eight", binding.checkBoxDif8.isChecked());
                editor.putBoolean("nine", binding.checkBoxDif9.isChecked());

                editor.putBoolean("type_zagadka", binding.checkBoxTypeZagadka.isChecked());
                editor.putBoolean("type_defoult", binding.checkBoxTypeDefoult.isChecked());

                editor.putBoolean("own", binding.checkBoxOwn.isChecked());
                editor.putBoolean("no_own", binding.checkBoxNoOwn.isChecked());

                editor.apply();
                //for( MapObject mp0:binding.mapview.getMap().getMapObjects().ge){}
                //binding.mapview.getMap().getMapObjects().remove;
                ;
                //удалить все обьекты
                for(PlacemarkMapObject mp0: mp){
                    binding.mapview.getMap().getMapObjects().remove(mp0);
                }
                mp.clear();
                metkis.clear();
                DownloadMetkis();
                binding.filterrLayout.setVisibility(View.INVISIBLE);
            }
        });
        binding.buttonFilterBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.filterrLayout.setVisibility(View.INVISIBLE);
            }
        });
        binding.button123.setOnClickListener(new View.OnClickListener()//OnBack
        {
            @Override
            public void onClick(View v)
            {
                binding.withoutLayout.setVisibility(View.INVISIBLE);
            }
        });
        binding.button3.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                binding.coordLayout.setVisibility(View.INVISIBLE);
            }
        });
        binding.button122.setOnClickListener(new View.OnClickListener()//OnComplete
        {
            @Override
            public void onClick(View v)
            {

                String name=binding.editTextText124.getText().toString();
                String opisanie=binding.editTextText125.getText().toString();
                Integer slognst=Integer.valueOf(binding.editTextNumber12.getText().toString());
                Boolean zagadka= binding.checkBox12.isChecked();
                if(slognst>9 || slognst<=0){
                    Toast.makeText(getContext(),"Напишите сложность от 1 до 9",Toast.LENGTH_SHORT).show();

                }else {
                    if(filePath.toString()!=null) {
                        if(filePath.toString().contains("firebasestorage")) {
                            HashMap<String, String> messageInfo = new HashMap<>();
                            messageInfo.put("name", name);
                            messageInfo.put("ownerId", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                            messageInfo.put("description", opisanie);
                            messageInfo.put("first_coord", String.valueOf(tap_point.getLatitude()));
                            messageInfo.put("second_coord", String.valueOf(tap_point.getLongitude()));
                            messageInfo.put("image", filePath.toString());
                            messageInfo.put("slognost", slognst.toString());
                            messageInfo.put("zagadka", zagadka.toString());

                            FirebaseDatabase.getInstance().getReference().child("Metki").push().setValue(messageInfo);
                            //uploadImage(String hhh);
                            binding.withoutLayout.setVisibility(View.INVISIBLE);
                            filePath = null;
                            DownloadMetkis();
                        }else{
                            Toast.makeText(getContext(),"Подождите пока фотография загрузится в БД",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getContext(),"Please Add Photo"+filePath.toString(),Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        binding.button2.setOnClickListener(new View.OnClickListener()//OnComplete
        {
            @Override
            public void onClick(View v)
            {

                String name=binding.editTextText4.getText().toString();
                String opisanie=binding.editTextText5.getText().toString();
                Integer slognst=Integer.valueOf(binding.editTextNumber.getText().toString());
                Double first=Double.valueOf(binding.editTextText6.getText().toString());
                Double second=Double.valueOf(binding.editTextText7.getText().toString());
                Boolean zagadka= binding.checkBox222.isChecked();

                if(slognst>9 || slognst<=0){
                    Toast.makeText(getContext(),"Напишите сложность от 1 до 9",Toast.LENGTH_SHORT).show();

                }else if((68.951833<=first && first<=68.955104 && 33.05737<=second && second<=33.067317) || (68.962525<=first && first<=68.96581 && 33.073041<=second && second<=33.076676)){
                    if(filePath.toString()!=null){
                        if(filePath.toString().contains("firebasestorage")) {
                            HashMap<String, String> messageInfo = new HashMap<>();
                            messageInfo.put("name", name);
                            messageInfo.put("ownerId", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                            messageInfo.put("description", opisanie);
                            messageInfo.put("first_coord", first.toString());
                            messageInfo.put("second_coord", second.toString());
                            messageInfo.put("image", filePath.toString());
                            messageInfo.put("slognost", slognst.toString());
                            messageInfo.put("zagadka", zagadka.toString());


                            FirebaseDatabase.getInstance().getReference().child("Metki").push().setValue(messageInfo);
                            binding.coordLayout.setVisibility(View.INVISIBLE);
                            filePath = null;
                            DownloadMetkis();
                        }else{
                            Toast.makeText(getContext(),"Подождите пока фотография загрузится в БД",Toast.LENGTH_SHORT).show();
                        }

                    } else{
                        Toast.makeText(getContext(),"Please Add Photo",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getContext(),"Поставьте метку в пределах Университета",Toast.LENGTH_SHORT).show();
                }

            }
        });
        /*InputListener inputListener=  new InputListener() {
            @Override
            public void onMapTap(@NonNull Map map, @NonNull Point point) {

            }

            @Override
            public void onMapLongTap(@NonNull Map map, @NonNull Point point) {
                Log.i("3243",String.valueOf(point.getLatitude()));
                binding.withoutLayout.setVisibility(View.VISIBLE);
            }
        };
        binding.mapview.getMapWindow().getMap().addInputListener(inputListener);*/
        inputListener = new InputListener() {
            @Override
            public void onMapTap(@NonNull Map map, @NonNull Point point) {

            }

            @Override
            public void onMapLongTap(@NonNull Map map, @NonNull Point point) {
                Log.i("3243",String.valueOf(point.getLatitude()));
                Double first=point.getLatitude();
                Double second=point.getLongitude();
                if((68.951833<=first && first<=68.955104 && 33.05737<=second && second<=33.067317) || (68.962525<=first && first<=68.96581 && 33.073041<=second && second<=33.076676)){
                    tap_point=point;
                    binding.withoutLayout.setVisibility(View.VISIBLE);
                    ReadyLoadPhoto=false;

                }else{
                    Toast.makeText(getContext(),"Поставьте метку в пределах Университета",Toast.LENGTH_SHORT).show();
                }
            }
        };

        WeakReference<InputListener> weakInputListener = new WeakReference<>(inputListener);
        binding.mapview.getMapWindow().getMap().addInputListener(weakInputListener.get());
        binding.profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        binding.profileImageView12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage1();
            }
        });
        return binding.getRoot();
    }
    private void selectImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        Log.i("hereee","hereee1");
        pickImageActivityResultLauncher.launch(intent);
    }
    private void selectImage1(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        Log.i("hereee","hereee1");
        pickImageActivityResultLauncher1.launch(intent);
    }
    private static String generateMetkaId(String userId1){
        String sumUser1User2 = userId1;
        char[] charArray = sumUser1User2.toCharArray();
        Arrays.sort(charArray);

        return new String(charArray);
    }
    ActivityResultLauncher<Intent> pickImageActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode()== Activity.RESULT_OK && result.getData()!=null && result.getData().getData()!=null){
                        filePath = result.getData().getData();
                        Log.i("hereee","hereee2"+filePath.toString());

                        try{
                            Bitmap bitmap = MediaStore.Images.Media
                                    .getBitmap(
                                            requireContext().getContentResolver(),
                                            filePath
                                    );
                            binding.profileImageView.setImageBitmap(bitmap);
                            Log.i("hereee","hereee3");
                        }catch(IOException e){
                            e.printStackTrace();
                            Log.i("hereee","hereee");
                        }
                        ReadyLoadPhoto=true;
                        uploadImage();
                    }
                }
            }
    );
    ActivityResultLauncher<Intent> pickImageActivityResultLauncher1 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode()== Activity.RESULT_OK && result.getData()!=null && result.getData().getData()!=null){
                        filePath = result.getData().getData();
                        Log.i("hereee","hereee2"+filePath.toString());

                        try{
                            Bitmap bitmap = MediaStore.Images.Media
                                    .getBitmap(
                                            requireContext().getContentResolver(),
                                            filePath
                                    );
                            binding.profileImageView12.setImageBitmap(bitmap);
                            Log.i("hereee","hereee3");
                        }catch(IOException e){
                            e.printStackTrace();
                            Log.i("hereee","hereee");
                        }
                        ReadyLoadPhoto=true;
                        uploadImage();
                    }
                }
            }
    );
    private void uploadImage(){
        if (filePath!=null){
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Log.i("hereee","hereee5");

            FirebaseStorage.getInstance().getReference().child("metki/"+uid)
                    .putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.i("hereee","hereee7");
                            //Toast.makeText(getContext(), "Photo upload complete", Toast.LENGTH_SHORT).show();

                            FirebaseStorage.getInstance().getReference().child("metki/"+uid).getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Log.i("hereee21",uri.toString());
                                            filePath=uri;
                                            /*FirebaseDatabase.getInstance().getReference().child("Metki").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                    .child("image").child().setValue(uri.toString());*/
                                        }
                                    });
                        }
                    });
        }
    }
    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission( getContext(),
                "android.permission.ACCESS_FINE_LOCATION")
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{"android.permission.ACCESS_FINE_LOCATION"},
                    PERMISSIONS_REQUEST_FINE_LOCATION);
        }
    }
    @Override
    public void onObjectAdded(UserLocationView userLocationView) {
        /*userLocationLayer.setAnchor(
                new PointF((float)(binding.mapview.getWidth() * 0.5), (float)(binding.mapview.getHeight() * 0.5)),
                new PointF((float)(binding.mapview.getWidth() * 0.5), (float)(binding.mapview.getHeight() * 0.83)));*/

        userLocationView.getArrow().setIcon(ImageProvider.fromResource(
                getContext(), R.drawable.email_icon));

       //CompositeIcon pinIcon = userLocationView.getPin().useCompositeIcon();

        /*pinIcon.setIcon(
                "icon",
                ImageProvider.fromResource(getContext(), R.drawable.email_icon),
                new IconStyle().setAnchor(new PointF(0f, 0f))
                        .setRotationType(RotationType.ROTATE)
                        .setZIndex(0f)
                        .setScale(1f)
        );

        pinIcon.setIcon(
                "pin",
                ImageProvider.fromResource(getContext(), R.drawable.send_icon),
                new IconStyle().setAnchor(new PointF(0.5f, 0.5f))
                        .setRotationType(RotationType.ROTATE)
                        .setZIndex(1f)
                        .setScale(0.5f)
        );*/

        userLocationView.getAccuracyCircle().setFillColor(Color.BLUE & 0x99ffffff);
    }
    @Override
    public void onObjectRemoved(UserLocationView view) {
    }

    @Override
    public void onObjectUpdated(UserLocationView view, ObjectEvent event) {
    }

    @Override
    public void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        binding.mapview.onStart();
        DownloadMetkis();

    }

    @Override
    public void onStop() {
        MapKitFactory.getInstance().onStop();
        binding.mapview.onStop();
        super.onStop();
    }
    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
    public void DownloadMetkis(){
        //ArrayList<MetkaClass> metkis = new ArrayList<MetkaClass>();
        FirebaseDatabase.getInstance().getReference().child("Metki").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, String> key_metkis =
                        new HashMap<>();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {

                    //String uid = userSnapshot.getKey();
                    String description=userSnapshot.child("description").getValue().toString();
                    String first_coord=userSnapshot.child("first_coord").getValue().toString();
                    String image=userSnapshot.child("image").getValue().toString();
                    String name=userSnapshot.child("name").getValue().toString();
                    String owner_id=userSnapshot.child("ownerId").getValue().toString();
                    String second_coord=userSnapshot.child("second_coord").getValue().toString();
                    String slognost=userSnapshot.child("slognost").getValue().toString();
                    String zagadka=userSnapshot.child("zagadka").getValue().toString();

                    metkis.add(new MetkaClass(description, first_coord,image,name,owner_id,second_coord,slognost,zagadka));
                    key_metkis.put(image, userSnapshot.getKey());

                }
                /*SharedPreferences preferences = getActivity().getSharedPreferences("filterr", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("one", binding.checkBoxDif1.isChecked());
                editor.putBoolean("two", binding.checkBoxDif2.isChecked());
                editor.putBoolean("three", binding.checkBoxDif3.isChecked());
                editor.putBoolean("four", binding.checkBoxDif4.isChecked());
                editor.putBoolean("five", binding.checkBoxDif5.isChecked());
                editor.putBoolean("six", binding.checkBoxDif6.isChecked());
                editor.putBoolean("seven", binding.checkBoxDif7.isChecked());
                editor.putBoolean("eight", binding.checkBoxDif8.isChecked());
                editor.putBoolean("nine", binding.checkBoxDif9.isChecked());

                editor.putBoolean("type_zagadka", binding.checkBoxTypeZagadka.isChecked());
                editor.putBoolean("type_defoult", binding.checkBoxTypeDefoult.isChecked());

                editor.putBoolean("own", binding.checkBoxOwn.isChecked());
                editor.putBoolean("no_own", binding.checkBoxNoOwn.isChecked());*/
                SharedPreferences preferences = getActivity().getSharedPreferences("filterr", MODE_PRIVATE);
                Boolean one_=preferences.getBoolean("one",true);
                Boolean two_=preferences.getBoolean("two",true);
                Boolean three_=preferences.getBoolean("three",true);
                Boolean four_=preferences.getBoolean("four",true);
                Boolean five_=preferences.getBoolean("five",true);
                Boolean six_=preferences.getBoolean("six",true);
                Boolean seven_=preferences.getBoolean("seven",true);
                Boolean eight_=preferences.getBoolean("eight",true);
                Boolean nine_=preferences.getBoolean("nine",true);

                Boolean type_zagadka_=preferences.getBoolean("type_zagadka",true);
                Boolean type_defoult_=preferences.getBoolean("type_defoult",true);

                Boolean own_=preferences.getBoolean("own",true);
                Boolean no_own_=preferences.getBoolean("no_own",true);
                FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Object current_find_metkis_obj=snapshot.child("HasFind").getValue();
                        String[] current_find_metkis;
                        if(current_find_metkis_obj==null){
                            current_find_metkis=new String[] {""};
                        }
                        else{
                            current_find_metkis=current_find_metkis_obj.toString().split(",");
                        }
                        for (MetkaClass qq: metkis) {
                            if ((type_zagadka_ && qq.zagadka.equals("true")) || (type_defoult_ && qq.zagadka.equals("false"))) {
                                if ((own_ && qq.owner_id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                        || (no_own_ && !qq.owner_id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))) {
                                    Log.i("ehwssd", "edsgsdgds");
                                    int slog = Integer.valueOf(qq.slognost);
                                    if((slog==1 && one_) || (slog==2 && two_) || (slog==3 && three_) || (slog==4 && four_)
                                            || (slog==5 && five_) || (slog==6 && six_) || (slog==7 && seven_) || (slog==8 && eight_) || (slog==9 && nine_) ){
                                        PlacemarkMapObject pl111;
                                        Log.i(key_metkis.get(qq.image),Arrays.toString(current_find_metkis));
                                        if (qq.zagadka.equals("true")) {
                                            if (slog <= 3) {
                                                if(Arrays.asList(current_find_metkis).contains(key_metkis.get(qq.image))){
                                                    pl111 = binding.mapview.getMapWindow().getMap().getMapObjects().addPlacemark(new Point(Double.valueOf(qq.first_coord), Double.valueOf(qq.second_coord)), ImageProvider.fromBitmap(getBitmapFromVectorDrawable(getContext(), R.drawable.baseline_check_24)));
                                                }else {
                                                    pl111 = binding.mapview.getMapWindow().getMap().getMapObjects().addPlacemark(new Point(Double.valueOf(qq.first_coord), Double.valueOf(qq.second_coord)), ImageProvider.fromBitmap(getBitmapFromVectorDrawable(getContext(), R.drawable.quest_vec_green)));
                                                }//pl111
                                                //mp.add(binding.mapview.getMapWindow().getMap().getMapObjects().addPlacemark(new Point(Double.valueOf(qq.first_coord), Double.valueOf(qq.second_coord)), ImageProvider.fromBitmap(getBitmapFromVectorDrawable(getContext(), R.drawable.quest_vec_green))));
                                                //binding.mapview.getMapWindow().getMap().getMapObjects().addTapListener(weakInputListener1.get());
                                            } else if (slog <= 7) {
                                                if(Arrays.asList(current_find_metkis).contains(key_metkis.get(qq.image))){
                                                    pl111 = binding.mapview.getMapWindow().getMap().getMapObjects().addPlacemark(new Point(Double.valueOf(qq.first_coord), Double.valueOf(qq.second_coord)), ImageProvider.fromBitmap(getBitmapFromVectorDrawable(getContext(), R.drawable.baseline_check_24)));
                                                }else {
                                                pl111 = binding.mapview.getMapWindow().getMap().getMapObjects().addPlacemark(new Point(Double.valueOf(qq.first_coord), Double.valueOf(qq.second_coord)), ImageProvider.fromBitmap(getBitmapFromVectorDrawable(getContext(), R.drawable.quest_vec_orange)));
                                                }
                                            } else {
                                                if(Arrays.asList(current_find_metkis).contains(key_metkis.get(qq.image))){
                                                    pl111 = binding.mapview.getMapWindow().getMap().getMapObjects().addPlacemark(new Point(Double.valueOf(qq.first_coord), Double.valueOf(qq.second_coord)), ImageProvider.fromBitmap(getBitmapFromVectorDrawable(getContext(), R.drawable.baseline_check_24)));
                                                }else {
                                                pl111 = binding.mapview.getMapWindow().getMap().getMapObjects().addPlacemark(new Point(Double.valueOf(qq.first_coord), Double.valueOf(qq.second_coord)), ImageProvider.fromBitmap(getBitmapFromVectorDrawable(getContext(), R.drawable.quest_vec_red)));
                                                }
                                            }
                                        } else {
                                            if (slog <= 3) {
                                                if(Arrays.asList(current_find_metkis).contains(key_metkis.get(qq.image))){
                                                    pl111 = binding.mapview.getMapWindow().getMap().getMapObjects().addPlacemark(new Point(Double.valueOf(qq.first_coord), Double.valueOf(qq.second_coord)), ImageProvider.fromBitmap(getBitmapFromVectorDrawable(getContext(), R.drawable.baseline_check_24)));
                                                }else {
                                                pl111 = binding.mapview.getMapWindow().getMap().getMapObjects().addPlacemark(new Point(Double.valueOf(qq.first_coord), Double.valueOf(qq.second_coord)), ImageProvider.fromBitmap(getBitmapFromVectorDrawable(getContext(), R.drawable.metka_vec_green)));
                                                }
                                            } else if (slog <= 7) {
                                                if(Arrays.asList(current_find_metkis).contains(key_metkis.get(qq.image))){
                                                    pl111 = binding.mapview.getMapWindow().getMap().getMapObjects().addPlacemark(new Point(Double.valueOf(qq.first_coord), Double.valueOf(qq.second_coord)), ImageProvider.fromBitmap(getBitmapFromVectorDrawable(getContext(), R.drawable.baseline_check_24)));
                                                }else {
                                                pl111 = binding.mapview.getMapWindow().getMap().getMapObjects().addPlacemark(new Point(Double.valueOf(qq.first_coord), Double.valueOf(qq.second_coord)), ImageProvider.fromBitmap(getBitmapFromVectorDrawable(getContext(), R.drawable.metka_vec_orange)));
                                                }
                                            } else {
                                                if(Arrays.asList(current_find_metkis).contains(key_metkis.get(qq.image))){
                                                    pl111 = binding.mapview.getMapWindow().getMap().getMapObjects().addPlacemark(new Point(Double.valueOf(qq.first_coord), Double.valueOf(qq.second_coord)), ImageProvider.fromBitmap(getBitmapFromVectorDrawable(getContext(), R.drawable.baseline_check_24)));
                                                }else {
                                                pl111=binding.mapview.getMapWindow().getMap().getMapObjects().addPlacemark(new Point(Double.valueOf(qq.first_coord), Double.valueOf(qq.second_coord)), ImageProvider.fromBitmap(getBitmapFromVectorDrawable(getContext(), R.drawable.metka_vec_red)));
                                                }
                                            }

                                            /*MapObjectTapListener mapObjectTapListener = new MapObjectTapListener() {
                                                @Override
                                                public boolean onMapObjectTap(@NonNull MapObject mapObject, @NonNull Point point) {
                                                    Log.i("sdgsdgsdgsd","dsgsdgsdgdsgsdgdsdgd");
                                                    mapObject==
                                                    return false;
                                                }
                                            };
                                            WeakReference<MapObjectTapListener> weakInputListener1 = new WeakReference<>(mapObjectTapListener);
                                            binding.mapview.getMapWindow().getMap().getMapObjects().addTapListener(weakInputListener1.get());*/
                                        }
                                        //здесь
                                        //qq1=qq;here
                                        WeakReference<MapObjectTapListener> weakInputListener1 = new WeakReference<>(mapObjectTapListener);
                                        pl111.addTapListener(weakInputListener1.get());
                                        mp.add(pl111);
                                    }
                                    //binding.mapview.getMapWindow().getMap().getMapObjects().addPlacemark(new Point(Double.valueOf(qq.first_coord),Double.valueOf(qq.second_coord)));
                                    //binding.mapview.getMapWindow().getMap().getMapObjects().addCircle(new Circle(new Point(Double.valueOf(qq.first_coord),Double.valueOf(qq.second_coord)),1000f));
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}