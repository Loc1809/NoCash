package com.example.nocash.ui.profile;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.nocash.R;
import com.google.android.material.navigation.NavigationView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileMain#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileMain extends Fragment {
    NavController navController;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileMain.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileMain newInstance(String param1, String param2) {
        ProfileMain fragment = new ProfileMain();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ProfileMain() {
        // Required empty public constructor
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
        View view = inflater.inflate(R.layout.fragment_profile_main, container, false);

// Tìm NavController từ NavHostFragment
//        navController = Navigation.findNavController(view);
//
//        // Thiết lập sự kiện item click cho NavigationView
//        NavigationView navigationView = view.findViewById(R.id.profile_nav_view);
//        if (navigationView != null) {
//            navigationView.setNavigationItemSelectedListener(item -> {
//                handleNavigationItemSelected(item, navController);
//                return true;
//            });
//        }

        return view;
    }


    private void handleNavigationItemSelected(@NonNull MenuItem item, NavController navController) {
        int id = item.getItemId();

        if (id == R.id.profile_navigation_my_account) {
            navController.navigate(R.id.profile_navigation_my_account);
        } else if (id == R.id.profile_navigation_setting) {
            navController.navigate(R.id.profile_navigation_setting);
        } else if (id == R.id.profile_navigation_help_center) {
            navController.navigate(R.id.profile_navigation_help_center);
        } else if (id == R.id.profile_navigation_contact) {
            navController.navigate(R.id.profile_navigation_contact);
        } else if (id == R.id.fragment_profile_main) {
            navController.navigate(R.id.fragment_profile_main);
        }
    }
}