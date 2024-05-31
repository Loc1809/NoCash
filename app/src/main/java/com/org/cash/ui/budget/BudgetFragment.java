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
import com.org.cash.StatiticsCategoryDialogFragment;
import com.org.cash.database.MoneyDb;
import com.org.cash.databinding.FragmentBudgetBinding;
import com.org.cash.model.*;
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
    private LimitDisplay limitDisplayTemp;
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

    @SuppressLint("SetTextI18n")
    private void getAndShowTransaction(Context context, View rootView, int month, int year){
        displayList.clear();
        income = outcome = 0;
        Long[] timestamp = Common.getStartEndOfMonth(month, year);
        MoneyDb.databaseWriteExecutor.execute(() -> {
            limitList = (ArrayList<Limit>) db.limitDao().getLimitsByMonth(timestamp[0], timestamp[1]);
            hnHandler.post(()->{
                if (limitList.isEmpty())
                    CustomToast.makeText(requireContext(), "No transactions", CustomToast.LENGTH_SHORT, 1).show();
            });
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
                binding.txtIncome.setText(Common.formatCurrency(String.valueOf((long)income)) + " đ");
                binding.txtOutcome.setText(Common.formatCurrency(String.valueOf((long)outcome)) + " đ");
                limitAdapter = new LimitAdapter(context, displayList, new LimitAdapter.ClickListenner() {
                    @Override
                    public void onItemClick(int position) {
                        Limit item = limitList.get(position);
                        StatiticsCategoryDialogFragment dialogFragment = new StatiticsCategoryDialogFragment(context, currentMonth,
                                item.getCategory(), currentYear, item.getId(), (long) item.getAmount(), item.getDirection(), requireActivity());
                        dialogFragment.show(getFragmentManager(), "MyDialogFragment");
                        //showAlertDialog(context, "Thông báo", "Đây là nội dung của dialog.", "OK");
                    }

                    @Override
                    public void onItemLongClick(int position) {
                        Snackbar mySnackbar = Snackbar.make(binding.fragmentBudget, "Are you sure delete this limit?", Snackbar.LENGTH_SHORT);
                        mySnackbar.setAction("Confirm", o -> {
                            showUndoLimit(position, limitList.get(position));
                            MoneyDb.databaseWriteExecutor.execute(() -> {
                                db.limitDao().deleteById(limitList.get(position).getId());
                            });
                            limitDisplayTemp = displayList.get(position);
                            displayList.remove(position);
                            limitAdapter.notifyItemRemoved(position);
                        });
                        mySnackbar.show();
                    }
                });
                recyclerView = rootView.findViewById(R.id.budget_trans_list);
                recyclerView.setAdapter(limitAdapter);

                recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            });
        });
    }

    public void showUndoLimit(int position, Limit limit) {
        try {
            Snackbar mySnackbar = Snackbar.make(binding.parentTransLayout, "Click to undo limit.", Snackbar.LENGTH_SHORT);
            mySnackbar.setAction("Undo", v -> {
                MoneyDb.databaseWriteExecutor.execute(() -> {
                    db.limitDao().insert(limit);
                    hnHandler.post(() -> {
                        limitList.add(position, limit);
                        displayList.add(position, limitDisplayTemp);
                        limitAdapter.notifyItemInserted(position);
                    });
                });

            });
            mySnackbar.show();
        } catch (Exception ex) {
            Log.e("ShowUndoWallet", ex.getMessage());
        }
    }
}
