package com.example.mac.opencvcard;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mac.opencvcard.activity.HttpThread;
import com.example.mac.opencvcard.activity.LastRigster;
import com.example.mac.opencvcard.activity.Main2Activity;
import com.example.mac.opencvcard.activity.UserData;

import java.util.HashMap;


/**
 * Created by Nguyen on 5/20/2016.
 */

public class MainActivity extends AppCompatActivity {


    EditText et_name, et_pass;
    private Button LoginButton, mRegister, forgetpass;
    private Button bt_username_clear;
    private Button bt_pwd_clear;
    private Button bt_pwd_eye;
    private Handler handle = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            String state =(String) bundle.get("state");
            if(state.equals("登陆成功")) {
                Intent intent=new Intent(MainActivity.this, Main2Activity.class);
                startActivity(intent);
                finish();
            }else{
                Toast.makeText(getApplicationContext(),state,Toast.LENGTH_LONG).show();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et_name = (EditText) findViewById(R.id.username);
        et_pass = (EditText) findViewById(R.id.password);
        bt_username_clear = (Button) findViewById(R.id.bt_username_clear);
        bt_pwd_clear = (Button) findViewById(R.id.bt_pwd_clear);
        bt_pwd_eye = (Button) findViewById(R.id.bt_pwd_eye);
        LoginButton = (Button) findViewById(R.id.login);
        mRegister = (Button) findViewById(R.id.register);
        forgetpass = (Button) findViewById(R.id.login_error);
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
