package com.org.cash.ui.home;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.org.cash.R;
import com.org.cash.database.MoneyDb;
import com.org.cash.databinding.ItemRecycleTransBinding;
import com.org.cash.model.Transaction;
import com.org.cash.utils.Common;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.MyViewHolder> {
    private ArrayList<Transaction> arrayList;
    private Context context;
    private ClickListenner clickListenner;
    private ItemRecycleTransBinding binding;

    public TransactionAdapter(Context context, ArrayList<Transaction> arrayList, ClickListenner clickListenner) {
        this.arrayList = arrayList;
        this.context = context;
        this.clickListenner = clickListenner;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycle_trans, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        Transaction transaction = arrayList.get(position);
        holder.title.setText(transaction.getDesc());
        holder.amount.setText(String.valueOf(Common.formatCurrency(String.valueOf(transaction.getAmount().longValue()))));
        MoneyDb db = MoneyDb.getDatabase(context);
        Integer icon = db.categoryDao().findIconByName(transaction.getCategory());
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(()->{
            try {
                holder.icon.setImageResource(icon);
            }catch (Exception e){
                holder.icon.setImageResource(R.drawable.baseline_wallet_24);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (arrayList != null && !arrayList.isEmpty())
            return arrayList.size();
        else
            return 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, amount;
        ImageView icon;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.wallet_image);
            title = itemView.findViewById(R.id.transaction_name);
            amount = itemView.findViewById(R.id.transaction_amount);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Code here
                        clickListenner.onItemClick(getAdapterPosition());
                    }
                });
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        clickListenner.onItemLongClick(getAdapterPosition());
                        return true;
                    }
                });
        }
    }


    public interface ClickListenner{
        void onItemClick(int position);
        void onItemLongClick(int position);
    }
}
