package com.dnd.readingworld_admin.Adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.dnd.readingworld_admin.Fragment.ListWord;
import com.dnd.readingworld_admin.Fragment.MyListWord;
import com.dnd.readingworld_admin.Fragment.SpeechToText;

;

/**
 * Created by Asus on 11/20/2016.
 */

public class PagerAdapter  extends FragmentPagerAdapter {
    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public android.support.v4.app.Fragment getItem(int position) {

        android.support.v4.app.Fragment frag = null;
        switch (position) {
            case 0:
                frag = new ListWord();
                break;
            case 1:
                frag = new MyListWord();
                break;
            case 2:
                frag = new SpeechToText();
                break;
        }
        return frag;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position) {
            case 0:
                title = "List Word";
                break;
            case 1:
                title = "User List Word";
                break;
            case 2:
                title = "Speed To Text";
                break;
        }
        return title;
    }

}

