package com.org.cash.utils;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.org.cash.R;

import java.text.DateFormatSymbols;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

public class MonthYearPickerDialog extends DialogFragment {

    private static final int MAX_YEAR = 2099;
    private DatePickerDialog.OnDateSetListener listener;

    public static MonthYearPickerDialog newInstance(DatePickerDialog.OnDateSetListener listener) {
        MonthYearPickerDialog fragment = new MonthYearPickerDialog();
        fragment.setListener(listener);
        return fragment;
    }

    public void setListener(DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar cal = Calendar.getInstance();
        final int currentYear = cal.get(Calendar.YEAR);
        final int currentMonth = cal.get(Calendar.MONTH);
        final View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.month_year_picker_dialog, null);

        final TextView selectedMonth = dialogView.findViewById(R.id.selected_month);
        final TextView selectedYear = dialogView.findViewById(R.id.selected_year);
        selectedYear.setText(String.valueOf(currentYear));
        final NumberPicker yearPicker = dialogView.findViewById(R.id.picker_year);
        final GridLayout monthGrid = dialogView.findViewById(R.id.month_grid);

        selectedMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                yearPicker.setVisibility(View.GONE);
                monthGrid.setVisibility(View.VISIBLE);
            }
        });

        selectedYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                yearPicker.setVisibility(View.VISIBLE);
                monthGrid.setVisibility(View.GONE);
            }
        });

        yearPicker.setVisibility(View.GONE);

        yearPicker.setMinValue(1900);
        yearPicker.setMaxValue(MAX_YEAR);
        yearPicker.setValue(currentYear);

        yearPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener(){
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                selectedYear.setText(String.valueOf(numberPicker.getValue()));
            }
        });

        for (int i = 0; i < monthGrid.getChildCount(); i++) {
            final int month = i;
            monthGrid.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedMonth.setText(new DateFormatSymbols().getShortMonths()[month]);
                }
            });
        }

        selectedMonth.setText(new DateFormatSymbols().getShortMonths()[currentMonth]);

        return new AlertDialog.Builder(getActivity())
            .setView(dialogView)
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    int currentYear = yearPicker.getValue();
                    int currentMonth = Arrays.asList(new DateFormatSymbols().getShortMonths())
                            .indexOf(selectedMonth.getText().toString());
                    listener.onDateSet(null, currentYear, currentMonth, 0);
                }
            })
            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            })
            .create();
    }
}



