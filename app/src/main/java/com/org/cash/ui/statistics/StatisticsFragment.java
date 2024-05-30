package com.org.cash.ui.statistics;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.org.cash.R;
import com.org.cash.database.MoneyDb;
import com.org.cash.databinding.FragmentStatisticsBinding;
import com.org.cash.model.StatisticAdapter;
import com.org.cash.model.SumByCategory;
import com.org.cash.model.Transaction;
import com.org.cash.model.Wallet;
import com.org.cash.utils.Common;
import com.org.cash.utils.MonthYearPickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class StatisticsFragment extends Fragment implements OnChartValueSelectedListener {

    FragmentManager fragmentManager;
    private MoneyDb db;
    private RecyclerView recyclerView;
    private StatisticAdapter statisticAdapter;
    private FragmentStatisticsBinding binding;
    private TextView  monthTitle, yearTitle;
    private ArrayList<Wallet> walletsList;
    private ArrayList<Transaction> transactionsList;
    private Handler hnHandler;
    private int currentMonth, currentYear;
    private final String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    private PieChart chart;
    private int direction = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentStatisticsBinding.inflate(getLayoutInflater());
    }

    private void moveOffScreen() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);

        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        int height = displayMetrics.heightPixels;

        int offset = (int) (height * 0.35); /* percent to move */

        RelativeLayout.LayoutParams rlParams = (RelativeLayout.LayoutParams) chart.getLayoutParams();
        rlParams.setMargins(0, 0, 0, -offset);
        chart.setLayoutParams(rlParams);
    }

    private void prepareDataChart() {
        db = MoneyDb.getDatabase(requireContext());
        Long[] timestamps = Common.getStartEndOfMonth(currentMonth,currentYear);
        double sum = db.transactionDao().getSumByMonth(timestamps[0], timestamps[1], direction);
        List<SumByCategory> sumByCategories = db.transactionDao().getSumByMonthDirection(timestamps[0], timestamps[1], direction);
        ArrayList<PieEntry> values = new ArrayList<>();

        hnHandler = new Handler(Looper.getMainLooper());
        hnHandler.post(()->{
            for (SumByCategory category : sumByCategories) {
                values.add(new PieEntry((float) (category.getSum()/sum), category.getCategory()));
            }
            PieDataSet dataSet = new PieDataSet(values, "Total:" + Common.formatCurrency(String.valueOf((long)sum)));
            dataSet.setSliceSpace(3f);
            dataSet.setSelectionShift(5f);

            dataSet.setColors(ColorTemplate.PASTEL_COLORS);
            //dataSet.setSelectionShift(0f);

            dataSet.setDrawValues(false);
            dataSet.setValueTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            dataSet.setValueTextSize(14f);
            PieData data = new PieData(dataSet);
            data.setValueFormatter(new PercentFormatter());
            data.setValueTextSize(0f);
            data.setValueTextColor(Color.TRANSPARENT);// yourDesiredTextSize is the size you want
            chart.setEntryLabelColor(Color.TRANSPARENT);
            chart.setEntryLabelTextSize(0f);
            chart.setData(data);
            chart.invalidate();
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout using data binding
        binding = FragmentStatisticsBinding.inflate(inflater, container, false);
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        fragmentManager = requireActivity().getSupportFragmentManager();

        recyclerView = binding.statiticsRecyclerView;
        recyclerView.setAdapter(statisticAdapter);
        // Access views through the binding object
        monthTitle = binding.getRoot().findViewById(R.id.budget_month_text);
        yearTitle = binding.getRoot().findViewById(R.id.budget_year_text);

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

        binding.budgetMonthText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMonthYearPicker();
            }
        });

        binding.budgetYearText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                showMonthYearPicker();
            }
        });

        // Set up click listeners
        binding.budgetGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeMonthView(-1);
            }
        });
        binding.budgetGoPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeMonthView(1);
            }
        });
//        prepareWalletData(binding.getRoot());
        prepareTransactionData(binding.getRoot());

        chart = binding.chart1;
        selectDirection(0);
        int chartBackground = ContextCompat.getColor(requireContext(), R.color.statistic_background);
        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(5, 10, 5, 5);

        chart.setDragDecelerationFrictionCoef(0.95f);

        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(chartBackground);

        chart.setTransparentCircleColor(chartBackground);
        chart.setTransparentCircleAlpha(110);

        chart.setHoleRadius(58f);
        chart.setTransparentCircleRadius(61f);

        chart.setDrawCenterText(true);

        chart.setRotationAngle(0);
        // enable rotation of the chart by touch
        chart.setRotationEnabled(true);
        chart.setHighlightPerTapEnabled(true);

        // chart.setUnit(" €");
        // chart.setDrawUnitsInChart(true);

        // add a selection listener
        chart.setOnChartValueSelectedListener(this);

