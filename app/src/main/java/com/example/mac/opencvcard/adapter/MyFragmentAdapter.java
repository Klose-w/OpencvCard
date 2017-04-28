package com.example.mac.opencvcard.adapter;



import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;


import com.example.mac.opencvcard.Fragment.*;
import com.example.mac.opencvcard.activity.Main2Activity;


/**
 * Created by jingyangyang on 2016/12/8.
 */

public class MyFragmentAdapter extends FragmentPagerAdapter {

    private final int PAGER_COUNT = 4;
    private FaceTopTen myFragment1 = null;
    private StudentRecommend myFragment2 = null;
    private Studenttalk myFragment3 = null;
    private StudentRecommend myFragment4 = null;


    public MyFragmentAdapter(FragmentManager fm) {
        super(fm);
        myFragment1 = new FaceTopTen();
        myFragment2 = new StudentRecommend();
        myFragment3 = new Studenttalk();
        myFragment4 = new StudentRecommend();
    }


    @Override
    public int getCount() {
        return PAGER_COUNT;
    }

    @Override
    public Object instantiateItem(ViewGroup vg, int position) {
        return super.instantiateItem(vg, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        System.out.println("position Destory" + position);
        super.destroyItem(container, position, object);
    }

    @Override
    public android.support.v4.app.Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case Main2Activity.PAGE_ONE:
                fragment = myFragment1;
                break;
            case Main2Activity.PAGE_TWO:
                fragment = myFragment2;
                break;
            case Main2Activity.PAGE_THREE:
                fragment = myFragment3;
                break;
            case Main2Activity.PAGE_FOUR:
                fragment = myFragment4;
                break;
        }
        return fragment;
    }
}
