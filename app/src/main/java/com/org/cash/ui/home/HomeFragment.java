package com.org.cash.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import com.org.cash.CustomToast;
import com.org.cash.R;
import com.org.cash.database.MoneyDb;
import com.org.cash.databinding.FragmentHomeBinding;
import com.org.cash.model.Transaction;
import com.org.cash.model.Wallet;
import com.org.cash.ui.add_record.AddTransactionFragment;
import com.org.cash.ui.add_record.AddWalletFragment;

import java.util.Calendar;
import java.util.Date;

import java.util.ArrayList;
import java.util.List;



    public class HomeFragment extends Fragment {
        private FragmentHomeBinding binding;
        private RecyclerView recyclerView;
        private HomeViewModel homeViewModel;
        private TextView uName, monthTitle, yearTitle, balance;
        private ArrayList<Wallet> walletsList;
        private ArrayList<Transaction> transactionsList;
        private WalletAdapter walletAdapter;
        private TransactionAdapter transAdapter;
        private MoneyDb db;
        private Handler hnHandler;
        private int currentMonth, currentYear;
        private final String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentHomeBinding.inflate(getLayoutInflater());
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout using data binding
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        // Access views through the binding object
        uName = binding.getRoot().findViewById(R.id.home_username);
        monthTitle = binding.getRoot().findViewById(R.id.month_text);
        yearTitle = binding.getRoot().findViewById(R.id.year_text);
        balance = binding.getRoot().findViewById(R.id.home_balance_amount);

        // Set up click listeners
        binding.goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeMonthView(-1);
            }
        });
        binding.go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeMonthView(1);
            }
        });
        prepareWalletData(binding.getRoot());
        prepareTransactionData(binding.getRoot());

        // Return the root view of the binding object
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        homeViewModel.getWallets().observe(getViewLifecycleOwner(), new Observer<List<Wallet>>() {
            @Override
            public void onChanged(List<Wallet> wallets) {
                if (!wallets.isEmpty()) {
                    uName.setText(wallets.get(0).getName());
                }
            }
        });
    }

    private void prepareWalletData(View rootView) {
        try {
            Context context = requireContext();
            db = MoneyDb.getDatabase(context);
            walletsList = new ArrayList<>();
            hnHandler = new Handler(Looper.getMainLooper());
            MoneyDb.databaseWriteExecutor.execute(() -> {
                walletsList = (ArrayList<Wallet>) db.walletDao().getWallets();
                Double amount = 0.0;
                for (Wallet w : walletsList) {
                    amount += w.getAmount();
                }
                balance.setText(amount.toString());
                hnHandler.post(() -> {
                    walletAdapter = new WalletAdapter(context, walletsList, new WalletAdapter.ClickListenner() {
                        @Override
                        public void onItemClick(int position) {
                            Wallet current = walletsList.get(position);
                            CustomToast.makeText(context, walletsList.get(position).getName(), Toast.LENGTH_SHORT).show();
                            AddWalletFragment fragment = new AddWalletFragment();
                            Bundle args = new Bundle();
                            args.putInt("id", current.getId());
                            args.putDouble("amount", current.getAmount());
                            args.putString("name", current.getName());
                            fragment.setArguments(args);
                            requireActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_home, fragment).addToBackStack("Edit").commit();
                        }
                        @Override
                        public void onItemLongClick(int position) {
                            //Hien thi snackbar confirm to delete wallet
                            Snackbar mySnackbar = Snackbar.make(binding.parentWalletLayout, "Are you sure delete this wallet?", Snackbar.LENGTH_SHORT);
                            mySnackbar.setAction("Confirm", v -> {
                                MoneyDb.databaseWriteExecutor.execute(() -> {
                                    db.walletDao().delete(walletsList.get(position));
                                    hnHandler.post(() -> {
                                        Wallet deletedWallet = walletsList.remove(position);
                                        walletAdapter.notifyItemRemoved(position);
                                        showUndoWallet(position, deletedWallet);
                                    });
                                });
                            });
                            mySnackbar.show();
                        }
                    });
                    //set du lieu cho recyclerview
                    recyclerView = rootView.findViewById(R.id.home_wallet_list);
                    recyclerView.setAdapter(walletAdapter);
                    //tao dong gach duoi giua cac item row
                    recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                });
            });
        } catch (Exception ex) {
            Log.e("Get all wallet: ", ex.getMessage());
        }
    }

    private void prepareTransactionData(View rootView) {
        try {
            Context context = requireContext();
            db = MoneyDb.getDatabase(context);
            transactionsList = new ArrayList<>();
            hnHandler = new Handler(Looper.getMainLooper());

            java.util.Date date = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            currentMonth= cal.get(Calendar.MONTH);
            currentYear = cal.get(Calendar.YEAR);
            monthTitle.setText(months[currentMonth]);
            yearTitle.setText(String.valueOf(currentYear));
            getAndShowTransaction(context, rootView, currentMonth, currentYear);
        } catch (Exception ex) {
            Log.e("Get all wallet: ", ex.getMessage());
        }
    }

    public void changeMonthView(int direction){
        if (currentMonth == 0 && direction == -1) {
            currentYear += direction;
            currentMonth = 11;
        }
        else if(currentMonth == 11 && direction == 1){
            currentYear += direction;
            currentMonth = 0;
        } else
            currentMonth += direction;
        getAndShowTransaction(requireContext(), binding.getRoot(), currentMonth, currentYear);
        monthTitle.setText(months[currentMonth]);
        yearTitle.setText(String.valueOf(currentYear));
    }

    public void showUndoWallet(int position, Wallet wallet) {
        try {
            Snackbar mySnackbar = Snackbar.make(binding.parentWalletLayout, "Click to undo wallet.", Snackbar.LENGTH_SHORT);
            mySnackbar.setAction("Undo", v -> {
                MoneyDb.databaseWriteExecutor.execute(() -> {
                    db.walletDao().insert(wallet);
                    hnHandler.post(() -> {
                        walletsList.add(position, wallet);
                        walletAdapter.notifyItemInserted(position);
                    });
                });

            });
            mySnackbar.show();
        } catch (Exception ex) {
            Log.e("ShowUndoWalletw", ex.getMessage());
        }
    }

    public void showUndoTransaction(int position, Transaction transaction) {
        try {
            Snackbar mySnackbar = Snackbar.make(binding.parentTransLayout, "Click to undo transaction.", Snackbar.LENGTH_SHORT);
            mySnackbar.setAction("Undo", v -> {
                MoneyDb.databaseWriteExecutor.execute(() -> {
                    db.transactionDao().insert(transaction);
                    hnHandler.post(() -> {
                        transactionsList.add(position, transaction);
                        transAdapter.notifyItemInserted(position);
                    });
                });

            });
            mySnackbar.show();
        } catch (Exception ex) {
            Log.e("ShowUndoWallet", ex.getMessage());
        }
    }

    public Long[] getStartEndOfMonth(int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month+1);
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
        return new Long[] {startOfMonth, endOfMonth};
    }

    public void getAndShowTransaction(Context context, View rootView, int month, int year) {
        Long[] timestamp = getStartEndOfMonth(month, year);
        MoneyDb.databaseWriteExecutor.execute(() -> {
            transactionsList = (ArrayList<Transaction>) db.transactionDao().getTransactionsByMonth(timestamp[0], timestamp[1]);
            hnHandler.post(() -> {
                transAdapter = new TransactionAdapter(context, transactionsList, new TransactionAdapter.ClickListenner() {
                    @Override
                    public void onItemClick(int position) {
                        Transaction current = transactionsList.get(position);
                        CustomToast.makeText(context, transactionsList.get(position).getDesc(), Toast.LENGTH_SHORT).show();
                        AddTransactionFragment fragment = new AddTransactionFragment();
                        Bundle args = new Bundle();
                        args.putInt("id", current.getId());
                        args.putDouble("amount", current.getAmount());
                        args.putString("category", current.getCategory());
                        args.putString("date", current.getTimeString());
                        args.putString("wallet", current.getWallet());
                        args.putString("note", current.getDesc());
                        args.putInt("direction", current.actualDirection());
                        fragment.setArguments(args);
                        requireActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_home, fragment).addToBackStack("Edit").commit();
                    }
                    @Override
                    public void onItemLongClick(int position) {
                        //Hien thi snackbar confirm to delete transaction
                        Snackbar mySnackbar = Snackbar.make(binding.parentTransLayout, "Are you sure delete this transaction?", Snackbar.LENGTH_SHORT);
                        mySnackbar.setAction("Confirm", v -> {
                            MoneyDb.databaseWriteExecutor.execute(() -> {
                                db.transactionDao().delete(transactionsList.get(position));
                                hnHandler.post(() -> {
                                    Transaction deletedTransaction = transactionsList.remove(position);
                                    transAdapter.notifyItemRemoved(position);
                                    showUndoTransaction(position, deletedTransaction);
                                });
                            });
                        });
                        mySnackbar.show();
                    }
                });
                //set du lieu cho recyclerview
                recyclerView = rootView.findViewById(R.id.home_trans_list);
                recyclerView.setAdapter(transAdapter);
                //tao dong gach duoi giua cac item row
                recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            });
        });
    }
}