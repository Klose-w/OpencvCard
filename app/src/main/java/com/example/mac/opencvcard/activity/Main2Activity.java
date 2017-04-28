package com.example.mac.opencvcard.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.mac.opencvcard.Fragment.CamearFragment;
import com.example.mac.opencvcard.Fragment.FaceTopTen;
import com.example.mac.opencvcard.Fragment.StudentRecommend;
import com.example.mac.opencvcard.Fragment.Studenttalk;
import com.example.mac.opencvcard.R;
import com.example.mac.opencvcard.adapter.MyFragmentAdapter;
import com.example.mac.opencvcard.model.spacetablayout.SpaceTabLayout;

import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {



        private long exitTime = 0;
        private MyFragmentAdapter mAdapter;

        //鍑犱釜浠ｈ〃椤甸潰鐨勫父閲?
        public static final int PAGE_ONE = 0;
        public static final int PAGE_TWO = 1;
        public static final int PAGE_THREE = 2;
        public static final int PAGE_FOUR = 3;
        SpaceTabLayout tabLayout;
        @Override
        protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
            mAdapter = new MyFragmentAdapter(getSupportFragmentManager());
            List<Fragment> fragmentList = new ArrayList<>();
            fragmentList.add(new FaceTopTen());
            fragmentList.add(new StudentRecommend());
            fragmentList.add(new CamearFragment());
            fragmentList.add(new  Studenttalk());
            fragmentList.add(new FaceTopTen());

            ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
            tabLayout = (SpaceTabLayout) findViewById(R.id.spaceTabLayout);

            //we need the savedInstanceState to retrieve the position
            tabLayout.initialize(viewPager, getSupportFragmentManager(), fragmentList, savedInstanceState);
        }


    //we need the outState to memorize the position
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        tabLayout.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    //閲嶅啓ViewPager椤甸潰鍒囨崲鐨勫鐞嗘柟娉?

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Snackbar.make(getWindow().getDecorView(),"退出",Snackbar.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }







        @Override
        public boolean onNavigationItemSelected (MenuItem item){
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camara) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
            Intent intent = new Intent(Main2Activity.this, UserData.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    }
