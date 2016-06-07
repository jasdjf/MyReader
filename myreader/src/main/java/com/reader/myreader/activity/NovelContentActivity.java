package com.reader.myreader.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import com.reader.myreader.R;

/**
 * Created by zhuangwei on 2016/6/4.
 */
public class NovelContentActivity extends Activity {

    private TextView novelTitle;
    private TextView novelContent;
    private String title;
    private File file;
    private BufferedReader bufferedReader;
    private List<LineBean> titleList = new ArrayList<>();
    String novel = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.novel_read_activity);
        novelTitle = (TextView) findViewById(R.id.read_novel_title);
        novelContent = (TextView) findViewById(R.id.read_novel_content);
        Intent intent = getIntent();
        String novelName = intent.getStringExtra("novel_name");
        try {
            file = new File(getApplicationContext().getExternalFilesDir("text").getPath() + File.separator + novelName + ".txt");
            bufferedReader = new BufferedReader(new FileReader(file));
            new MyThread().start();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    class MyThread extends Thread {
        @Override
        public void run() {
            getNovelTitle();
            novel += getNovelContent(400);
            novel = novel.replace("  ","        ");
            handler.sendEmptyMessage(0x123);
        }
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 0x123:
                    novelContent.setText(novel);
                    break;

            }
            return true;
        }
    });

    private String getNovelTitle() {
        String title = "";
        if (bufferedReader != null) {
            String line = "";
            try {
                int num = 0;
                LineBean bean = null;
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.startsWith("第") && line.contains("章")) {
                        bean = new LineBean();
                        bean.title = line;
                        bean.lineNum = num;
                        titleList.add(bean);
                    }
                    num++;
                }
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return title;
    }

    private String getNovelContent(int chapterNum) {
        String content = "";
        String tmpTitle = titleList.get(chapterNum - 1).title;
        int lineNum1 = titleList.get(chapterNum - 1).lineNum;
        int lineNum2 = titleList.get(chapterNum).lineNum;
        LineNumberReader lr = null;
        Log.d("aaaa",titleList.size()+"");
        for(int i=0;i<titleList.size();i++){
            Log.d("aaaa",titleList.get(i).title);
        }
        try {
            FileReader reader = new FileReader(file);
            lr = new LineNumberReader(reader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            String str = null;
            while ((str=lr.readLine())!=null){
                if(lr.getLineNumber()>lineNum1){
                    content += str+"\r\n";
                    if(lr.getLineNumber()>=lineNum2){
                        break;
                    }
                }
            }
            lr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content;
    }

    class LineBean {
        String title;
        int lineNum;
    }
}
