package com.example.mac.opencvcard.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.media.FaceDetector;
import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mac.opencvcard.MainActivity;
import com.example.mac.opencvcard.R;
import com.example.mac.opencvcard.activity.HTTPThread1;
import com.example.mac.opencvcard.activity.HTTPThread2;
import com.example.mac.opencvcard.activity.HttpThread;
import com.example.mac.opencvcard.activity.Main2Activity;
import com.example.mac.opencvcard.activity.PeopleFind;
import com.example.mac.opencvcard.activity.ui.FaceOverlayView;
import com.example.mac.opencvcard.activity.ui.FaceView;
import com.example.mac.opencvcard.adapter.ImagePreviewAdapter;
import com.example.mac.opencvcard.model.FaceResult;
import com.example.mac.opencvcard.utils.CameraErrorCallback;
import com.example.mac.opencvcard.utils.ImageUtils;
import com.example.mac.opencvcard.utils.Util;
import com.tuyenmonkey.mkloader.MKLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class CamearFragment extends Fragment implements SurfaceHolder.Callback, Camera.PreviewCallback {
    private static final int PICK_IMAGE_REQUEST =11 ;
    private int numberOfCameras;
    Bitmap Faceload=null;
    int num=0;
    public static final String TAG = "hh";
    boolean paizhao=false;
    private Camera mCamera;
    private int cameraId = 0;
    Bitmap bmp;
    // Let's keep track of the display rotation and orientation also:
    private int mDisplayRotation;
    private int mDisplayOrientation;

    private int previewWidth;
    private int previewHeight;
    private static final int TIME_OUT = 10 * 1000; // 超时时间
    private static final String CHARSET = "utf-8";// 设置编码
    // The surface view for the camera data
    private SurfaceView mView;
    private Bitmap faceCroped = null;
    // Draw rectangles and other fancy stuff:
    ArrayList<FaceResult> faces_;

    // Log all errors:
    private final CameraErrorCallback mErrorCallback = new CameraErrorCallback();
    Bitmap cropedFace;
    String FileName;
    private static final int MAX_FACE = 10;
    private boolean isThreadWorking = false;
    private int prevSettingWidth;
    private int prevSettingHeight;
    private android.media.FaceDetector fdet;

    private byte[] grayBuff;
    private int bufflen;
    private int[] rgbs;
    private FaceView faceView;
    private FaceResult faces[];
    private FaceResult faces_previous[];
    private int Id = 0;

    private String BUNDLE_CAMERA_ID = "camera";
    private String path= Environment.getExternalStorageDirectory()+"/DCIM/uy.jpg";
    private FaceDetectThread detectThread = null;
    //RecylerView face image
    private HashMap<Integer, Integer> facesCount = new HashMap<>();
    private RecyclerView recyclerView;
    private ImagePreviewAdapter imagePreviewAdapter;
    private ArrayList<Bitmap> facesBitmap;

    ImageView backCam,Buxc;
    View view;
    ImageView bt;
    ImageView Imagepz;
    String numshu;
    SurfaceHolder holder;
    Bitmap bitmap;
    RequestQueue requestQueue;
    String Facetoken;
    ProgressDialog progressDialog;
    String nump;
    RequestQueue mQueue;
    private Handler handler1 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==0x456){
                progressDialog.dismiss();
                progressDialog.dismiss();
                dialog();

            }else if(msg.arg1==117){
                if (cropedFace != null) {
                    imagePreviewAdapter.add(cropedFace);
                }
                if(num!=0) {
                    Toast.makeText(getActivity().getApplicationContext(), "共获得" + num + "张人脸,请选择你要识别的一张", Toast.LENGTH_LONG).show();
                }else if(num==0){
                    Toast.makeText(getActivity().getApplicationContext(), "图中没有人脸，你可以在相册中将图片放大，截图后尝试再次识别", Toast.LENGTH_LONG).show();
                }
                FaceView overlay = (FaceView)faceView.findViewById(R.id.faceView);
                overlay.setContent(bitmap, faces_);
                progressDialog.dismiss();

            }
        }
    };
    final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.arg1==123){
                if (bmp != null){
                    progressDialog.dismiss();
                    detectFace(bmp);

                }
                else {
                    Toast.makeText(getActivity().getApplicationContext(), "Cann't open this image.", Toast.LENGTH_LONG).show();
                }
            }else{
                super.handleMessage(msg);
                Bundle bundle = msg.getData();
                numshu =(String) bundle.get("numshu");
                 Facetoken =(String) bundle.get("facetoken");
               // Log.e("pipei",Facetoken);
                if(Facetoken!=null) {
                    new Thread(){
                        @Override
                        public void run() {
                            try {
                                URL url=new URL("http://115.159.188.113/phpCode/upload/"+Facetoken+".jpg");
                                InputStream is=url.openStream();
                                bitmap=BitmapFactory.decodeStream(is);
                                handler1.sendEmptyMessage(0x456);
                                is.close();
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }else{
                    Toast.makeText(getActivity().getApplicationContext(),"没有找到相关人员，你可以尝试重新拍摄",Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                    progressDialog.dismiss();
                }

            }
        }
    };
    public CamearFragment() {
        // Required empty public constructor
    }
    public void dialog()
    {
        mQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        ImageView view=new ImageView(getActivity());
        view.setImageBitmap(bitmap);
        AlertDialog.Builder dia=new AlertDialog.Builder(getActivity());
        dia.setTitle("是他吗？");
        dia.setMessage("匹配率最高，为" + numshu);
        dia.setView(view);
        dia.setPositiveButton("是他", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("facetoken", Facetoken);
                JSONObject jsonObject=new JSONObject(map);
                JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST,"http://115.159.188.113/CaoCao/findface.php",jsonObject,new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.e("hhhh", jsonObject.toString());
                        try {
                            JSONObject jsonObject2 =new JSONObject(jsonObject.toString());
                            nump=jsonObject2.getString("phonenum");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Intent intent=new Intent(getActivity(), PeopleFind.class);
                        intent.putExtra("numphone",nump);
                        startActivity(intent);
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
        });
        dia.setNegativeButton("不是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dia.show();

    }
    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG, "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(TAG, "onDestroyView");
        ((ViewGroup)view.getParent()).removeView(view);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e(TAG, "onActivity");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.e(TAG, "onCreareView");

        //getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        if(view==null) {
            view = inflater.inflate(R.layout.fragment_camear, container, false);
            mView = (SurfaceView) view.findViewById(R.id.surfaceview1);
            faceView = (FaceView) view.findViewById(R.id.faceView);
            recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
            requestQueue= Volley.newRequestQueue(getActivity().getApplicationContext());
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            Buxc=(ImageView)view.findViewById(R.id.backCamera);
            Buxc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getImage();
                }
            });
            backCam=(ImageView)view.findViewById(R.id.buttonxc);
            backCam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cameraId = (cameraId + 1) % 2;
                    getActivity().recreate();
                }
            });
            bt = (ImageView) view.findViewById(R.id.camerapz);
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    paizhao = true;
                    progressDialog = ProgressDialog.show(getActivity(), "getting", "Please wait...", true, false);
                    //Toast.makeText(getActivity().getApplicationContext(),"正在识别，请稍后",Toast.LENGTH_LONG).show();
                    //perss.setVisibility(View.VISIBLE);
                }
            });

            holder = mView.getHolder();
            holder.addCallback(this);
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        // Now create the OverlayView:

        // Create and Start the OrientationListener:
        if (savedInstanceState != null)
            cameraId = savedInstanceState.getInt(BUNDLE_CAMERA_ID, 0);
        return view;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap;
        if (requestCode == PICK_IMAGE_REQUEST && data.getData() != null) {
            //Toast.makeText(this, "hahha1111", Toast.LENGTH_LONG).show();
            Uri uri = data.getData();

            bitmap = ImageUtils.getBitmap(ImageUtils.getRealPathFromURI(getActivity(), uri), 2048, 1232);
            if (bitmap != null){
                detectFace(bitmap);
            }

            else
                Toast.makeText(getActivity().getApplicationContext(), "Cann't open this image.", Toast.LENGTH_LONG).show();
        }
    }
    public void getImage() {
        // Create intent to Open Image applications like Gallery, Google Photos
        try {
            //perss.setVisibility(View.VISIBLE);
            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            // Start the Intent

            startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
        } catch (ActivityNotFoundException i) {

            getActivity().finish();
        }
    }
    /**
     * Restarts the camera.
     */
    @Override
    public void onResume() {
        super.onResume();

        Log.e(TAG, "onResume");
        startPreview();
    }

    /**
     * Stops the camera.
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }

    /**
     * Releases the resources associated with the camera source, the associated detector, and the
     * rest of the processing pipeline.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BUNDLE_CAMERA_ID, cameraId);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        //Find the total number of cameras available


        numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                if (cameraId == 0) cameraId = i;
            }
        }

        mCamera = Camera.open(cameraId);

        Camera.getCameraInfo(cameraId, cameraInfo);
        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {

        }

        try {
            mCamera.setPreviewDisplay(mView.getHolder());
        } catch (Exception e) {
            Log.e(TAG, "Could not preview the image.", e);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        // We have no surface, return immediately:

        if (surfaceHolder.getSurface() == null) {
            return;
        }
        // Try to stop the current preview:
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // Ignore...
        }

        configureCamera(width, height);
        setDisplayOrientation();
        setErrorCallback();

        // Create media.FaceDetector
        float aspect = (float) previewHeight / (float) previewWidth;
        fdet = new android.media.FaceDetector(prevSettingWidth, (int) (prevSettingWidth * aspect), MAX_FACE);
        Log.e("1", previewHeight +"  "+ previewWidth);
        bufflen = previewWidth * previewHeight;
        grayBuff = new byte[bufflen];
        rgbs = new int[bufflen];

        // Everything is configured! Finally start the camera preview again:
        startPreview();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void setErrorCallback() {
        mCamera.setErrorCallback(mErrorCallback);
    }

    private void setDisplayOrientation() {
        // Now set the display orientation:
        mDisplayRotation = Util.getDisplayRotation(getActivity());
        mDisplayOrientation = Util.getDisplayOrientation(mDisplayRotation, cameraId);

        mCamera.setDisplayOrientation(mDisplayOrientation);


    }

    private void configureCamera(int width, int height) {
        Camera.Parameters parameters = mCamera.getParameters();
        // Set the PreviewSize and AutoFocus:
        setOptimalPreviewSize(parameters, width, height);
        setAutoFocus(parameters);
        // And set the parameters:
        mCamera.setParameters(parameters);
    }

    private void setOptimalPreviewSize(Camera.Parameters cameraParameters, int width, int height) {
        List<Camera.Size> previewSizes = cameraParameters.getSupportedPreviewSizes();
        float targetRatio = (float) width / height;
        Camera.Size previewSize = Util.getOptimalPreviewSize(getActivity(), previewSizes, targetRatio);
        previewWidth = previewSize.width;
        previewHeight = previewSize.height;

        Log.e(TAG, "previewWidth" + previewWidth);
        Log.e(TAG, "previewHeight" + previewHeight);

        /**
         * Calculate size to scale full frame bitmap to smaller bitmap
         * Detect face in scaled bitmap have high performance than full bitmap.
         * The smaller image size -> detect faster, but distance to detect face shorter,
         * so calculate the size follow your purpose
         */
        if (previewWidth / 4 >=360) {
            prevSettingWidth = 360;
            prevSettingHeight = 270;
        } else if (previewWidth / 4 > 320) {
            prevSettingWidth = 320;
            prevSettingHeight = 240;
        } else if (previewWidth / 4 > 240) {
            prevSettingWidth = 240;
            prevSettingHeight = 160;
        } else {
            prevSettingWidth = 160;
            prevSettingHeight = 120;
        }

        cameraParameters.setPreviewSize(previewSize.width, previewSize.height);


    }

    private void setAutoFocus(Camera.Parameters cameraParameters) {
        List<String> focusModes = cameraParameters.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE))
            cameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
    }

    private void startPreview() {
        if (mCamera != null) {
            isThreadWorking = false;
            mCamera.startPreview();
            mCamera.setPreviewCallback(this);
            counter = 0;
        }
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mCamera.setPreviewCallbackWithBuffer(null);
        mCamera.setErrorCallback(null);
        mCamera.release();
        mCamera = null;
    }


    // fps detect face (not FPS of camera)
    long start, end;
    int counter = 0;
    double fps;

    @Override
    public void onPreviewFrame(byte[] _data, Camera _camera) {
        if (counter == 0)
            start = System.currentTimeMillis();

        isThreadWorking = true;
        if(paizhao==true) {
            paizhao=false;
            waitForFdetThreadComplete();
            detectThread = new FaceDetectThread(handler, getActivity());
            detectThread.setData(_data);
            detectThread.start();
        }
    }

    private void waitForFdetThreadComplete() {
        if (detectThread == null) {
            return;
        }

        if (detectThread.isAlive()) {
            try {
                detectThread.join();
                detectThread = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private class FaceDetectThread extends Thread {
        private Handler handler;
        private byte[] data = null;
        private Context ctx;
        private Bitmap faceCroped;

        public FaceDetectThread(Handler handler, Context ctx) {
            this.ctx = ctx;
            this.handler = handler;
        }


        public void setData(byte[] data) {
            this.data = data;
        }

        public void run() {
//            Log.i("FaceDetectThread", "running");

            float aspect = (float) previewHeight / (float) previewWidth;
            int w = prevSettingWidth;
            int h = (int) (prevSettingWidth * aspect);
            //Log.e("2", previewHeight +"  "+ previewWidth);
           // Log.e("3", prevSettingHeight +"  "+ prevSettingWidth);
            Bitmap bitmap = Bitmap.createBitmap(previewWidth, previewHeight, Bitmap.Config.RGB_565);
            // face detection: first convert the image from NV21 to RGB_565
            YuvImage yuv = new YuvImage(data, ImageFormat.NV21,
                    bitmap.getWidth(), bitmap.getHeight(), null);
            // TODO: make rect a member and use it for width and height values above
            Rect rectImage = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

            // TODO: use a threaded option or a circular buffer for converting streams?
            //see http://ostermiller.org/convert_java_outputstream_inputstream.html
            ByteArrayOutputStream baout = new ByteArrayOutputStream();
            if (!yuv.compressToJpeg(rectImage, 100, baout)) {
                Log.e("CreateBitmap", "compressToJpeg failed");
            }

            BitmapFactory.Options bfo = new BitmapFactory.Options();
            bfo.inPreferredConfig = Bitmap.Config.RGB_565;
            bitmap = BitmapFactory.decodeStream(
                    new ByteArrayInputStream(baout.toByteArray()), null, bfo);

            bmp = Bitmap.createScaledBitmap(bitmap, 1440, 1080, false);

            float xScale = (float) previewWidth / (float) prevSettingWidth;
            float yScale = (float) previewHeight / (float) h;
            //Log.e("4", h+"  "+ w);
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(cameraId, info);
            int rotate = mDisplayOrientation;
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT && mDisplayRotation % 180 == 0) {
                if (rotate + 180 > 360) {
                    rotate = rotate - 180;
                } else
                    rotate = rotate + 180;
            }

            switch (rotate) {
                case 90:
                    bmp = ImageUtils.rotate(bmp, 90);
                    xScale = (float) previewHeight / bmp.getWidth();
                    yScale = (float) previewWidth / bmp.getHeight();
                    break;
                case 180:
                    bmp = ImageUtils.rotate(bmp, 180);
                    break;
                case 270:
                    bmp = ImageUtils.rotate(bmp, 270);
                    xScale = (float) previewHeight / (float) h;
                    yScale = (float) previewWidth / (float) prevSettingWidth;
                    break;
            }


            Message message = new Message();
            message.arg1 = 123;
            handler.sendMessage(message);
            paizhao=false;
            isThreadWorking = false;

        }
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
    private void detectFace(Bitmap bitmap1) {
        resetData();

        final Bitmap bitmap=bitmap1;
        progressDialog = ProgressDialog.show(getActivity(), "Finding", "Please wait...", true, false);
        new Thread(){
            @Override
            public void run() {
                android.media.FaceDetector fdet_ = new android.media.FaceDetector(bitmap.getWidth(), bitmap.getHeight(), MAX_FACE);
                android.media.FaceDetector.Face[] fullResults = new android.media.FaceDetector.Face[MAX_FACE];

                Log.e("kk",fdet_.findFaces(bitmap, fullResults)+"");

                faces_ = new ArrayList<>();


                for (int i = 0; i < MAX_FACE; i++) {
                    if (fullResults[i] != null) {
                        PointF mid = new PointF();
                        fullResults[i].getMidPoint(mid);

                        float eyesDis = fullResults[i].eyesDistance();
                        float confidence = fullResults[i].confidence();
                        float pose = fullResults[i].pose(android.media.FaceDetector.Face.EULER_Y);

                        Rect rect = new Rect(
                                (int) (mid.x - eyesDis * 1.20f),
                                (int) (mid.y - eyesDis * 0.55f),
                                (int) (mid.x + eyesDis * 1.20f),
                                (int) (mid.y + eyesDis * 1.85f));

                        /**
                         * Only detect face size > 100x100
                         */
                        //num=0;
                        if (rect.height() * rect.width() > 100 * 100) {
                            FaceResult faceResult = new FaceResult();
                            faceResult.setFace(0, mid, eyesDis, confidence, pose, System.currentTimeMillis());
                            faces_.add(faceResult);
                            num++;

                            //
                            // Crop Face to display in RecylerView
                            //
                            cropedFace = ImageUtils.cropFace(faceResult, bitmap, 0);

                        }
                    }
                }
                Message message = new Message();
                message.arg1 =117;
                handler1.sendMessage(message);
            }
        }.start();

       // Log.e("kk", bitmap.getHeight() + "");
        //Log.e("kk", bitmap.getWidth() + "");



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
    private void resetData() {

        if (imagePreviewAdapter == null) {
            facesBitmap = new ArrayList<>();
            imagePreviewAdapter = new ImagePreviewAdapter(getActivity(), facesBitmap, new ImagePreviewAdapter.ViewHolder.OnItemClickListener() {
                @Override
                public void onClick(View v, int position) {
                    Faceload=imagePreviewAdapter.setCheck(position);

                    //Toast.makeText(getActivity().getApplicationContext(), "你点击了第" + position+1 + "张人脸,", Toast.LENGTH_LONG).show();
                    FileName=saveImage(Faceload);

                    imagePreviewAdapter.notifyDataSetChanged();
                    progressDialog = ProgressDialog.show(getActivity(), "你点击了第" + position+1 + "张人脸,", "Please wait...", true, false);
                    new HTTPThread2(Environment.getExternalStorageDirectory()+"/ziliao/"+FileName,handler).start();
                }
            });
            recyclerView.setAdapter(imagePreviewAdapter);
        } else {
            imagePreviewAdapter.clearAll();
        }
        faceView.reset();
    }
}
