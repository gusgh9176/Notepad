package com.example.notepad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.example.notepad.util.BitmapResizeUtils;
import com.example.notepad.vo.DetailNotepadVO;
import com.example.notepad.vo.ImageVO;


public class DetailActivity extends AppCompatActivity {

    private TextView tvTitle;
    private TextView tvNotepadNumber;
    private TextView tvDescription;
    private String key;

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

        ImageDB.load(getFilesDir()); // 이전 이미지 불러오기

        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvNotepadNumber = (TextView) findViewById(R.id.tvNotepadNumber);
        tvDescription = (TextView) findViewById(R.id.tvDescription);

        Intent intent = getIntent();
        key = intent.getStringExtra("key");

        DetailNotepadVO articleVO = NoteDB.getNotepad(key);
        int imageOrder = 0;
        ImageVO imageVO = ImageDB.getImage(key+imageOrder);
        tvTitle.setText(articleVO.getTitleStr());
        tvNotepadNumber.setText(Integer.toString(articleVO.getNotepadNo()));
        tvDescription.setText(articleVO.getDescription());

        while(imageVO != null){

            setImage(imageVO.getImageUrl());
            imageOrder++;
            imageVO = ImageDB.getImage(key+imageOrder);
        }

    }

    private void setImage(String path) {
        LinearLayout li = (LinearLayout) findViewById(R.id.detailPicList);
        ImageView imageView = new AppCompatImageView(this);

        Glide.with(this).load(path).override(1280, 720).centerCrop().into(imageView);

        li.addView(imageView);
    }

    // 액션버튼을 클릭했을때의 동작
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home: // actionbar의 back 키 눌렀을 때 동작
                finish();
                break;
            case R.id.action_edit: // edit 키 눌렀을 때 WriteActivity로 key 담은 intent 전달
                // TODO : use key data.
                // WriteActivity index 값 전달
                Intent intent = new Intent(DetailActivity.this, WriteActivity.class);
                intent.putExtra("key", key);
                startActivity(intent);
                finish();
                break;
            case R.id.action_delete: // actionbar의 delete 키 눌렀을 때 동작
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
