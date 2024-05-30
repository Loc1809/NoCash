package com.org.cash;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.org.cash.database.MoneyDb;
import com.org.cash.model.Category;
import com.org.cash.model.StatiticsCategoryAdapter;
import com.org.cash.model.Transaction;
import com.org.cash.ui.add_record.AddTransactionFragment;
import com.org.cash.utils.Common;
import org.hibernate.hql.spi.id.inline.AbstractInlineIdsDeleteHandlerImpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StatiticsCategoryDialogFragment extends DialogFragment {

    private MoneyDb db;
    private TextView categoryTime;
    private TextView categoryName;
    private RecyclerView recyclerView;
    private ImageView statiticsCateIcon;
    private LinearLayout mainTarget;
    private StatiticsCategoryAdapter statiticsCategoryAdapter;
    private Context context;
    private long time, amount;
    private String category;
    private int year, id, direction;
    FragmentActivity activity;


    public StatiticsCategoryDialogFragment(Context context, long time, String category, int year, int id, long amount, int direction, FragmentActivity activity) {
        this.context = context;
        this.time = time;
        this.category = category;
        this.year = year;
        this.id = id;
        this.amount = amount;
        this.direction = direction;
        if (activity != null)
            this.activity = activity;
    }

    @SuppressLint("MissingInflatedId")
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Tạo một AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflate layout của Fragment
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_statitics_category, null);
        categoryTime = view.findViewById(R.id.statitics_category_time);
        categoryName = view.findViewById(R.id.statitics_category_name);
        statiticsCateIcon = view.findViewById(R.id.statitics_cate_icon);
        categoryName.setText(category);


        db = MoneyDb.getDatabase(context);
        List<Transaction> list = new ArrayList<>();
        if (time < 32){
            categoryName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AddTransactionFragment fragment = new AddTransactionFragment();
                    Bundle args = new Bundle();
                    args.putInt("id", id);
                    args.putDouble("amount", amount);
                    args.putString("category", category);
                    args.putString("date", (time + 1)+ "/" +year);
                    args.putInt("direction", direction);
                    args.putBoolean("limit", true);

//                    fragment.setArguments(args);
//                    requireActivity().getSupportFragmentManager().beginTransaction()
//                            .replace(R.id.fragment_budget, fragment)
//                            .addToBackStack(null) // You can add a tag here if needed
//                            .commit();
                }
            });
            Long[] timestamps = Common.getStartEndOfMonth((int) time, year);
            categoryTime.setText((time + 1)+ "-" +year);
            list = db.transactionDao().getTransactionsByMonthCategory(timestamps[0], timestamps[1], category);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            categoryTime.setText(sdf.format(new Date(time)).toString());
            list = db.transactionDao().getTransactionsByTimeAndCategory(time, category);
        }
        try{
            Category categoryOfItem = db.categoryDao().findByIdOrName(-1,category);
            statiticsCateIcon.setImageResource(categoryOfItem.getIcon());
        }
        catch (Exception ex){}

        statiticsCategoryAdapter = new StatiticsCategoryAdapter(context, list);

        this.recyclerView = view.findViewById(R.id.statitics_category_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(statiticsCategoryAdapter);
        // Thêm layout của Fragment vào AlertDialog
        builder.setView(view);

        return builder.create();
    }
}