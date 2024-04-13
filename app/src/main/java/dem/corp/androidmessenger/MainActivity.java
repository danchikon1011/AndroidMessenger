package dem.corp.androidmessenger;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.yandex.mapkit.MapKitFactory;

import java.util.HashMap;
import java.util.Map;

import dem.corp.androidmessenger.bottomnav.chats.ChatsFragment;
import dem.corp.androidmessenger.bottomnav.new_chat.NewChatFragment;
import dem.corp.androidmessenger.bottomnav.profile.ProfileFragment;
import dem.corp.androidmessenger.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        MapKitFactory.setApiKey("e3a26867-ca88-4822-96c2-87eeafa68da6");
        if (FirebaseAuth.getInstance().getCurrentUser()==null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
        getSupportFragmentManager().beginTransaction().replace(binding.fragmentContainer.getId(), new ChatsFragment()).commit();
        binding.bottomNav.setSelectedItemId(R.id.chats);

        Map<Integer, Fragment> fragmentMap = new HashMap<>();
        fragmentMap.put(R.id.chats, new ChatsFragment());
        fragmentMap.put(R.id.new_chat, new NewChatFragment());
        fragmentMap.put(R.id.profile, new ProfileFragment());
        fragmentMap.put(R.id.maps, new MapFragment());

        binding.bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment = fragmentMap.get(item.getItemId());
            getSupportFragmentManager().beginTransaction().replace(binding.fragmentContainer.getId(), fragment).commit();

            return true;
        });
    }
}