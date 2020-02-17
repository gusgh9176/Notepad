package com.example.notepad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.notepad.db.NoteDB;
import com.example.notepad.vo.DetailNotepadVO;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class WriteActivity extends AppCompatActivity {

    Button load, save;
    EditText inputTitle, inputText;

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

        load= (Button) findViewById(R.id.load);
        save = (Button) findViewById(R.id.save);
        inputTitle = (EditText) findViewById(R.id.inputTitle);
        inputText = (EditText) findViewById(R.id.inputText);

        load.setOnClickListener(listener);
        save.setOnClickListener(listener);
    }
    View.OnClickListener listener = new View.OnClickListener() {

        @Override

        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.load:
                    Log.i("TAG", "load 진행");
                    FileInputStream fis = null;
                    try{
                        NoteDB.load(getFilesDir());
                        Toast.makeText(WriteActivity.this, "load 완료", Toast.LENGTH_SHORT).show();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    break;

                case R.id.save:
                    Log.i("TAG", "save 진행");
                    FileOutputStream fos = null;
                    int size = NoteDB.getIndexes().size();
                    try {
                        System.out.println("size"+size);
                        NoteDB.addArticle(size+"번 메모", new DetailNotepadVO(size, inputTitle.getText().toString(), inputText.getText().toString()));
                        NoteDB.save(getFilesDir());

                        Toast.makeText(WriteActivity.this, "save 완료", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }

    };
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
}
