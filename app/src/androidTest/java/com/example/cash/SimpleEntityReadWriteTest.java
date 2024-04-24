package com.example.cash;

import android.content.Context;
import androidx.room.Room;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.org.cash.DAO.WalletDao;
import com.org.cash.database.MoneyDb;
import com.org.cash.model.Wallet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import androidx.test.core.app.ApplicationProvider;

import java.io.IOException;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class SimpleEntityReadWriteTest {
    private WalletDao walletDao;
    private MoneyDb db;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, MoneyDb.class).build();
        walletDao = db.walletDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void writeUserAndReadInList() throws Exception {
        Wallet newWallet = new Wallet( 5, "Test", 10000.0);
        walletDao.insert(newWallet);
        List<Wallet> allBooks = walletDao.getWallets();
        System.out.println(allBooks);
    }
}
