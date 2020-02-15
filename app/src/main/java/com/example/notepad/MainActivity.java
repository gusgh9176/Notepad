package com.example.notepad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.notepad.db.NoteDB;
import com.example.notepad.vo.NotepadVO;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //액션바 설정하기//
        //액션바 타이틀 변경하기
        getSupportActionBar().setTitle("메모리스트");
        //액션바 배경색 변경
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFF339999));

        prepareNoteDB();

        LinearLayout ll = (LinearLayout) findViewById(R.id.itemList);

        // 반복 시작
        for ( int i = 0; i < NoteDB.getIndexes().size(); i++ ) {
            Button button = new AppCompatButton(this);
            button.setText(NoteDB.getIndexes().get(i));

            

            ll.addView(button);
        }
        // 반복 끝

    }

    //액션버튼 메뉴 액션바에 집어 넣기
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    //액션버튼을 클릭했을때의 동작
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        //or switch문을 이용하면 될듯 하다.
        if (id == R.id.action_write) {
            Toast.makeText(this, "메모 작성 클릭", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void prepareNoteDB() {
        for ( int i = 1; i < 100; i++ ){
            NoteDB.addArticle(i + "번 메모", new NotepadVO(i, i + "번 메모 제목", i + "번 메모 내용"));
        }
    }

}
