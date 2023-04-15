package com.example.myapplication.Adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.Model.Profile;
import com.example.myapplication.Model.RequestedService;
import com.example.myapplication.Model.Service;
import com.example.myapplication.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ServiceItemAdaptor extends ArrayAdapter<Service> {
    Context context;
    int resource;
    Profile profile;
    public ServiceItemAdaptor(@NonNull Context context, int resource, List<Service> dataArrayList) {
        super(context, resource, dataArrayList);
        this.context = context;
        this.resource = resource;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        Service item = getItem(position);
        String url = item.getImage();
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(resource, parent, false);
        }
        ImageView listImage = view.findViewById(R.id.listImage);
        TextView listName = view.findViewById(R.id.listName);
        TextView listTime = view.findViewById(R.id.listPrice);
        Button requestButton = view.findViewById(R.id.request_btn);
        loadImageFromURL(url, listImage);
        listName.setText(item.getName());
        listTime.setText(String.valueOf(item.getPrice())+"$");
        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestedService requestedService = new RequestedService(item.getName(),item.getPrice(),"pending");
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                if (profile.getCode() == null){
                    Toast.makeText(getContext(),"Please book rooms before call service",Toast.LENGTH_SHORT).show();
                    return;
                }
                String code = profile.getCode();
                DatabaseReference pathRef = database.getReference("/Service_request/"+code+"/"+(new Date()).getTime());
                pathRef.setValue(requestedService);
                Toast.makeText(getContext(),"Request successfully, check your requested list",Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    private void loadImageFromURL(String url, ImageView avatarImageView) {

        // Create RequestOptions object
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.ic_profile)
                .override(500, 500);

        // Load image using Glide
        Glide.with(context)
                .load(url)
                .apply(requestOptions)
                .into(avatarImageView);
    }
}
