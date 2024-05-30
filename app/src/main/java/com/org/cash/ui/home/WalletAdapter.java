package com.org.cash.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncDifferConfig;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.org.cash.R;
import com.org.cash.databinding.ItemRecycleWalletBinding;
import com.org.cash.model.Wallet;
import com.org.cash.utils.Common;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;


public class WalletAdapter extends RecyclerView.Adapter<WalletAdapter.MyViewHolder> {
    private ArrayList<Wallet> arrayList;
    private Context context;
    private ClickListenner clickListenner;

    public WalletAdapter(Context context, ArrayList<Wallet> arrayList, ClickListenner clickListenner) {
        this.arrayList = arrayList;
        this.context = context;
        this.clickListenner = clickListenner;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycle_wallet, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        Wallet wallet = arrayList.get(position);
        holder.title.setText(wallet.getName());
        holder.amount.setText(Common.formatCurrency(String.valueOf((int) wallet.getAmount())));
    }

    @Override
    public int getItemCount() {
        if (arrayList != null && arrayList.size() > 0)
            return arrayList.size();
        else
            return 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, amount;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.wallet_name);
            amount = itemView.findViewById(R.id.wallet_amount);
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
