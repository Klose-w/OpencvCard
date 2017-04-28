package com.example.mac.opencvcard.activity.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import com.example.mac.opencvcard.model.FaceResult;

import java.util.ArrayList;




/**
 * Created by Nguyen on 5/20/2016.
 */

/**
 * View which displays a bitmap containing a face along with overlay graphics that identify the
 * locations of detected facial landmarks.
 */
public class FaceView extends View {
    private static final float ID_TEXT_SIZE = 60.0f;
    private Bitmap mBitmap;
    private ArrayList<FaceResult> mFaces;
    double topx;
    double topy;
    public FaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Sets the bitmap background and the associated face detections.
     */
    public void setContent(Bitmap bitmap, ArrayList<FaceResult> faces) {
        mBitmap = bitmap;
        mFaces = faces;
        invalidate();
    }

    /**
     * Draws the bitmap background and the associated face landmarks.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if ((mBitmap != null) && (mFaces != null)) {

            double scale = drawBitmap(canvas);
            drawFaceBox(canvas, scale);
        }
    }

    /**
     * Draws the bitmap background, scaled to the device size.  Returns the scale for future use in
     * positioning the facial landmark graphics.
     */
    private double drawBitmap(Canvas canvas) {
        double viewWidth = canvas.getWidth();
        double viewHeight = canvas.getHeight();
        double imageWidth = mBitmap.getWidth();
        double imageHeight = mBitmap.getHeight();
        double scale = Math.min(viewWidth / imageWidth, viewHeight / imageHeight);
        topx=(canvas.getWidth()-(imageWidth * scale))/2;
        topy=(canvas.getHeight()-(imageHeight * scale))/2;
        Rect destBounds = new Rect((int)topx, (int)topy, (int) (imageWidth * scale+topx), (int) (imageHeight * scale+topy));
        canvas.drawBitmap(mBitmap, null, destBounds, null);
        return scale;
    }

    private void drawFaceBox(Canvas canvas, double scale) {

        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int stroke = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, metrics);
        paint.setStrokeWidth(stroke);

        Paint mIdPaint = new Paint();
        mIdPaint.setColor(Color.GREEN);
        float textSize = (float) (ID_TEXT_SIZE * scale);
        if (textSize > 50) textSize = 50;
        mIdPaint.setTextSize(textSize);

        for (int i = 0; i < mFaces.size(); ++i) {
            FaceResult face = mFaces.get(i);

            PointF mid = new PointF();
            face.getMidPoint(mid);

            RectF rectF = new RectF();
            if (mid.x != 0.0f && mid.y != 0.0f) {
                float eyesDis = face.eyesDistance();

                rectF.set(new RectF(
                        ((float)topx+(mid.x - eyesDis * 1.2f) * (float) scale),
                        (mid.y - eyesDis * 0.55f) * (float) scale+(float)topy,
                        (mid.x + eyesDis * 1.2f) * (float) scale+(float)topx,
                        (mid.y + eyesDis * 1.85f) * (float) scale+(float)topy));

                canvas.drawRect(rectF, paint);
            }
        }
    }

    /**
     * Release Memory
     */
    public void reset() {
        if (mBitmap != null)
            mBitmap.recycle();
    }
}
