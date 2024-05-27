package com.org.cash.ui.budget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import com.org.cash.CustomToast;
import com.org.cash.R;
import com.org.cash.database.MoneyDb;
import com.org.cash.databinding.FragmentBudgetBinding;
import com.org.cash.databinding.FragmentHomeBinding;
import com.org.cash.model.BudgetAdapter;
import com.org.cash.model.StatisticsAdapter;
import com.org.cash.model.Transaction;
import com.org.cash.model.Wallet;
import com.org.cash.ui.add_record.AddTransactionFragment;
import com.org.cash.ui.add_record.AddWalletFragment;
import com.org.cash.ui.home.HomeViewModel;
import com.org.cash.ui.home.TransactionAdapter;
import com.org.cash.ui.home.WalletAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BudgetFragment extends Fragment {

    private MoneyDb db;
    private RecyclerView recyclerView;
    private List<Long> list = new ArrayList<>();
    private BudgetAdapter budgetAdapter;

    private FragmentBudgetBinding binding;
    private HomeViewModel homeViewModel;
    private TextView uName, monthTitle, yearTitle, balance;
    private ArrayList<Wallet> walletsList;
    private ArrayList<Transaction> transactionsList;
    private Handler hnHandler;
    private int currentMonth, currentYear;
    private final String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentBudgetBinding.inflate(getLayoutInflater());
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout using data binding
        binding = FragmentBudgetBinding.inflate(inflater, container, false);
        View view = inflater.inflate(R.layout.fragment_budget, container, false);
        recyclerView = view.findViewById(R.id.statitics_recycler_view);
        recyclerView.setAdapter(budgetAdapter);
        // Access views through the binding object
        monthTitle = binding.getRoot().findViewById(R.id.budget_month_text);
        yearTitle = binding.getRoot().findViewById(R.id.budget_year_text);
        balance = binding.getRoot().findViewById(R.id.balance_statitics);

        // Set up click listeners
        binding.budgetGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeMonthView(-1);
            }
        });
        binding.budgetGoPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeMonthView(1);
            }
        });
        prepareWalletData(binding.getRoot());
        prepareTransactionData(binding.getRoot());

        // Return the root view of the binding object
        return binding.getRoot();
    }

    public BudgetFragment() {
    }

//    @SuppressLint({"MissingInflatedId", "SuspiciousIndentation"})
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_budget, container, false);
//        recyclerView = view.findViewById(R.id.statitics_recycler_view);
//
//        db = MoneyDb.getDatabase(this.getContext());
//        list = db.transactionDao().getUniqueTransactionTimes();
//        budgetAdapter = new BudgetAdapter(this.getContext(), list);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
//        recyclerView.setLayoutManager(linearLayoutManager);
//        recyclerView.setAdapter(budgetAdapter);
//
//        return view;
//    }


    private void prepareWalletData(View rootView) {
        try {
            Context context = requireContext();
            db = MoneyDb.getDatabase(context);
            walletsList = new ArrayList<>();
            hnHandler = new Handler(Looper.getMainLooper());
            MoneyDb.databaseWriteExecutor.execute(() -> {
                walletsList = (ArrayList<Wallet>) db.walletDao().getWallets();
                Double amount = 0.0;
                for (Wallet w : walletsList) {
                    amount += w.getAmount();
                }
                balance.setText(amount.toString());

            });
        } catch (Exception ex) {
            Log.e("Get all wallet: ", ex.getMessage());
        }
    }

    private void prepareTransactionData(View rootView) {
        try {
            Context context = requireContext();
            db = MoneyDb.getDatabase(context);
            transactionsList = new ArrayList<>();
            hnHandler = new Handler(Looper.getMainLooper());

            java.util.Date date = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            currentMonth = cal.get(Calendar.MONTH);
            currentYear = cal.get(Calendar.YEAR);
            monthTitle.setText(months[currentMonth]);
            yearTitle.setText(String.valueOf(currentYear));
            getAndShowTransaction(context, rootView, currentMonth, currentYear);
        } catch (Exception ex) {
            Log.e("Get all wallet: ", ex.getMessage());
        }
    }

    public void changeMonthView(int direction) {
        if (currentMonth == 0 && direction == -1) {
            currentYear += direction;
            currentMonth = 11;
        } else if (currentMonth == 11 && direction == 1) {
            currentYear += direction;
            currentMonth = 0;
        } else
            currentMonth += direction;
        getAndShowTransaction(requireContext(), binding.getRoot(), currentMonth, currentYear);
        monthTitle.setText(months[currentMonth]);
        yearTitle.setText(String.valueOf(currentYear));
    }


    public Long[] getStartEndOfMonth(int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month + 1);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long startOfMonth = calendar.getTimeInMillis();
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        long endOfMonth = calendar.getTimeInMillis();
        return new Long[]{startOfMonth, endOfMonth};
    }

    public void getAndShowTransaction(Context context, View rootView, int month, int year) {
        Long[] timestamp = getStartEndOfMonth(month-1, year);
        transactionsList = (ArrayList<Transaction>) db.transactionDao().getTransactionsByMonth(timestamp[0], timestamp[1]);
        recyclerView.setAdapter(null);
        list.clear();
        for (Transaction e : transactionsList
        ) {
            if (!list.contains(e.getTime())) {
                list.add(e.getTime());
            }

            budgetAdapter = new BudgetAdapter(context, list);

            recyclerView = rootView.findViewById(R.id.statitics_recycler_view);
            recyclerView.setAdapter(budgetAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        }
        ;
    }
}
