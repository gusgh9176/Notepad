package com.example.notepad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.notepad.db.NoteDB;
import com.example.notepad.util.ImageResizeUtils;
import com.example.notepad.vo.DetailNotepadVO;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;


public class WriteActivity extends AppCompatActivity {

    EditText inputTitle, inputText;
    private String key = null;
    private DetailNotepadVO detailNotepadVO = null;

    private static final int PICK_FROM_ALBUM = 1;
    private static final int PICK_FROM_CAMERA = 2;
    private static final String TAG = "WriteActivity";
    private File tempFile;
    private String packegeName = "com.example.notepad";
    boolean isCamera = false;

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
            case R.id.action_camera:
                takePhoto();
                break;
            case R.id.action_album:
                goToAlbum();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void goToAlbum() {
        isCamera = false;

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
            if(tempFile != null) {
                if (tempFile.exists()) {
                    if (tempFile.delete()) {
//                        Log.e(TAG, tempFile.getAbsolutePath() + " 삭제 성공");
                        tempFile = null;
                    }
                }
            }
            return;
        }

        if (requestCode == PICK_FROM_ALBUM) {
            Uri photoUri = data.getData();
            Cursor cursor = null;
            try {
                /*
                 *  Uri 스키마를
                 *  content:/// 에서 file:/// 로  변경한다.
                 */
                String[] proj = { MediaStore.Images.Media.DATA };

                assert photoUri != null;
                cursor = getContentResolver().query(photoUri, proj, null, null, null);

                assert cursor != null;
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                cursor.moveToFirst();

                tempFile = new File(cursor.getString(column_index));
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            setImage();
        }
        else if (requestCode == PICK_FROM_CAMERA) {
            setImage();
        }
    }

    private void setImage() {

        LinearLayout li = (LinearLayout) findViewById(R.id.picList);
        ImageView imageView = new AppCompatImageView(this);

        ImageResizeUtils.resizeFile(tempFile, tempFile, 320, isCamera);

        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap originalBm = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);

        imageView.setImageBitmap(originalBm);

        li.addView(imageView);

    }

    private void takePhoto() {

        isCamera = true;

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            tempFile = createImageFile();
        } catch (IOException e) {
            Toast.makeText(this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            finish();
            e.printStackTrace();
        }
        if (tempFile != null) {

            // Android OS 7 이후
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                Uri photoUri = FileProvider.getUriForFile(this, packegeName+".provider", tempFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, PICK_FROM_CAMERA);
            }
            // 이전
            else{
                Uri photoUri = Uri.fromFile(tempFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, PICK_FROM_CAMERA);
            }
        }
    }
    private File createImageFile() throws IOException {

        // 이미지 파일 이름 ( _{시간}_ )
        String timeStamp = DateFormat.getDateInstance(DateFormat.MEDIUM).format(new Date());
        String imageFileName = timeStamp + "_";

        // 이미지가 저장될 폴더 이름 ( 각 index(key) 값 )
        File storageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/"+key+"/");
        if (!storageDir.exists()) storageDir.mkdirs();

        // 빈 파일 생성
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        Log.d(TAG, "createImageFile : " + image.getAbsolutePath());

        return image;
    }
}
