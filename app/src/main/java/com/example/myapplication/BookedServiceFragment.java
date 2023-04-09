package com.example.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.myapplication.Adaptor.ServiceRequestedAdaptor;
import com.example.myapplication.Model.RequestedService;
import com.example.myapplication.Model.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookedServiceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookedServiceFragment extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BookedServiceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BookedServiceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BookedServiceFragment newInstance(String param1, String param2) {
        BookedServiceFragment fragment = new BookedServiceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
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
        int total = 0;

        List<RequestedService> sv = new ArrayList<>();
        sv.add(new RequestedService("Cut hair", 100, 2, "Pending"));
        sv.add(new RequestedService("Laundry", 200, 3, "Pending"));
        sv.add(new RequestedService("Wash car", 300, 1, "Pending"));
        sv.add(new RequestedService("Cut hair", 100, 2, "Pending"));
        sv.add(new RequestedService("Laundry", 200, 3, "Pending"));
        sv.add(new RequestedService("Wash car", 300, 1, "Pending"));
        ServiceRequestedAdaptor adaptor = new ServiceRequestedAdaptor(getActivity(),R.layout.requested_item_adaptor, sv);
        ls.setAdapter(adaptor);

        for (RequestedService s : sv) {
            total += s.getPrice() * s.getCount();
        }
        total_text.setText("TOTAL: " + total + "$");
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