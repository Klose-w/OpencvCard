package com.example.mac.opencvcard.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.mac.opencvcard.R;
import com.example.mac.opencvcard.activity.ui.FaceView;
import com.example.mac.opencvcard.adapter.ImagePreviewAdapter;
import com.example.mac.opencvcard.model.FaceResult;
import com.example.mac.opencvcard.utils.ImageUtils;

import java.io.File;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Date;

;

import static android.app.AlertDialog.*;


/**
 * Created by Nguyen on 5/20/2016.
 */

/**
 * Demonstrates basic usage of the GMS vision face detector by running face landmark detection on a
 * photo and displaying the photo with associated landmarks in the UI.
 */
public class PhotoDetectActivity extends AppCompatActivity {

    private static final String TAG = PhotoDetectActivity.class.getSimpleName();

    private static final int RC_HANDLE_WRITE_EXTERNAL_STORAGE_PERM = 3;
    private static int PICK_RESULT_OK = 15;
    private static int PICK_IMAGE_REQUEST = 5;
    private FaceView faceView;
    private RecyclerView recyclerView;
    private ImagePreviewAdapter imagePreviewAdapter;
    private ArrayList<Bitmap> facesBitmap;
    private String sdPath;//SD卡的路径
    private String picPath;//图片存储路径
    private static final int MAX_FACE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_viewer);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("人脸检测");
        faceView = (FaceView) findViewById(R.id.faceView);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            getImage();
        } else {
            requestWriteExternalPermission();
        }
        // Bitmap bitmap = ImageUtils.getBitmap(ImageUtils.getRealPathFromURI(this, uri), 2048, 1232);
        // Bitmap  bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.people3);
        // detectFace(bitmap);
       // Dialog();



    }
    public void  Dialog()
    {
        Builder builder=new Builder(this).setTitle("选择")
                .setPositiveButton("相机", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       /* String out_file_path = "/Face";
                        File dir = new File(out_file_path);
                        if (!dir.exists()) {
                            dir.mkdirs();
                        } // 把文件地址转换成Uri格式
                        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd_hhmmss");
                        java.util.Date date=new java.util.Date();
                        String str=sdf.format(date);
                        sdPath = Environment.getExternalStorageDirectory().getPath();
                        picPath = sdPath + "/Face/"+str+ ".jpg";
                        Toast.makeText(getApplicationContext(), picPath, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        Uri uri = Uri.fromFile(new File(picPath));*/
                        File outputImage = new File(Environment.getExternalStorageDirectory(),
                                "output_image.jpg");
                        try {
                            if (outputImage.exists()) {
                                outputImage.delete();
                            }
                            outputImage.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //将File对象转换成Uri对象
                        //Uri表标识着图片的地址

                        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                        Uri uri = Uri.fromFile(outputImage);
                        // 设置系统相机拍摄照片完成后图片文件的存放地址
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                        startActivityForResult(intent,PICK_RESULT_OK);


                    }
                }).setNegativeButton("相册", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int rc = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        if (rc == PackageManager.PERMISSION_GRANTED) {
                            getImage();
                        } else {
                            requestWriteExternalPermission();
                        }
                    }
                });
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_photo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;

            case R.id.gallery:

                int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (rc == PackageManager.PERMISSION_GRANTED) {
                    getImage();
                } else {
                    requestWriteExternalPermission();
                }

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        resetData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap;
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //Toast.makeText(this, "hahha1111", Toast.LENGTH_LONG).show();
            Uri uri = data.getData();

            bitmap = ImageUtils.getBitmap(ImageUtils.getRealPathFromURI(this, uri), 2048, 1232);
            if (bitmap != null)
                detectFace(bitmap);
            else
                Toast.makeText(this, "Cann't open this image.", Toast.LENGTH_LONG).show();
        }
        else if (requestCode == PICK_RESULT_OK&& resultCode == RESULT_OK ) {
            //Toast.makeText(this, "hahha", Toast.LENGTH_LONG).show();
            /*Uri uri = Uri.fromFile(new File(picPath));*/
            //将刚拍照的相片在相册中显示
            Uri uri = data.getData();
            bitmap = ImageUtils.getBitmap(ImageUtils.getRealPathFromURI(this, uri), 2048, 1232);
            if (bitmap != null) {
                detectFace(bitmap);
            }
            else {
                Toast.makeText(this, "Cann't open this image.", Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != RC_HANDLE_WRITE_EXTERNAL_STORAGE_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Write External permission granted");
            // we have permission
            getImage();
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));
    }

    public void getImage() {
        // Create intent to Open Image applications like Gallery, Google Photos
        try {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            // Start the Intent

            startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
        } catch (ActivityNotFoundException i) {
            Toast.makeText(PhotoDetectActivity.this, "Your Device can not select image from gallery.", Toast.LENGTH_LONG).show();
            finish();
        }
    }


    private void detectFace(Bitmap bitmap) {
        resetData();
        Log.e("kk", bitmap.getHeight() + "");
        Log.e("kk", bitmap.getWidth() + "");
        android.media.FaceDetector fdet_ = new android.media.FaceDetector(bitmap.getWidth(), bitmap.getHeight(), MAX_FACE);
        android.media.FaceDetector.Face[] fullResults = new android.media.FaceDetector.Face[MAX_FACE];

        fdet_.findFaces(bitmap, fullResults);

        ArrayList<FaceResult> faces_ = new ArrayList<>();

        int num=0;
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
                if (rect.height() * rect.width() > 100 * 100) {
                    FaceResult faceResult = new FaceResult();
                    faceResult.setFace(0, mid, eyesDis, confidence, pose, System.currentTimeMillis());
                    faces_.add(faceResult);
                    num++;

                    //
                    // Crop Face to display in RecylerView
                    //
                    Bitmap cropedFace = ImageUtils.cropFace(faceResult, bitmap, 0);
                    if (cropedFace != null) {
                        imagePreviewAdapter.add(cropedFace);
                    }
                }
            }
        }
        if(num!=0) {
            Toast.makeText(getApplicationContext(), "共获得" + num + "张人脸,请选择你要识别的一张", Toast.LENGTH_LONG).show();
        }else
        {
            Toast.makeText(getApplicationContext(), "图中没有人脸，你可以在相册中将图片放大，截图后尝试再次识别", Toast.LENGTH_LONG).show();
        }
        FaceView overlay = (FaceView) findViewById(R.id.faceView);
        overlay.setContent(bitmap, faces_);
    }


    private void requestWriteExternalPermission() {
        Log.w(TAG, "Write External permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_WRITE_EXTERNAL_STORAGE_PERM);
    }

    private void resetData() {

        if (imagePreviewAdapter == null) {
            facesBitmap = new ArrayList<>();
            imagePreviewAdapter = new ImagePreviewAdapter(PhotoDetectActivity.this, facesBitmap, new ImagePreviewAdapter.ViewHolder.OnItemClickListener() {
                @Override
                public void onClick(View v, int position) {
                    imagePreviewAdapter.setCheck(position);
                    imagePreviewAdapter.notifyDataSetChanged();
                }
            });
            recyclerView.setAdapter(imagePreviewAdapter);
        } else {
            imagePreviewAdapter.clearAll();
        }

        faceView.reset();
    }


}
