package com.org.cash.database;

import android.content.Context;
import androidx.room.Room;
import com.org.cash.DAO.WalletDao;
import org.jetbrains.annotations.NotNull;
//LOC NOTE: not using this
public class DatabaseHelper {
        private static volatile DatabaseHelper INSTANCE;
        private MoneyDb db;

        private DatabaseHelper(@NotNull Context context) {
            db = Room.databaseBuilder(context.getApplicationContext(),
                    MoneyDb.class, "money_db").allowMainThreadQueries()
                    .build();
        }

        public static synchronized DatabaseHelper getInstance(Context context) {
            if (INSTANCE == null) {
                INSTANCE = new DatabaseHelper(context);
            }
            return INSTANCE;
        }

        public MoneyDb getDatabase() {
            return db;
        }
    }