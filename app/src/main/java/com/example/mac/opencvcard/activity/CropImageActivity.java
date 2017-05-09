package com.example.mac.opencvcard.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.ab.global.AbConstant;
import com.ab.util.AbFileUtil;
import com.ab.view.cropimage.CropImage;
import com.ab.view.cropimage.CropImageView;
import com.example.mac.opencvcard.R;


import java.io.File;


/**
 * �ü�����
 *
 */
public class CropImageActivity extends Activity implements OnClickListener {
	private static final String TAG = "CropImageActivity";
	private static final boolean D = true;
	private CropImageView mImageView;
	private Bitmap mBitmap;

	private CropImage mCrop;
	
	private Button mSave;
	private Button mCancel,rotateLeft,rotateRight;
	private String mPath = "CropImageActivity";
	public int screenWidth = 0;
	public int screenHeight = 0;

	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 1:
				break;
			
			}

		}
	};
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crop_image);
        init();
    }
    @Override
    protected void onStop(){
    	super.onStop();
    	if(mBitmap!=null){
    		mBitmap=null;
    	}
    }
    
    private void init(){
    	getWindowWH();
    	mPath = getIntent().getStringExtra("PATH");
    	if(D) Log.e(TAG, mPath);
        mImageView = (CropImageView) findViewById(R.id.crop_image);
        mSave = (Button) this.findViewById(R.id.okBtn);
        mCancel = (Button) this.findViewById(R.id.cancelBtn);
        rotateLeft = (Button) this.findViewById(R.id.rotateLeft);
        rotateRight = (Button) this.findViewById(R.id.rotateRight);
        mSave.setOnClickListener(this);
        mCancel.setOnClickListener(this);
        rotateLeft.setOnClickListener(this);
        rotateRight.setOnClickListener(this);
        File mFile = new File(mPath);
        try{
        	mBitmap = AbFileUtil.getBitmapFromSD(mFile, AbConstant.SCALEIMG, 500, 500);
            if(mBitmap==null){
            	Toast.makeText(CropImageActivity.this, "r", Toast.LENGTH_LONG).show();
    			finish();
            }else{
            	resetImageView(mBitmap);
            }
        }catch (Exception e) {
        	Toast.makeText(CropImageActivity.this, "e", Toast.LENGTH_LONG).show();
			finish();
		}
    }
    /**
     * ��ȡ��Ļ�ĸߺͿ�
     */
    private void getWindowWH(){
		DisplayMetrics dm=new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
	}
    private void resetImageView(Bitmap b){
    	 mImageView.clear();
    	 mImageView.setImageBitmap(b);
         mImageView.setImageBitmapResetBase(b, true);
         mCrop = new CropImage(this, mImageView,mHandler);
         mCrop.crop(b);
    }

    public void onClick(View v){
    	switch (v.getId()){
    	case R.id.cancelBtn:
    		finish();
    		break;
    	case R.id.okBtn:
    		String path = mCrop.saveToLocal(mCrop.cropAndSave());
    		if(D) Log.e(TAG,  path);
    		Intent intent = new Intent();
    		intent.putExtra("PATH", path);
    		setResult(14, intent);
    		finish();
    		break;
    	case R.id.rotateLeft:
    		mCrop.startRotate(270.f);
    		break;
    	case R.id.rotateRight:
    		mCrop.startRotate(90.f);
    		break;
    		
    	}
    }
   
}