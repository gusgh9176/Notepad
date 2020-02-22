package com.example.notepad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.notepad.adapter.RecyclerImageTextAdapter;
import com.example.notepad.db.ImageDB;
import com.example.notepad.db.NoteDB;
import com.example.notepad.vo.ImageVO;
import com.example.notepad.vo.RecyclerItemVO;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import java.util.ArrayList;
import java.util.List;

// 이미지 로딩 늦는 이슈 해결해야
public class MainActivity extends AppCompatActivity {

    RecyclerView mRecyclerView = null ;
    RecyclerImageTextAdapter mAdapter = null ;
    ArrayList<RecyclerItemVO> mList = new ArrayList<RecyclerItemVO>();

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


        //aasdasdsa
        mRecyclerView = findViewById(R.id.recycler1);
        // 리사이클러뷰에 LinearLayoutManager 지정. (vertical)
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
        mAdapter = new RecyclerImageTextAdapter(mList);
        mRecyclerView.setAdapter(mAdapter);

        //asdasdas

        mAdapter.notifyDataSetChanged();
    }
    private void setAdapter(ArrayList<RecyclerItemVO> list) {
        mAdapter = new RecyclerImageTextAdapter(list);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setNestedScrollingEnabled(false);
    }
    private void refreshCastAdapter() {
        if (mAdapter == null) return;
        ArrayList<RecyclerItemVO> list = mAdapter.getItems();
        setAdapter(list);
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

        switch (id){
            case R.id.action_write:
                Intent intent = new Intent(MainActivity.this, WriteActivity.class);
                startActivity(intent);
                Toast.makeText(this, "메모 작성 클릭", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_refresh:
//                addItemAdapter();
                Toast.makeText(this, "새로 고침", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        addItemAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshCastAdapter();
    }

    private void addItemAdapter() {

        for (int i = 0; i < NoteDB.getIndexes().size(); i++) {
            Drawable drawable;
            String index = NoteDB.getIndexes().get(i);
            String title = NoteDB.getNotepad(index).getTitleStr();
            String description = NoteDB.getNotepad(index).getDescription();

            ImageVO imageVO = ImageDB.getImage(index + 0);
            boolean delete = NoteDB.getNotepad(index).isDelete();

            System.out.println("1");

            if (delete) { // 삭제를 한 상태라면 해당 인덱스 작업 스킵
                continue;
            }
            System.out.println("2");

            // maxDescription 값보다 본문이 길면 값만큼 잘라서 보여줌
            if (description.length() > maxDescription) {
                description = description.substring(0, maxDescription) + "...";
            }


            if (imageVO != null) {
                ImageView imageView = setImage(imageVO.getImageUrl());

                drawable = imageView.getDrawable();
                System.out.println("3");
            }
            else{
                drawable = getDrawable(R.drawable.ic_action_camera);

                System.out.println("4");
            }
            addItem(drawable, index, title, description);
        }
    }

    private ImageView setImage(String path) {
        ImageView imageView = new AppCompatImageView(this);
//        Glide.with(this).load(path).error(R.drawable.ic_action_camera).override(320, 320).centerCrop().into(imageView);
        Glide.with(this).load(path).into(imageView);
        System.out.println(path);
//        imageView.invalidate();
        return imageView;
    }

    //asdasda
    public void addItem(Drawable icon, String index, String title, String desc) {
        RecyclerItemVO item = new RecyclerItemVO();

        item.setIconDrawable(icon);
        item.setIndex(index);
        item.setTitleStr(title);
        item.setDescStr(desc);

        mList.add(item);
    }
    //asdasd
}
