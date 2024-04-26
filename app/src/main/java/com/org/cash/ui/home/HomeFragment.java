package com.org.cash.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
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
import com.org.cash.R;
import com.org.cash.database.MoneyDb;
import com.org.cash.databinding.FragmentHomeBinding;
import com.org.cash.model.Transaction;
import com.org.cash.model.Wallet;
import com.org.cash.ui.add_record.AddRecordFragment;
import com.org.cash.ui.add_record.AddTransactionFragment;

import java.util.ArrayList;
import java.util.List;



    public class HomeFragment extends Fragment {
        private FragmentHomeBinding binding;
        private WalletAdapter adapter;
        private RecyclerView recyclerView;
        private HomeViewModel homeViewModel;
        private TextView uName;
        private ArrayList<Wallet> walletsList;
        private ArrayList<Transaction> transactionsList;
        private WalletAdapter walletAdapter;
        private TransactionAdapter transAdapter;

        private MoneyDb db;
        private Handler hnHandler;
        @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentHomeBinding.inflate(getLayoutInflater());
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        uName = rootView.findViewById(R.id.home_username);
        prepareWalletData(rootView);
        prepareTransactionData(rootView);
        return rootView;
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
                Wallet newWallet = new Wallet( 6,"Ai don nau", 10000.0);
                walletsList.add(newWallet);
                //
                hnHandler.post(() -> {
                    walletAdapter = new WalletAdapter(context, walletsList, new WalletAdapter.ClickListenner() {
                        @Override
                        public void onItemClick(int position) {
                            Toast.makeText(context, walletsList.get(position).getName(), Toast.LENGTH_SHORT).show();
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
            MoneyDb.databaseWriteExecutor.execute(() -> {
                transactionsList = (ArrayList<Transaction>) db.transactionDao().getTransactions();
//                DUMMY Transaction
                Transaction newTransaction = new Transaction(60000.0, "1237102123L", "aabbcc", "cate1", 1);
                transactionsList.add(newTransaction);
                //
                hnHandler.post(() -> {
                    transAdapter = new TransactionAdapter(context, transactionsList, new TransactionAdapter.ClickListenner() {
                        @Override
                        public void onItemClick(int position) {
                            Transaction current = transactionsList.get(position);
                            Toast.makeText(context, transactionsList.get(position).getDesc(), Toast.LENGTH_SHORT).show();
                            AddRecordFragment fragment = new AddRecordFragment();
                            Bundle args = new Bundle();
                            args.putDouble("amount", current.getAmount());
                            args.putString("category", current.getCategory());
                            args.putString("date", current.getTime());
                            args.putString("wallet", current.getWallet());
                            args.putString("note", current.getDesc());
                            fragment.setArguments(args);
                            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home, fragment).commit();
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
        } catch (Exception ex) {
            Log.e("Get all wallet: ", ex.getMessage());
        }
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
            Log.e("ShowUndoWalletw", ex.getMessage());
        }
    }
}