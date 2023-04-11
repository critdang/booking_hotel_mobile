package com.example.myapplication.Adaptor;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.Model.Profile;
import com.example.myapplication.Model.RequestedService;
import com.example.myapplication.Model.Service;
import com.example.myapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

public class ServiceItemAdaptor extends ArrayAdapter<Service> {
    Context context;
    int layout;
    List<Service> contentList;
    Profile profile;
    public ServiceItemAdaptor(@NonNull Context context, int resource, @NonNull List<Service> objects) {
        super(context, resource, objects);
        this.context = context;
        contentList = objects;
        layout = resource;
    }

    @Override
    public int getCount() {
        return contentList.size();
    }
    public void setProfile(Profile profile) {
        this.profile = profile;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View cellView = convertView;
        if (cellView == null)
            cellView = LayoutInflater.from(getContext()).inflate(this.layout, parent, false);

        final Service content = contentList.get(position);
        String url = content.getImage();
        String name = (position+1) +". " + content.getName();
        String price = "Price: "+ content.getPrice()+ "$";
        TextView serviceName = cellView.findViewById(R.id.name_view);
        serviceName.setText(name);
        TextView servicePrice = cellView.findViewById(R.id.price_view);
        servicePrice.setText(price);
        ImageView serviceImage = cellView.findViewById(R.id.img_view);
//        serviceImage.setImageResource(R.drawable.buffet);
        loadImageFromURL(url, serviceImage);
        Button serviceButton = cellView.findViewById(R.id.request_btn);
        serviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestedService requestedService = new RequestedService(content.getName(),content.getPrice(),"pending");
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
        return cellView;
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