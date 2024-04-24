package com.org.cash;

import android.content.Context;
import androidx.room.Room;
import com.org.cash.DAO.CategoryDao;
import com.org.cash.DAO.WalletDao;
import com.org.cash.Repository.WalletRepository;
import com.org.cash.database.MoneyDb;

//NOT USING
public class Application extends android.app.Application {
    private String currentDirectory;
    private WalletRepository walletRepository;

    public WalletRepository getWalletRepository() {
        return walletRepository;
    }

    public void setWalletRepository(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        WalletDao walletDao = MoneyDb.getDatabase(this).walletDao();
        WalletRepository walletRepository = new WalletRepository(walletDao);
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
    }


}
