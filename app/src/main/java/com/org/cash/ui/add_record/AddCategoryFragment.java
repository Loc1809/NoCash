package com.org.cash.ui.add_record;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.org.cash.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddCategoryFragment} factory method to
 * create an instance of this fragment.
 */
public class AddCategoryFragment extends Fragment {
    public AddCategoryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_category, container, false);
    }
}