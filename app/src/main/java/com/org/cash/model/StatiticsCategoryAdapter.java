package com.org.cash.model;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.org.cash.R;
import com.org.cash.database.MoneyDb;
import com.org.cash.utils.Common;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class StatiticsCategoryAdapter extends RecyclerView.Adapter<StatiticsCategoryAdapter.transactionViewHolder> {

    private MoneyDb db;
    private Context context;

    public List<Transaction> list;
    public RecyclerView recyclerView;


    public StatiticsCategoryAdapter(Context context, List<Transaction> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @NotNull
    @Override
    public transactionViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.transaction_by_date, viewGroup, false);
        return new transactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull StatiticsCategoryAdapter.transactionViewHolder transactionViewHolder, int i) {
        Transaction item = list.get(i);
        if(item==null){
            return;
        }
        db = MoneyDb.getDatabase(context);
        try{
            Category category = db.categoryDao().findByIdOrName(-1,item.getCategory());
            transactionViewHolder.cateIcon.setImageResource(category.getIcon());}
        catch(Exception ignored){}
        transactionViewHolder.categoryname.setText(item.getDesc());
        if(item.getAmount()<0){
            transactionViewHolder.transaction.setTextColor(Color.RED);
            transactionViewHolder.transaction.setText("-" + Common.formatCurrency(String.valueOf(item.getAmount().longValue())) + " VND");
        }
        else if(item.getAmount() >0){
            transactionViewHolder.transaction.setTextColor(Color.parseColor("#009688"));
            transactionViewHolder.transaction.setText("+" + Common.formatCurrency(String.valueOf(item.getAmount().longValue())) + " VND");
        }
        else
            transactionViewHolder.transaction.setText(Common.formatCurrency(String.valueOf(item.getAmount().longValue())) + " VND");
    }

    @Override
    public int getItemCount() {
        if (list != null) return list.size();
        return 0;
    }

    class transactionViewHolder extends RecyclerView.ViewHolder {
        public TextView categoryname;

        public TextView transaction;
        public ImageView cateIcon;

        public transactionViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            cateIcon = itemView.findViewById(R.id.cateIcon);
            categoryname = itemView.findViewById(R.id.category_name);
            transaction = itemView.findViewById(R.id.transaction);

        }
    }


}
