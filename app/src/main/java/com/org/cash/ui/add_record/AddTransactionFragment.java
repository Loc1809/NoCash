package com.org.cash.ui.add_record;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.room.RoomSQLiteQuery;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.org.cash.CustomToast;
import com.org.cash.R;
import com.org.cash.database.MoneyDb;
import com.org.cash.databinding.FragmentAddTransactionBinding;
import com.org.cash.model.Category;
import com.org.cash.model.Transaction;
import com.org.cash.model.Wallet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import com.org.cash.utils.Common;

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
    private int mYear, mMonth, mDay, datetime, transId = 0, direction;
    private GridLayout gridLayout;
    private BottomSheetDialog bottomSheetDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddTransactionBinding.inflate(inflater, container, false);

        bottomSheetDialog = new BottomSheetDialog(requireContext());
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);
        bottomSheetDialog.setContentView(sheetView);

        gridLayout = new GridLayout(requireContext());
        gridLayout = sheetView.findViewById(R.id.iconGrid);

        try {
            if (requireArguments().getString("category") != null) {
                transId = requireArguments().getInt("id");
                binding.editTextAmount.setText(String.valueOf(requireArguments().getDouble("amount")));
                binding.editTextCategory.setText(String.valueOf(requireArguments().getString("category")));
                binding.editTextCal.setText(String.valueOf(requireArguments().getString("date")));
                binding.editTextWallet.setText(String.valueOf(requireArguments().getString("wallet")));
                binding.editTextNote.setText(String.valueOf(requireArguments().getString("note")));

                binding.cancelButton.setVisibility(View.VISIBLE);
                binding.optionButton.setText("Delete");
                selectDirection(requireArguments().getInt("direction"));

                binding.cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
                        navController.navigate(R.id.navigation_home);
                    }
                });
                binding.optionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Snackbar mySnackbar = Snackbar.make(binding.parentLayout, "Are you sure delete this wallet?", Snackbar.LENGTH_SHORT);
                        mySnackbar.setAction("Confirm", o -> {
                            MoneyDb.databaseWriteExecutor.execute(() -> {
                                db.transactionDao().deleteById(requireArguments().getInt("id"));
                            });
                        });
                        mySnackbar.show();
                    }
                });
            }
        }
        catch (Exception e){
            System.out.println("Add transaction");
            selectDirection(0);
        }

        binding.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.hideSoftKeyboard(requireContext(), binding.getRoot());
            }
        });

        binding.inBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDirection(0);
            }
        });
        binding.outBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDirection(1);
            }
        });

        binding.addTransactionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomToast.makeText(requireContext(), "Created", Toast.LENGTH_SHORT, 1).show();
                onClickEvent();
            }
        });

        binding.optionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomToast.makeText(requireContext(), "Cleared", Toast.LENGTH_SHORT, 2).show();
                binding.editTextAmount.setText("");
                binding.editTextCategory.setText("");
                binding.editTextWallet.setText("");
                binding.editTextCal.setText("");
                binding.editTextNote.setText("");
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

        binding.editTextCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.arrowSelectCate.setImageResource(R.drawable.baseline_keyboard_arrow_down_24);
                addCateToBottomSheet(requireContext());
                showBottomSheet();
            }
        });

        binding.editTextWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.arrowSelectCate.setImageResource(R.drawable.baseline_keyboard_arrow_down_24);
                addWalletToBottomSheet(requireContext());
                showBottomSheet();
            }
        });

        bottomSheetDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                binding.arrowSelectCate.setImageResource(R.drawable.baseline_keyboard_arrow_right_24_purple);
                binding.arrowSelectWallet.setImageResource(R.drawable.baseline_keyboard_arrow_right_24_purple);
            }
        });

        return binding.getRoot();
    }

    private void addWalletToBottomSheet(Context context) {
        if (gridLayout != null) {
            clearBottomSheet();
            db = MoneyDb.getDatabase(context);

            List<Wallet> itemList = db.walletDao().getWallets();
            if (itemList.isEmpty())
                hideBottomSheet();
            int columnCount = 3;
            int rowCount = (itemList.size() + columnCount - 1) / columnCount;
            gridLayout.setRowCount(rowCount);
            int cRow = 0;
            int cCol = 0;
            for (Wallet item : itemList) {
                GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
                layoutParams.rowSpec = GridLayout.spec(cRow, 1, 1f);
                layoutParams.columnSpec = GridLayout.spec(cCol, 1, 1f);
                View itemView = LayoutInflater.from(requireContext()).inflate(R.layout.cate_record, gridLayout, false);
                itemView.setLayoutParams(layoutParams);

                ImageView iconImageView = itemView.findViewById(R.id.iconImageView);
                TextView nameTextView = itemView.findViewById(R.id.nameTextView);
                iconImageView.setImageResource(R.drawable.baseline_wallet_24);
                nameTextView.setText(item.getName());

                itemView.setOnClickListener(view -> {
                    hideBottomSheet();
                    binding.editTextWallet.setText(item.getName());
                });

                int[] newVal = plus(cCol, cRow, columnCount);
                cRow = newVal[1];
                cCol = newVal[0];
                gridLayout.addView(itemView);
            }
        }
    }

    private void addCateToBottomSheet(Context context) {
        if (gridLayout != null) {
            clearBottomSheet();
            db = MoneyDb.getDatabase(context);

            List<Category> itemList = db.categoryDao().findAllByType(direction);
            if (itemList.isEmpty())
                hideBottomSheet();
            int columnCount = 3;
            int rowCount = (itemList.size() + columnCount - 1) / columnCount;
            gridLayout.setRowCount(rowCount);
            int cRow = 0;
            int cCol = 0;
            for (Category item : itemList) {
                GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
                layoutParams.rowSpec = GridLayout.spec(cRow, 1, 1f);
                layoutParams.columnSpec = GridLayout.spec(cCol, 1, 1f);
                View itemView = LayoutInflater.from(requireContext()).inflate(R.layout.cate_record, gridLayout, false);
                itemView.setLayoutParams(layoutParams);

                ImageView iconImageView = itemView.findViewById(R.id.iconImageView);
                TextView nameTextView = itemView.findViewById(R.id.nameTextView);
                iconImageView.setImageResource(item.getIcon());
                nameTextView.setText(item.getName());

                itemView.setOnClickListener(view -> {
                    hideBottomSheet();
                    binding.editTextCategory.setText(item.getName());
                });

                int[] newVal = plus(cCol, cRow, columnCount);
                cRow = newVal[1];
                cCol = newVal[0];
                gridLayout.addView(itemView);
            }
        }
    }

    private void showBottomSheet() {
        bottomSheetDialog.show();
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
        Long datetime = Common.getTimeFromString(String.valueOf(binding.editTextCal.getText()));
        String wallet = String.valueOf(binding.editTextWallet.getText());
        String note = String.valueOf(binding.editTextNote.getText());

            db = MoneyDb.getDatabase(context);
            hnHandler = new Handler(Looper.getMainLooper());
            MoneyDb.databaseWriteExecutor.execute(() -> {
                Transaction transaction = new Transaction(amount, datetime, note, category, wallet, direction);
                if (transId != 0)
                    transaction.setId(transId);

                long newid = db.transactionDao().insert(transaction);
                    hnHandler.post(() -> {
                        binding.editTextAmount.setText("");
                        binding.editTextCategory.setText("");
                        binding.editTextWallet.setText("");
                        binding.editTextCal.setText("");
                        binding.editTextNote.setText("");
                });
            });
//        }
        return true;
    }

    public void selectDirection(int id){
        switch (id) {
            case 1:
//                outcome
                direction = 1;
                binding.outBtn.setSelected(true);
                binding.inBtn.setSelected(false);
                break;
            default:
//                income
                direction = 0;
                binding.outBtn.setSelected(false);
                binding.inBtn.setSelected(true);
                break;
        }
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

    private int[] plus(int c_col, int c_row, int max_item){
        if (c_col + 1 == max_item ){
            c_col = 0;
            c_row += 1;
        } else
            c_col += 1;
        return new int[]{c_col, c_row};
    }

    private void clearBottomSheet() {
        if (gridLayout != null)
            gridLayout.removeAllViews();
    }

    private void hideBottomSheet() {
        if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
            bottomSheetDialog.dismiss();
            clearBottomSheet();
        }
    }
}