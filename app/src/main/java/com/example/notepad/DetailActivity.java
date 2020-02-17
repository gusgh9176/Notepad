package com.example.notepad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.notepad.db.NoteDB;
import com.example.notepad.vo.DetailNotepadVO;

public class DetailActivity extends AppCompatActivity {

    private TextView tvTitle;
    private TextView tvNotepadNumber;
    private TextView tvDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // 액션바 설정하기 //
        // 액션바 타이틀 변경하기
        getSupportActionBar().setTitle("상세 메모 보기");
        // 액션바 뒤로가기 버튼 추가
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // 액션바 배경색 변경
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFF339999));
        // 액션바 설정 끝

        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvNotepadNumber = (TextView) findViewById(R.id.tvNotepadNumber);
        tvDescription = (TextView) findViewById(R.id.tvDescription);

        Intent intent = getIntent();
        String key = intent.getStringExtra("key");

        DetailNotepadVO articleVO = NoteDB.getArticle(key);

        tvTitle.setText(articleVO.getTitleStr());
        tvNotepadNumber.setText(Integer.toString(articleVO.getNotepadNo()));
        tvDescription.setText(articleVO.getDescription());
    }

    // 액션버튼을 클릭했을때의 동작
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ // actionbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    // 액션버튼 메뉴 액션바에 집어 넣기
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }
}
