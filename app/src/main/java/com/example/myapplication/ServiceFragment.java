package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.airbnb.lottie.L;
import com.android.volley.Request;
import com.example.myapplication.Adaptor.ServiceItemAdaptor;
import com.example.myapplication.Model.Profile;
import com.example.myapplication.Model.Service;
import com.example.myapplication.Utils.RequestInvoker;
import com.example.myapplication.Utils.VolleyCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ServiceFragment#getInstance(Profile)} factory method to
 * get an instance of this fragment.
 */
public class ServiceFragment extends Fragment {


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "serviceList";
    private static final String ARG_PARAM2 = "profile";
    private static final ServiceFragment serviceFragmentInstance = new ServiceFragment();
    // TODO: Rename and change types of parameters
    private Profile mParam1;
    private List<Service> mParam2;

    public ServiceFragment() {
        // Required empty public constructor
    }

    public static ServiceFragment getInstance(Profile param) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM2, param);
        serviceFragmentInstance.setArguments(args);
        return serviceFragmentInstance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = (Profile) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView;
        ListView listView;
        SearchView searchView;
        Button sortButton, filterButton;
        AppCompatImageView listButton;
        rootView = inflater.inflate(R.layout.fragment_service, container, false);
        View editView = inflater.inflate(R.layout.fragment_profile_edit,container,true);
        LinearLayout edit_profile_layout = editView.findViewById(R.id.edit_profile_layout);
        listView = rootView.findViewById(R.id.service_list);

        List<Service> initialList = new ArrayList<>();
        List<Service> serviceList = new ArrayList<>();
        List<Service> filterList = new ArrayList<>();
        searchView = rootView.findViewById(R.id.search_view);
        sortButton = rootView.findViewById(R.id.button_sort);
        filterButton = rootView.findViewById(R.id.button_filter);
        listButton = rootView.findViewById(R.id.list_service_btn);
        RequestInvoker.renderService(getContext(), new VolleyCallback<Service>() {
            @Override
            public void onSuccess(Service result) throws JSONException {
            }
            @Override
            public void onSuccess(List<Service> result) throws JSONException {
                Service.getInstance(result);
                initialList.addAll(result);
                serviceList.addAll(result);
                filterList.addAll(result);
                ServiceItemAdaptor adaptor = new ServiceItemAdaptor(getActivity(), R.layout.service_item_adaptor, initialList);
                listView.setAdapter(adaptor);
            }
            @Override
            public void onError(String result) {
                Toast.makeText(getContext(), "Error Occured!", Toast.LENGTH_SHORT).show();
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String search = newText;
                serviceList.clear();
                for (Service service : Service.getInstance()) {
                    if (service.getName().toLowerCase().contains(search.toLowerCase().trim())) {
                        serviceList.add(service);
                    }
                }
                ServiceItemAdaptor adaptor = new ServiceItemAdaptor(getActivity(), R.layout.service_item_adaptor, serviceList);
                listView.setAdapter(adaptor);
                return false;
            }
        });
        // This is to make the search view expand without clicking on search icon
//        searchView.setIconifiedByDefault(false);
//        searchView.setQueryHint("Search here");
        searchView.setOnCloseListener(() -> {
            serviceList.addAll(initialList);
            ServiceItemAdaptor adaptor = new ServiceItemAdaptor(getActivity(), R.layout.service_item_adaptor, serviceList);
            listView.setAdapter(adaptor);
            return false;
        });

        listButton.setOnClickListener(v -> {
//            rootView.setVisibility(View.GONE);
//            edit_profile_layout.setVisibility(View.VISIBLE);
            FragmentManager fm = getParentFragmentManager();
            BookedServiceFragment dialog = new BookedServiceFragment();
            dialog.show(fm, "rate_us_dialog");
        });
        sortButton.setOnClickListener(v -> {
            PopupMenu menu = new PopupMenu(this.getContext(), v);
            menu.getMenuInflater().inflate(R.menu.sort_popup_menu, menu.getMenu());
            menu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.nane_increasing:
                        Service.nameIncreasingSort(serviceList);
                        ServiceItemAdaptor adaptor = new ServiceItemAdaptor(this.getActivity(), R.layout.service_item_adaptor, serviceList);
                        listView.setAdapter(adaptor);
                        return true;
                    case R.id.nane_decreasing:
                        Service.nameDecreasingSort(serviceList);
                        ServiceItemAdaptor adaptor1 = new ServiceItemAdaptor(this.getActivity(), R.layout.service_item_adaptor, serviceList);
                        listView.setAdapter(adaptor1);
                        return true;
                    case R.id.price_increasing:
                        Service.priceIncreasingSort(serviceList);
                        ServiceItemAdaptor adaptor2 = new ServiceItemAdaptor(this.getActivity(), R.layout.service_item_adaptor, serviceList);
                        listView.setAdapter(adaptor2);
                        return true;
                    case R.id.price_decreasing:
                        Service.priceDecreasingSort(serviceList);
                        ServiceItemAdaptor adaptor3 = new ServiceItemAdaptor(this.getActivity(), R.layout.service_item_adaptor, serviceList);
                        listView.setAdapter(adaptor3);
                        return true;
                }
                return false;
            });
            menu.show();
        });
        filterButton.setOnClickListener(v -> {
            PopupMenu menu = new PopupMenu(this.getContext(), v);
            menu.getMenuInflater().inflate(R.menu.filter_popup_menu, menu.getMenu());
            menu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.filter_clear:
                        ServiceItemAdaptor adaptor = new ServiceItemAdaptor(this.getActivity(), R.layout.service_item_adaptor, serviceList);
                        listView.setAdapter(adaptor);
                        return true;
                    case R.id.filter_price_1_100:
                        filterList.clear();
                        for (Service service : serviceList) {
                            if (service.getPrice() >= 1 && service.getPrice() <= 100) {
                                filterList.add(service);
                            }
                        }
                        ServiceItemAdaptor adaptor1 = new ServiceItemAdaptor(this.getActivity(), R.layout.service_item_adaptor, filterList);
                        listView.setAdapter(adaptor1);
                        return true;
                    case R.id.filter_price_101_500:
                        filterList.clear();
                        for (Service service : serviceList) {
                            if (service.getPrice() >= 101 && service.getPrice() <= 500) {
                                filterList.add(service);
                            }
                        }
                        ServiceItemAdaptor adaptor2 = new ServiceItemAdaptor(this.getActivity(), R.layout.service_item_adaptor, filterList);
                        listView.setAdapter(adaptor2);
                        return true;
                    case R.id.filter_price_501_1000:
                        filterList.clear();
                        for (Service service : serviceList) {
                            if (service.getPrice() >= 501 && service.getPrice() <= 1000) {
                                filterList.add(service);
                            }
                        }
                        ServiceItemAdaptor adaptor3 = new ServiceItemAdaptor(this.getActivity(), R.layout.service_item_adaptor, filterList);
                        listView.setAdapter(adaptor3);
                        return true;
                    case R.id.filter_price_1001_5000:
                        filterList.clear();
                        for (Service service : serviceList) {
                            if (service.getPrice() >= 1001) {
                                filterList.add(service);
                            }
                        }
                        ServiceItemAdaptor adaptor4 = new ServiceItemAdaptor(this.getActivity(), R.layout.service_item_adaptor, filterList);
                        listView.setAdapter(adaptor4);
                        return true;
                }
                return false;
            });
            menu.show();
        });
        return rootView;
    }
}