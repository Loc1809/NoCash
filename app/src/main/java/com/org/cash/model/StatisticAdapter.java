package com.org.cash.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.org.cash.R;
import com.org.cash.database.MoneyDb;
import com.org.cash.utils.Common;
import org.jetbrains.annotations.NotNull;

import javax.persistence.criteria.CriteriaBuilder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StatisticAdapter extends RecyclerView.Adapter<StatisticAdapter.BudgetViewHolder> {
    public List<Integer> list;
    private MoneyDb db;
    private Context context; // Add a context variable
    private int month, year, direction;

    FragmentManager fragmentManager;

    public StatisticAdapter(FragmentManager fragmentManager, Context context, List<Integer> list, int month, int year, int direction) {
        this.context = context;
        this.list = list;
        this.fragmentManager = fragmentManager;
        this.month = month;
        this.year = year;
        this.direction = direction;
    }

    @NonNull
    @NotNull
    @Override
    public StatisticAdapter.BudgetViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.statistic_outer_item, viewGroup, false);
        return new StatisticAdapter.BudgetViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull @NotNull StatisticAdapter.BudgetViewHolder budgetViewHolder, int i) {
        Integer item = list.get(i);
        if (item == null) {
            return;
        }
        double sum = 0;

        budgetViewHolder.time.setText(list.get(i) + "-" + ( month + 1 )+ "-" + year);

        db = MoneyDb.getDatabase(context);
        Long[] timestamps = Common.getStartEndOfDay(list.get(i), month, year);
        List<Transaction> rawList = db.transactionDao().getTransactionsByTime(timestamps[0], timestamps[1], direction);

        for (Transaction e : rawList
        ) {
            double amount = (e.direction() == 0) ?  e.getAmount() : -e.getAmount();
            sum += amount;
        }
        if (sum < 0) {
//            budgetViewHolder.sum.setTextColor(Color.RED);
            budgetViewHolder.sum.setText(sum + " VND");
        } else if (sum > 0) {
//            budgetViewHolder.sum.setTextColor(Color.parseColor("#008000"));
            budgetViewHolder.sum.setText("+" + sum + " VND");
        } else
            budgetViewHolder.sum.setText(sum + " VND");
        budgetViewHolder.transactionAdapter = new TransactionAdapter(fragmentManager,context,rawList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        budgetViewHolder.transaction.setLayoutManager(linearLayoutManager);
        budgetViewHolder.transaction.setAdapter(budgetViewHolder.transactionAdapter);
    }

    @Override
    public int getItemCount() {
        if (list != null) return list.size();
        return 0;
    }

    class BudgetViewHolder extends RecyclerView.ViewHolder {
        public TextView time;
        public TextView sum;

        public RecyclerView transaction;

        private List<Transaction> list = new ArrayList<>();
        private TransactionAdapter transactionAdapter;

        public BudgetViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.day_of_transaction);
            sum = itemView.findViewById(R.id.sum_of_transaction);
            transaction = itemView.findViewById(R.id.transaction_per_day);
        }
    }
}
