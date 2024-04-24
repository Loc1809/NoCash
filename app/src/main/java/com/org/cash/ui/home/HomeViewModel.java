package com.org.cash.ui.home;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
//import com.org.cash.Application;
import com.org.cash.Repository.WalletRepository;
import com.org.cash.database.MoneyDb;
import com.org.cash.model.Wallet;

import java.util.List;
//import androidx.lifecycle.viewModelScope;
import android.app.Application;

public class HomeViewModel extends AndroidViewModel {
    private LiveData<List<Wallet>> wallets;
    private WalletRepository walletRepository;

    public HomeViewModel(Application application) {
        super(application);
        MoneyDb moneyDb = MoneyDb.getDatabase(application);
        walletRepository = new WalletRepository(moneyDb.walletDao());
        wallets = walletRepository.getWallets();
        fetchData();
    }

    private void fetchData() {
    }

    public LiveData<List<Wallet>> getWallets() {
        return walletRepository.getWallets();
    }
}
