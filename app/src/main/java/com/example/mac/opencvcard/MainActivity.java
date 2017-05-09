package com.example.mac.opencvcard;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.example.mac.opencvcard.activity.HttpThread;
import com.example.mac.opencvcard.activity.LastRigster;
import com.example.mac.opencvcard.activity.Main2Activity;
import com.example.mac.opencvcard.activity.UserData;
import com.google.gson.JsonObject;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Nguyen on 5/20/2016.
 */

public class MainActivity extends AppCompatActivity {

    SharedPreferences sp;
    SharedPreferences.Editor ed;
    EditText et_name, et_pass;
    private Button LoginButton, mRegister, forgetpass;
    private Button bt_username_clear;
    private Button bt_pwd_clear;
    private Button bt_pwd_eye;
    String perphonenum;
    String pername;
    String persexgrade;
    String perschool;
    String percollege;
    String perxueli;
    String perprovince;
    String percity;
    String pernum;
    String perqq;
    RequestQueue mQueue;
    private Handler handle = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            String state =(String) bundle.get("state");
            if(state.equals("success")) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("phonenum", et_name.getText().toString());
                JSONObject jsonObject=new JSONObject(map);
                JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST,"http://115.159.188.113/CaoCao/finduser.php",jsonObject,new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.e("hhhh", jsonObject.toString());
                        try {
                            JSONObject jsonObject2 =new JSONObject(jsonObject.toString());
                            perphonenum=jsonObject2.getString("phonenum");
                            pername=jsonObject2.getString("name");
                            persexgrade=jsonObject2.getString("gender")+jsonObject2.get("dob");
                            perschool=jsonObject2.getString("school");
                            percollege=jsonObject2.getString("college");
                            perxueli=jsonObject2.getString("degree");
                            perprovince=jsonObject2.getString("province");
                            percity=jsonObject2.getString("city");
                            pernum=jsonObject2.getString("sid");
                            perqq=jsonObject2.getString("qq");
                            ed.putString("pname",pername);
                            ed.putString("psexage",persexgrade);
                            ed.putString("pschool",perschool);
                            ed.putString("pcollage",percollege);
                            ed.putString("pxueligrade",perxueli);
                            ed.putString("pprovice",perprovince);
                            ed.putString("pnumber",pernum);
                            ed.putString("pphonenum",perphonenum);
                            ed.putString("pqq",perqq);
                            ed.putString("plogin","是");
                            ed.commit();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },new Response.ErrorListener(){

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TAG", error.getMessage(), error);
                    }
                }){

                };
                mQueue.add(jsonObjectRequest);
                Intent intent=new Intent(MainActivity.this, Main2Activity.class);
                startActivity(intent);
                finish();
            }else{
                Toast.makeText(getApplicationContext(),state,Toast.LENGTH_LONG).show();
            }
        }
    };
    private void saveBitmapToSharedPreferences(){
        Bitmap bitmap= BitmapFactory.decodeResource(getResources(), R.drawable.penple2);
        //第一步:将Bitmap压缩至字节数组输出流ByteArrayOutputStream
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        //第二步:利用Base64将字节数组输出流中的数据转换成字符串String
        byte[] byteArray=byteArrayOutputStream.toByteArray();
        String imageString=new String(Base64.encodeToString(byteArray, Base64.DEFAULT));
        //第三步:将String保持至SharedPreferences
        ed.putString("image", imageString);
        ed.commit();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mQueue = Volley.newRequestQueue(getApplicationContext());
        et_name = (EditText) findViewById(R.id.username);
        et_pass = (EditText) findViewById(R.id.password);
        bt_username_clear = (Button) findViewById(R.id.bt_username_clear);
        bt_pwd_clear = (Button) findViewById(R.id.bt_pwd_clear);
        bt_pwd_eye = (Button) findViewById(R.id.bt_pwd_eye);
        LoginButton = (Button) findViewById(R.id.login);
        mRegister = (Button) findViewById(R.id.register);
        forgetpass = (Button) findViewById(R.id.login_error);
        sp=getSharedPreferences("person",MODE_WORLD_READABLE);
        ed=sp.edit();
       // saveBitmapToSharedPreferences();
        if(sp.getString("plogin","").equals("是")) {
            Intent intent=new Intent(MainActivity.this, Main2Activity.class);
            startActivity(intent);
            finish();
        }
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,LastRigster.class);
                startActivity(intent);
            }
        });
        et_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                {
                    et_name.setBackgroundColor(Color.parseColor("#aa0000"));
                }else{
                    et_name.setBackgroundColor(Color.parseColor("#ffffff"));
                }
            }
        });
        et_pass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                {
                    et_pass.setBackgroundColor(Color.parseColor("#aa0000"));
                }else{
                    et_pass.setBackgroundColor(Color.parseColor("#ffffff"));
                }
            }
        });
        et_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    bt_username_clear.setVisibility(View.VISIBLE);
                } else {
                    bt_username_clear.setVisibility(View.INVISIBLE);
                }
            }
        });
        et_pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    bt_pwd_clear.setVisibility(View.VISIBLE);
                } else {
                    bt_pwd_clear.setVisibility(View.INVISIBLE);
                }
            }
        });
        bt_pwd_eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_pass.getInputType() == (InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD)){
                    et_pass.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_NORMAL);
                }else{
                    et_pass.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });
        bt_username_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_name.setText("");
            }
        });
        bt_pwd_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_pass.setText("");
            }
        });

    }
    public void login() {
        ;
        HashMap loginmap = new HashMap();
        loginmap.put("phonenum", et_name.getText().toString());
        loginmap.put("passwd", et_pass.getText().toString());
        loginmap.put("method", "login.php");

        new HttpThread(handle,loginmap).start();

    }
    @Override
    protected void onResume() {
        super.onResume();
        if(getIntent()!=null)
        {
            et_name.setText(getIntent().getStringExtra("phone"));
            et_pass.setText(getIntent().getStringExtra("pass"));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(getIntent()!=null)
        {
            et_name.setText(getIntent().getStringExtra("phone"));
            et_pass.setText(getIntent().getStringExtra("pass"));
        }
    }

    public void loginbt(View view) {

      login();
    }
}
