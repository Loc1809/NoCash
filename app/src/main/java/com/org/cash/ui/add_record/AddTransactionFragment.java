package com.org.cash.ui.add_record;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.ViewModelProvider;
import com.org.cash.R;
import com.org.cash.database.MoneyDb;
import com.org.cash.databinding.FragmentAddRecordBinding;
import com.org.cash.databinding.FragmentAddTransactionBinding;
import com.org.cash.model.Transaction;
import com.org.cash.model.Wallet;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddTransactionFragment} factory method to
 * create an instance of this fragment.
 */
public class AddTransactionFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private FragmentAddTransactionBinding binding;
    private MoneyDb db;
    private Handler hnHandler;
    private Button button;
    private int mYear, mMonth, mDay, datetime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddTransactionBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        binding.addWalletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(requireContext(), "Created", Toast.LENGTH_SHORT).show();
                onClickEvent();
            }
        });

        binding.btnSelectCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                String text = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                                binding.editTextCal.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
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
        if (!validateInput())
            return false;
        Double amount = Double.valueOf(String.valueOf(binding.editTextAmount.getText()));
        String category = binding.editTextCategory.getText().toString();
        Long datetime = Long.valueOf(String.valueOf(binding.editTextCal.getText()));
        String wallet = String.valueOf(binding.editTextWallet.getText());
        String note = String.valueOf(binding.editTextNote.getText());

        Intent intent = new Intent();
        if ((amount != null && !amount.equals("")) || (category != null && !category.equals(""))) {
            db = MoneyDb.getDatabase(context);
            hnHandler = new Handler(Looper.getMainLooper());
            MoneyDb.databaseWriteExecutor.execute(() -> {
//                Wallet wallet = new Wallet(category, amount);
                Transaction transaction = new Transaction(amount, datetime.toString(), note, category, 1);
                long newid = db.transactionDao().insert(transaction);
                    hnHandler.post(() -> {
    //                update ui
                        binding.editTextAmount.setText("");
                        binding.editTextCategory.setText("");
                        binding.editTextWallet.setText("");
                        binding.editTextCal.setText("");
                        binding.editTextNote.setText("");
                });
            });
        }
        return true;
    }

    public boolean validateInput() {
        if (binding.editTextAmount.getText().length() == 0 || binding.editTextCategory.getText().toString().equals("0")) {
            binding.editTextAmount.setError("Missing this field");
            return false;
        }
        if (binding.editTextCategory.getText().length() == 0 || binding.editTextCategory.getText().toString().equals("0")) {
            binding.editTextCategory.setError("Missing this field");
            return false;
        }
        if (binding.editTextCal.getText().length() == 0 || binding.editTextCal.getText().toString().equals("0")) {
            binding.editTextCal.setError("Missing this field");
            return false;
        }
        if (binding.editTextWallet.getText().length() == 0 || binding.editTextWallet.getText().toString().equals("0")) {
            binding.editTextWallet.setError("Missing this field");
            return false;
        }
        return true;
    }
}