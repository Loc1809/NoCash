package com.org.cash.ui.budget;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.org.cash.R;
import com.org.cash.databinding.FragmentSpendLimitBinding;
import com.org.cash.databinding.ItemRecycleTransBinding;
import com.org.cash.model.Limit;
import com.org.cash.model.LimitDisplay;
import com.org.cash.model.Transaction;
import com.org.cash.ui.home.TransactionAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class LimitAdapter extends RecyclerView.Adapter<LimitAdapter.MyViewHolder>{
    private ArrayList<LimitDisplay> arrayList;
    private Context context;
    FragmentSpendLimitBinding binding;
    private ClickListenner clickListenner;

    public LimitAdapter(Context context, ArrayList<LimitDisplay> arrayList, ClickListenner clickListenner) {
        this.arrayList = arrayList;
        this.context = context;
        this.clickListenner = clickListenner;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_spend_limit, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        LimitDisplay limit = arrayList.get(position);
        holder.icon.setImageResource(limit.getIcon());
        holder.title.setText(limit.getCategory());
        holder.amount.setText(String.valueOf(limit.getLimitAmount()));
        holder.detail.setText(String.valueOf(limit.getDetails()));
        holder.progress.setProgress(limit.getProgress());

        if (limit.getDirection() == 0){
            // 0 for income, 1 for outcome
            holder.amount.setTextColor(Color.GREEN);
        } else {
            holder.amount.setTextColor(Color.RED);
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title, amount, detail;
        ProgressBar progress;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            title = itemView.findViewById(R.id.title);
            amount = itemView.findViewById(R.id.amount);
            detail = itemView.findViewById(R.id.detail);
            progress = itemView.findViewById(R.id.progress);
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

    @Override
    public int getItemCount() {
        if (arrayList != null && !arrayList.isEmpty())
            return arrayList.size();
        else
            return 0;
    }


    public interface ClickListenner{
        void onItemClick(int position);
        void onItemLongClick(int position);
    }
}
