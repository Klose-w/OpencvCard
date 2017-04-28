package com.example.mac.opencvcard;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mac.opencvcard.Fragment.FaceTopTen;
import com.example.mac.opencvcard.Fragment.StudentRecommend;
import com.example.mac.opencvcard.Fragment.Studenttalk;
import com.example.mac.opencvcard.activity.LastRigster;
import com.example.mac.opencvcard.model.spacetablayout.SpaceTabLayout;

import java.util.ArrayList;
import java.util.List;

public class NamePut extends AppCompatActivity {

    EditText textViewcol;
    EditText textViewnum;
    SpaceTabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_put);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        textViewcol=(EditText)findViewById(R.id.usercoll);
        textViewnum=(EditText)findViewById(R.id.numPhone);

    }


    //we need the outState to memorize the position
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        tabLayout.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return false;
        }
    }

    public void exit_b(View view) {
        Intent intent=new Intent();
        intent.putExtra("usercol",textViewcol.getText().toString());
        intent.putExtra("usernum",textViewnum.getText().toString());
        setResult(4,intent);
        finish();
    }
}
