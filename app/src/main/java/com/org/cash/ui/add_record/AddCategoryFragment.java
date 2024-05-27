package com.org.cash.ui.add_record;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.org.cash.R;
import com.org.cash.database.MoneyDb;
import com.org.cash.databinding.FragmentAddCategoryBinding;
import com.org.cash.model.Category;
import com.org.cash.utils.Common;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddCategoryFragment} factory method to
 * create an instance of this fragment.
 */
public class AddCategoryFragment extends Fragment {
    public AddCategoryFragment() {}
    private FragmentAddCategoryBinding binding;
    private BottomSheetDialog bottomSheetDialog;
    private Handler hnHandler;
    private GridLayout gridLayout;
    private MoneyDb db;
    private ImageView selectedIcon;
    private int currentIcon = -1, cateId = -1, direction;
    private String cateName;

    @Override
    public void onStop() {
        super.onStop();
        if (selectedIcon != null) {
            selectedIcon.setAlpha(1.0f);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddCategoryBinding.inflate(inflater, container, false);

        selectDirection(0);

        bottomSheetDialog = new BottomSheetDialog(requireContext());
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);
        bottomSheetDialog.setContentView(sheetView);

        gridLayout = new GridLayout(requireContext());
        gridLayout = sheetView.findViewById(R.id.iconGrid);

        bottomSheetDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                binding.arrowSelectCate.setImageResource(R.drawable.baseline_keyboard_arrow_right_24_purple);
            }
        });

        binding.selectIconCate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addIconsToContainer();
                Common.hideSoftKeyboard(requireContext(), binding.getRoot());
                binding.arrowSelectCate.setImageResource(R.drawable.baseline_keyboard_arrow_down_24);
                showBottomSheet();
            }
        });

        binding.placeholderIconSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addIconsToContainer();
                Common.hideSoftKeyboard(requireContext(), binding.getRoot());
                binding.arrowSelectCate.setImageResource(R.drawable.baseline_keyboard_arrow_down_24);
                showBottomSheet();
            }
        });

        binding.viewAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCateToBottomSheet(requireContext());
                showBottomSheet();
            }
        });

        binding.addCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSave();
                clearInfo();
            }
        });

        binding.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.hideSoftKeyboard(requireContext(), binding.getRoot());
                clearBottomSheet();
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
        return binding.getRoot();
    }

    private void addIconsToContainer() {
        if (gridLayout != null) {
            clearBottomSheet();
            int[] iconIds = {R.drawable.baseline_calendar_month_24, R.drawable.baseline_wallet_24, R.drawable.baseline_attach_money_24, R.drawable.baseline_edit_note_24,
            R.drawable.baseline_calendar_month_24, R.drawable.baseline_wallet_24, R.drawable.baseline_attach_money_24, R.drawable.baseline_edit_note_24,
            R.drawable.baseline_calendar_month_24, R.drawable.baseline_wallet_24, R.drawable.baseline_attach_money_24, R.drawable.baseline_edit_note_24};
            int columnCount = 5;
            int rowCount = (iconIds.length + columnCount - 1) / columnCount;

            gridLayout.setRowCount(rowCount);

            for (int i = 0; i < iconIds.length; i++) {
                int row = i / columnCount;
                int column = i % columnCount;

                ImageView imageView = new ImageView(requireContext());
                imageView.setImageResource(iconIds[i]);
                GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
                layoutParams.rowSpec = GridLayout.spec(row, 1, 1f);
                layoutParams.columnSpec = GridLayout.spec(column, 1, 1f);
                imageView.setLayoutParams(layoutParams);
                imageView.setAdjustViewBounds(true);
                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                final int iconIndex = i;
                imageView.setOnClickListener(view -> {
                    currentIcon = iconIds[iconIndex];
                    if (selectedIcon != null) {
                        selectedIcon.setAlpha(1.0f);
                    }
                    imageView.setAlpha(0.5f);
                    selectedIcon = imageView;
                    setIconResource(currentIcon);
                    binding.placeholderIconSelect.setHint("");
                });

                gridLayout.addView(imageView);
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

                itemView.setOnLongClickListener(view -> {
                    hideBottomSheet();
                    displayEditInfo(item.getId(), item.getName(), item.getIcon(), item.getType());
                    return true;
                });

                int[] newVal = plus(cCol, cRow, columnCount);
                cRow = newVal[1];
                cCol = newVal[0];
                gridLayout.addView(itemView);
            }
        }
    }

    private int[] plus(int c_col, int c_row, int max_item){
        if (c_col + 1 == max_item ){
            c_col = 0;
            c_row += 1;
        } else
            c_col += 1;
        return new int[]{c_col, c_row};
    }

    public boolean onSave(){
        Context context = requireContext();
        if (!validateInput())
            return false;

        String name = String.valueOf(binding.editTextName.getText());
        int currentId = cateId;
        db = MoneyDb.getDatabase(context);
        hnHandler = new Handler(Looper.getMainLooper());

        MoneyDb.databaseWriteExecutor.execute(() -> {
            Category category = new Category(name, direction, currentIcon);
            if (currentId != -1) {
                category.setId(currentId);
            }

            db.categoryDao().checkBeforeInsert(category, context, hnHandler);

            hnHandler.post(() -> {
                if (isAdded()) {
                    Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show();
                    binding.editTextName.setText("");
                    binding.placeholderIconSelect.setHint(R.string.category_icon);
                }
            });
        });

        return true;
    }

    public boolean validateInput() {
        if (binding.editTextName.getText().length() == 0) {
            binding.editTextName.setError("Missing this field");
            return false;
        }
        if (currentIcon == -1) {
            binding.placeholderIconSelect.setError("Missing this field");
            return false;
        }
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

    private void hideBottomSheet() {
        if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
            bottomSheetDialog.dismiss();
            clearBottomSheet();
        }
    }

    private void showBottomSheet() {
        bottomSheetDialog.show();
    }

    private void clearBottomSheet() {
        if (gridLayout != null)
            gridLayout.removeAllViews();
    }

    private void setIconResource(int icon){
        binding.iconPreview.setImageResource(icon);
        binding.iconPreview.setVisibility(View.VISIBLE);
    }

    private void clearInfo(){
        cateId = -1;
        binding.editTextName.setText("");
        binding.placeholderIconSelect.setHint(R.string.category_icon);
        binding.iconPreview.setVisibility(View.GONE);
        binding.clearButton.setVisibility(View.GONE);
        binding.optionButton.setText("");
    }

    private void displayEditInfo(int id, String name, int icon, int direction){
        cateId = id;
        binding.editTextName.setText(name);
        binding.placeholderIconSelect.setHint("");
        setIconResource(icon);

        binding.clearButton.setVisibility(View.VISIBLE);
        binding.optionButton.setText("Delete");
        selectDirection(direction);

        binding.clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearInfo();
            }
        });

        binding.optionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar mySnackbar = Snackbar.make(binding.parentLayout, "Are you sure delete this category?", Snackbar.LENGTH_SHORT);
                mySnackbar.setAction("Confirm", o -> {
                    MoneyDb.databaseWriteExecutor.execute(() -> {
                        db.categoryDao().deleteById(cateId);
                    });
                });
                mySnackbar.show();
            }
        });
    }


}