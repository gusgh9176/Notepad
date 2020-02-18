package com.example.notepad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.notepad.db.NoteDB;
import com.example.notepad.vo.DetailNotepadVO;


public class WriteActivity extends AppCompatActivity {

    EditText inputTitle, inputText;
    private String key = null;
    private DetailNotepadVO detailNotepadVO = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        // 액션바 설정하기 //
        // 액션바 타이틀 변경하기
        getSupportActionBar().setTitle("새 메모 작성하기");
        // 액션바 뒤로가기 버튼 추가
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // 액션바 배경색 변경
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFF339999));
        // 액션바 설정 끝 //

        inputTitle = (EditText) findViewById(R.id.inputTitle);
        inputText = (EditText) findViewById(R.id.inputText);

        // Intent 받아옴
        Intent intent = getIntent();
        if(intent.hasExtra("key")) {
            key = intent.getStringExtra("key");
            detailNotepadVO = NoteDB.getArticle(key);
            inputTitle.setText(detailNotepadVO.getTitleStr());
            inputText.setText(detailNotepadVO.getDescription());
        }
    }

    // 액션버튼 메뉴 액션바에 집어 넣기
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.write_menu, menu);
        return true;
    }

    // 액션버튼을 클릭했을때의 동작
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home: { // actionbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
            case R.id.action_writeComplete: { // actionbar의 작성 완료 눌렀을 때 동작
                Log.i("TAG", "save 진행");
                int size = NoteDB.getIndexes().size();
                try {
                    if(key != null){ // 편집시 실행되는 부분
                        NoteDB.addArticle(key, new DetailNotepadVO(detailNotepadVO.getNotepadNo(), inputTitle.getText().toString(), inputText.getText().toString()));
                    }
                    else { // 작성시 실행되는 부분
                        NoteDB.addArticle(size + "번 메모", new DetailNotepadVO(size, inputTitle.getText().toString(), inputText.getText().toString()));
                    }
                    NoteDB.save(getFilesDir());
                    Toast.makeText(WriteActivity.this, "save 완료", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    finish();
                }
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
