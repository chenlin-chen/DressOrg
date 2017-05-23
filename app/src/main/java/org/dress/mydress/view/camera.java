package org.dress.mydress.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import org.dress.mydress.R;


public class camera extends AppCompatActivity {

    ImageView mImageView = null;
    Bitmap mBitmap = null;
    String mPhotoPath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        mImageView = (ImageView) findViewById(R.id.imgDisplay);
        if(CheckPreActivityDeliverData())
        {
            mPhotoPath = GetPhotoPath();
            SetImageView(mPhotoPath);
        }

    }

    private boolean CheckPreActivityDeliverData()
    {
        Intent preintent = this.getIntent();//取得傳遞過來的資料
        String data = preintent.getStringExtra("photofile");
        boolean have_data = data!=null;
        return have_data;
    }

    private  String GetPhotoPath()
    {
        Intent preintent = this.getIntent();//取得傳遞過來的資料
        String photo_path = preintent.getStringExtra("photofile");
        return photo_path;
    }

    private void SetImageView(String photo_src)
    {

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photo_src, bmOptions);

        int photoW = bmOptions.outWidth;
        bmOptions.inJustDecodeBounds = false;

        mBitmap = BitmapFactory.decodeFile(photo_src, bmOptions);
        mImageView.setImageBitmap(mBitmap);
    }

}
