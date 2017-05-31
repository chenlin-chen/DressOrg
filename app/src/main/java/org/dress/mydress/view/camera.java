package org.dress.mydress.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.dress.mydress.R;


public class camera extends AppCompatActivity {

    ImageView mImageView = null;
    Bitmap mBitmap = null;
    String mPhotoPath = null;
    private TextView mTextMessage;
    private int PICK_IMAGE_REQUEST = 1;

    Button m_preedit_buttom ;
    Button m_selectphoto_buttom ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        init();
    }

    private void init()
    {
        mImageView = (ImageView) findViewById(R.id.imgDisplay);
        m_preedit_buttom = (Button) findViewById(R.id.buttom_preedit);
        m_selectphoto_buttom = (Button) findViewById(R.id.buttom_select);
        mTextMessage = (TextView) findViewById(R.id.camera_textmessage);
        CameraOnClickListener button_click_listener = new CameraOnClickListener();
        m_preedit_buttom.setOnClickListener(button_click_listener);
        m_selectphoto_buttom.setOnClickListener(button_click_listener);
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


    class CameraOnClickListener implements View.OnClickListener{
        public void onClick(View v){
            switch(v.getId()){
                case R.id.buttom_preedit:
                    break;
                case R.id.buttom_select:
                    StartSelectPhoto();
                    break;
            }
        }
    }

    private void StartSelectPhoto()
    {
        //TODO: reference customer gallery(https://guides.codepath.com/android/Accessing-the-Camera-and-Stored-Media#file-pickers)
        Intent select_photo_intent = new Intent(camera.this, custom_photo_gallery.class);
        startActivityForResult(select_photo_intent, PICK_IMAGE_REQUEST);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE_REQUEST )
        {
            if(resultCode == Activity.RESULT_OK)
            {
                String selected_mages = data.getStringExtra("data");
                SetImageView(selected_mages);
                mTextMessage.setText(getString(R.string.find_object));
            }

        }
    }
}
