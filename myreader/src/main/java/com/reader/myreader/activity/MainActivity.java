package com.reader.myreader.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.reader.myreader.R;
import com.reader.myreader.bean.NovelBean;
import com.reader.myreader.bean.PopupListBean;
import com.reader.myreader.function.GridViewAdapter;
import com.reader.myreader.function.PopupListAdapter;

public class MainActivity extends BaseActivity implements View.OnClickListener,AdapterView.OnItemClickListener {

    private GridView novelGridView;
    private Button listSwitch;
    private Button management;
    private RelativeLayout addBookLayout;
    private ImageButton addBook;
    private LinearLayout searchText;
    private List<NovelBean> list = new ArrayList<>();
    private int bookNum = 0;
    private AlertDialog dialog = null;
    private int windowWidth;
    private int windowHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindowSize();//获取屏幕的宽和高，留着后面用
        setStatusBackground(0xFF47B3A8);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.home_back);
        toolbar.setTitle("书架");
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
        novelGridView = (GridView) findViewById(R.id.novel_grid);
        listSwitch = (Button) findViewById(R.id.list_or_grid);
        management = (Button) findViewById(R.id.management);
        addBookLayout = (RelativeLayout) findViewById(R.id.add_book_layout);
        addBook = (ImageButton) findViewById(R.id.add_book);
        searchText = (LinearLayout) findViewById(R.id.search_text);
        bookNum = getBookNum();
        if(bookNum==0){
            novelGridView.setVisibility(View.GONE);
            listSwitch.setVisibility(View.GONE);
            management.setVisibility(View.GONE);
        } else {
            addBookLayout.setVisibility(View.GONE);
            SharedPreferences sharedPreferences = getSharedPreferences("book",MODE_WORLD_WRITEABLE);
            String imgBasePath = getApplicationContext().getExternalFilesDir("image").getPath()+ File.separator;
            NovelBean bean;
            Bitmap bitmap = null;
            for(int i=0;i<bookNum;i++){
                bean = new NovelBean();
                String title = sharedPreferences.getString("book_"+i,null);
                if(title!=null){
                    bitmap = BitmapFactory.decodeFile(imgBasePath+title+".png");
                    if(bitmap!=null){
                        bean.setNovelIcon(bitmap);

                    } else {
                        //
                    }
                    bean.setNovelTitle(title);
                    list.add(bean);
                }
            }
            bean = new NovelBean();
            list.add(bean);
            GridViewAdapter adapter = new GridViewAdapter(this,list);
            novelGridView.setAdapter(adapter);
            novelGridView.setOnItemClickListener(this);
        }
        searchText.setOnClickListener(this);
        addBook.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.search_text:
                Intent intent = new Intent(MainActivity.this,SearchBookActivity.class);
                startActivity(intent);
                break;
            case R.id.add_book:
                showDialog();
                break;
        }
    }

    public void showDialog(){
        dialog = new AlertDialog.Builder(this,R.style.FullScreenDialog).create();
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_layout,null);
        ListView listView = (ListView) view.findViewById(R.id.dialog_listview);
        int[] icon = {R.drawable.book_search,R.drawable.book_import};
        String[] title = {"搜索书籍","导入数据"};
        String[] describe = {"想搜啥搜啥","导入本地书籍"};
        List<PopupListBean> popList = new ArrayList<>();
        PopupListBean bean = null;
        for(int i=0;i<2;i++){
            bean = new PopupListBean();
            bean.setPopupIcon(icon[i]);
            bean.setPopupTitle(title[i]);
            bean.setPopupDescribe(describe[i]);
            popList.add(bean);
        }
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(windowWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
        dialog.setContentView(view,params);
        PopupListAdapter adapter = new PopupListAdapter(this,popList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = null;
                switch (position){
                    case 0:
                        intent = new Intent(MainActivity.this,SearchBookActivity.class);
                        dialog.dismiss();
                        break;
                    case 1:
                        intent = new Intent(MainActivity.this,ImportBookActivity.class);
                        dialog.dismiss();
                        break;
                }
                if(intent!=null){
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_import) {
            Intent intent = new Intent(MainActivity.this,ImportBookActivity.class);
            startActivity(intent);
        }
        if (id == R.id.action_manage) {

        }
        if(id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public int getBookNum(){
        SharedPreferences sharedPreferences = getSharedPreferences("book",MODE_WORLD_WRITEABLE);
        return sharedPreferences.getInt("book_num",0);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(position == bookNum){
            showDialog();
        } else {
            Toast.makeText(MainActivity.this,list.get(position).getNovelTitle(),Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this,NovelContentActivity.class);
            intent.putExtra("novel_name",list.get(position).getNovelTitle());
            startActivity(intent);
        }
    }

    private void getWindowSize(){
        WindowManager manager = getWindowManager();
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        windowWidth = metrics.widthPixels;
        windowHeight = metrics.heightPixels;
    }
}
