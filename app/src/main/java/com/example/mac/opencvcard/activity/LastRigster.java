package com.example.mac.opencvcard.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.mac.opencvcard.ImageManipulationsActivity;
import com.example.mac.opencvcard.MainActivity;
import com.example.mac.opencvcard.R;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import cn.bmob.sms.listener.RequestSMSCodeListener;
import cn.bmob.sms.listener.VerifySMSCodeListener;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;

import cn.bmob.sms.BmobSMS;


public class LastRigster extends AppCompatActivity {
    private static final int TIME_OUT = 10 * 1000; // 超时时间
    private static final String CHARSET = "utf-8";// 设置编码
    EditText textphone;
    EditText textnum;
    EditText textpass;
    EditText textpassagain;
    EditText textname;
    Boolean ImageBJ=false;
    ImageButton Image;
    Bitmap bitmaptoux;
    RadioGroup Rg;
    RadioButton Rb_man;
    RadioButton Rb_weman;
    String usersex="男";
    String tephone;
    String Facetoken;
    private static final String  TAG                 = "OCVSample::Activity";
    byte[] bis;
    private Handler handler1=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Intent intent = new Intent(LastRigster.this, ImageManipulationsActivity.class);
            intent.putExtra("name",textname.getText().toString());
            intent.putExtra("phonenum",textphone.getText().toString());
            intent.putExtra("password",textpass.getText().toString());
            intent.putExtra("image",bis);
            intent.putExtra("sex",usersex);
            startActivity(intent);
           /* Bundle bundle = msg.getData();
            String state =(String) bundle.get("state");
            if(state.equals("success")) {
                BmobSMS.initialize(LastRigster.this, "2391bb5081468d431831b7a382313383");
                if(textpass.getText().toString().equals(textpassagain.getText().toString())&&textpass.length()>=8) {
                    BmobSMS.verifySmsCode(getApplicationContext(), textphone.getText().toString(), textnum.getText().toString(), new VerifySMSCodeListener() {
                        @Override
                        public void done(cn.bmob.sms.exception.BmobException e) {
                            if (e == null) {//短信验证码已验证成功
                                if(textname.getText().toString().length()>1&&ImageBJ)
                                {

                                    //String FileName=saveImage(bitmaptoux,tephone);
                                    //MutliThread m1=new MutliThread(FileName);
                                    //m1.start();
                                    Toast.makeText(getApplicationContext(), "hahhaha", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(LastRigster.this, ImageManipulationsActivity.class);
                                    intent.putExtra("name",textname.getText().toString());
                                    intent.putExtra("phonenum",textphone.getText().toString());
                                    intent.putExtra("password",textpass.getText().toString());
                                    intent.putExtra("image",bis);
                                    intent.putExtra("sex",usersex);
                                    startActivity(intent);
                                }

                                //finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "验证失败：code =" + e.getErrorCode() + ",msg = " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            }
                        }

                    });
                }else{
                    Toast.makeText(getApplicationContext(), "密码不一致" , Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(getApplicationContext(),state,Toast.LENGTH_LONG).show();
            }*/
        }
    };
    private Handler handle = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            String state =(String) bundle.get("qk");
            if(state.equals("1")) {
                Facetoken=(String) bundle.get("facetoken");
                HashMap insertnmap = new HashMap();
                insertnmap.put("phonenum", tephone);
                insertnmap.put("facetoken", Facetoken);
                insertnmap.put("method", "insertfaceset.php");
                new HttpThread(handler1,insertnmap).start();

            }else{
                Toast.makeText(getApplicationContext(),"头像添加失败,请重新点击下一步",Toast.LENGTH_LONG).show();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_rigster);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        textphone=(EditText)findViewById(R.id.userphone);
        textnum=(EditText)findViewById(R.id.code);
        textpass=(EditText)findViewById(R.id.password);
        textpassagain=(EditText)findViewById(R.id.yzpassword);
        textname=(EditText)findViewById(R.id.name);
        Image=(ImageButton)findViewById(R.id.Imagetoux);
        Rg=(RadioGroup)findViewById(R.id.bt_sex);
        Rb_man=(RadioButton)findViewById(R.id.bt_man);
        Rb_weman=(RadioButton)findViewById(R.id.bt_weman);
        Rg.check(Rb_man.getId());
        Rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb=(RadioButton)LastRigster.this.findViewById(group.getCheckedRadioButtonId());
                usersex=rb.getText().toString();
            }
        });
        Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LastRigster.this,FaceDetectGrayActivity.class);
                ImageBJ=false;
                startActivityForResult(intent,1);
            }
        });
        BmobConfig config=new BmobConfig.Builder(this).setApplicationId("2391bb5081468d431831b7a382313383").build();
        Bmob.initialize(config);

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
    public void yzbutton(View view) {
        BmobSMS.initialize(LastRigster.this, "2391bb5081468d431831b7a382313383");
        if(textphone.getText().length()!=11)
        {
            return;
        }
        BmobSMS.requestSMSCode(LastRigster.this, textphone.getText().toString(), "senttest", new RequestSMSCodeListener() {
            @Override
            public void done(Integer integer, cn.bmob.sms.exception.BmobException e) {
                if(e==null){
                    Log.i("bmob", "短信id：" + integer);//用于查询本次短信发送详情
                    Toast.makeText(getApplicationContext(),"发送成功",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1){
            if(data.getByteArrayExtra("Imagetx")!=null)
            {
                bis=data.getByteArrayExtra("Imagetx");
                bitmaptoux= BitmapFactory.decodeByteArray(bis,0,bis.length);
                Image.setImageBitmap(bitmaptoux);
                ImageBJ=true;
                Log.e("hh", "3");
            }
            Log.e("hh","2");

        }else{
            Log.e("hh1",requestCode+"  "+resultCode);
        }
    }
    public static String saveImage(Bitmap bmp,String te) {
        File appDir = new File(Environment.getExternalStorageDirectory(), "ziliao");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss"); // 格式化时间
        String fileName = te + ".jpg";
        Log.e("success",fileName);
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileName;
    }
    public static String uploadFile(File file,String RequestURL) throws IOException
    {
        String result = null;
        String  BOUNDARY =  UUID.randomUUID().toString();  //边界标识   随机生成
        String PREFIX = "--" , LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data";   //内容类型

        try {
            URL url = new URL(RequestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true);  //允许输入流
            conn.setDoOutput(true); //允许输出流
            conn.setUseCaches(false);  //不允许使用缓存
            conn.setRequestMethod("POST");  //请求方式
            conn.setRequestProperty("Charset", CHARSET);  //设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);

            if(file!=null)
            {
                /**
                 * 当文件不为空，把文件包装并且上传
                 */
                DataOutputStream dos = new DataOutputStream( conn.getOutputStream());
                StringBuffer sb = new StringBuffer();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                /**
                 * 这里重点注意：
                 * name里面的值为服务器端需要key   只有这个key 才可以得到对应的文件
                 * filename是文件的名字，包含后缀名的   比如:abc.png
                 */

                sb.append("Content-Disposition: form-data; name=\"file\"; filename=\""+file.getName()+"\""+LINE_END);
                sb.append("Content-Type: application/octet-stream; charset="+CHARSET+LINE_END);
                sb.append(LINE_END);
                dos.write(sb.toString().getBytes());
                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while((len=is.read(bytes))!=-1)
                {
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX+BOUNDARY+PREFIX+LINE_END).getBytes();
                dos.write(end_data);
                dos.flush();
                /**
                 * 获取响应码  200=成功
                 * 当响应成功，获取响应的流
                 */
                int res = conn.getResponseCode();
                Log.e(TAG, "response code:"+res);
                if(res==200)
                {
                    Log.e(TAG, "request success");
                    InputStream input =  conn.getInputStream();
                    StringBuffer sb1= new StringBuffer();
                    int ss ;
                    while((ss=input.read())!=-1)
                    {
                        sb1.append((char)ss);
                    }
                    result = sb1.toString();
                    Log.e(TAG, "result : "+ result);
                }
                else{
                    Log.e(TAG, "request error");             }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    @Override
    protected void onStart() {
        super.onStart();
        if(bitmaptoux!=null){
            Image.setImageBitmap(bitmaptoux);
        }
    }
    public class MutliThread extends Thread {
        String URL;
        public MutliThread (){}
        public MutliThread (String url){
            //super(name);
            this.URL=url;
        }

        @Override
        public void run() {
            File file=new File(Environment.getExternalStorageDirectory()+ "/ziliao/"+URL);
            try {
                uploadFile(file,"http://115.159.188.113//phpCode//upload_file.php");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
    public static String saveImage(Bitmap bmp) {
        File appDir = new File(Environment.getExternalStorageDirectory(), "ziliao");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss"); // 格式化时间
        String fileName = format.format(date) + ".jpg";
        Log.e("success", fileName);
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileName;
    }
    public void bt_sure(View view) {
        tephone=textphone.getText().toString();
        String fm=saveImage(bitmaptoux);
        new HTTPThread1(fm,handle).start();

    }
}
