package com.example.android.videoplayer;

/**
 * Created by Dsol on 2019-06-17.
 */

public class CommonData {
    public long mVideoId;
    public String       mVideoFilePath;
    public String       mVideoTitle;
    public String     mVideoSize;
    public long         mVideoDuration;
    public String       mVideoAddDate;
    public int      mVideoWidth;
    public int      mVideoHeight;

    public CommonData() {
        mVideoId = 0;
        mVideoFilePath = null;
        mVideoTitle = null;
        mVideoSize = null;
        mVideoDuration = 0;
        mVideoAddDate = null;
        mVideoWidth = 0;
        mVideoHeight = 0;
    }

    public long getmVideoId() {
        return mVideoId;
    }

    public String getmVideoFilePath() {
        return mVideoFilePath;
    }

    public String getmVideoTitle() {
        return mVideoTitle;
    }

    public String getmVideoSize() {
        return mVideoSize;
    }

    public long getmVideoDuration() {
        return mVideoDuration;
    }

    public String getmVideoAddDate() {
        return mVideoAddDate;
    }

    public int getmVideoWidth() {
        return mVideoWidth;
    }

    public int getmVideoHeight() {
        return mVideoHeight;
    }

    public void setmVideoId(long mVideoId) {
        this.mVideoId = mVideoId;
    }

    public void setmVideoFilePath(String mVideoFilePath) {
        this.mVideoFilePath = mVideoFilePath;
    }

    public void setmVideoTitle(String mVideoTitle) {
        this.mVideoTitle = mVideoTitle;
    }

    public void setmVideoSize(String mVideoSize) {
        this.mVideoSize = mVideoSize;
    }

    public void setmVideoDuration(long mVideoDuration) {
        this.mVideoDuration = mVideoDuration;
    }

    public void setmVideoAddDate(String mVideoAddDate) {
        this.mVideoAddDate = mVideoAddDate;
    }

    public void setmVideoWidth(int mVideoWidth) {
        this.mVideoWidth = mVideoWidth;
    }

    public void setmVideoHeight(int mVideoHeight) {
        this.mVideoHeight = mVideoHeight;
    }
}
