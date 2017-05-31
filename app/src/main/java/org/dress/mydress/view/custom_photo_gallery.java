package org.dress.mydress.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import org.dress.mydress.R;

import java.io.File;
import java.util.ArrayList;

public class custom_photo_gallery extends AppCompatActivity {

    ImageAdapter myImageAdapter;
    private Button button_select;
    private GridView gridview;
    private boolean[] nthumbnailsselection;
    private  String photo_director = null;
    private  File[] photo_list = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_photo_gallery);
        init();
        String targetPath = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        Toast.makeText(getApplicationContext(), targetPath, Toast.LENGTH_LONG).show();
        AddImagetoImageAdapter();

    }

    private  void init()
    {
        button_select= (Button) findViewById(R.id.btnSelect);
        gridview = (GridView) findViewById(R.id.gallery_gridimg);
        myImageAdapter = new ImageAdapter();
        gridview.setAdapter(myImageAdapter);
        photo_director = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        photo_list = new File(photo_director).listFiles();
        nthumbnailsselection = new boolean[photo_list.length];

        button_select.setOnClickListener(SelectButtonClickListen);

    }

    View.OnClickListener SelectButtonClickListen = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            final int len = nthumbnailsselection.length;
            int cnt = 0;
            String selectImages = "";
            for (int i = 0; i < len; i++) {
                if (nthumbnailsselection[i]) {
                    cnt++;
                    selectImages = photo_list[i].getAbsolutePath();
                    //selectImages = selectImages + arrPath[i] + "|";
                }
            }
            if (cnt == 0) {
                Toast.makeText(getApplicationContext(), "Please select at least one image", Toast.LENGTH_LONG).show();
            } else {
                Log.d("SelectedImages", selectImages);
                Intent select_result = new Intent();
                select_result.putExtra("data", selectImages);
                setResult(Activity.RESULT_OK, select_result);
                finish();
            }
        }
    };

    private  void AddImagetoImageAdapter( )
    {
        for (File file : photo_list){
            myImageAdapter.add(file.getAbsolutePath());
        }
    }

    public class ImageAdapter extends BaseAdapter {

        int counter = 0;
        int selected_id = 0;
        private Context mContext;
        ArrayList<String> itemList = new ArrayList<String>();
        private LayoutInflater mInflater;
        private  CheckBox selectedCheckBox = null;
        public ImageAdapter() {
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        void add(String path){
            itemList.add(path);
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
        public View getView(int position, View convertView, ViewGroup parent) {
            class ViewHolder {
                ImageView imgThumb;
                CheckBox chkImage;
                int id;
            }
            final ViewHolder holder;
            if (convertView == null) {
                counter++;
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.custom_gallery_item, null);
                holder.imgThumb = (ImageView) convertView.findViewById(R.id.gallery_imgthumb);
                holder.chkImage = (CheckBox) convertView.findViewById(R.id.gallery_chkimage);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.imgThumb.setId(position);
            holder.chkImage.setId(position);

            holder.imgThumb.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    int id = holder.imgThumb.getId();
                    if (nthumbnailsselection[id]) {
                        selectedCheckBox.setChecked(false);
                        nthumbnailsselection[id] = false;
                        selectedCheckBox = null;
                        Log.i("Cancel:", String.valueOf(id));
                    } else {
                        if(selectedCheckBox !=null) {
                            selectedCheckBox.setChecked(false);
                            nthumbnailsselection[selected_id] = false;
                        }
                        selectedCheckBox = holder.chkImage;
                        selected_id = id;
                        selectedCheckBox.setChecked(true);
                        nthumbnailsselection[id] = true;
                        Log.i("Select::", String.valueOf(id));
                    }
                }
            });

            holder.chkImage.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    int id = holder.chkImage.getId();
                    if (nthumbnailsselection[id]) {
                        selectedCheckBox.setChecked(false);
                        nthumbnailsselection[id] = false;
                        selectedCheckBox = null;
                        Log.i("Cancel:", String.valueOf(id));
                    } else {
                        if(selectedCheckBox !=null) {
                            selectedCheckBox.setChecked(false);
                            nthumbnailsselection[selected_id] = false;
                        }
                        selectedCheckBox = holder.chkImage;
                        selected_id = id;
                        selectedCheckBox.setChecked(true);
                        nthumbnailsselection[id] = true;
                        Log.i("Select::", String.valueOf(id));
                    }
                }
            });


            Bitmap bm = decodeSampledBitmapFromUri(itemList.get(position), 400, 400);
            holder.imgThumb.setImageBitmap(bm);
            holder.chkImage.setChecked(nthumbnailsselection[position]);
            holder.id = position;
            Log.i("-getView-", String.valueOf(counter));
            return convertView;
        }

        public Bitmap decodeSampledBitmapFromUri(String path, int reqWidth, int reqHeight) {

            Bitmap bm = null;
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            bm = BitmapFactory.decodeFile(path, options);

            return bm;
        }

        public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
            // Raw height and width of image
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

            if (height > reqHeight || width > reqWidth) {
                if (width > height) {
                    inSampleSize = Math.round((float)height / (float)reqHeight);
                } else {
                    inSampleSize = Math.round((float)width / (float)reqWidth);
                }
            }

            return inSampleSize;
        }

    }

}
