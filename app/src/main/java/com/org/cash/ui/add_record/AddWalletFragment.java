package com.org.cash.ui.add_record;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.org.cash.R;
import com.org.cash.database.MoneyDb;
import com.org.cash.databinding.FragmentAddTransactionBinding;
import com.org.cash.databinding.FragmentAddWalletBinding;
import com.org.cash.model.Wallet;

public class AddWalletFragment extends Fragment {
    private FragmentAddWalletBinding binding;
    private MoneyDb db;
    private Handler hnHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddWalletBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        binding.addWalletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(requireContext(), "Created", Toast.LENGTH_SHORT).show();
                onClickEvent();
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public boolean onClickEvent() {
        Context context = requireContext();
        String name = binding.editTextName.getText().toString();
        Integer amount = Integer.parseInt(String.valueOf(binding.editTextAmount.getText()));
        if ((amount != null && !amount.equals("")) || (name != null && !name.equals(""))) {
            db = MoneyDb.getDatabase(context);
            hnHandler = new Handler(Looper.getMainLooper());
            MoneyDb.databaseWriteExecutor.execute(() -> {
                Wallet wallet = new Wallet(name, amount);
                long newid = db.walletDao().insert(wallet);
                    hnHandler.post(() -> {
                        binding.editTextName.setText("");
                        binding.editTextAmount.setText("");
                });
            });
        }
        return true;
    }
}