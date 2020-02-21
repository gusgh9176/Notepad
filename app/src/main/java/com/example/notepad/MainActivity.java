package com.example.notepad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.notepad.adapter.ListViewAdapter;
import com.example.notepad.db.ImageDB;
import com.example.notepad.db.NoteDB;
import com.example.notepad.vo.ImageVO;
import com.example.notepad.vo.ListViewNotepadVO;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listview;
    ListViewAdapter adapter;

    private final int maxDescription = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tedPermission(); // 권한 체크

        // 액션바 설정하기 //
        // 액션바 타이틀 변경하기
        getSupportActionBar().setTitle("메모리스트");
        // 액션바 배경색 변경
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFF339999));
        // 액션바 설정 끝 //

        ImageDB.load(getFilesDir()); // 이전 이미지 불러오기
        NoteDB.load(getFilesDir()); // 이전 메모 불러오기

        addItemAdapter();

        // 위에서 생성한 listview에 클릭 이벤트 핸들러 정의.
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // get item
                ListViewNotepadVO item = (ListViewNotepadVO) parent.getItemAtPosition(position);

                String index = item.getIndex();
                String titleStr = item.getTitleStr();
                String descStr = item.getDescStr();
                Drawable iconDrawable = item.getIconDrawable();

                // TODO : use item data.
                // DetailActivity로 index 값 전달
                Intent intent = new Intent(v.getContext(), DetailActivity.class);
                intent.putExtra("key", index);
                startActivity(intent);
            }
        });
    }

    private void tedPermission() {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // 권한 요청 성공
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                // 권한 요청 실패
            }
        };
        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.permission_2))
                .setDeniedMessage(getResources().getString(R.string.permission_1))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.INTERNET)
                .check();
    }

    // 액션버튼 메뉴 액션바에 집어 넣기
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    // 액션버튼을 클릭했을때의 동작
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //or switch문을 이용하면 될듯 하다.
        if (id == R.id.action_write) {
            Intent intent = new Intent(MainActivity.this, WriteActivity.class);
            startActivity(intent);

            Toast.makeText(this, "메모 작성 클릭", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.action_refresh) {
            addItemAdapter();
            Toast.makeText(this, "새로 고침", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        addItemAdapter();
    }

    private void addItemAdapter() {
        // Adapter 생성
        adapter = new ListViewAdapter();
        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) findViewById(R.id.listview1);
        listview.setAdapter(adapter);
        for (int i = 0; i < NoteDB.getIndexes().size(); i++) {
            String index = NoteDB.getIndexes().get(i);
            boolean delete = NoteDB.getNotepad(index).isDelete();
            String title = NoteDB.getNotepad(index).getTitleStr();
            String description = NoteDB.getNotepad(index).getDescription();

            ImageVO imageVO = ImageDB.getImage(index + 0);
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_action_camera);
            if (imageVO != null) {
                ImageView imageView = setImage(imageVO.getImageUrl());
                drawable = imageView.getDrawable();
            }

            // maxDescription 값보다 본문이 길면 값만큼 잘라서 보여줌
            if (description.length() > maxDescription) {
                description = description.substring(0, maxDescription) + "...";
            }

            if (delete) { // 삭제를 한 상태라면 해당 인덱스 작업 스킵
                continue;
            }
            adapter.addItem(drawable, index, title, description);
            adapter.notifyDataSetChanged();

        }
    }

    private ImageView setImage(String path) {
        ImageView imageView = new AppCompatImageView(this);
        Glide.with(this).load(path).override(320, 320).centerCrop().into(imageView);
        return imageView;
    }
}
