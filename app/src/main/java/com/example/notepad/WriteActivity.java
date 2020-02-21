package com.example.notepad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.FileProvider;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.notepad.db.ImageDB;
import com.example.notepad.db.NoteDB;
import com.example.notepad.util.BitmapResizeUtils;
import com.example.notepad.util.ImageResizeUtils;
import com.example.notepad.vo.DetailNotepadVO;
import com.example.notepad.vo.ImageVO;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// 처음 작성시 key에 null 있음!!!!!!!!! 수정필요!!!
// 편집시 imageOrder 정해줘야함
public class WriteActivity extends AppCompatActivity {

    EditText inputTitle, inputText;
    Button btnInputPic;
    Bitmap urlBitmap;

    private String key = null; // 편집 시 키
    private DetailNotepadVO detailNotepadVO = null;
    private int imageOrder;
    private int NotepadNo;

    private static final int PICK_FROM_ALBUM = 1;
    private static final int PICK_FROM_CAMERA = 2;
    private static final String TAG = "WriteActivity";
    private static final int resizePicSize = 320;

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

        btnInputPic = (Button) findViewById(R.id.btnInputPic);
        inputTitle = (EditText) findViewById(R.id.inputTitle);
        inputText = (EditText) findViewById(R.id.inputText);

        btnInputPic.setOnClickListener(listener);

        // Intent 받아옴
        Intent intent = getIntent();
        // 받아온 Intent에 key 존재 = 기존 것 편집
        if(intent.hasExtra("key")) {
            key = intent.getStringExtra("key");
            detailNotepadVO = NoteDB.getNotepad(key);
            inputTitle.setText(detailNotepadVO.getTitleStr());
            inputText.setText(detailNotepadVO.getDescription());
        }
        else{
            imageOrder = 0;
            NotepadNo = NoteDB.getIndexes().size();
            key = NotepadNo + "번 메모";
        }
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnInputPic: {
                    selPicInput();
                    break;
                }
            }
        }

    };
    private void selPicInput()
    {
        final List<String> ListItems = new ArrayList<>();
        ListItems.add("카메라");
        ListItems.add("앨범");
        ListItems.add("URL");
        final CharSequence[] items =  ListItems.toArray(new String[ ListItems.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("추가 방식");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int pos) {
                String selectedText = items[pos].toString();
                // pos = 0 카메라, pos = 1 앨범, pos = 2 URL
                switch (pos){
                    case 0:
                        takePhoto();
                        break;
                    case 1:
                        goToAlbum();
                        break;
                    case 2:
                        selUrlInput();
                        break;
                }
                Toast.makeText(getApplicationContext(), selectedText, Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
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
                try {
                    if(detailNotepadVO != null){ // 편집시 실행되는 부분
                        NoteDB.addNotepad(key, new DetailNotepadVO(detailNotepadVO.getNotepadNo(), inputTitle.getText().toString(), inputText.getText().toString()));
                    }
                    else { // 작성시 실행되는 부분
                        NoteDB.addNotepad(key, new DetailNotepadVO(NotepadNo, inputTitle.getText().toString(), inputText.getText().toString()));
                    }
                    ImageDB.save(getFilesDir());
                    NoteDB.save(getFilesDir()); // DB 파일 경로에 저장
                    Toast.makeText(getApplicationContext(), "작성 완료", Toast.LENGTH_SHORT).show();
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

    private void takePhoto() {

        isCamera = true;

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            tempFile = createImageFile();
            // @@@@@@@@@@@@@@@@@@@@@ 카메라 통해 사진 추가(context, imagePath, imgKind, memoKey)
            ImageDB.addImage(key + imageOrder, new ImageVO(key, 0, tempFile.getAbsolutePath(), imageOrder));
            imageOrder++;
            System.out.println(tempFile.getAbsolutePath());
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
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

    private void goToAlbum() {
        isCamera = false;

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    private void selUrlInput(){
        final EditText edittext = new EditText(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("URL");
        builder.setMessage("원하는 이미지 URL을 입력해주세요");
        builder.setView(edittext);
        builder.setPositiveButton("입력",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        setUrlImage("https://pbs.twimg.com/media/ERESihnU8AAptEW?format=jpg&name=small");
//                        setUrlImage(edittext.getText().toString());
                    }
                });
        builder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"취소되었습니다" ,Toast.LENGTH_SHORT).show();
                    }
                });
        builder.show();
    }

    // Url로 이미지 추가
    private void setUrlImage(String baseURL){
        final String baseImageURL = baseURL;
        LinearLayout li = (LinearLayout) findViewById(R.id.picList);
        ImageView imageView = new AppCompatImageView(this);

        Thread mThread = new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(baseImageURL); // URL 주소를 이용해서 URL 객체 생성

                    //  아래 코드는 웹에서 이미지를 가져온 뒤
                    //  이미지 뷰에 지정할 Bitmap을 생성하는 과정

                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is = conn.getInputStream();
                    urlBitmap = BitmapFactory.decodeStream(is);
                } catch(IOException ex) {
                    ex.printStackTrace();
                }
            }
        };
        mThread.start(); // 웹에서 이미지를 가져오는 작업 스레드 실행.
        try {
            //  메인 스레드는 작업 스레드가 이미지 작업을 가져올 때까지
            //  대기해야 하므로 작업스레드의 join() 메소드를 호출해서
            //  메인 스레드가 작업 스레드가 종료될 까지 기다리도록 합니다.

            // @@@@@@@@@@@@@@@@@@@@@ Url 통해 사진 추가(context, imagePath, imgKind, memoKey)
            ImageDB.addImage(key + imageOrder, new ImageVO(key, 0, baseImageURL, imageOrder));
            imageOrder++;

            System.out.println(baseImageURL);

            mThread.join();

            //  이제 작업 스레드에서 이미지를 불러오는 작업을 완료했기에
            //  UI 작업을 할 수 있는 메인스레드에서 이미지뷰에 이미지를 지정합니다.

            urlBitmap = BitmapResizeUtils.resizeBitmap(urlBitmap, resizePicSize);
            imageView.setImageBitmap(urlBitmap);

            li.addView(imageView);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"잘못된 URL 입니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // 카메라 실행 후, 앨범 사진 선택 후 실행되는 메소드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(getApplicationContext(), "취소 되었습니다.", Toast.LENGTH_SHORT).show();
            if(tempFile != null) {
                if (tempFile.exists()) {
                    if (tempFile.delete()) {
                        tempFile = null;
                    }
                }
            }
            return;
        }

        // 카메라 선택
        if (requestCode == PICK_FROM_CAMERA) {
            setImage();
        }

        // 앨범 선택
        else if (requestCode == PICK_FROM_ALBUM) {
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
                System.out.println(tempFile.getAbsolutePath());

                // @@@@@@@@@@@@@@@@@@@@@ 앨범 통해 사진 추가(context, imagePath, imgKind, memoKey)
                ImageDB.addImage(key + imageOrder, new ImageVO(key, 0, tempFile.getAbsolutePath(), imageOrder));
                imageOrder++;
                System.out.println("Write Key: "+key+imageOrder);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            setImage();
        }
    }

    private void setImage() {

        LinearLayout li = (LinearLayout) findViewById(R.id.picList);
        ImageView imageView = new AppCompatImageView(this);

        Glide.with(this).load(tempFile).override(resizePicSize, resizePicSize).centerCrop().into(imageView);

        li.addView(imageView);
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
