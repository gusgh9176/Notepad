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
import com.example.notepad.vo.DetailNotepadVO;
import com.example.notepad.vo.ImageVO;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WriteActivity extends AppCompatActivity {

    // 화면에 정적인 view
    EditText inputTitle, inputText; // 제목, 본문 입력부분
    Button btnInputPic; // 이미지 추가 버튼

    Bitmap urlBitmap; // 잘못된 이미지 추가 했는지 판단용 변수

    private String key = null; // 편집을 통해 WirteActivity를 실행시켰을 때 DetailActivity로 부터 받아오는 key
    private DetailNotepadVO detailNotepadVO = null; // 편집을 통해 WirteActivity를 실행시켰을 때 기존 내용 담아줄 변수
    private File tempFile; // 새로 카메라를 통해 찍은 이미지, 앨범을 통해 선택한 이미지, URL을 통해 불러온 이미지를 담고 있을 변수
    private int imageOrder = 0; // 이미지가 현재 몇번째 이미지인지
    private int NotepadNo; // 현재 몇번 째 메모인지

    // NoteDB와 달리 이미지 저장 방식이 여러개이므로 List 배열을 따로 작성함
    private List<String> imageIndexList = new ArrayList<>(); // 최종 저장 전 추가한 이미지 index 저장
    private List<ImageVO> imageVOList = new ArrayList<>(); // 최종 저장 전 추가한 이미지 정보 저장

    private static final int PICK_FROM_CAMERA = 1; // 카메라 작업 후 onActivityResult 메소드에서 카메라 코드 실행 되게 함
    private static final int PICK_FROM_ALBUM = 2; // 앨범 작업 후 onActivityResult 메소드에서 앨범 코드 실행 되게 함
    private static final int resizePicSize = 320; // 추가한 이미지 미리보기 크기

    private final String packegeName = "com.example.notepad"; // FileProvider.getUriForFile 메소드에서 필요한 현재 패키지 이름

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

        // 이미지 추가 버튼에 Listener 달아줌
        btnInputPic.setOnClickListener(listener);

        // Intent 받아옴
        Intent intent = getIntent();
        // WriteActivity에 편집으로 실행시켰는지, 작성으로 실행시켰는지 판단
        if (intent.hasExtra("key")) { // 받아온 Intent에 key 존재 = 기존 것 편집
            key = intent.getStringExtra("key");
            ImageVO imageVO = ImageDB.getImage(key + imageOrder);
            detailNotepadVO = NoteDB.getNotepad(key);
            inputTitle.setText(detailNotepadVO.getTitleStr());
            inputText.setText(detailNotepadVO.getDescription());

            // DetailActivity의 이미지 불러오는 부분과 비슷한 부분
            // setImage 메소드 내부에서 사용하는 resizePicSize 값이 다름
            while (imageVO != null) {
                if(!imageVO.isDelete()) {
                    setExistingImage(imageVO.getImageUrl());
                }
                imageOrder++;
                imageVO = ImageDB.getImage(key + imageOrder);
            }
        } else { // 작성으로 실행했을 때
            imageOrder = 0;
            NotepadNo = NoteDB.getIndexes().size();
            key = NotepadNo + "번 메모";
        }
    }

    // 화면에 정적으로 존재하는 view의 Listener 설정 부분
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnInputPic:
                    selPicInput();
                    break;
            }
        }

    };

    // 액션버튼 메뉴 액션바에 집어 넣기
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.write_menu, menu);
        return true;
    }

    // 액션버튼을 클릭했을때의 동작
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { // actionbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
            case R.id.action_writeComplete: { // actionbar의 작성 완료 눌렀을 때 동작
                try {
                    if (detailNotepadVO != null) { // 편집시 실행되는 부분, key가 기존의 불러온 key와 동일
                        NoteDB.addNotepad(key, new DetailNotepadVO(detailNotepadVO.getNotepadNo(), inputTitle.getText().toString(), inputText.getText().toString()));
                    } else { // 작성시 실행되는 부분, key가 새로 생성된 key임
                        NoteDB.addNotepad(key, new DetailNotepadVO(NotepadNo, inputTitle.getText().toString(), inputText.getText().toString()));
                    }
                    // 앞서 3가지 방식으로 추가 했던 이미지 DB에 저장
                    for(int i=0; i < imageIndexList.size(); i++) {
                        ImageDB.addImage(imageIndexList.get(i), imageVOList.get(i));
                    }
                    // NoteDB, ImageDB 업데이트한 내용 로컬에 저장
                    NoteDB.save(getFilesDir());
                    ImageDB.save(getFilesDir());
                    Toast.makeText(getApplicationContext(), "작성 완료", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally { // 작업 완료시 WriteActivity 종료
                    finish();
                }
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    // 카메라 실행 후, 앨범 사진 선택 후 실행되는 메소드
    // URL 추가 방식은 다른 앱을 실행시켜서 한 것이 아니기 때문에 메소드에 없음
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 카메라 or 앨범 선택 후 이미지 추가 도중 취소
        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(getApplicationContext(), "취소 되었습니다.", Toast.LENGTH_SHORT).show();
            if (tempFile != null) {
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
                String[] proj = {MediaStore.Images.Media.DATA};

                assert photoUri != null;
                cursor = getContentResolver().query(photoUri, proj, null, null, null);

                assert cursor != null;
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                cursor.moveToFirst();

                tempFile = new File(cursor.getString(column_index));

                imageIndexList.add(key+imageOrder);
                imageVOList.add(new ImageVO(key, 0, tempFile.getAbsolutePath(), imageOrder));

                imageOrder++;
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            setImage();
        }
    }

    // 이미지 추가 버튼 클릭시 AlertDialog 띄워 실행 되는 메소드 선택하게 함
    private void selPicInput() {
        final List<String> ListItems = new ArrayList<>();
        ListItems.add("카메라");
        ListItems.add("앨범");
        ListItems.add("URL");
        final CharSequence[] items = ListItems.toArray(new String[ListItems.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("추가 방식");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int pos) {
                String selectedText = items[pos].toString();
                // pos = 0 카메라, pos = 1 앨범, pos = 2 URL
                switch (pos) {
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
            }
        });
        builder.show();
    }

    // 카메라 앱을 통해 이미지 추가
    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            tempFile = createImageFile(); // 카메라로 찍어서 저장되는 이미지의 파일 생성
            imageIndexList.add(key+imageOrder);
            imageVOList.add(new ImageVO(key, 0, tempFile.getAbsolutePath(), imageOrder));
            imageOrder++;
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            finish();
            e.printStackTrace();
        }
        if (tempFile != null) {
            // Android OS 7 이후부터는 Uri를 통한 접근 안되서 두가지 방식으로 나누어둠
            // Android OS 7 이후
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                Uri photoUri = FileProvider.getUriForFile(this, packegeName + ".provider", tempFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, PICK_FROM_CAMERA);
            }
            // 이전
            else {
                Uri photoUri = Uri.fromFile(tempFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, PICK_FROM_CAMERA);
            }
            // onActivityResult 메소드 실행됨
        }
    }

    // 앨범을 통해 이미지 추가
    private void goToAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
        // onActivityResult 메소드 실행됨
    }

    // URL을 통해 이미지 추가
    // AlertDialog 통해 사용자로부터 URL 입력 받음
    private void selUrlInput() {
        final EditText edittext = new EditText(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("URL");
        builder.setMessage("원하는 이미지 URL을 입력해주세요");
        builder.setView(edittext);
        builder.setPositiveButton("입력",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        setUrlImage(edittext.getText().toString()); // 입력 받은 URL로 이미지 받아오는 메소드 실행
                    }
                });
        builder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "취소되었습니다", Toast.LENGTH_SHORT).show();
                    }
                });
        builder.show();
    }

    // 사용자로부터 입력 받은 URL 통해 이미지 받아오는 메소드
    private void setUrlImage(String baseURL) {
        final String baseImageURL = baseURL; // 입력 받은 URL
        LinearLayout li = (LinearLayout) findViewById(R.id.picList);
        ImageView imageView = new AppCompatImageView(this);

        Thread mThread = new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(baseImageURL); // URL 주소를 이용해서 URL 객체 생성

                    //  아래 코드는 웹에서 이미지를 가져온 뒤
                    //  이미지 뷰에 지정할 Bitmap을 생성하는 과정

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is = conn.getInputStream();
                    urlBitmap = BitmapFactory.decodeStream(is); // Bitmap 객체로 이미지 저장
                } catch (MalformedURLException uex) {
                    uex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        };
        mThread.start(); // 웹에서 이미지를 가져오는 작업 스레드 실행.
        try {
            //  메인 스레드는 작업 스레드가 이미지 작업을 가져올 때까지
            //  대기해야 하므로 작업스레드의 join() 메소드를 호출해서
            //  메인 스레드가 작업 스레드가 종료될 까지 기다리도록 합니다.

            mThread.join();

            // 잘못된 url 체크
            if (urlBitmap == null) {
                Toast.makeText(getApplicationContext(), "잘못된 url 주소입니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            imageIndexList.add(key+imageOrder);
            imageVOList.add(new ImageVO(key, 0, baseImageURL, imageOrder));
            imageOrder++;

            //  이제 작업 스레드에서 이미지를 불러오는 작업을 완료했기에
            //  UI 작업을 할 수 있는 메인스레드에서 이미지뷰에 이미지를 지정합니다.

            Glide.with(this).load(urlBitmap).override(resizePicSize, resizePicSize).centerCrop().into(imageView);

            li.addView(imageView);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    // 새로 이미지 추가할 때
    // tempFile에 저장되어 있는 이미지 imageView에 저장
    private void setImage() {

        LinearLayout li = (LinearLayout) findViewById(R.id.picList);
        final ImageView imageView = new AppCompatImageView(this);

        Glide.with(this).load(tempFile).override(resizePicSize, resizePicSize).centerCrop().into(imageView);

        li.addView(imageView);
    }

    // 편집 시 기존 이미지 불러올 때
    // path(이미지 경로)를 통해 이미지 iamgeView에 저장
    // 편집 시에는 기존 이미지를 삭제할 수 있음
    private void setExistingImage(String path) {
        LinearLayout li = (LinearLayout) findViewById(R.id.picList);
        final ImageView imageView = new AppCompatImageView(this);
        imageView.setId(imageOrder);

        Glide.with(this).load(path).override(resizePicSize, resizePicSize).centerCrop().into(imageView);

        imageView.setOnClickListener(
                new View.OnClickListener() {
                    private long lastTimePicPressed;
                    @Override
                    public void onClick(View v) {
                        //2초 이내에 이미지 재 클릭 시 이미지 삭제
                        if (System.currentTimeMillis() - lastTimePicPressed < 2000)
                        {
                            ImageDB.getImage(key + imageView.getId()).setDelete(true);
                            ((LinearLayout)findViewById(R.id.picList)).removeView(v);
                            return;
                        }
                        // 이미지 한번 클릭 시 메시지
                        Toast.makeText(getApplicationContext(), "'이미지를 한번 더 누르시면 이미지가 삭제됩니다.", Toast.LENGTH_SHORT).show();
                        //lastTimePicPressed에 이미지가 클릭 된 시간을 기록
                        lastTimePicPressed = System.currentTimeMillis();
                    }
                }
        );

        li.addView(imageView);
    }

    // 카메라 앱을 통해 새로운 이미지를 만들었을 때 해당 이미지 저장하는 메소드
    private File createImageFile() throws IOException {
        // 이미지 파일 이름 ( _{시간}_ )
        String timeStamp = DateFormat.getDateInstance(DateFormat.MEDIUM).format(new Date());
        String imageFileName = timeStamp + "_";

        // 이미지가 저장될 폴더 이름 ( 각 index(key) 값 )
        File storageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + key + "/");
        if (!storageDir.exists()) storageDir.mkdirs();

        // 빈 파일 생성
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        return image;
    }


}
