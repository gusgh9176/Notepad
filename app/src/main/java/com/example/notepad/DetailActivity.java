package com.example.notepad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.notepad.db.ImageDB;
import com.example.notepad.db.NoteDB;
import com.example.notepad.vo.DetailNotepadVO;
import com.example.notepad.vo.ImageVO;


public class DetailActivity extends AppCompatActivity {

    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        TextView tvTitle;
        TextView tvNotepadNumber;
        TextView tvDescription;
        int imageOrder = 0; // 메모의 몇번째 이미지인지 판단하는 숫자

        // 액션바 설정하기 //
        // 액션바 타이틀 변경하기
        getSupportActionBar().setTitle("상세 메모 보기");
        // 액션바 뒤로가기 버튼 추가
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // 액션바 배경색 변경
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFF339999));
        // 액션바 설정 끝

        // 뷰 변수에 받아오기
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvNotepadNumber = (TextView) findViewById(R.id.tvNotepadNumber);
        tvDescription = (TextView) findViewById(R.id.tvDescription);

        // MainActivity에서 클릭한 메모의 DB key 값 받아옴
        Intent intent = getIntent();
        key = intent.getStringExtra("key");

        // 받아온 key 값 통해서 DB에서 해당 메모에 대한 내용(제목, 본문), 이미지 받아옴
        // key 값 = NoteDB Map<String, DetailNotepadVO> db 변수의 key, 메모를 구분해줌
        // key + imageOrder = ImageDB Map<String, ImageVO> db 변수의 key, 앞선 key로 어느 메모에 있는 이미지인지 판단하고 imageOrder 통해 메모 내부의 이미지 구분해줌
        DetailNotepadVO detailNotepadVO = NoteDB.getNotepad(key);
        ImageVO imageVO = ImageDB.getImage(key + imageOrder);

        // 받아온 내용 변수에 저장 후 화면에 세팅
        String title = detailNotepadVO.getTitleStr();
        String notepadNumber = Integer.toString(detailNotepadVO.getNotepadNo());
        String description = detailNotepadVO.getDescription();
        tvTitle.setText(title);
        tvNotepadNumber.setText(notepadNumber);
        tvDescription.setText(description);

        // 메모에 이미지가 있다면 반복문 진행함
        while (imageVO != null) {
            if(!imageVO.isDelete()) { // 이미지가 삭제 작업을 받은 이미지가 아닌 경우 화면에 세팅
                setImage(imageVO.getImageUrl());
            }
            // 메모의 다음 이미지를 불러오기 위해 imageOrder의 값을 1 늘려주고 새로 이미지를 얻어옴
            imageOrder++;
            imageVO = ImageDB.getImage(key + imageOrder);
        }

    }

    // 이미지를 표시하는 레이아웃에 임시 imageView 객체 만들어서 Glide 함수 통하여 이미지뷰를 추가해줌
    private void setImage(String path) {
        LinearLayout li = (LinearLayout) findViewById(R.id.detailPicList);
        ImageView imageView = new AppCompatImageView(this);

        Glide.with(this).load(path).override(1280, 720).centerCrop().into(imageView);

        li.addView(imageView);
    }

    // actionbar의 버튼을 눌렀을때의 동작
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // actionbar의 back 키 눌렀을 때 동작
                // 현재 DetailActivity를 종료 시키고, 이전 Activity(MainActivity)로 이동시켜줌
                finish();
                break;
            case R.id.action_edit: // actionbar의 edit 키 눌렀을 때 동작
                // 기존에 있던 메모 편집
                // key를 담아서 WriteActivity를 실행시키고 DetailActivity는 종료
                Intent intent = new Intent(DetailActivity.this, WriteActivity.class);
                intent.putExtra("key", key);
                startActivity(intent);
                finish();
                break;
            case R.id.action_delete: // actionbar의 delete 키 눌렀을 때 동작
                // 각 DB 객체에 들어있는 db 변수의 내부 데이터 값(여기선 delete 변수)을 변경하여 삭제된 데이터 판단을 함
                NoteDB.getNotepad(key).setDelete(true); // NoteDB의 delete 변수를 true로 만들어 삭제된 데이터라고 알림
                NoteDB.save(getFilesDir());
                Toast.makeText(this, "해당 메모가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
                break;
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
