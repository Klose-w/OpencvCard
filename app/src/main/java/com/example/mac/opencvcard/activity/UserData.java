package com.example.mac.opencvcard.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.util.AbStrUtil;
import com.example.mac.opencvcard.R;
import com.example.mac.opencvcard.activity.ui.CircleImageView;
import com.example.mac.opencvcard.utils.ImageUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Calendar;

public class UserData extends AppCompatActivity {

    SharedPreferences sp;
    SharedPreferences.Editor ed;
    CircleImageView circleImageView;
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
    RelativeLayout relativeLayout;
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
        relativeLayout=(RelativeLayout)findViewById(R.id.login_layout);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(UserData.this,ChangeUserDate.class);
                startActivityForResult(intent, 17);
            }
        });
        circleImageView=(CircleImageView)findViewById(R.id.head_img);
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImage();
            }
        });
        getBitmapFromSharedPreferences();
        chushi();
    }
    private void getBitmapFromSharedPreferences(){

        //第一步:取出字符串形式的Bitmap
        String imageString=sp.getString("image", "");
        if(!imageString.equals(""))
        {
            //第二步:利用Base64将字符串转换为ByteArrayInputStream
            byte[] byteArray= Base64.decode(imageString, Base64.DEFAULT);
            ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(byteArray);
            //第三步:利用ByteArrayInputStream生成Bitmap
            Bitmap bitmap= BitmapFactory.decodeStream(byteArrayInputStream);
            circleImageView.setImageBitmap(bitmap);
        }else{
            circleImageView.setImageResource(R.drawable.user_eye);
        }

       // relativeLayout.
    }

    private void saveBitmapToSharedPreferences(Bitmap bitmap){

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
    public void chushi(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        String a=sp.getString("page", "");
        int i=0;
        if(!a.equals(""))
        {
            String age=a.substring(0, 4);
            i=Integer.parseInt(age);
            i=year-i;
        }

        Cname=(TextView)findViewById(R.id.username);
        Cname.setText(sp.getString("pname",""));
        Cnike_name=(TextView)findViewById(R.id.nick_name);
        Cnike_name.setText(sp.getString("pname",""));
        Csexage=(TextView)findViewById(R.id.agesex);
        Csexage.setText(sp.getString("psexage","")+" "+i+"岁");
        Cschool=(TextView)findViewById(R.id.school);
        Cschool.setText(sp.getString("pschool",""));
        Ccollege=(TextView)findViewById(R.id.collage);
        Ccollege.setText(sp.getString("pcollage",""));
        Cxueli=(TextView)findViewById(R.id.grade);
        Cxueli.setText(sp.getString("pxueligrade","")+" "+sp.getString("pgrade","")+"级");
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
        Bitmap bitmap1;
        if (requestCode == 17 && resultCode == 4) {
            chushi();
        }else if(requestCode==1&&data!=null){
            Uri uri = data.getData();
            Log.e("url", uri.toString());
            String currentFilePath = getPath(uri);
            if(!AbStrUtil.isEmpty(currentFilePath)){
                Intent intent1 = new Intent(UserData.this, CropImageActivity.class);
                intent1.putExtra("PATH", currentFilePath);
                startActivityForResult(intent1, 11);
            }
        }
        else if(requestCode==11&&resultCode==14){
            String pathImg=data.getStringExtra("PATH");
            File file=new File(pathImg);
            if(file.exists()){
                Bitmap bi=BitmapFactory.decodeFile(pathImg);
                saveBitmapToSharedPreferences(bi);
                getBitmapFromSharedPreferences();
            }
        }
    }
    public String getPath(Uri uri) {
        if(AbStrUtil.isEmpty(uri.getAuthority())){
            return null;
        }
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        return path;
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


    public void getImage() {
        // Create intent to Open Image applications like Gallery, Google Photos
        try {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            // Start the Intent

            startActivityForResult(galleryIntent,1);
        } catch (ActivityNotFoundException i) {
            finish();
        }
    }
}
