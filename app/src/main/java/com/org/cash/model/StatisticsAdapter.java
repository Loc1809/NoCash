package com.org.cash.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.org.cash.R;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StatisticsAdapter extends RecyclerView.Adapter<StatisticsAdapter.StatisticViewHolder> {

    public List<String> list;

    public StatisticsAdapter(List<String> list) {
        this.list = list;
    }

    @NonNull
    @NotNull
    @Override
    public StatisticViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.statistic_item, viewGroup, false);
        return new StatisticViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull StatisticsAdapter.StatisticViewHolder statisticViewHolder, int i) {
        String item = list.get(i);
        if(item==null){
            return;
        }
        statisticViewHolder.budgetName.setText(item);
        statisticViewHolder.spendBugdet.setText(item);
        statisticViewHolder.usingBudget.setText(item);
    }

    @Override
    public int getItemCount() {
        if (list != null) return list.size();
        return 0;
    }

    class StatisticViewHolder extends RecyclerView.ViewHolder {
        public TextView budgetName;
        public TextView spendBugdet;
        public TextView usingBudget;

        public StatisticViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            budgetName = itemView.findViewById(R.id.budget_name);
            spendBugdet = itemView.findViewById(R.id.budget_left);
            usingBudget = itemView.findViewById(R.id.budget_using);
        }
    }

}