//        prepareDataChart();
        chart.animateY(1400, Easing.EaseInOutQuad);
        // chart.spin(2000, 0, 360);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // entry label styling
        chart.setEntryLabelColor(chartBackground);
        chart.setEntryLabelTextSize(12f);
        return binding.getRoot();
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
        getAndShowTransaction(requireContext(), binding.getRoot(), currentMonth, currentYear);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if (e == null)
            return;

        PieEntry pe = (PieEntry) e;
        String category = pe.getLabel();
        chart.setCenterText(generateCenterSpannableText(category));
        Log.i("VAL SELECTED", "Category: " + category + ", Value: " + pe.getValue());
    }
    
    @Override
    public void onNothingSelected() {
        Log.i("PieChart", "nothing selected");
    }

    private SpannableString generateCenterSpannableText(String category) {
        SpannableString s = new SpannableString(category);
        s.setSpan(new RelativeSizeSpan(1.2f), 0, category.length(), 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 0, category.length(), 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 0, category.length(), 0);
        s.setSpan(new RelativeSizeSpan(1.2f), 0, category.length(), 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), 0, category.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), 0, category.length(), 0);
        return s;
    }
    
    public StatisticsFragment() {
    }

//    @SuppressLint({"MissingInflatedId", "SuspiciousIndentation"})
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_budget, container, false);
//        recyclerView = view.findViewById(R.id.statitics_recycler_view);
//
//        db = MoneyDb.getDatabase(this.getContext());
//        list = db.transactionDao().getUniqueTransactionTimes();
//        budgetAdapter = new BudgetAdapter(this.getContext(), list);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
//        recyclerView.setLayoutManager(linearLayoutManager);
//        recyclerView.setAdapter(budgetAdapter);
//
//        return view;
//    }

    private void prepareTransactionData(View rootView) {
        try {
            Context context = requireContext();
            db = MoneyDb.getDatabase(context);
            transactionsList = new ArrayList<>();
            hnHandler = new Handler(Looper.getMainLooper());

            java.util.Date date = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            currentMonth = cal.get(Calendar.MONTH);
            currentYear = cal.get(Calendar.YEAR);
            monthTitle.setText(months[currentMonth]);
            yearTitle.setText(String.valueOf(currentYear));
            getAndShowTransaction(context, rootView, currentMonth, currentYear);
        } catch (Exception ex) {
            Log.e("Get all wallet: ", ex.getMessage());
        }
    }

    public void changeMonthView(int direction) {
        if (currentMonth == 0 && direction == -1) {
            currentYear += direction;
            currentMonth = 11;
        } else if (currentMonth == 11 && direction == 1) {
            currentYear += direction;
            currentMonth = 0;
        } else
            currentMonth += direction;
        getAndShowTransaction(requireContext(), binding.getRoot(), currentMonth, currentYear);
        monthTitle.setText(months[currentMonth]);
        yearTitle.setText(String.valueOf(currentYear));
    }

    private void showMonthYearPicker() {
        MonthYearPickerDialog dialog = MonthYearPickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                binding.budgetMonthText.setText(months[month]);
                binding.budgetYearText.setText(String.valueOf(year));
                currentMonth = month;
                currentYear = year;
                getAndShowTransaction(requireContext(), binding.getRoot(), currentMonth, currentYear);
            }
        });
        dialog.show(getFragmentManager(), "MonthYearPickerDialog");
    }


    public Long[] getStartEndOfMonth(int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month + 1);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long startOfMonth = calendar.getTimeInMillis();
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        long endOfMonth = calendar.getTimeInMillis();
        return new Long[]{startOfMonth, endOfMonth};
    }

    public void getAndShowTransaction(Context context, View rootView, int month, int year) {
        prepareDataChart();
        Long[] timestamp = getStartEndOfMonth(month-1, year);
        db = MoneyDb.getDatabase(context);
        List<Integer> daysList = db.transactionDao().getDaysByMonth(timestamp[0], timestamp[1], direction);
        while (daysList.contains(0))
            daysList.remove((Integer) 0);

        recyclerView.setAdapter(null);
        statisticAdapter = new StatisticAdapter( fragmentManager,context, daysList, month, year, direction);
        recyclerView = rootView.findViewById(R.id.statitics_recycler_view);
        recyclerView.setAdapter(statisticAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

}
