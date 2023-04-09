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
import com.example.myapplication.Model.RequestedService;
import com.example.myapplication.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ServiceRequestedAdaptor extends ArrayAdapter<RequestedService> {
    Context context;
    int layout;
    List<RequestedService> contentList;

    public ServiceRequestedAdaptor(@NonNull Context context, int resource, @NonNull List<RequestedService> objects) {
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

        final RequestedService content = contentList.get(position);
        String name = (position+1) +". " + content.getName();
        String price = "Price: "+ content.getPrice()+ "$";
        String count = "Count: "+ content.getCount();
        String status = "Status: "+ content.getStatus();
        TextView serviceName = cellView.findViewById(R.id.requested_service_name_text);
        serviceName.setText(name);
        TextView servicePrice = cellView.findViewById(R.id.requested_service_price_text);
        servicePrice.setText(price);
        TextView serviceCount = cellView.findViewById(R.id.requested_service_count_text);
        serviceCount.setText(count);
        TextView serviceStatus = cellView.findViewById(R.id.requested_service_status_text);
        serviceStatus.setText(status);
        return cellView;
    }
}