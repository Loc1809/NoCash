package com.org.cash.ui.add_record;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.org.cash.CustomToast;
import com.org.cash.R;
import com.org.cash.database.MoneyDb;
import com.org.cash.databinding.FragmentAddTransactionBinding;
import com.org.cash.model.Category;
import com.org.cash.model.Limit;
import com.org.cash.model.Transaction;
import com.org.cash.model.Wallet;
import java.util.Calendar;
import java.util.List;
import com.org.cash.utils.Common;
import com.org.cash.utils.MonthYearPickerDialog;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddTransactionFragment} factory method to
 * create an instance of this fragment.
 */
public class AddTransactionFragment extends Fragment  {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private FragmentAddTransactionBinding binding;
    private MoneyDb db;
    private Handler hnHandler = new Handler();
    private Button button;
    private int mYear, mMonth, mDay, datetime, transId = 0, limitId = 0, direction;
    private GridLayout gridLayout;
    private BottomSheetDialog bottomSheetDialog;
    private String mode="limit";
    private boolean suggestionAdded = false, isDeleting  = false, isFormatting = false;
    private Runnable runnable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddTransactionBinding.inflate(inflater, container, false);
        final String[] suggestions = new String[2];
        bottomSheetDialog = new BottomSheetDialog(requireContext());
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);
        bottomSheetDialog.setContentView(sheetView);

        gridLayout = new GridLayout(requireContext());
        gridLayout = sheetView.findViewById(R.id.iconGrid);


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
                onSave();
            }
        });

        binding.optionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomToast.makeText(requireContext(), "Cleared", Toast.LENGTH_SHORT, 2).show();
                Common.hideSoftKeyboard(requireContext(), binding.getRoot());
                binding.editTextAmount.setText("");
                binding.editTextCategory.setText("");
                binding.editTextWallet.setText("");
                binding.editTextCal.setText("");
                binding.editTextNote.setText("");
            }
        });
        binding.editTextCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
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
                selectWallet();
            }
        });

        binding.changeModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeModeTo(mode);
            }
        });

        bottomSheetDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Common.hideSoftKeyboard(requireContext(), binding.getRoot());
                binding.arrowSelectCate.setImageResource(R.drawable.baseline_keyboard_arrow_right_24_purple);
                binding.arrowSelectWallet.setImageResource(R.drawable.baseline_keyboard_arrow_right_24_purple);
            }
        });

        try {
            if (requireArguments().getString("category") != null) {
                binding.editTextAmount.setText(Common.formatCurrency(String.valueOf((long) requireArguments().getDouble("amount"))));
                binding.editTextCategory.setText(String.valueOf(requireArguments().getString("category")));
                binding.editTextCal.setText(String.valueOf(requireArguments().getString("date")));
                if (!requireArguments().getBoolean("limit")){
                    transId = requireArguments().getInt("id");
                    binding.editTextWallet.setText(String.valueOf(requireArguments().getString("wallet")));
                    binding.editTextNote.setText(String.valueOf(requireArguments().getString("note")));
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
                            Snackbar mySnackbar = Snackbar.make(binding.parentLayout, "Are you sure delete this trans?", Snackbar.LENGTH_SHORT);
                            mySnackbar.setAction("Confirm", o -> {
                                MoneyDb.databaseWriteExecutor.execute(() -> {
                                    db.transactionDao().deleteById(requireArguments().getInt("id"));
                                });
                            });
                            mySnackbar.show();
                        }
                    });
                } else {
                    limitId = requireArguments().getInt("id");
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
                            Snackbar mySnackbar = Snackbar.make(binding.parentLayout, "Are you sure delete this limit?", Snackbar.LENGTH_SHORT);
                            mySnackbar.setAction("Confirm", o -> {
                                MoneyDb.databaseWriteExecutor.execute(() -> {
                                    db.limitDao().deleteById(requireArguments().getInt("id"));
                                });
                            });
                            mySnackbar.show();
                        }
                    });
                    changeModeTo("limit");
                }
                binding.cancelButton.setVisibility(View.VISIBLE);
                binding.optionButton.setText("Delete");
                selectDirection(requireArguments().getInt("direction"));
            }
        }
        catch (Exception e){
            System.out.println("Add transaction");
            selectDirection(0);
        }


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
                if (isFormatting) {
                    return;
                }
                isFormatting = true;
                String input = editable.toString().replaceAll("[^\\d]", "");
                if (!input.isEmpty()) {
                    String formatted = Common.formatCurrency(input);
                    binding.editTextAmount.setText(formatted);
                    binding.editTextAmount.setSelection(formatted.length());
                }
                isFormatting = false;
            }
        });

        binding.suggestionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != AdapterView.INVALID_POSITION) {
                    String selectedItem = (String) parent.getItemAtPosition(position);
                    if (!suggestionAdded && selectedItem.equals(suggestions[0])) {
                        binding.editTextAmount.setText(selectedItem);
                        binding.editTextAmount.setSelection(selectedItem.length()); // Đặt con trỏ vào cuối
                        //binding.suggestionSpinner.setVisibility(View.GONE);
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

    private void selectWallet(){
        binding.arrowSelectCate.setImageResource(R.drawable.baseline_keyboard_arrow_down_24);
        addWalletToBottomSheet(requireContext());
        showBottomSheet();
    }

    private void changeModeTo(String change_mode){
        binding.editTextCal.setText("");
        if (change_mode.equals("trans")){
            mode = "limit";
            binding.changeModeButton.setText("Create Limit");
            binding.noteBlock.setEnabled(true);
            binding.walletBlock.setEnabled(true);
            binding.editTextWallet.setEnabled(true);
            binding.editTextNote.setEnabled(true);
            binding.editTextCal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Common.hideSoftKeyboard(requireContext(), binding.getRoot());
                    showDatePickerDialog();
            }
        });
        } else {
            mode = "trans";
            binding.changeModeButton.setText("Create Transaction");
            binding.noteBlock.setEnabled(false);
            binding.walletBlock.setEnabled(false);
            binding.editTextWallet.setEnabled(false);
            binding.editTextNote.setEnabled(false);

            binding.editTextCal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Get Current Date
                    Common.hideSoftKeyboard(requireContext(), binding.getRoot());
                    showMonthPicker();
                }
            });
        }
    }

    private void showDatePickerDialog(){
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

    private void showMonthPicker() {
        showMonthYearPicker();
    }

    private void showMonthYearPicker() {
        MonthYearPickerDialog dialog = MonthYearPickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String selectedDate = (month + 1) + "/" + year;
                binding.editTextCal.setText(selectedDate);
            }
        });
        dialog.show(getFragmentManager(), "MonthYearPickerDialog");
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

    public boolean onSave() {
        Context context = requireContext();
        if (!validateInput())
            return false;
        Double amount = Double.valueOf(String.valueOf(binding.editTextAmount.getText()));
        String category = binding.editTextCategory.getText().toString();
        String wallet = String.valueOf(binding.editTextWallet.getText());
        String note = String.valueOf(binding.editTextNote.getText());
        db = MoneyDb.getDatabase(context);
        hnHandler = new Handler(Looper.getMainLooper());
        if (mode.equals("limit")){
//           LOC NOTE: TRANSACTION
            Long datetime = Common.getTimeFromString(String.valueOf(binding.editTextCal.getText()));
            MoneyDb.databaseWriteExecutor.execute(() -> {
                Wallet thisWallet = db.walletDao().findByName(wallet).get(0);
                hnHandler.post(() -> {
                    double money = (direction == 0) ? amount : -amount;
                    if (thisWallet.getAmount() + money < 0){
                        CustomToast.makeText(requireContext(), "Wallet out of money!", Toast.LENGTH_SHORT, 2).show();
                        return;
                    }
                    thisWallet.setAmount(thisWallet.getAmount() + money);
                    db.walletDao().insert(thisWallet);
                    Transaction transaction = new Transaction(amount, datetime, note, category, wallet, direction);
                    if (transId != 0)
                        transaction.setId(transId);
                    String[] monthYear = binding.editTextCal.getText().toString().split("-");
                    Long[] timestamp = Common.getStartEndOfMonth(Integer.parseInt(monthYear[1]) - 1, Integer.parseInt(monthYear[2]));
                    List<Limit> limit = db.limitDao().getLimitsByMonthTypeAndCate(timestamp[0], timestamp[1], transaction.direction(), transaction.getCategory());
                    String message;
                    int type;
                    if (!limit.isEmpty()) {
                        double sum = db.transactionDao().getSumByMonthAndCate(timestamp[0], timestamp[1], transaction.getCategory());
                        if (sum + transaction.getAmount() < limit.get(0).getAmount()){
                            message = "You reached " + (int) sum + "|" + (int) limit.get(0).getAmount();
                            type = 1;
                        } else {
                            message = "Limit exceeded "  +  (int) sum + "|" + (int) limit.get(0).getAmount();
                            type = 2;
                        }
                    }else {
                        message = "Success";
                        type = 1;
                    }
                    CustomToast.makeText(requireContext(), message, Toast.LENGTH_SHORT, type).show();
                    long newid = db.transactionDao().insert(transaction);
                    hnHandler.post(() -> {
                        binding.editTextAmount.setText("");
                        binding.editTextCategory.setText("");
                        binding.editTextWallet.setText("");
                        binding.editTextCal.setText("");
                        binding.editTextNote.setText("");
                    });
                });
            });
        } else {
//           LOC NOTE: LIMIT
            String datetimeString = binding.editTextCal.getText().toString();
            String[] monthYear = datetimeString.split("/");
            Long[] timestamps = Common.getStartEndOfMonth(Integer.parseInt(monthYear[0]) - 1, Integer.parseInt(monthYear[1]));
            MoneyDb.databaseWriteExecutor.execute(() -> {
                Limit limit = new Limit(-1, amount, category, timestamps[0], timestamps[1], direction);
                if (limitId != 0)
                    limit.setId(limitId);
                if (!db.limitDao().isAnySimilarLimit(limit)){
                    Limit insert = new Limit(amount, category, timestamps[0], timestamps[1], direction);
                    db.limitDao().insert(insert);
                    hnHandler.post(() -> {
                        CustomToast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT, 1).show();
                        binding.editTextAmount.setText("");
                        binding.editTextCategory.setText("");
                        binding.editTextCal.setText("");
                    });
                } else
                    hnHandler.post(() -> {
                        CustomToast.makeText(requireContext(), "Limit existed", Toast.LENGTH_SHORT, 2).show();
                        binding.editTextCategory.setError("Limit existed");
                    });

            });
        }
        return true;
    }

    public void clearInputs(){
        binding.editTextAmount.setText("");
        binding.editTextCategory.setText("");
        binding.editTextWallet.setText("");
        binding.editTextCal.setText("");
        binding.editTextNote.setText("");
        Common.hideSoftKeyboard(requireContext(), binding.getRoot());
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
        if (mode.equals("limit") && (binding.editTextWallet.getText().length() == 0 || binding.editTextWallet.getText().toString().equals("0"))) {
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