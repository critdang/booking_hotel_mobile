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
import com.example.myapplication.Model.Service;
import com.example.myapplication.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ServiceItemAdaptor extends ArrayAdapter<Service> {
    Context context;
    int layout;
    List<Service> contentList;

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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View cellView = convertView;
        if (cellView == null)
            cellView = LayoutInflater.from(getContext()).inflate(this.layout, parent, false);

        final Service content = contentList.get(position);
//        String url = "https://res.cloudinary.com/dmr3ppomm/image/upload/v1656870349/user_avatar/cld-sample-4.jpg";
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
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("request");
                Log.i("Request", myRef.getKey());
                myRef.setValue(content.getName());
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