package com.reader.myreader.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.reader.myreader.R;

/**
 * Created by zhuangwei on 2016/5/27.
 */
public class SearchBookActivity extends BaseActivity {

    private ImageView deleteSearchContent;
    private TextView searchOrCancel;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_book);
        setStatusBackground(0xFFD2D2D2);
        deleteSearchContent = (ImageView) findViewById(R.id.delete_search_content);
        searchOrCancel = (TextView) findViewById(R.id.search_or_cancel);
        editText = (EditText) findViewById(R.id.search_content);
        deleteSearchContent.setVisibility(View.INVISIBLE);
        if(editText.getText()==null || editText.getText().length()==0){
            deleteSearchContent.setVisibility(View.INVISIBLE);
            searchOrCancel.setText(R.string.cancel);
        } else {
            deleteSearchContent.setVisibility(View.VISIBLE);
            searchOrCancel.setText(R.string.search);
        }
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                deleteSearchContent.setVisibility(View.VISIBLE);
                searchOrCancel.setText(R.string.search);
                if(editText.getText()==null || editText.getText().length()==0){
                    deleteSearchContent.setVisibility(View.INVISIBLE);
                    searchOrCancel.setText(R.string.cancel);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    InputMethodManager manager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(),0);
                    String searchContent = editText.getText().toString().trim();
                    Intent intent = new Intent(SearchBookActivity.this,SearchResultActivity.class);
                    intent.putExtra("search_content",searchContent);
                    startActivity(intent);
                    finish();
                }
                return true;
            }
        });
        deleteSearchContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText(null);
                deleteSearchContent.setVisibility(View.INVISIBLE);
                searchOrCancel.setText(R.string.cancel);
            }
        });
        searchOrCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(searchOrCancel.getText().toString().equals("搜索")){
                    String searchContent = editText.getText().toString().trim();
                    Intent intent = new Intent(SearchBookActivity.this,SearchResultActivity.class);
                    intent.putExtra("search_content",searchContent);
                    startActivity(intent);
                    finish();
                } else if(searchOrCancel.getText().toString().equals("取消")){
                    finish();
                }
            }
        });
    }
}
