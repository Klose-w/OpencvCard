package com.example.mac.opencvcard;


import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.media.session.IMediaControllerCallback;
import android.support.v4.widget.TextViewCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.green.model.v20161216.ImageDetectionRequest;
import com.aliyuncs.green.model.v20161216.ImageDetectionResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.example.mac.opencvcard.activity.HttpThread;
import com.example.mac.opencvcard.activity.LastRigster;

import org.opencv.core.Size;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.InvalidMarkException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.opencv.imgproc.Imgproc.MORPH_RECT;
import static org.opencv.imgproc.Imgproc.equalizeHist;
import static org.opencv.imgproc.Imgproc.erode;
import static org.opencv.imgproc.Imgproc.getStructuringElement;

public class ImageManipulationsActivity extends Activity implements CvCameraViewListener2 {
    private static final String  TAG                 = "OCVSample::Activity";


    private CameraBridgeViewBase mOpenCvCameraView;

    private Mat                  mRgb;
    private Mat                  mGray;
    private double    Cardheight;
    private double   Cardwidth;
    private double   wid;
    private double   hei;
    private Point   CardS;
    private Point   CardE;
    private Point   NameS;
    private Point   NameE;
    ImageView imageView;
    TextView textView;
    public String FileName;
    public String peopleName;
    private static final int TIME_OUT = 10 * 1000; // 超时时间
    private static final String CHARSET = "utf-8";// 设置编码
     String username;
     String usercol;
    String userphone;
    String userpass;
    String usersex;
    byte[] ima;
    Bitmap Imagetx;
    private String usernum="";
    String str1="";
    String[] collage={"信息工程学院","经济管理学院","植物保护学院","园艺学院","农学院","动物科技学院"
            ,"动物医学院","林学院","风景园林艺术学院","资源环境学院","水利与建筑工程学院"
            ,"机械与电子工程学院","食品科学与工程学院","葡萄酒学院","生命科学学院"
            ,"理学院","化学与药学院","人文社会发展学院","外语系","创新实验学院","国际学院"};
    final Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.arg1==123){
                //PickupCollage(str1);
                if(username==""||usercol==""||usernum=="")
                {
                    Toast.makeText(getApplicationContext(),"扫描不成功,请在光线好的地方重新扫描",Toast.LENGTH_LONG).show();
                }else {
                    dialog();
                }
            }
            else if(msg.getData()!=null)
            {
                if(msg.getData().getString("state").equals("success"))
                {
                    Toast.makeText(getApplicationContext(),"注册成功",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ImageManipulationsActivity.this, MainActivity.class);
                    intent.putExtra("phone", userphone);
                    intent.putExtra("pass", userpass);
                    startActivity(intent);
                    finish();
                }else
                {
                    Toast.makeText(getApplicationContext(),"注册失败",Toast.LENGTH_LONG).show();
                }

            }
        }
    };

    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public ImageManipulationsActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }
    public void dialog()
    {
        AlertDialog.Builder dia=new AlertDialog.Builder(ImageManipulationsActivity.this);
        dia.setTitle("确认你的信息");
        dia.setMessage("姓名：" + username + "\n" + "学院：" + usercol + "\n" + "学号：" + usernum);
        dia.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                signup();
            }
        });
        dia.setNegativeButton("重新识别", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dia.show();

    }
    public void signup() {
        Log.d(TAG, "Signup");

//        if (!validate()) {
//            onSignupFailed();
//            return;
//        }

        HashMap loginmap = new HashMap();
        loginmap.put("qq","");
        loginmap.put("city","");
        loginmap.put("province","");
        loginmap.put("degree","");
        loginmap.put("dob","");
        loginmap.put("name",username);
        loginmap.put("gender",usersex);
        loginmap.put("phonenum", userphone);
        loginmap.put("passwd", userpass);
        loginmap.put("college", usercol);
        loginmap.put("school", "西北农林科技大学");
        loginmap.put("sid",usernum);
        loginmap.put("method", "signup.php");
        // TODO: Implement your own signup logic here.
        new HttpThread(mHandler,loginmap).start();
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        wid=metric.widthPixels;
        hei=metric.heightPixels;
        CardSize(wid, hei);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_cardmain);
        username=getIntent().getStringExtra("name");
        userphone=getIntent().getStringExtra("phonenum");
        userpass=getIntent().getStringExtra("password");
        usersex=getIntent().getStringExtra("sex");
        ima=getIntent().getByteArrayExtra("image");
        Imagetx= BitmapFactory.decodeByteArray(ima, 0, ima.length);
        imageView=(ImageView)findViewById(R.id.Image01);
        textView=(TextView)findViewById(R.id.textname);
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.image_manipulations_activity_surface_view);
        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    public void CardSize(double hei, double wid) {
        Cardwidth=wid-260;
        Cardheight=Cardwidth*(8.5/5.2);
        CardS=new Point((hei-Cardheight)/2,130);
        CardE=new Point((hei-Cardheight)/2+Cardheight,130+Cardwidth);
        NameE=new Point((hei-Cardheight)/2+70,130+(1.6/5.2)*Cardwidth);
        NameS=new Point((hei-Cardheight)/2+Cardheight*(5/8.5),130+(4.2/5.2)*Cardwidth);
    }


    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }




    public void onCameraViewStarted(int width, int height) {
        mRgb= new Mat();
        mGray=new Mat();

    }

    public void onCameraViewStopped() {
        // Explicitly deallocate Mats
        mGray.release();
        mRgb.release();
    }
    //字符匹配
    public void PickupCollage(String string) {
        String str;
        str=string.substring(0,6);
        if(str.indexOf("系")>=0){usercol=collage[18];}
        else if(str.indexOf("农")>=0){usercol=collage[4];}
        else if(str.indexOf("技")>=0){usercol=collage[5];}
        else if(str.indexOf("医")>=0){usercol=collage[6];}
        else if(str.indexOf("机")>=0||str.indexOf("械")>=0||str.indexOf("电")>=0||str.indexOf("子")>=0){usercol=collage[11];}
        else if(str.indexOf("水")>=0||str.indexOf("利")>=0||str.indexOf("建")>=0||str.indexOf("筑")>=0){usercol=collage[10];}
        else if(str.indexOf("人")>=0||str.indexOf("文")>=0||str.indexOf("社")>=0||str.indexOf("会")>=0){usercol=collage[17];}
        else if(str.indexOf("风")>=0||str.indexOf("景")>=0||str.indexOf("术")>=0){usercol=collage[8];}
        else if(str.indexOf("信")>=0||str.indexOf("息")>=0){usercol=collage[0];}
        else if(str.indexOf("化")>=0||str.indexOf("药")>=0){usercol=collage[16];}
        else if(str.indexOf("生")>=0||str.indexOf("命")>=0){usercol=collage[14];}
        else if(str.indexOf("葡")>=0||str.indexOf("萄")>=0){usercol=collage[13];}
        else if(str.indexOf("食")>=0||str.indexOf("品")>=0){usercol=collage[12];}
        else if(str.indexOf("创")>=0||str.indexOf("新")>=0){usercol=collage[19];}
        else if(str.indexOf("国")>=0||str.indexOf("际")>=0){usercol=collage[20];}
        else if(str.indexOf("园")>=0){usercol=collage[3];}
        else if(str.indexOf("经")>=0||str.indexOf("济")>=0||str.indexOf("管")>=0){usercol=collage[1];}
        else if(str.indexOf("理")>=0){usercol=collage[15];}
        else if(str.indexOf("林")>=0){usercol=collage[7];}
        else if(str.indexOf("植")>=0||str.indexOf("保")>=0||str.indexOf("护")>=0){usercol=collage[2];}
        else if(str.indexOf("资")>=0||str.indexOf("源")>=0||str.indexOf("环")>=0||str.indexOf("境")>=0){usercol=collage[9];}
        else{usercol="";}
        String str2;
        int dex=string.indexOf("名");
        Log.e("str2",dex+"");
        str2=string.substring(dex);
        if(dex==-1)
        {
            return;
        }
        if((str2.indexOf(username.charAt(0))>=0&&str2.indexOf(username.charAt(1))>=0)){

        }else{
            username="";
        }
        String regEx="[^0-9]";
        // 编译正则表达式
        Pattern pattern = Pattern.compile(regEx);
        // 忽略大小写的写法
        // Pattern pat = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
        Matcher m = pattern.matcher(str2);
        // 字符串是否与正则表达式相匹配
      if(m.replaceAll("").trim().length()==10){
          usernum=m.replaceAll("").trim();
      }
    }
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        mRgb = inputFrame.rgba();
        mGray=inputFrame.gray();
        Imgproc.rectangle(mRgb, CardS,CardE, new Scalar(255,0,0,255),3,0,0);
        Imgproc.rectangle(mRgb, NameS,NameE, new Scalar(255,0,0,255),3,0,0);
        Imgproc.rectangle(mRgb, new Point(0,0), new Point(wid,(hei-Cardwidth)/2), new Scalar(77,77,77,100),-1,0,0);
        Imgproc.rectangle(mRgb, new Point(0,0), new Point((wid-Cardheight)/2,hei), new Scalar(77,77,77,100),-1,0,0);
        Imgproc.rectangle(mRgb, new Point(wid-(wid-Cardheight)/2,0), new Point(wid,hei), new Scalar(77,77,77,100),-1,0,0);
        Imgproc.rectangle(mRgb, new Point(0,hei-130), new Point(wid,hei), new Scalar(77,77,77,100),-1,0,0);

        return mRgb;
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

    public void sureClick(View view) {
        textView.setText("");
        Mat Image;
        Rect rect=new Rect(NameS,NameE);
        Image=new Mat(mGray,rect);
        Mat cImage=Image.clone();
        //equalizeHist(Image,cImage);直方图均衡化
        //Imgproc.threshold(Image, cImage, 85, 255, Imgproc.THRESH_BINARY);二值化
        //Mat element = getStructuringElement(MORPH_RECT, new Size(3, 3));
        //erode(Image, cImage, element);
       // Imgproc.threshold(cImage, cImage, 150, 255, Imgproc.THRESH_BINARY_INV);
        Bitmap bmp = null;
        /*for(int i=0;i<cImage.rows();i++)
        {
            for(int k=0;k<cImage.cols();k++)
            {
                if(Math.abs(cImage.get(i,k)[0]-cImage.get(i,k)[1])<60&&Math.abs(cImage.get(i,k)[0]-cImage.get(i,k)[2])<60&&Math.abs(cImage.get(i,k)[1]-cImage.get(i,k)[2])<60){

                    cImage.put(i,k,data);
                }

            }
        }*/
        bmp = Bitmap.createBitmap(Image.cols(), Image.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(cImage, bmp);
        FileName=saveImage(bmp);
        imageView.setImageBitmap(bmp);
        MutliThread m1=new MutliThread(bmp);
        m1.start();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == 4) {
            usercol=data.getStringExtra("usercol");
            usernum=data.getStringExtra("usernum");
            signup();
        }
    }

    public void open(View view) {
        Intent intent=new Intent(ImageManipulationsActivity.this,NamePut.class);
        startActivityForResult(intent,2);
    }

    public class MutliThread extends Thread {
        Bitmap bitmap;
        public MutliThread (){}
        public MutliThread (Bitmap bitmap){
            //super(name);
            this.bitmap=bitmap;
        }

        @Override
        public void run() {
            try {
                ImageCheak(bitmap);
            } catch (MalformedURLException e) {
                e.printStackTrace();
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
    public void ImageCheak(Bitmap bitmap) throws MalformedURLException {

        URL url = new URL("http://115.159.188.113//upload.php");
        File file=new File(Environment.getExternalStorageDirectory()+ "/ziliao/"+FileName);
        try {
            uploadFile(file,"http://115.159.188.113//phpCode//upload_file.php");
        } catch (IOException e) {
            e.printStackTrace();
        }
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou",  "LTAIOPCvvW7005VC", "y7eqVUBgSvRbLJXMkDATHycR7gGmDd");
        IAcsClient client = new DefaultAcsClient(profile);
        ImageDetectionRequest imageDetectionRequest = new ImageDetectionRequest();
        /**
         * 是否同步调用
         * false: 同步
         */

        imageDetectionRequest.setAsync(false);
        /**
         * 同步图片检测支持多个场景, 但不建议一次设置太多场景:
         * porn:  黄图检测
         * ocr:  ocr文字识别
         * illegal: 暴恐敏感检测
         * ad: 图片广告
         * sensitiveFace: 指定人脸
         * qrcode: 二维码
         */
        imageDetectionRequest.setScenes(Arrays.asList("ocr"));
        imageDetectionRequest.setConnectTimeout(4000);
        imageDetectionRequest.setReadTimeout(4000);
        /**
         * 同步图片检测一次只支持单张图片进行检测
         */https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1491203013123&di=4e5d14cb355afcbbc88622648c4167c7&imgtype=0&src=http%3A%2F%2Fcdn.duitang.com%2Fuploads%2Fitem%2F201205%2F26%2F20120526114519_JQGFS.thumb.600_0.jpeg
        imageDetectionRequest.setImageUrls(Arrays.asList("http://115.159.188.113/phpCode/upload/"+FileName));
        try {
            ImageDetectionResponse imageDetectionResponse = client.getAcsResponse(imageDetectionRequest);
            //System.out.println(JSON.toJSONString(imageDetectionResponse));
            if("Success".equals(imageDetectionResponse.getCode())){
                List<ImageDetectionResponse.ImageResult> imageResults = imageDetectionResponse.getImageResults();
                if(imageResults != null && imageResults.size() > 0) {
                    //同步图片检测只有一个返回的ImageResult
                    ImageDetectionResponse.ImageResult imageResult = imageResults.get(0);
                    //porn场景对应的检测结果放在pornResult字段中
                    //ocr场景对应的检测结果放在ocrResult字段中

                    str1="";
                    ImageDetectionResponse.ImageResult.OcrResult ocrResult = imageResult.getOcrResult();
                    if(ocrResult !=  null) {
                        List<String> texts=  ocrResult.getText();
                        Log.e("lengh",texts.size()+"");
                        if(texts != null && texts.size() > 0){
                            for (String text : texts) {
                                System.out.println(text);
                                str1=str1+"  "+text;
                            }
                        }
                    }
                }
            }else{
                /**
                 * 检测失败
                 */
            }
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
        if(str1!=null){
        PickupCollage(str1);}
        else {
            Toast.makeText(getApplicationContext(),"扫描不成功,请在光线好的地方重新扫描",Toast.LENGTH_LONG).show();
        }
        Message message = new Message();
        message.arg1 = 123;
        mHandler.sendMessage(message);
    }
}

