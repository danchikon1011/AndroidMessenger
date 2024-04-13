package dem.corp.androidmessenger;

import static android.app.PendingIntent.getActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MyAdapterMetkaClass extends RecyclerView.Adapter<MyViewHolderClassMetka> {
    Context context;

    public MyAdapterMetkaClass(Context context, List<MetkaClass> metkiis) {
        this.context = context;
        this.metkiis = metkiis;
    }

    List<MetkaClass> metkiis;
    @NonNull
    @Override
    public MyViewHolderClassMetka onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolderClassMetka(LayoutInflater.from(context).inflate(R.layout.metka_exmpl,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderClassMetka holder, int position) {
        holder.nameView.setText(metkiis.get(position).name);
        FirebaseDatabase.getInstance().getReference().child("Users").child(metkiis.get(position).owner_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.ownerView.setText(snapshot.child("username").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //holder.ownerView.setText(metkiis.get(position).owner_id);
        Glide.with(holder.ownerView.getContext()).load(metkiis.get(position).image).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return metkiis.size();
    }
}
