package com.xiuxiuchat.main;

import java.util.HashMap;
import java.util.Map;


import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.xiuxiuchat.R;
import com.xiuxiuchat.main.chat.ChatFragment;
import com.xiuxiuchat.main.discover.DiscoverFragment;

/**
 * Created by huzhi on 15-3-9.
 */
public class TabsFragmentManager {

    private static TabsFragmentManager mInstance;

    private TabsFragmentManager(){};

    public static TabsFragmentManager getInstance(){
        if(mInstance == null){
            mInstance = new TabsFragmentManager();
        }
        return mInstance;
    }

    private Map<String ,Fragment> fragments = new HashMap<String ,Fragment>();

    public void clear(){
        if(fragments!=null){
            fragments.clear();
        }
    }

    private int mPosition;

    public Fragment getCurrentFragment(){
        return getFragment(mPosition);
    }

    public Fragment getFragment(int position){
        Fragment fragment = null;
        switch (position) {
		case 0:
			fragment = new DiscoverFragment();
			break;
		case 1:
			fragment = new ChatFragment();
			break;
		case 2:
			fragment = new UserFragment();
			break;
		default:
			break;
		}
		fragments.put(position+"", fragment);
        return fragment;
    }

    @SuppressLint("NewApi")
	public void commitFragment(int position, FragmentActivity ac){
        mPosition = position;
        FragmentManager fragmentManager = ac.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment;
//        Fragment fragment = fragmentManager.findFragmentByTag(String.valueOf(position));
        if(fragments.get(position+"")==null){
            fragment = getFragment(position);
        }else {
        	fragment = fragments.get(position + "");
        }
        fragmentTransaction.replace(R.id.container_body_layout,fragment,String.valueOf(position));
//        fragmentTransaction.show(fragment);
//        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public int getCurrentPosition(){
        return mPosition;
    }
}
