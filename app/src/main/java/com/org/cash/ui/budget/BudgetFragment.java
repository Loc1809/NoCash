package com.org.cash.ui.budget;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import com.org.cash.CustomToast;
import com.org.cash.R;
import com.org.cash.database.MoneyDb;
import com.org.cash.databinding.FragmentBudgetBinding;
import com.org.cash.model.*;
import com.org.cash.ui.add_record.AddTransactionFragment;
import com.org.cash.ui.home.TransactionAdapter;
import com.org.cash.utils.Common;
import com.org.cash.utils.MonthYearPickerDialog;
import io.reactivex.internal.schedulers.TrampolineScheduler;

import java.util.*;

public class BudgetFragment extends Fragment {
    private RecyclerView recyclerView;
    private LimitAdapter limitAdapter;
    private FragmentBudgetBinding binding;
    private ArrayList<Limit> limitList;
    private ArrayList<Transaction> transactionsList;
    private ArrayList<Category> categoryList;
    private ArrayList<LimitDisplay> displayList;
    public BudgetFragment() {}
    private MoneyDb db;
    private Handler hnHandler;
    private int currentMonth, currentYear;
    private double income = 0.0, outcome = 0.0;
    private TextView monthTitle;
    private final String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};


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
        binding = com.org.cash.databinding.FragmentBudgetBinding.inflate(inflater, container, false);
        binding.monthText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMonthYearPicker();
            }
        });

                binding.goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeMonthView(-1);
            }
        });
        binding.go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeMonthView(1);
            }
        });
        prepareTransactionData(binding.getRoot());
        return binding.getRoot();
    }


    public void changeMonthView(int direction){
        if (currentMonth == 0 && direction == -1) {
            currentYear += direction;
            currentMonth = 11;
        }
        else if(currentMonth == 11 && direction == 1){
            currentYear += direction;
            currentMonth = 0;
        } else
            currentMonth += direction;
        getAndShowTransaction(requireContext(), binding.getRoot(), currentMonth, currentYear);
        binding.monthText.setText(months[currentMonth] + " " + currentYear);
    }

    @SuppressLint("SetTextI18n")
    private void prepareTransactionData(View rootView) {
        try {
            Context context = requireContext();
            db = MoneyDb.getDatabase(context);
            transactionsList = new ArrayList<>();
            displayList = new ArrayList<>();
            categoryList = new ArrayList<>();
            hnHandler = new Handler(Looper.getMainLooper());

            Date date = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            currentMonth= cal.get(Calendar.MONTH);
            currentYear = cal.get(Calendar.YEAR);
            binding.monthText.setText(months[currentMonth] + " " + currentYear);
            getAndShowTransaction(context, rootView, currentMonth, currentYear);
        } catch (Exception ex) {
            Log.e("Get all wallet: ", ex.getMessage());
        }
    }

    private void showMonthYearPicker() {
        MonthYearPickerDialog dialog = MonthYearPickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String selectedDate = months[month] + " " + year;
                binding.monthText.setText(selectedDate);
                currentMonth = month;
                currentYear = year;
                getAndShowTransaction(requireContext(), binding.getRoot(), currentMonth, currentYear);
            }
        });
        dialog.show(getFragmentManager(), "MonthYearPickerDialog");
    }

    public void showUndoLimit(int position, Limit limit) {
        try {
            Snackbar mySnackbar = Snackbar.make(binding.parentTransLayout, "Click to undo limit.", Snackbar.LENGTH_SHORT);
            mySnackbar.setAction("Undo", v -> {
                MoneyDb.databaseWriteExecutor.execute(() -> {
                    db.limitDao().insert(limit);
                    hnHandler.post(() -> {
                        limitList.add(position, limit);
                        limitAdapter.notifyItemInserted(position);
                    });
                });
            });
            mySnackbar.show();
        } catch (Exception ex) {
            Log.e("ShowUndo", ex.getMessage());
        }
    }

    private void getAndShowTransaction(Context context, View rootView, int month, int year){
        displayList.clear();
        income = outcome = 0;
        Long[] timestamp = Common.getStartEndOfMonth(month, year);
        MoneyDb.databaseWriteExecutor.execute(() -> {
            limitList = (ArrayList<Limit>) db.limitDao().getLimitsByMonth(timestamp[0], timestamp[1]);
            for (Limit limit : limitList) {
                double sum = 0.0;
                try{
                    sum = db.transactionDao().getSumByMonthAndCate(timestamp[0], timestamp[1], limit.getCategory());
                } catch (Exception e){
                }
                if (limit.getDirection() == 0)
                    income += sum;
                else
                    outcome += sum;
                Category category = db.categoryDao().findByName(limit.getCategory());
                if (category == null) {
                    category = new Category(limit.getCategory(), limit.getDirection(), R.drawable.baseline_wallet_24);
                }
                displayList.add(new LimitDisplay(limit.getId(), limit.getAmount(), sum, "total " + sum,
                    category.getName(), category.getIcon(), limit.getDirection(),  (int) (100*(sum/limit.getAmount()))));
            }
            hnHandler.post(() -> {
                binding.txtIncome.setText(String.valueOf(income));
                binding.txtOutcome.setText(String.valueOf(outcome));
                limitAdapter = new LimitAdapter(context, displayList, new LimitAdapter.ClickListenner() {
                    @Override
                    public void onItemClick(int position) {

                    }

                    @Override
                    public void onItemLongClick(int position) {
//                        Snackbar mySnackbar = Snackbar.make(binding.parentTransLayout, "Are you sure delete this limit?", Snackbar.LENGTH_SHORT);
//                        mySnackbar.setAction("Confirm", v -> {
//                            MoneyDb.databaseWriteExecutor.execute(() -> {
//                                db.limitDao().delete(limitList.get(position));
//                                hnHandler.post(() -> {
//                                    Limit deletedLimit = limitList.remove(position);
//                                    limitAdapter.notifyItemRemoved(position);
//                                    showUndoLimit(position, deletedLimit);
//                                });
//                            });
//                        });
//                        mySnackbar.show();
                    }
                });
                recyclerView = rootView.findViewById(R.id.budget_trans_list);
                recyclerView.setAdapter(limitAdapter);

                recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            });
        });
    }
}
