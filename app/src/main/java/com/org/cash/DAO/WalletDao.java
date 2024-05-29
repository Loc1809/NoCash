package com.org.cash.DAO;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.org.cash.CustomToast;
import com.org.cash.model.Wallet;
import com.org.cash.model.Wallet;

import java.util.Collection;
import java.util.List;

@Dao
public interface WalletDao {
    @Query("SELECT * FROM wallet")
    LiveData<List<Wallet>> getAll();

    @Query("SELECT * FROM wallet")
    List<Wallet> getWallets();

    @Query("SELECT * FROM wallet WHERE id IN (:walletIds)")
    LiveData<List<Wallet>> loadAllByIds(Collection<Integer> walletIds);

    @Query("SELECT * FROM wallet WHERE id IN (:walletIds)")
    Wallet findById(int walletIds);

    @Query("SELECT * FROM wallet WHERE name = :name")
    List<Wallet> findByName(String name);

//    @Query("SELECT * FROM wallet WHERE first_name LIKE :first AND " +
//           "last_name LIKE :last LIMIT 1")
//    Wallet findByName(String first, String last);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Wallet wallet);

    @Insert
    void insertAll(Wallet... wallets);

    @Delete
    void delete(Wallet wallet);

    default void deleteById(int walletId){
        delete(findById(walletId));
    };

    default void checkBeforeInsert(Wallet wallet, Context context, Handler handler){
        List<Wallet> list = findByName(wallet.getName());
        if (list.isEmpty() || wallet.getId() != -1){
            insert(wallet);
        } else {
            handler.post(() -> {
                CustomToast.makeText(context, "Wallet existed", Toast.LENGTH_SHORT).show();
            });
        }
    }


    default boolean isAnySimilarWallet(Wallet wallet){
        if (wallet.getId() == null)
            return false;
        List<Wallet> list = findByName(wallet.getName());
        return !list.isEmpty();
    }
}
