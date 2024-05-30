package com.org.cash.model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import com.org.cash.R;
import com.org.cash.StatiticsCategoryDialogFragment;
import com.org.cash.database.MoneyDb;
import com.org.cash.utils.Common;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.transactionViewHolder> {

    private MoneyDb db;

    private Context context;

    public List<Transaction> list;
    FragmentManager fragmentManager;

    public TransactionAdapter(FragmentManager fragmentManager,Context context, List<Transaction> list) {
        this.context = context;
        this.list = list;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @NotNull
    @Override
    public transactionViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.transaction_by_date, viewGroup, false);
        return new transactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull TransactionAdapter.transactionViewHolder transactionViewHolder, int i) {
        Transaction item = list.get(i);
        if(item==null){
            return;
        }
        db = MoneyDb.getDatabase(context);



        try{
        Category category = db.categoryDao().findByIdOrName(-1,item.getCategory());
        transactionViewHolder.cateIcon.setImageResource(category.getIcon());}
        catch(Exception ex){}
        transactionViewHolder.categoryname.setText(item.getCategory());
        if(item.direction() == 1){
            transactionViewHolder.transaction.setTextColor(Color.RED);
            transactionViewHolder.transaction.setText("-" + Common.formatCurrency( String.valueOf(item.getAmount().longValue())) + " VND");
        }
        else {
            transactionViewHolder.transaction.setTextColor(Color.parseColor("#009688"));
            transactionViewHolder.transaction.setText("+" + Common.formatCurrency(String.valueOf(item.getAmount().longValue())) + " VND");
        }

        transactionViewHolder.cateContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long time = item.getTime();
                StatiticsCategoryDialogFragment dialogFragment = new StatiticsCategoryDialogFragment(context, time, item.getCategory(), -1, -1, -1, -1, null);
                dialogFragment.show(fragmentManager, "MyDialogFragment");
                //showAlertDialog(context, "Thông báo", "Đây là nội dung của dialog.", "OK");
            }
        });
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
        public View cateContainer;

        public transactionViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            cateIcon = itemView.findViewById(R.id.cateIcon);
            categoryname = itemView.findViewById(R.id.category_name);
            transaction = itemView.findViewById(R.id.transaction);
            cateContainer = itemView.findViewById(R.id.cate_container);

        }
    }


    private void showAlertDialog(Context context, String title, String message, String buttonText) {
        // Tạo một AlertDialog.Builder mới
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Thiết lập tiêu đề của dialog
        builder.setTitle(title);

        // Thiết lập nội dung của dialog
        builder.setMessage(message);

        // Thiết lập button và xử lý sự kiện khi button được nhấn
        builder.setPositiveButton(buttonText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Đóng dialog khi button được nhấn
                dialog.dismiss();
            }
        });

        // Tạo và hiển thị dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
