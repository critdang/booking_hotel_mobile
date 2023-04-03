package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.List;

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
    private LinearLayout edit_profile_layout;
    private RadioGroup genderRadioGroup;
    private RadioButton maleRadioButton, femaleRadioButton, otherRadioButton;
    private Button editButton, saveButton, cancelButton, logoutButton;
    private TextView fullNameTextView, addressTextView, genderTextView, phoneTextView;
    private EditText fullNameEditText, addressEditText, phoneEditText;
    private ImageView avatarImageView;
    public ProfileFragment() {
        // Required empty public constructor
    }

    // get User data from the login activity
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
        View editView = inflater.inflate(R.layout.fragment_profile_edit,container,true);
        // Find views by their ids

        // view of fragment profile
        avatarImageView = view.findViewById(R.id.image_avatar);
        fullNameTextView = view.findViewById(R.id.full_name_text_view);
        addressTextView = view.findViewById(R.id.address_text_view);
        genderTextView = view.findViewById(R.id.gender_text_view);
        phoneTextView = view.findViewById(R.id.phone_text_view);
        logoutButton = view.findViewById(R.id.button_logout);
        editButton = view.findViewById(R.id.button_edit);

        //view of fragment profile edit
        fullNameEditText =editView.findViewById((R.id.name_edit_text));
        phoneEditText = editView.findViewById((R.id.phone_edit_text));
        addressEditText = editView.findViewById((R.id.address_edit_text));

        genderRadioGroup = editView.findViewById(R.id.gender_radio_group);
        maleRadioButton = editView.findViewById(R.id.button_male);
        femaleRadioButton = editView.findViewById(R.id.button_female);
        otherRadioButton = editView.findViewById(R.id.button_other);

        edit_profile_layout = editView.findViewById(R.id.edit_profile_layout);
        cancelButton = editView.findViewById(R.id.button_cancel);
        saveButton = editView.findViewById(R.id.button_save);


        //set value for profile
        fullNameTextView.setText(profileParam.getName());
        addressTextView.setText(profileParam.getAddress());
        genderTextView.setText(profileParam.getGender());
        phoneTextView.setText(profileParam.getPhone());
        RequestInvoker.loadImageFromURL(this.getContext(),profileParam.getAvatar(), avatarImageView);
//                avatarImageView.setImageResource(R.drawable.ic_profile);

        // Set click listener for logout button
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//              Navigate to login screen
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });
        // set initial visibility of edit layout
//        editView.setVisibility(View.GONE);

        // Set click listener for edit button
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // hide the view layout and show the edit layout
                view.setVisibility(View.GONE);
                edit_profile_layout.setVisibility(View.VISIBLE);


                // set edit layout fields to current values
                fullNameEditText.setText(fullNameTextView.getText());
                phoneEditText.setText(phoneTextView.getText());
                addressEditText.setText(addressTextView.getText());

                String gender = genderTextView.getText().toString();
                if (gender.equals("Male")) {
                    maleRadioButton.setChecked(true);
                } else if (gender.equals("Female")) {
                    femaleRadioButton.setChecked(true);
                } else {
                    otherRadioButton.setChecked(true);
                }
            }
        });

        // Set click listener for save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = genderRadioGroup.getCheckedRadioButtonId();
                RadioButton selectedRadioButton = genderRadioGroup.findViewById(selectedId);
                // object that save the change on user profile
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("fullName", fullNameEditText.getText());
                    jsonObject.put("phone", phoneEditText.getText());
                    jsonObject.put("address",addressEditText.getText());
                    jsonObject.put("gender",selectedRadioButton.getText());

                    // send update request to server
                    RequestInvoker.updateUser(getContext(), jsonObject, profileParam.getAccessToken(), new VolleyCallback<String>() {
                        @Override
                        public void onSuccess(String result) throws JSONException {
                            Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                            // save the edited values
                            fullNameTextView.setText(fullNameEditText.getText());
                            phoneTextView.setText(phoneEditText.getText());
                            addressTextView.setText(addressEditText.getText());
                            genderTextView.setText(selectedRadioButton.getText());

                            // show the view layout and hide the edit layout
                            view.setVisibility(View.VISIBLE);
                            edit_profile_layout.setVisibility(View.GONE);
                        }

                        @Override
                        public void onSuccess(List<String> result) throws JSONException {

                        }

                        @Override
                        public void onError(String result) {
                            Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }


            }
        });

        // Set click listener for cancel button
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show the view layout and hide the edit layout
                view.setVisibility(View.VISIBLE);
                edit_profile_layout.setVisibility(View.GONE);
            }
        });
        return view;
    }
}