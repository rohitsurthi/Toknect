package com.rohheat.realtimechatapp;

import android.graphics.drawable.Drawable;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

class SectionPagerAdapter extends FragmentPagerAdapter {


    public SectionPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position){

            case 0:
                ChatFragment chatFragment = new ChatFragment();
                return chatFragment;

            case 1:
                FriendFragment friendFragment = new FriendFragment();
                return friendFragment;

            case 2:
                RequestFragment requestFragment = new RequestFragment();
                return requestFragment;

            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return 3;
    }


    public CharSequence getPageTitle(int position){

        switch (position){

            case 0:
                return "Chat";
            case 1:
                return "Friends";
            case 2:
                return "Notifications";

            default:
                return null;

        }

    }

}
