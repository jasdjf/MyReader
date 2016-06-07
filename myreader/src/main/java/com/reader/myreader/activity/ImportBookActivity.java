package com.reader.myreader.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.reader.myreader.R;


/**
 * Created by zhuangwei on 2016/5/28.
 */
public class ImportBookActivity extends BaseActivity implements View.OnClickListener {

    private Button autoScan;
    private Button manuallyChoose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_book);
        setStatusBackground(0xFF47B3A8);
        Toolbar toolbar = (Toolbar) findViewById(R.id.import_toolbar);
        toolbar.setNavigationIcon(R.drawable.home_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setTitle("导入本地书籍");
        toolbar.setTitleTextColor(0xFFFFFFFF);
        autoScan = (Button) findViewById(R.id.import_atuo_scan);
        manuallyChoose = (Button) findViewById(R.id.import_choose);
        autoScan.setOnClickListener(this);
        manuallyChoose.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.import_atuo_scan:
                Log.d("aaaa","智能扫描");
                break;
            case R.id.import_choose:
                Log.d("aaaa","手动选择");
                break;
        }
    }
}
