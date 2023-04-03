package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.Model.Profile;
import com.example.myapplication.Utils.RequestInvoker;
import com.example.myapplication.Utils.VolleyCallback;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#getInstance(Profile)} factory method to
 * get an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM = "profile";
    private static final ProfileFragment profileFragmentInstance = new ProfileFragment();
    // TODO: Rename and change types of parameters
    private Profile profileParam;
    public ProfileFragment() {
        // Required empty public constructor
    }
    public static ProfileFragment getInstance(Profile param) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM, param);
        profileFragmentInstance.setArguments(args);
        return profileFragmentInstance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            profileParam = (Profile) getArguments().getSerializable(ARG_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Find views by their ids
        ImageView avatarImageView = view.findViewById(R.id.image_avatar);
        TextView fullNameTextView = view.findViewById(R.id.full_name_text_view);
        TextView addressTextView = view.findViewById(R.id.address_text_view);
        TextView genderTextView = view.findViewById(R.id.gender_text_view);
        TextView phoneTextView = view.findViewById(R.id.phone_text_view);
        Button logoutButton = view.findViewById(R.id.button_logout);

        loadImageFromURL(profileParam.getAvatar(), avatarImageView);
//                avatarImageView.setImageResource(R.drawable.ic_profile);
        fullNameTextView.setText(profileParam.getName());
        addressTextView.setText(profileParam.getAddress());
        genderTextView.setText(profileParam.getGender());
        phoneTextView.setText(profileParam.getPhone());
        // Set click listener for logout button
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//              Navigate to login screen
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    private void loadImageFromURL(String url, ImageView avatarImageView) {

        // Create RequestOptions object
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.ic_profile)
                .override(500, 500)
                .circleCrop();
        Transformation<Bitmap> circleTransformation = new CircleCrop();
        // Load image using Glide
        Glide.with(getActivity())
                .load(url)
                .apply(requestOptions)
                .transform(circleTransformation)
                .into(avatarImageView);
    }
}