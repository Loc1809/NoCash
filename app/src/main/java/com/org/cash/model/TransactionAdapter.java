package com.org.cash.model;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.org.cash.R;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.transactionViewHolder> {

    public List<Transaction> list;

    public TransactionAdapter(List<Transaction> list) {
        this.list = list;
    }

    @NonNull
    @NotNull
    @Override
    public transactionViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.transaction_item, viewGroup, false);
        return new transactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull TransactionAdapter.transactionViewHolder transactionViewHolder, int i) {
        Transaction item = list.get(i);
        if(item==null){
            return;
        }
        transactionViewHolder.categoryname.setText(item.getCategory());
        if(item.getAmount()<0){
            transactionViewHolder.transaction.setTextColor(Color.RED);
            transactionViewHolder.transaction.setText("-" + item.getAmount().toString() + " VND");
        }
        else if(item.getAmount() >0){
            transactionViewHolder.transaction.setTextColor(Color.parseColor("#008000"));
            transactionViewHolder.transaction.setText("+" + item.getAmount().toString() + " VND");
        }
        else
        transactionViewHolder.transaction.setText(item.getAmount().toString() + " VND");
    }

    @Override
    public int getItemCount() {
        if (list != null) return list.size();
        return 0;
    }

    class transactionViewHolder extends RecyclerView.ViewHolder {
        public TextView categoryname;
        public TextView transaction;

        public transactionViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            categoryname = itemView.findViewById(R.id.category_name);
            transaction = itemView.findViewById(R.id.transaction);
        }
    }

}
