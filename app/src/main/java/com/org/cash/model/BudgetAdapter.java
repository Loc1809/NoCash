package com.org.cash.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.org.cash.R;
import com.org.cash.database.MoneyDb;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder> {
    public List<Long> list;
    private MoneyDb db;
    private Context context; // Add a context variable

    public BudgetAdapter(Context context, List<Long> list) {
        this.context = context;
        this.list = list;
    }


    @NonNull
    @NotNull
    @Override
    public BudgetAdapter.BudgetViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.budget_item, viewGroup, false);
        return new BudgetAdapter.BudgetViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull @NotNull BudgetAdapter.BudgetViewHolder budgetViewHolder, int i) {
        Long item = list.get(i);
        if (item == null) {
            return;
        }
        double sum = 0;

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        budgetViewHolder.time.setText(sdf.format(new Date(item)).toString());

        db = MoneyDb.getDatabase(context);
        List<Transaction> rawList = db.transactionDao().getTransactionsByTime(item);

        for (Transaction e : rawList
        ) {
            if (budgetViewHolder.list == null) {
                budgetViewHolder.list.add(e);
            } else {
                boolean check = true;
                for (Transaction ee : budgetViewHolder.list
                ) {
                    if (ee.getCategory().equals(e.getCategory())) {
                        ee.setAmount(ee.getAmount() + e.getAmount());
                        check=false;
                        break;
                    }

                }
                if(check){
                    budgetViewHolder.list.add(e);
                }
            }
        }

        for (Transaction e : budgetViewHolder.list
        ) {
            sum += e.getAmount();
        }
        if (sum < 0) {
            budgetViewHolder.sum.setTextColor(Color.RED);
            budgetViewHolder.sum.setText("-" + sum + " VND");
        } else if (sum > 0) {
            budgetViewHolder.sum.setTextColor(Color.parseColor("#008000"));
            budgetViewHolder.sum.setText("+" + sum + " VND");
        } else
            budgetViewHolder.sum.setText(sum + " VND");
        budgetViewHolder.transactionAdapter = new TransactionAdapter(budgetViewHolder.list);
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
