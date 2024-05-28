package com.org.cash.ui.budget;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.org.cash.R;
import com.org.cash.databinding.FragmentBudgetBinding;
import com.org.cash.model.StatisticsAdapter;

import java.util.ArrayList;
import java.util.List;

public class BudgetFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<String> list= new ArrayList<>();
    private StatisticsAdapter statisticsAdapter;
    private FragmentBudgetBinding binding;
    public BudgetFragment() {
        // Required empty public constructor
    }


    public static BudgetFragment newInstance(String param1, String param2) {
        BudgetFragment fragment = new BudgetFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_budget, container, false);
        binding = com.org.cash.databinding.FragmentBudgetBinding.inflate(inflater, container, false);

        recyclerView = binding.recyclerView;
        list.add("1");
        list.add("2");
        list.add("4");
        list.add("5");
        list.add("1");
        list.add("2");
        list.add("4");
        list.add("5");
        list.add("1");
        list.add("2");
        list.add("4");
        list.add("5");
        statisticsAdapter = new StatisticsAdapter(list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(statisticsAdapter);

        return view;
    }
}
