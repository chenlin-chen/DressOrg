package org.dress.mydress.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import org.dress.mydress.R;
import org.dress.mydress.Crop.ImageAdapter;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class overview extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    private static final int PREEDIT_REQUEST = 1111;
    private File m_photofile = null;
    private static final String TAG = "overview";
    private ImageView m_imageview;
    ImageAdapter myImageAdapter;
    private GridView gridview;
    Toast toast = null;
    private int selectedphoto_num = 0;
    private  String photo_director = null;
    private  File[] photo_list = null;

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
                    return true;
                case R.id.navigation_notifications:
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LoadActivity();
    }

    private  void LoadActivity()
    {
        photo_director = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        photo_list = new File(photo_director).listFiles();

        if(photo_list.length!=0) {
            setContentView(R.layout.activity_overview);
            init();
        }
        else
        {
            setContentView(R.layout.activity_overview_noclothes);
            NoClothesInit();
        }
        mBottomView =(BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
    private  void init()
    {
        gridview = (GridView) findViewById(R.id.gallery_gridimg);
        myImageAdapter = new ImageAdapter( this, this, photo_list);
        gridview.setAdapter(myImageAdapter);
        CheckAlbumDir();
    }

    private  void NoClothesInit()
    {
        m_imageview = (ImageView) findViewById(R.id.gallery_addimg);
        m_imageview.setImageResource(R.drawable.addimage);
        m_imageview.setOnClickListener (new View.OnClickListener() {
            public void onClick(View v) {
                if(HasReadAndWriteExteranlStoragePermission()) {
                    dispatchTakePictureIntent();
                }
                else {
                    RequestReadAndWritePermission();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.imgthumb_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.imgthumb_select_photo:
                DoPreeditSelectPhoto();
                return true;
            case R.id.imgthumb_deleete_photo:
                DoDeleteSelectedPhoto();
                return true;
            case R.id.imgthumb_cancel_photo:
                myImageAdapter.DoCancelSelectBox();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void DoPreeditSelectPhoto()
    {
        if( !HasReadAndWriteExteranlStoragePermission() ) {
            RequestReadAndWritePermission();
        }
        else {
            selectedphoto_num = myImageAdapter.getSelectedphotoNum();
            if (selectedphoto_num == 0) {
                MakeTextAndShow(overview.this, getString(R.string.not_select_photo), Toast.LENGTH_SHORT);
            } else if (selectedphoto_num == 1) {
                ArrayList<String> selected_photo = myImageAdapter.GetSelectPhotoPath();
                Intent preedit_photo_intent = new Intent();
                preedit_photo_intent.setClass(overview.this, preedit.class);
                preedit_photo_intent.putExtra("photo_path", selected_photo.get(0));
                startActivityForResult( preedit_photo_intent,PREEDIT_REQUEST);
            } else {
                MakeTextAndShow(overview.this, getString(R.string.many_select_photo), Toast.LENGTH_SHORT);
            }
        }
    }

    private void DoDeleteSelectedPhoto()
    {
        myImageAdapter.DoDeleteSelectedPhoto();
        photo_list = new File(photo_director).listFiles();

        if( photo_list.length == 0)
            Refresh();
    }

    private  void MakeTextAndShow(final Context context, final String text, final int duration) {
        if (toast == null) {
            toast = Toast.makeText(context, text, duration);
        } else {
            toast.setText(text);
            toast.setDuration(duration);
        }
        toast.show();
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
                        DoPreeditSelectPhoto();
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

        if (takePictureIntent.resolveActivity( getPackageManager() ) != null) {
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
        return this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    }

    private void CheckAlbumDir()
    {
        File storage_dir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if(!storage_dir.exists())
            storage_dir.mkdir();
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
                if(photo_list.length ==0) {
                    Refresh();
                }
                else {
                    photo_list = new File(photo_director).listFiles();
                    myImageAdapter.ReStart(photo_list);
                }

            }else if(resultCode == Activity.RESULT_CANCELED)
               m_photofile.delete();
        }
        if( requestCode == PREEDIT_REQUEST)
        {
            Refresh();
        }

    }

    private void Refresh()
    {
        if (android.os.Build.VERSION.SDK_INT >= 11){
            recreate();
        }else{
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }

    }

}


