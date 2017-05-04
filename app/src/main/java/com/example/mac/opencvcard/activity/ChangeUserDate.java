package com.example.mac.opencvcard.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.mac.opencvcard.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangeUserDate extends AppCompatActivity {

    RadioGroup Zrg;
    EditText Zhome;
    EditText Zqq;
    EditText Zlike;
    com.ab.view.wheel.AbWheelView Zgrade;
    com.ab.view.wheel.AbWheelView Zage;
    String xueli;
    String age;
    String grade;
    String home;
    String qq;
    String like;
    SharedPreferences sp;
    SharedPreferences.Editor ed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user_date);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        Zrg=(RadioGroup)findViewById(R.id.zi_xw);
        Zrg.check(R.id.bt_bk);
        Zrg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) ChangeUserDate.this.findViewById(group.getCheckedRadioButtonId());
                xueli = rb.getText().toString();
            }
        });
        Zhome=(EditText)findViewById(R.id.zi_home);
        Zage=(com.ab.view.wheel.AbWheelView)findViewById(R.id.zi_age);
        Zgrade=(com.ab.view.wheel.AbWheelView)findViewById(R.id.zi_grade);
        Zqq=(EditText)findViewById(R.id.zi_qq);
        Zlike=(EditText)findViewById(R.id.zi_like);
        sp=getSharedPreferences("person",MODE_WORLD_READABLE);
        ed=sp.edit();
        chushi();
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
    public void chushi(){
        Zqq.setText(sp.getString("pqq",""));
        Zlike.setText(sp.getString("plike",""));
        Zhome.setText(sp.getString("pprovice",""));
    }
    public void changeDate() {
    }
    public void wancheng(View view) {
        ed.putString("pxueligrade",xueli);
        ed.putString("pqq",Zqq.getText().toString());
        ed.putString("plike",Zlike.getText().toString());
        ed.putString("pprovice",Zhome.getText().toString());
        ed.commit();
        Intent intent=new Intent();
        setResult(4, intent);
        finish();
    }
}
