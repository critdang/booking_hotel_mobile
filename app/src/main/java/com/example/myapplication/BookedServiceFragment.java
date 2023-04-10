package com.example.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myapplication.Adaptor.ServiceRequestedAdaptor;
import com.example.myapplication.Model.Profile;
import com.example.myapplication.Model.RequestedService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookedServiceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookedServiceFragment extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "requested service";

    // TODO: Rename and change types of parameters
    private Profile profile;
    public BookedServiceFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BookedServiceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BookedServiceFragment newInstance(Profile profile) {
        BookedServiceFragment fragment = new BookedServiceFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, profile);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            profile = (Profile) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_booked_service, container, false);
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_booked_service, null);

        ListView ls = view.findViewById(R.id.rq_service_list);
        TextView total_text = view.findViewById(R.id.total_price);
        Button returnBtn = view.findViewById(R.id.returnBtn);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference("/Service_request/"+profile.getCode());
        List<RequestedService> requests = new ArrayList<>();
        ServiceRequestedAdaptor adaptor = new ServiceRequestedAdaptor(getActivity(),R.layout.requested_item_adaptor, requests);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Get the list of comments from the snapshot
                int total = 0;
                for (DataSnapshot commentSnapshot : snapshot.getChildren()) {
                    RequestedService s = commentSnapshot.getValue(RequestedService.class);
                    requests.add(s);
                    total += s.getPrice();
                }
                total_text.setText("TOTAL: " + total + "$");
                // Update the RecyclerView adapter with the new comments
                adaptor.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        });
        ls.setAdapter(adaptor);
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        builder.setView(view);

        // Create the AlertDialog object and return it
        return builder.create();
    }
}