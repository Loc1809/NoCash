package com.org.cash.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import com.org.cash.DAO.TransactionDao;
import com.org.cash.DAO.WalletDao;
import com.org.cash.model.Category;
import com.org.cash.DAO.CategoryDao;
import com.org.cash.model.CategoryChild;
import com.org.cash.model.Transaction;
import com.org.cash.model.Wallet;
import androidx.room.Room;
import com.org.cash.utils.Converters;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {Category.class, Wallet.class,
                CategoryChild.class, Transaction.class},
        version = 1,
        exportSchema = false
)
@TypeConverters({Converters.class})
public abstract class MoneyDb extends RoomDatabase {
    public abstract CategoryDao categoryDao();
    public abstract TransactionDao transactionDao();
    public abstract WalletDao walletDao();

    private static volatile MoneyDb INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    public static MoneyDb getDatabase(final Context context) {
    if (INSTANCE == null) {
        synchronized (MoneyDb.class) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        MoneyDb.class, "money_database").allowMainThreadQueries()
                        .build();
            }
        }
    }
    return INSTANCE;
}
}