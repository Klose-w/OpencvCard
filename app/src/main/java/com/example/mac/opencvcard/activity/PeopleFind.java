package com.example.mac.opencvcard.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mac.opencvcard.R;
import com.example.mac.opencvcard.activity.ui.FaceView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PeopleFind extends AppCompatActivity {

    private String phonenum;
    private String fpname="1";
    private String fpage;
    private String fpgrade;
    private String fpcol;
    private String fpqq;
    TextView fname;
    TextView fage;
    TextView fgrade;
    TextView fcol;
    TextView fqq;
    RequestQueue mQueue;
    private Handler handler1 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.arg1==177){

            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_find);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        phonenum=getIntent().getStringExtra("numphone");
        mQueue = Volley.newRequestQueue(getApplicationContext());
        fname=(TextView)findViewById(R.id.fp_name);
        fage=(TextView)findViewById(R.id.fp_age);
        fgrade=(TextView)findViewById(R.id.fp_grade);
        fcol=(TextView)findViewById(R.id.fp_col);
        fqq=(TextView)findViewById(R.id.fp_qq);
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

        Map<String, String> map = new HashMap<String, String>();
        map.put("phonenum", phonenum);
        JSONObject jsonObject=new JSONObject(map);
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST,"http://115.159.188.113/CaoCao/finduser.php",jsonObject,new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e("hhhh", jsonObject.toString());
                try {
                    JSONObject jsonObject2 =new JSONObject(jsonObject.toString());
                    fpname=jsonObject2.getString("name");
                    fpage=jsonObject2.getString("gender")+jsonObject2.get("dob");
                    fpcol=jsonObject2.getString("college");
                    fpgrade=jsonObject2.getString("degree");
                    fpqq=jsonObject2.getString("qq");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                fname.setText(fpname);
                fage.setText(fpage);
                fgrade.setText(fpgrade);
                fcol.setText(fpcol);
                fqq.setText(fpqq);

            }
        },new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        }){

        };
        mQueue.add(jsonObjectRequest);

    }
    public void zhiliao_bt(View view) {
        finish();
    }
}
