package com.org.cash.ui.add_record;

import android.content.Context;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.org.cash.R;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private final Context mContext;

    public ViewPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0)
        {
            fragment = new AddTransactionFragment();
        }
        else if (position == 1)
        {
            fragment = new AddCategoryFragment();
        }
        else if (position == 2)
        {
            fragment = new AddWalletFragment();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if (position == 0)
        {
            title = "Transaction";
        }
        else if (position == 1)
        {
            title = "Category";
        }
        else if (position == 2)
        {
            title = "Wallet";
        }
        return title;
    }
}
