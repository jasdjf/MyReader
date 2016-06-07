package com.reader.myreader.bean;

import android.graphics.Bitmap;

/**
 * Created by zhuangwei on 2016/5/21.
 */
public class NovelBean {
    private Bitmap novelIcon;
    private String novelTitle;

    public Bitmap getNovelIcon() {
        return novelIcon;
    }

    public void setNovelIcon(Bitmap novelIcon) {
        this.novelIcon = novelIcon;
    }

    public String getNovelTitle() {
        return novelTitle;
    }

    public void setNovelTitle(String novelTitle) {
        this.novelTitle = novelTitle;
    }
}
