package com.org.cash;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.org.cash.database.MoneyDb;
import com.org.cash.model.Category;
import com.org.cash.model.StatiticsCategoryAdapter;
import com.org.cash.model.Transaction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class StatiticsCategoryDialogFragment extends DialogFragment {

    private MoneyDb db;
    private TextView categoryTime;
    private TextView categoryName;
    private RecyclerView recyclerView;
    private ImageView statiticsCateIcon;
    private StatiticsCategoryAdapter statiticsCategoryAdapter;
    private Context context;
    private long time;
    private String category;

    public StatiticsCategoryDialogFragment(Context context, long time, String category) {
        this.context = context;
        this.time = time;
        this.category = category;
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
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        categoryTime.setText(sdf.format(new Date(time)).toString());

        db = MoneyDb.getDatabase(context);
        List<Transaction> list = db.transactionDao().getTransactionsByTimeAndCategory(time, category);
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