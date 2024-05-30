package com.org.cash.ui.add_record;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
    private Handler hnHandler = new Handler();
    private int walletId;
    private boolean suggestionAdded = false, isDeleting  = false;
    private Runnable runnable;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddWalletBinding.inflate(inflater, container, false);
        final String[] suggestions = new String[2];
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
                onClickEvent();
            }
        });
        runnable = new Runnable() {
            @Override
            public void run() {
                if (!suggestionAdded && !isDeleting) {
                    String inputText = binding.editTextAmount.getText().toString();
                    suggestions[0] = inputText + "000"; // Gợi ý thêm 3 số 0 vào cuối
                    suggestions[1] = "..."; // Gợi ý thêm 3 số 0 vào cuối
                    showSuggestions(suggestions);
                }
            }
        };

        binding.editTextAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                isDeleting = after < count || start == after;
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                hnHandler.removeCallbacks(runnable);
                suggestionAdded = false;
                binding.suggestionSpinner.setVisibility(View.GONE);
            }


            @Override
            public void afterTextChanged(Editable editable) {
                hnHandler.postDelayed(runnable, 1500);
            }
        });

        binding.suggestionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != AdapterView.INVALID_POSITION) {
                    String selectedItem = (String) parent.getItemAtPosition(position);
                    if (!suggestionAdded && selectedItem.equals(suggestions[0])) {
                        binding.editTextAmount.setText(selectedItem);
                        binding.editTextAmount.setSelection(selectedItem.length());
                        suggestionAdded = true;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return binding.getRoot();
    }

    private void showSuggestions(String[] suggestions) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, suggestions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.suggestionSpinner.setAdapter(adapter);
        binding.suggestionSpinner.setVisibility(View.VISIBLE);
        binding.suggestionSpinner.performClick();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public boolean onClickEvent() {
        Context context = requireContext();
        if (validateInput()) {
            String name = binding.editTextName.getText().toString();
            Integer amount = Integer.parseInt(String.valueOf(binding.editTextAmount.getText()));
            db = MoneyDb.getDatabase(context);
            hnHandler = new Handler(Looper.getMainLooper());
            MoneyDb.databaseWriteExecutor.execute(() -> {
                Wallet wallet = new Wallet(name, amount);
                if (walletId != 0)
                    wallet.setId(walletId);
                if (!db.walletDao().isAnySimilarWallet(wallet)){
                    db.walletDao().checkBeforeInsert(wallet, requireContext(), hnHandler);
                    hnHandler.post(() -> {
                        if (isAdded()) {
                            binding.editTextName.setText("");
                            binding.editTextAmount.setText("");
                        }
                    });
                } else {
                    hnHandler.post(() -> {
                        CustomToast.makeText(requireContext(), "Wallet existed", Toast.LENGTH_SHORT, 2).show();
                        binding.editTextName.setError("Wallet existed");
                    });
                }
            });
        }
        return true;
    }

    public boolean validateInput() {
        if (binding.editTextName.getText().length() == 0) {
            binding.editTextName.setError("Missing this field");
            return false;
        }
        if (binding.editTextAmount.getText().length() == 0) {
            binding.editTextAmount.setError("Missing this field");
            return false;
        }
        return true;
    }

}