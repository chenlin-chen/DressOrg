package org.dress.mydress.Crop;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;

import org.dress.mydress.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by user on 2017/7/14.
 */

public class ImageAdapter extends BaseAdapter {

    ArrayList<String> itemList = new ArrayList<String>();
    private LayoutInflater mInflater;
    private Context m_context;
    private boolean[] nthumbnailsselection;
    private int selectedphoto_num;
    class ViewHolder {
        ImageView imgThumb;
        CheckBox chkImage;
        int id;
    }

    public ImageAdapter(Activity act, Context context, File[] photo_list ) {
        m_context = context;
        mInflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        add_photo(photo_list);
        Init();
    }

    public void ReStart( File[] photo_list )
    {
        itemList = new ArrayList<String>();
        add_photo(photo_list);
        Init();
    }
    public int getSelectedphotoNum()
    {
        return  selectedphoto_num;
    }

    public ArrayList<String> GetSelectPhotoPath()
    {
        int len = nthumbnailsselection.length;
        ArrayList<String> photos_path = new ArrayList<String>();;
        for (int i = 0; i < len; i++) {
            if (nthumbnailsselection[i]) {
                photos_path.add(itemList.get(i)) ;
                break;
            }
        }
        return  photos_path;
    }

    public void DoCancelSelectBox()
    {
        if(selectedphoto_num > 0 )
        {
            int len = nthumbnailsselection.length;
            for (int i = 0; i < len; i++) {
                nthumbnailsselection[i] = false;
            }
            selectedphoto_num = 0;
            this.notifyDataSetChanged();
        }
    }

    public void DoDeleteSelectedPhoto()
    {
        if(selectedphoto_num > 0)
        {
            int photo_num = nthumbnailsselection.length;
            int deletedphoto_num = 0;
            for (int i = 0; i < photo_num; i++) {
                if(nthumbnailsselection[i])
                {
                    String photo_name = itemList.remove(i-deletedphoto_num);
                    DeleteFile(photo_name);
                    ++deletedphoto_num;
                }
            }

            photo_num -= selectedphoto_num;
            nthumbnailsselection =new boolean[photo_num];
            selectedphoto_num = 0;

            this.notifyDataSetChanged();
        }
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
        final ImageAdapter.ViewHolder holder;
        if (convertView == null) {
            holder = new ImageAdapter.ViewHolder();
            convertView = mInflater.inflate(R.layout.custom_gallery_item, null);
            holder.imgThumb = (ImageView) convertView.findViewById(R.id.gallery_imgthumb);
            holder.chkImage = (CheckBox) convertView.findViewById(R.id.gallery_chkimage);
            convertView.setTag(holder);
        } else {
            holder = (ImageAdapter.ViewHolder) convertView.getTag();
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

    private void Init()
    {
        nthumbnailsselection = new boolean[ getCount() ];
        selectedphoto_num = 0;
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

    private void add_photo(File[] photo_list)
    {
        for (File file : photo_list){
            itemList.add( file.getAbsolutePath() );
        }
    }

    private void DeleteFile(String file_path)
    {
        File file= new File(file_path);
        if(file.exists())
            file.delete();
    }

}
