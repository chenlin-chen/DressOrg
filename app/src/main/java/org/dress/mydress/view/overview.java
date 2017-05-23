package org.dress.mydress.view;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import org.dress.mydress.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class overview extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    private File m_photofile = null;
    private static final String TAG = "overview";
    private TextView mTextMessage;
    private BottomNavigationView mBottomView;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    PopupHomeMenu();
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        mTextMessage = (TextView) findViewById(R.id.message);
        mBottomView =(BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void PopupHomeMenu(){
        PopupMenu popup = new PopupMenu(overview.this, mBottomView);
        popup.getMenuInflater().inflate(R.menu.home_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home_take_picture: {
                        if(HasReadAndWriteExteranlStoragePermission()) {
                            dispatchTakePictureIntent();
                        }
                        else {
                            RequestReadAndWritePermission();
                        }
                        return true;
                    }
                    case R.id.home_edit_photo: {
                        Intent edit_photo_intent = new Intent();
                        edit_photo_intent.setClass(overview.this  , camera.class);
                        startActivity(edit_photo_intent);
                        return true;
                    }
                }
                return false;
            }
        });
        popup.show();
    }

    private boolean HasReadAndWriteExteranlStoragePermission()
    {
        int read_exteranl_storage_permission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int write_exteranl_storage_permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        boolean is_read_exteranl_srotage = (read_exteranl_storage_permission == PackageManager.PERMISSION_GRANTED);
        boolean is_write_exteranl_srotage = (write_exteranl_storage_permission == PackageManager.PERMISSION_GRANTED);
        return (is_read_exteranl_srotage && is_write_exteranl_srotage);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            m_photofile = null;
            try {
                m_photofile = createImageFile();
            } catch (IOException ex) {
                Log.v(TAG, ":createImageFile error");
            }
            // Continue only if the File was successfully created
            if (m_photofile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, getString(R.string.photo_provider), m_photofile);
                takePictureIntent.putExtra("return-data", false);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
            }
        }
    }

    private File createImageFile() throws IOException
    {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_"+ timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, ".jpg", albumF);
        return imageF;
    }

    private File getAlbumDir()
    {
        File storage_dir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if(!storage_dir.exists())
            storage_dir.mkdir();
        return storage_dir;
    }

    private void RequestReadAndWritePermission()
    {
        int REQUEST_READWRITE_STORAGE = 1;
        final int version = Build.VERSION.SDK_INT;
        String external_readwrite_permission[] =
                {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (version >= 23) {
            ActivityCompat.requestPermissions(this, external_readwrite_permission, REQUEST_READWRITE_STORAGE);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == CAMERA_REQUEST )
        {
            if(resultCode == Activity.RESULT_OK)
            {
                Intent edit_photo_intent = new Intent();
                edit_photo_intent.setClass(overview.this, camera.class);
                edit_photo_intent.putExtra("photofile",m_photofile.toString());
                startActivity(edit_photo_intent);
            }else if(resultCode == Activity.RESULT_CANCELED)
               m_photofile.delete();
        }
    }

}


