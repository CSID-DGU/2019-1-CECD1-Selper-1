package com.example.android.videoplayer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class CompareVideo2Activity extends Activity {

    private ArrayList<CommonData> arrayList;
    private ListView fileList;
    private GroupAdapter adapter;

    Button download;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_video);
        download = (Button)findViewById(R.id.downloadbtn);
        makeFileList();
    }

    public ArrayList<CommonData> mGetBVideoList() {
        ArrayList<CommonData> mTempVideoList = new ArrayList<CommonData>();
        Cursor mVideoCursor;
        String[] proj = {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.SIZE ,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media.RESOLUTION
        };
        mVideoCursor = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, proj, "bucket_display_name='ghost'", null, null);
        if(mVideoCursor.moveToFirst()) {
            int id = mVideoCursor.getColumnIndex(MediaStore.Video.Media._ID);
            int size = mVideoCursor.getColumnIndex(MediaStore.Video.Media.SIZE);
            int title = mVideoCursor.getColumnIndex(MediaStore.Video.Media.TITLE);
            int data = mVideoCursor.getColumnIndex(MediaStore.Video.Media.DATA);
            int duration = mVideoCursor.getColumnIndex(MediaStore.Video.Media.DURATION);
            do {
                CommonData mTempCommonData = new CommonData();
                String filename = mVideoCursor.getString(data);
                mTempCommonData .   mVideoId                =   mVideoCursor.getLong(id);
                mTempCommonData . mVideoFilePath  = filename;
                mTempCommonData . mVideoDuration = mVideoCursor.getLong(duration);
                mTempCommonData . mVideoSize   = mVideoCursor.getString(size);
                mTempCommonData . mVideoTitle   = mVideoCursor.getString(title);
                Log.d("dasol","MediaStore.Video.Media.EXTERNAL_CONTENT_URI :"    +MediaStore.Video.Media.EXTERNAL_CONTENT_URI.getEncodedPath());
                Log.d("dasol","mVideoFilePath :"    +filename);
                Log.d("dasol","mVideoTitle :"    +mVideoCursor.getString(title));
                mTempVideoList.add(mTempCommonData);
                //String vod_size = mVideoCursor.getString(size);
                //mTempCommonData.setmVideoSize(vod_size);
                //mTempVideoList.add(mTempCommonData);
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
        fileList = (ListView)findViewById(R.id.listView1);
        arrayList = mGetBVideoList();
        adapter = new GroupAdapter(this,R.layout.activity_get_video, arrayList);
        fileList.setAdapter(adapter);
        fileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final CommonData item = arrayList.get(position);
                File file = new File(item.mVideoFilePath);
                if(file.isFile() && file.canRead()) {

                    download.setOnClickListener(new View.OnClickListener(){

                        public void onClick(View view){
                            String fileName = Environment.getExternalStorageDirectory()
                                    .getAbsolutePath()+"/DCIM/compare/comparevideo.mp4";
                            Intent intent = new Intent(getApplicationContext(), PlayVideoActivity.class);
                            intent.putExtra("FilePath", fileName);
                            startActivity(intent);
                        }
                    });
                }
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
                v = vi.inflate(R.layout.list_item, null);
            }
            temp = item.get(position);
            if(temp != null) {
                ImageView icon = (ImageView)v.findViewById(R.id.imageView1);
                TextView name = (TextView)v.findViewById(R.id.textView1);
                TextView duration = (TextView)v.findViewById(R.id.textView2);
                ContentResolver crThumb = getContentResolver();
                BitmapFactory.Options options=new BitmapFactory.Options();
                options.inSampleSize = 1;
                Bitmap bmp = MediaStore.Video.Thumbnails.getThumbnail( crThumb, temp.mVideoId, MediaStore.Video.Thumbnails.MICRO_KIND, options);
                icon.setImageBitmap(bmp);
                name.setText(temp.mVideoTitle);
                duration.setText(getPlayTime(temp.getmVideoFilePath()));
            }
            return v;
        }
    }

    //시간 길이 구하기
    private String getPlayTime(String path) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(path);
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInmillisec = Long.parseLong( time );
        long duration = timeInmillisec / 1000;
        long hours = duration / 3600;
        long minutes = (duration - hours * 3600) / 60;
        long seconds = duration - (hours * 3600 + minutes * 60);
        return hours + ":" + minutes + ":" + seconds;
    }

    public void showDownloadDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("User Video");
        builder.setMessage("선택한 영상을 User Video로 하시겠습니까?");
        builder.setIcon(android.R.drawable.ic_dialog_alert);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
