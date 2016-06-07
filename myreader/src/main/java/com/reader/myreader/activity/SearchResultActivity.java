package com.reader.myreader.activity;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.reader.myreader.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by zhuangwei on 2016/5/31.
 */
public class SearchResultActivity extends BaseActivity implements View.OnClickListener {

    private ImageView resultIcon;
    private TextView resultTitle;
    private TextView resultType;
    private TextView resultState;
    private TextView resultWordNum;
    private TextView resultNew;
    private Button downloadBook;
    private String searchContent;
    private LinearLayout resultLayout;
    private LinearLayout loadingLayout;
    private LinearLayout timeOutLayout;
    private String novelUrl = "";
    private int  firstUrl = 0;//开始下载Url
    private int chapterNum = 0;//章节数
    private String xinbiqugeUrl = "";
    private String qidianUrl = "";
    private String imageUrl = "";
    private String novelTitle = "";
    private String novelType = "";
    private String novelState = "";
    private String novelWordNum = "";
    private String novelNew = "最新章节:";
    private CompleteReceiver completeReceiver;
    private long downloadId = 0;
    private int searchCount = 0;
    private int searchUrlCount = 0;
    private int searchInfoCount = 0;
    private File file;
    private FileOutputStream fo;
    private boolean[] threadDone;
    private String[] novelText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        searchContent = intent.getStringExtra("search_content");
        novelTitle = searchContent;
        new SearchThread().start();
        setContentView(R.layout.search_result_activity);
        setStatusBackground(0xFF47B3A8);
        Toolbar toolbar = (Toolbar) findViewById(R.id.result_toolbar);
        toolbar.setNavigationIcon(R.drawable.home_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setTitle("搜书结果");
        toolbar.setTitleTextColor(0xFFFFFFFF);
        resultIcon = (ImageView) findViewById(R.id.result_icon);
        resultTitle = (TextView) findViewById(R.id.result_title);
        resultType = (TextView) findViewById(R.id.result_type);
        resultState = (TextView) findViewById(R.id.result_state);
        resultWordNum = (TextView) findViewById(R.id.result_word_num);
        resultNew = (TextView) findViewById(R.id.result_new);
        downloadBook = (Button) findViewById(R.id.result_add_book);
        resultLayout = (LinearLayout) findViewById(R.id.result_layout);
        loadingLayout = (LinearLayout) findViewById(R.id.loading_layout);
        timeOutLayout = (LinearLayout) findViewById(R.id.time_out_layout);
        resultLayout.setVisibility(View.GONE);
        timeOutLayout.setVisibility(View.GONE);
        completeReceiver = new CompleteReceiver();
        downloadBook.setOnClickListener(this);
        registerReceiver(completeReceiver,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.result_add_book:
                int bookNum = 0;
                SharedPreferences sharedPreferences = getSharedPreferences("book", MODE_WORLD_WRITEABLE);
                boolean isNovelExist = sharedPreferences.getBoolean(novelTitle, false);
                if (isNovelExist) {
                    Toast.makeText(SearchResultActivity.this, "书架中已存在该书籍!", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    bookNum = sharedPreferences.getInt("book_num", 0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(novelTitle, true);
                    editor.putString("book_" + bookNum, novelTitle);
                    editor.putInt("book_num", bookNum + 1);
                    editor.commit();
                    Intent intent = new Intent(SearchResultActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                //开启下载线程
                try {
                    file = new File(getApplicationContext().getExternalFilesDir("text").getPath()+File.separator+novelTitle+".txt");
                    fo = new FileOutputStream(file,true);
                    new DownLoadNovelThread(firstUrl,firstUrl+10,0).start();
                    int threadCount = (chapterNum-9)/100;
                    threadDone = new boolean[threadCount+2];
                    novelText = new String[threadCount+2];
                    for(int i=0;i<threadDone.length;i++){
                        threadDone[i] = false;
                        novelText[i] = "";
                    }
                    int tmpStartUrl = 0;
                    for(int i=0;i<threadCount;i++){
                        tmpStartUrl = firstUrl+10+i*100;
                        new DownLoadNovelThread(tmpStartUrl,tmpStartUrl+100,i+1).start();
                    }
                    new DownLoadNovelThread(tmpStartUrl+100,firstUrl+chapterNum,threadCount+1).start();
                    new WriteThread().start();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    class DownLoadNovelThread extends Thread{

        private int startUrl;
        private int endUrl;
        private int threadId;

        public DownLoadNovelThread(int startUrl,int endUrl,int id){
            this.startUrl = startUrl;
            this.endUrl = endUrl;
            this.threadId = id;
        }

        @Override
        public void run() {
            for(int i=startUrl;i<endUrl;i++){
                novelText[threadId] += downloadNovel(novelUrl+i+".html");
            }
            threadDone[threadId] = true;
            Log.e("aaaa", threadId+"------------Download DONE!");
            /*boolean Done = true;
            for(int i=0;i<threadDone.length;i++){
                if(threadDone[i]){
                    Done = false;
                }
            }
            if(Done){
                try {
                    for(int i=0;i<novelText.length;i++){
                        fo.write(novelText[i].getBytes());
                        fo.flush();
                    }
                    fo.close();
                    Log.e("aaaa", "DownLoad DONE!");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }*/
        }
    }

    class WriteThread extends Thread{
        @Override
        public void run() {
            for(int i=0;i<threadDone.length;i++){
                while(!threadDone[i]){
                }
                try {
                    fo.write(novelText[i].getBytes());
                    fo.flush();
                    Log.e("aaaa",i+"------------Write DONE");
                    if(i==threadDone.length-1){
                        fo.close();
                        Log.e("aaaa", "ALL DONE!");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String downloadNovel(String urlStr){
        Document doc = null;
        String novelStr = "";
        try {
            doc = Jsoup.connect(urlStr).get();
            Element titleDiv = doc.getElementsByClass("bookname").first();
            Element titleH1 = titleDiv.getElementsByTag("h1").first();
            novelStr += titleH1.text();
            Element div = doc.getElementById("content");
            String novelContent = div.text();
            novelContent = novelContent.replace("     ","\r\n    ");
            novelContent = novelContent.replace("    ","\r\n    ");
            novelStr += novelContent+"\r\n";
        } catch (IOException e) {
            downloadNovel(urlStr);
            Log.d("aaaa","download_novel time out!");
        }
        return novelStr;
    }

    class CompleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            long completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if(completeDownloadId==downloadId){
                handler.sendEmptyMessage(0x123);
            }
        }
    };

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 0x123:
                    Bitmap bitmap = BitmapFactory.decodeFile(getApplicationContext().getExternalFilesDir("image").getPath()+File.separator+novelTitle+".png");
                    resultIcon.setImageBitmap(bitmap);
                    resultTitle.setText(novelTitle);
                    resultType.setText(novelType);
                    resultState.setText(novelState);
                    resultWordNum.setText(novelWordNum);
                    resultNew.setText(novelNew);
                    resultLayout.setVisibility(View.VISIBLE);
                    loadingLayout.setVisibility(View.GONE);
                    break;
                case 0x124:
                    Toast.makeText(SearchResultActivity.this,"Time out!",Toast.LENGTH_LONG).show();
                    loadingLayout.setVisibility(View.GONE);
                    timeOutLayout.setVisibility(View.VISIBLE);
                    break;
            }
            return true;
        }
    });

    class SearchThread extends Thread{
        @Override
        public void run() {
            Document doc = null;
            try {
                doc = Jsoup.connect("http://www.baidu.com/s?word="+searchContent+"&tn=39015028_hao_pg&ie=utf-8").timeout(5000).get();
                Elements h3 = doc.getElementsByClass("t");
                for (Element element : h3){
                    Element a = element.getElementsByTag("a").first();
                    if(a.text().contains("新笔趣阁")){
                        xinbiqugeUrl = a.attr("href");
                        new SearchBookUrl().start();
                    }
                    if(a.text().contains("起点")){
                        qidianUrl = a.attr("href");
                        new SearchBookInfo().start();
                    }
                }
            } catch (IOException e) {
                Log.e("aaaa","search-----timeout");
                searchCount++;
                if(searchCount<3){
                    new SearchThread().start();
                } else {
                    handler.sendEmptyMessage(0x124);
                }
            }
        }
    }

    class SearchBookUrl extends Thread{
        @Override
        public void run() {
            Document doc = null;
            try {
                doc = Jsoup.connect(xinbiqugeUrl).timeout(5000).get();
                Element head = doc.head();
                Element meta = head.child(4);
                String content = meta.attr("content");
                novelUrl = content.substring(content.indexOf("url")+4);
                novelUrl = novelUrl.replace("wap","www");
                Element div = doc.getElementById("list");
                Elements a = div.getElementsByTag("a");
                chapterNum = a.size();
                for(Element element : a){
                    if(a.text().contains("第一章")){
                        String strUrl = element.attr("href");
                        int index = strUrl.indexOf(".");
                        firstUrl = Integer.parseInt(strUrl.substring(0,index));
                        break;
                    }
                }
            } catch (IOException e) {
                Log.e("aaaa","url-----timeout");
                searchUrlCount++;
                if(searchUrlCount<3){
                    new SearchBookUrl().start();
                } else {
                    handler.sendEmptyMessage(0x124);
                }
            }
        }
    }

    class SearchBookInfo extends Thread{
        @Override
        public void run() {
            Document doc = null;
            try {
                doc = Jsoup.connect(qidianUrl).timeout(5000).get();
                Element iconDiv = doc.getElementsByClass("pic_box").first();
                Element iconA = iconDiv.getElementsByTag("a").first();
                Element iconImg = iconA.getElementsByTag("img").first();
                imageUrl = iconImg.attr("src");
                Element typeDiv = doc.getElementsByClass("info_box").first();
                Elements typeTd = typeDiv.getElementsByTag("td");
                StringBuilder builder = new StringBuilder();
                for(Element element : typeTd){
                    builder.append(element.text()+",");
                }
                int index1 = builder.toString().indexOf("小说类别");
                int index2 = builder.indexOf(",", index1);
                novelType = builder.substring(index1,index2);
                int index3 = builder.indexOf("写作进程");
                int index4 = builder.indexOf(",",index3);
                novelState = builder.substring(index3, index4);
                int index5 = builder.indexOf("完成字数");
                int index6 = builder.indexOf(",",index5);
                novelWordNum = builder.substring(index5, index6);
                Element newDiv = doc.getElementById("readV");
                Element newA = newDiv.getElementsByTag("a").first();
                novelNew += newA.text();
                new DownloadImage().start();
            } catch (IOException e) {
                searchInfoCount++;
                if(searchInfoCount<3){
                    new SearchBookInfo().start();
                } else {
                    handler.sendEmptyMessage(0x124);
                }
            }
        }
    }

    class DownloadImage extends Thread{
        @Override
        public void run() {
            File file = new File(getApplicationContext().getExternalFilesDir("image").getPath()+File.separator+novelTitle+".png");
            if(!file.exists()) {
                DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uri = Uri.parse(imageUrl);
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.allowScanningByMediaScanner();
                request.setDestinationUri(Uri.fromFile(file));
                downloadId = downloadManager.enqueue(request);
            } else {
                handler.sendEmptyMessage(0x123);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(completeReceiver);
    }
}
