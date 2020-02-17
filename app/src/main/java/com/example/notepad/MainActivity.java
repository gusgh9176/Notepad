package com.example.notepad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.notepad.adapter.ListViewAdapter;
import com.example.notepad.db.NoteDB;
import com.example.notepad.vo.ListViewNotepadVO;
import com.example.notepad.vo.DetailNotepadVO;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 액션바 설정하기 //
        // 액션바 타이틀 변경하기
        getSupportActionBar().setTitle("메모리스트");
        // 액션바 배경색 변경
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFF339999));
        // 액션바 설정 끝 //

        ListView listview ;
        ListViewAdapter adapter;

        // Adapter 생성
        adapter = new ListViewAdapter() ;

        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) findViewById(R.id.listview1);
        listview.setAdapter(adapter);

        prepareNoteDB(); // 임시 DB 생성

        for ( int i = 0; i < NoteDB.getIndexes().size(); i++ ) {
            String index = NoteDB.getIndexes().get(i);
            String description = NoteDB.getArticle(index).getDescription();
            adapter.addItem(ContextCompat.getDrawable(this, R.drawable.ic_action_camera), index, description);
        }

        // 위에서 생성한 listview에 클릭 이벤트 핸들러 정의.
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // get item
                ListViewNotepadVO item = (ListViewNotepadVO) parent.getItemAtPosition(position) ;

                String titleStr = item.getTitle() ;
                String descStr = item.getDesc() ;
                Drawable iconDrawable = item.getIcon() ;

                // TODO : use item data.
                // DetailActivity로 index 값 전달
                Intent intent = new Intent(v.getContext(), DetailActivity.class);
                intent.putExtra("key", titleStr);
                startActivity(intent);
            }
        }) ;

    }

    // 액션버튼 메뉴 액션바에 집어 넣기
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    // 액션버튼을 클릭했을때의 동작
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        //or switch문을 이용하면 될듯 하다.
        if (id == R.id.action_write) {
            Intent intent = new Intent(MainActivity.this, WriteActivity.class);
            startActivity(intent);

            Toast.makeText(this, "메모 작성 클릭", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void prepareNoteDB() {
        for ( int i = 1; i < 100; i++ ){
            NoteDB.addArticle(i + "번 메모", new DetailNotepadVO(i, i + "번 메모 제목", i + "번 메모 내용"));
        }
    }

}
