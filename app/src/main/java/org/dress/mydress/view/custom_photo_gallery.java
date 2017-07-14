package org.dress.mydress.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;

import org.dress.mydress.R;

import java.io.File;
import java.util.ArrayList;


public class custom_photo_gallery extends AppCompatActivity {

    ImageAdapter myImageAdapter;
    private GridView gridview;
    Toast toast = null;
    private boolean[] nthumbnailsselection;
    private int selectedphoto_num = 0;
    private  String photo_director = null;
    private  File[] photo_list = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_photo_gallery);
        init();
        AddImagetoImageAdapter();
    }

    private  void init()
    {

        gridview = (GridView) findViewById(R.id.gallery_gridimg);
        myImageAdapter = new ImageAdapter(this);
        gridview.setAdapter(myImageAdapter);
        photo_director = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        photo_list = new File(photo_director).listFiles();
        nthumbnailsselection = new boolean[photo_list.length];
        //button_select= (Button) findViewById(R.id.btnSelect);
        //button_select.setOnClickListener(SelectButtonClickListen);
    }

    private  void AddImagetoImageAdapter( )
    {
        for (File file : photo_list){
            myImageAdapter.add_photo(file.getAbsolutePath());
        }
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
                DoCancelSelectBox();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void DoPreeditSelectPhoto()
    {
        if (selectedphoto_num  == 0) {
            makeTextAndShow(custom_photo_gallery.this,getString(R.string.not_select_photo),Toast.LENGTH_SHORT);
        } else if(selectedphoto_num == 1){
            String selected_photos = GetEditPhoto();
            Intent select_result = new Intent();
            select_result.putExtra("data", selected_photos);
            setResult(Activity.RESULT_OK, select_result);
            finish();
        }
        else
        {
            makeTextAndShow(custom_photo_gallery.this,getString(R.string.many_select_photo),Toast.LENGTH_SHORT);
        }
    }

    private void DoCancelSelectBox()
    {
        if(selectedphoto_num > 0 )
        {
            int len = nthumbnailsselection.length;
            for (int i = 0; i < len; i++) {
                nthumbnailsselection[i] = false;
            }
            selectedphoto_num = 0;
            myImageAdapter.notifyDataSetChanged();
        }
    }

    private void DoDeleteSelectedPhoto()
    {
        if(selectedphoto_num > 0)
        {
            int photo_num = nthumbnailsselection.length;
            int deletedphoto_num = 0;
            for (int i = 0; i < photo_num; i++) {
                if(nthumbnailsselection[i])
                {
                    String photo_name = myImageAdapter.remove_photo(i-deletedphoto_num);
                    DeleteFile(photo_name);
                    ++deletedphoto_num;
                }
            }
            photo_num -= selectedphoto_num;
            nthumbnailsselection =new boolean[photo_num];
            selectedphoto_num = 0;
            photo_list = new File(photo_director).listFiles();
            myImageAdapter.notifyDataSetChanged();
        }
    }

    private String GetEditPhoto()
    {
        int len = nthumbnailsselection.length;
        String select_photos = "";
        for (int i = 0; i < len; i++) {
            if (nthumbnailsselection[i]) {
                select_photos = photo_list[i].getAbsolutePath();
                break;
            }
        }
        return  select_photos;
    }

    private void DeleteFile(String file_path)
    {
        File file= new File(file_path);
        if(file.exists())
            file.delete();
    }

    /*View.OnClickListener SelectButtonClickListen = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            if (selectedphoto_num  == 0) {
                makeTextAndShow(custom_photo_gallery.this,getString(R.string.not_select_photo),Toast.LENGTH_SHORT);
            } else if(selectedphoto_num == 1){
                String selected_photos = GetEditPhoto();
                Intent select_result = new Intent();
                select_result.putExtra("data", selected_photos);
                setResult(Activity.RESULT_OK, select_result);
                finish();
            }
            else
            {
                makeTextAndShow(custom_photo_gallery.this,getString(R.string.many_select_photo),Toast.LENGTH_SHORT);
            }
        }
    };*/

    private  void makeTextAndShow(final Context context, final String text, final int duration) {
        if (toast == null) {
            toast = Toast.makeText(context, text, duration);
        } else {
            toast.setText(text);
            toast.setDuration(duration);
        }
        toast.show();
    }


    public class ImageAdapter extends BaseAdapter {
        ArrayList<String> itemList = new ArrayList<String>();
        private LayoutInflater mInflater;
        private Context m_context;
        class ViewHolder {
            ImageView imgThumb;
            CheckBox chkImage;
            int id;
        }

        public ImageAdapter( Context context ) {
            m_context = context;
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return itemList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.custom_gallery_item, null);
                holder.imgThumb = (ImageView) convertView.findViewById(R.id.gallery_imgthumb);
                holder.chkImage = (CheckBox) convertView.findViewById(R.id.gallery_chkimage);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.imgThumb.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    int id = holder.id;
                    if (nthumbnailsselection[id]) {
                        holder.chkImage.setChecked(false);
                        nthumbnailsselection[id] = false;
                        --selectedphoto_num;
                    } else {
                        holder.chkImage.setChecked(true);
                        nthumbnailsselection[id] = true;
                        ++selectedphoto_num;
                    }
                }
            });

            holder.chkImage.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    int id = holder.id;
                    if (nthumbnailsselection[id]) {
                        cb.setChecked(false);
                        nthumbnailsselection[id] = false;
                        --selectedphoto_num;
                    } else {
                        cb.setChecked(true);
                        nthumbnailsselection[id] = true;
                        ++selectedphoto_num;
                    }
                }
            });

            String photo_path = itemList.get(position);
            LoadPhotoThumbnai(photo_path,m_context,holder.imgThumb);
            holder.chkImage.setChecked(nthumbnailsselection[position]);
            holder.id = position;
            return convertView;
        }

        private void LoadPhotoThumbnai(String photo_path, Context context, ImageView photo_view)
        {
            DrawableRequestBuilder<String> thumbnailRequest = Glide.with( context ).load(photo_path);

            Glide.with( context )
                 .load( photo_path )
                 .placeholder(R.drawable.ic_notifications_black_24dp)
                 .thumbnail( thumbnailRequest )
                 .into( photo_view );
        }

        void add_photo(String path){
            itemList.add(path);
        }

        String remove_photo(int index)
        {
            return  itemList.remove(index);
        }

    }

}
