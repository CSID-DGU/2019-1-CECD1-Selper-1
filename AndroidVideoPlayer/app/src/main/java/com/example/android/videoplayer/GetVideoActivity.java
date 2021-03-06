package com.example.android.videoplayer;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
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

public class GetVideoActivity extends Activity {
    private ArrayList<CommonData> arrayList;
    private ListView fileList;
    private GroupAdapter adapter;

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_video);

        if (checkPermissionREAD_EXTERNAL_STORAGE(this)) {
            makeFileList();
        }

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
        mVideoCursor = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, proj, "bucket_display_name='guide'", null, null);
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
                CommonData item = arrayList.get(position);
                File file = new File(item.mVideoFilePath);
                if(file.isFile() && file.canRead()) {
                    Intent intent = new Intent(getApplicationContext(), PlayVideoActivity.class);
                    intent.putExtra("FilePath", item.mVideoFilePath);
                    startActivity(intent);
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

    public boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context,
                            Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[] { permission },
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    /*public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // do your stuff
                } else {
                    Toast.makeText(Login.this, "GET_ACCOUNTS Denied",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }*/
}
