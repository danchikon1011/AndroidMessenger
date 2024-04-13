package dem.corp.androidmessenger;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import dem.corp.androidmessenger.R;

public class MyViewHolderClassMetka extends RecyclerView.ViewHolder {
    ImageView imageView;
    TextView nameView,ownerView;
    public MyViewHolderClassMetka(@NonNull View itemView) {
        super(itemView);
        imageView=itemView.findViewById(R.id.imageViewIm);
        nameView=itemView.findViewById(R.id.name_metkaaas);
        ownerView=itemView.findViewById(R.id.ownerr_metkaaas);
    }
}
