package com.example.mac.opencvcard.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.mac.opencvcard.R;

public class UserData extends AppCompatActivity {

    SharedPreferences sp;
    SharedPreferences.Editor ed;
    TextView Cname;
    TextView Cnike_name;
    TextView Csexage;
    TextView Cschool;
    TextView Ccollege;
    TextView Cxueli;
    TextView Chome;
    TextView Cphone;
    TextView Cnum;
    TextView Cqq;
    TextView Clike;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        sp=getSharedPreferences("person", MODE_WORLD_READABLE);
        ed=sp.edit();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(UserData.this,ChangeUserDate.class);
                startActivityForResult(intent, 17);
            }
        });
        chushi();
    }
    public void chushi(){
        Cname=(TextView)findViewById(R.id.username);
        Cname.setText(sp.getString("pname",""));
        Cnike_name=(TextView)findViewById(R.id.nick_name);
        Cnike_name.setText(sp.getString("pname",""));
        Csexage=(TextView)findViewById(R.id.agesex);
        Csexage.setText(sp.getString("psexage",""));
        Cschool=(TextView)findViewById(R.id.school);
        Cschool.setText(sp.getString("pschool",""));
        Ccollege=(TextView)findViewById(R.id.collage);
        Ccollege.setText(sp.getString("pcollage",""));
        Cxueli=(TextView)findViewById(R.id.grade);
        Cxueli.setText(sp.getString("pxueligrade",""));
        Chome=(TextView)findViewById(R.id.home);
        Chome.setText(sp.getString("pprovice", ""));
        Cphone=(TextView)findViewById(R.id.phonenum);
        Cphone.setText(sp.getString("pphonenum", ""));
        Cnum=(TextView)findViewById(R.id.num);
        Cnum.setText(sp.getString("pnumber", ""));
        Cqq=(TextView)findViewById(R.id.qqnum);
        Cqq.setText(sp.getString("pqq",""));
        Clike=(TextView)findViewById(R.id.likething);
        Clike.setText(sp.getString("plike", ""));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 17 && resultCode == 4) {
            chushi();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //onCreate(null);
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
    public void changeperson(View view) {
        Intent intent=new Intent(UserData.this,ChangeUserDate.class);
        startActivityForResult(intent,17);
    }
}
