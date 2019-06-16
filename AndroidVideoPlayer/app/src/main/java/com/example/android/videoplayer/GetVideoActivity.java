package com.example.android.videoplayer;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class GetVideoActivity extends ListActivity {
    private ArrayList<CommonData> arrayList;
    private ListView fileList;
    private GroupAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_video);

        makeFileList();
    }

    public ArrayList<CommonData> mGetBVideoList() {
        ArrayList<CommonData> mTempVideoList = new ArrayList<CommonData>();
        Cursor mVideoCursor;
        String[] proj = {
                //MediaStore.Video.Media._ID,
                //MediaStore.Video.Media.DATA,
                //MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.SIZE ,
                //MediaStore.Video.Media.TITLE,
                //MediaStore.Video.Media.DURATION,
                //MediaStore.Video.Media.DATE_ADDED,
                //MediaStore.Video.Media.RESOLUTION
        };
        mVideoCursor = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, proj, null, null, null);
        if(mVideoCursor.moveToFirst()) {
            //int id = mVideoCursor.getColumnIndex(MediaStore.Video.Media._ID);
            int size = mVideoCursor.getColumnIndex(MediaStore.Video.Media.SIZE);
            //int title = mVideoCursor.getColumnIndex(MediaStore.Video.Media.TITLE);
            //int data = mVideoCursor.getColumnIndex(MediaStore.Video.Media.DATA);
            //int duration = mVideoCursor.getColumnIndex(MediaStore.Video.Media.DURATION);
            do {
                CommonData mTempCommonData = new CommonData();
                /*String filename = mVideoCursor.getString(data);
                mTempCommonData .   mVideoId                =   mVideoCursor.getLong(id);
                mTempCommonData . mVideoFilePath  = filename;
                mTempCommonData . mVideoDuration = mVideoCursor.getLong(duration);
                mTempCommonData . mVideoSize   = mVideoCursor.getLong(size);
                mTempCommonData . mVideoTitle   = mVideoCursor.getString(title);
                Log.e("   ","mVideoFilePath :"    +filename);
                Log.e("   ","mVideoTitle :"    +mVideoCursor.getString(title));
                mTempVideoList.add(mTempCommonData);
                mTempCommonData                 = null;*/
                String vod_size = mVideoCursor.getString(size);
                mTempCommonData.setmVideoSize(vod_size);
                mTempVideoList.add(mTempCommonData);
            }while(mVideoCursor.moveToNext());
        }
        /*try{
            if(mVideoCursor!=null) mVideoCursor.close();
        }catch(Exception err){}
        mVideoCursor  = null;*/
        mVideoCursor.close();
        return mTempVideoList;
    }
    public void makeFileList() {
        fileList = (ListView)findViewById(R.id.listView2);
        arrayList = mGetBVideoList();
        adapter = new GroupAdapter(this,R.layout.activity_get_video, arrayList);
        fileList.setAdapter(adapter);
        fileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CommonData item = arrayList.get(position);
                File file = new File(item.mVideoFilePath);
                /*if(file.isFile() && file.canRead()) {
                    Intent intent = new Intent(getApplicationContext(), GetVideoActivity.class);
                    intent.putExtra("FilePath", item.mVideoFilePath);
                    startActivity(intent);
                }*/
            }
        });
    }
    private class GroupAdapter extends ArrayAdapter<Object> {
        private ArrayList<CommonData> item;
        private CommonData temp;
        @SuppressWarnings({ "unchecked", "rawtypes" })
        public GroupAdapter(Context ctx, int resourceID, ArrayList item) {
            super(ctx, resourceID, item);
            this.item = item;
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if(v == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.activity_get_video, null);
            }
            temp = item.get(position);
            if(temp != null) {
                ImageView icon = (ImageView)v.findViewById(R.id.imageView2);
                TextView name = (TextView)v.findViewById(R.id.textView1);
                ContentResolver crThumb = getContentResolver();
                BitmapFactory.Options options=new BitmapFactory.Options();
                options.inSampleSize = 1;
                Bitmap bmp = MediaStore.Video.Thumbnails.getThumbnail( crThumb, temp.mVideoId, MediaStore.Video.Thumbnails.MICRO_KIND, options);
                icon.setImageBitmap(bmp);
                name.setText(temp.mVideoTitle);
            }
            return v;
        }
    }
}
