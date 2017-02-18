package com.ben.opinionslice.listener;

import java.util.ArrayList;
import java.util.List;

import com.ben.opinionslice.FeedFragment;
import com.ben.opinionslice.application.OSConfig;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
 
public class FeedPagerAdapter extends FragmentPagerAdapter {
 
	private List<FeedFragment> fragments;
	private List<String> titles;
    public FeedPagerAdapter(FragmentManager fm) {
        super(fm);
        this.fragments = new ArrayList<FeedFragment>();
        this.titles = new ArrayList<String>();
    }
    
    public void addItem(FeedFragment myFragment, String title){
    	this.fragments.add(myFragment);
    	this.titles.add(title);
    }
    @Override
    public Fragment getItem(int index) {
    	return this.fragments.get(index);
    }
 
    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return OSConfig.FEED_COUNT;
    }
 
}
