package com.example.mac.opencvcard.Fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mac.opencvcard.R;
import com.example.mac.opencvcard.activity.ui.FaceView;
import com.example.mac.opencvcard.adapter.ImagePreviewAdapter;
import com.example.mac.opencvcard.model.FaceResult;
import com.example.mac.opencvcard.utils.ImageUtils;

import java.util.ArrayList;

public class CameraF1 extends Fragment {
    private FaceView faceView;
    private FaceResult faces[];
    private FaceResult faces_previous[];
    private RecyclerView recyclerView;
    private static final int MAX_FACE = 10;
    private ImagePreviewAdapter imagePreviewAdapter;
    private ArrayList<Bitmap> facesBitmap;
    private Bitmap bmp;

    public CameraF1(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_camera_f1, container, false);
        faceView = (FaceView)view.findViewById(R.id.faceView);
        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        return view;
    }
    private void detectFace(Bitmap bitmap) {
        resetData();
        Log.e("kk", bitmap.getHeight() + "");
        Log.e("kk", bitmap.getWidth() + "");
        android.media.FaceDetector fdet_ = new android.media.FaceDetector(bitmap.getWidth(), bitmap.getHeight(), MAX_FACE);
        android.media.FaceDetector.Face[] fullResults = new android.media.FaceDetector.Face[MAX_FACE];

        Log.e("kk",fdet_.findFaces(bitmap, fullResults)+"");

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
            Toast.makeText(getActivity().getApplicationContext(), "共获得" + num + "张人脸,请选择你要识别的一张", Toast.LENGTH_LONG).show();
        }else
        {
            Toast.makeText(getActivity().getApplicationContext(), "图中没有人脸，你可以在相册中将图片放大，截图后尝试再次识别", Toast.LENGTH_LONG).show();
        }
        FaceView overlay = (FaceView)faceView.findViewById(R.id.faceView);
        overlay.setContent(bitmap, faces_);
    }
    private void resetData() {

        if (imagePreviewAdapter == null) {
            facesBitmap = new ArrayList<>();
            imagePreviewAdapter = new ImagePreviewAdapter(getActivity(), facesBitmap, new ImagePreviewAdapter.ViewHolder.OnItemClickListener() {
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
