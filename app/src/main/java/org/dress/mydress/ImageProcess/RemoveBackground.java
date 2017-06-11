package org.dress.mydress.ImageProcess;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import org.dress.mydress.ImageProcess.ProcessData;

/**
 * Created by user on 2017/6/3.
 */


public class RemoveBackground extends AsyncTask<ProcessData, Integer, Integer> {


    private Context mContext;
    private  ImageView mImageView;
    private ProgressDialog mdialog;
    private String mCurrentPhotoPath;
    private double mresize_ration = 0;
    private int sum_image_pixel = 360000;
    public RemoveBackground(Context activity_context, ImageView activity_imageview) {
        mContext = activity_context;
        mImageView = activity_imageview;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        mdialog = new ProgressDialog(mContext);
        mdialog.setMessage("Processing Image...");
        mdialog.setCancelable(false);
        mdialog.setIndeterminate(true);
        mdialog.show();
    }

    @Override
    protected Integer doInBackground(ProcessData ... params) {
        ProcessData remove_background_data = params[0];
        Point top_l = remove_background_data.top_left;
        Point bottom_r = remove_background_data.bottom_right;
        mCurrentPhotoPath = remove_background_data.PhotoPath;
        Mat img = Imgcodecs.imread(mCurrentPhotoPath);
        mresize_ration = CaluResizeRation(sum_image_pixel, img.width(), img.height());
        ResizePoint(top_l, bottom_r, mresize_ration);
        Mat resize_img = new Mat();
        Size newimg_size = new Size(img.width()*mresize_ration,img.height()*mresize_ration );
        Imgproc.resize(img, resize_img, newimg_size);

        Mat background = new Mat(resize_img.size(), CvType.CV_8UC3,
                new Scalar(255, 255, 255));
        Mat firstMask = new Mat();
        Mat bgModel = new Mat();
        Mat fgModel = new Mat();
        Mat mask;
        Mat source = new Mat(1, 1, CvType.CV_8U, new Scalar(Imgproc.GC_PR_FGD));
        Mat dst = new Mat();
        Rect rect = new Rect(top_l, bottom_r);

        Imgproc.grabCut(resize_img, firstMask, rect, bgModel, fgModel,
                5, Imgproc.GC_INIT_WITH_RECT);
        Core.compare(firstMask, source, firstMask, Core.CMP_EQ);

        Mat foreground = new Mat(resize_img.size(), CvType.CV_8UC3,
                new Scalar(255, 255, 255));
        resize_img.copyTo(foreground, firstMask);

        Scalar color = new Scalar(255, 0, 0, 255);
        Imgproc.rectangle(resize_img, top_l, bottom_r, color);

        Mat tmp = new Mat();
        Imgproc.resize(background, tmp, resize_img.size());
        background = tmp;
        mask = new Mat(foreground.size(), CvType.CV_8UC1,
                new Scalar(255, 255, 255));

        Imgproc.cvtColor(foreground, mask, Imgproc.COLOR_BGR2GRAY);
        Imgproc.threshold(mask, mask, 254, 255, Imgproc.THRESH_BINARY_INV);
        System.out.println();
        Mat vals = new Mat(1, 1, CvType.CV_8UC3, new Scalar(0.0));
        background.copyTo(dst);

        background.setTo(vals, mask);

        Core.add(background, foreground, dst, mask);

        firstMask.release();
        source.release();
        bgModel.release();
        fgModel.release();
        vals.release();

        Imgcodecs.imwrite(mCurrentPhotoPath + ".png", dst);

        return 0;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);

        Bitmap jpg = BitmapFactory
                .decodeFile(mCurrentPhotoPath + ".png");

        mImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        mImageView.setAdjustViewBounds(true);
        mImageView.setPadding(2, 2, 2, 2);
        mImageView.setImageBitmap(jpg);
        mImageView.invalidate();

        mdialog.dismiss();
    }

    private double CaluResizeRation(double sum_pixel, double img_h, double img_w)
    {
        double nowsum_pixel = img_h*img_w;
        if(nowsum_pixel<= sum_pixel)
            return  1;
        return Math.sqrt(sum_pixel/img_h/img_w);
    }
    private void ResizePoint(Point top_l, Point bot_r, double ration)
    {
        top_l.x = top_l.x*ration;
        top_l.y = top_l.y*ration;
        bot_r.x = bot_r.x*ration;
        bot_r.y = bot_r.y*ration;
    }
}

