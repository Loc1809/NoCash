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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.google.android.material.snackbar.Snackbar;
import com.org.cash.CustomToast;
import com.org.cash.R;
import com.org.cash.database.MoneyDb;
import com.org.cash.databinding.FragmentAddTransactionBinding;
import com.org.cash.databinding.FragmentAddWalletBinding;
import com.org.cash.model.Wallet;
import com.org.cash.utils.Common;

public class AddWalletFragment extends Fragment {
    private FragmentAddWalletBinding binding;
    private MoneyDb db;
    private Handler hnHandler;
    private int walletId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddWalletBinding.inflate(inflater, container, false);
        try {
            if (requireArguments().getString("name") != null) {
                walletId = requireArguments().getInt("id");
                binding.editTextAmount.setText(String.valueOf(requireArguments().getDouble("amount")));
                binding.editTextName.setText(String.valueOf(requireArguments().getString("name")));

                binding.walletCancelButton.setVisibility(View.VISIBLE);
                binding.walletOptionButton.setText("Delete");

                binding.walletCancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
                        navController.navigate(R.id.navigation_home);
                    }
                });
                binding.walletOptionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Snackbar mySnackbar = Snackbar.make(binding.parentLayout, "Are you sure delete this wallet?", Snackbar.LENGTH_SHORT);
                        mySnackbar.setAction("Confirm", o -> {
                            MoneyDb.databaseWriteExecutor.execute(() -> {
                                db.walletDao().deleteById(requireArguments().getInt("id"));
                            });
                        });
                        mySnackbar.show();
                    }
                });
            }
        }
        catch (Exception e){
            System.out.println("Add wallet");
        }

        binding.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.hideSoftKeyboard(requireContext(), binding.getRoot());
            }
        });

        View view = binding.getRoot();
        binding.addWalletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomToast.makeText(requireContext(), "Created", Toast.LENGTH_SHORT).show();
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