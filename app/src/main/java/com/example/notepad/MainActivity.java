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
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView listview;
    ListViewAdapter adapter;

    private final int maxDescription = 10; // MainActivity에서 보여줄 메모 본문 내용의 최대 개수

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

        addItemAdapter(); // DB.load 통해서 불러온 DB 데이터 adapter 통해 화면에 출력

        // addItemAdapter() 함수 내부에서 생성한 listview에 클릭 이벤트 핸들러 정의.
        // 메모 클릭시 key를 담아서 DetailActivity 실행시켜 자세한 내용 볼 수 있게 해줌
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

        switch (id){
            case R.id.action_write: // actionbar의 write 키 눌렀을 때 동작
                // WriteActivity 실행 시킴
                Intent intent = new Intent(MainActivity.this, WriteActivity.class);
                startActivity(intent);
                Toast.makeText(this, "메모 작성 클릭", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_refresh: // actionbar의 refresh 키 눌렀을 때 동작
                // addItemAdapter() 함수 통해 adapter와 listview를 재 생성 시켜줌
                addItemAdapter();
                Toast.makeText(this, "새로 고침", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() { // 다른 Activity에서 MainActivity 로 돌아왔을 때 새로 고침 실행
        super.onResume();
        addItemAdapter();
    }

    // 첫 실행시 사용자에게 권한 요청 부분
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
        // 쓰기, 카메라, 인터넷 권한 요구
        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.permission_2))
                .setDeniedMessage(getResources().getString(R.string.permission_1))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.INTERNET)
                .check();
    }

    private void addItemAdapter() {
        // Adapter 생성
        adapter = new ListViewAdapter();
        // Drawable 생성
        Drawable drawable = null;
        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) findViewById(R.id.listview1);
        listview.setAdapter(adapter);

        // NoteDB 통해서 메모의 내용 정보 받아옴
        for (int i = 0; i < NoteDB.getIndexes().size(); i++) {
            String index = NoteDB.getIndexes().get(i);
            boolean delete = NoteDB.getNotepad(index).isDelete();
            String title = NoteDB.getNotepad(index).getTitleStr();
            String description = NoteDB.getNotepad(index).getDescription();

            if (delete) { // 삭제를 한 상태라면 해당 인덱스 작업 스킵
                continue;
            }

            // 화면에서 일부분 내용 보여지게 함
            // 엔터키 제거
            // maxDescription 값보다 본문이 길면 값만큼 잘라서 보여줌
            if (description.length() > maxDescription) {
                description = description.replaceAll("\n","").substring(0, maxDescription) + "...";
            }
            else{
                description = description.replaceAll("\n","");
            }

            // ImageDB 통해서 메모의 이미지 정보 받아옴
            // 미리 보기 이미지 설정해주는 부분
            for(int j=0; j< ImageDB.getIndexes().size(); j++) {
                ImageVO imageVO = ImageDB.getImage(index + j);
                if (imageVO != null && !imageVO.isDelete()) { // 이미지가 존재하고, 그 이미지가 delete 되지 않았을 때
                    ImageView imageView = setImage(imageVO.getImageUrl());
                    drawable = imageView.getDrawable();
                    break;
                }
                else{ // 이미지가 미존재하거나 해당 메모의 모든 이미지가 delete 되었을 때
                    drawable = ContextCompat.getDrawable(this, R.drawable.ic_action_camera);
                }
            }
            // adapter에 Item 추가하여 사용자 화면에 보이게함
            adapter.addItem(drawable, index, title, description);
        }
        adapter.notifyDataSetChanged(); // adapter 새로 고침
    }

    // 매개변수로 path(경로) 정보 받아 imageView에 추가함
    // 메모 내용 왼쪽 미리 보기 이미지
    private ImageView setImage(String path) {
        ImageView imageView = new AppCompatImageView(this);
        Glide.with(this).load(path).override(320, 320).centerCrop().into(imageView);
        imageView.invalidate();
        return imageView;
    }
}
