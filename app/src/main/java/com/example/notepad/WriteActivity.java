package com.example.notepad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class WriteActivity extends AppCompatActivity {

    Button load, save;
    EditText inputText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        //액션바 타이틀 변경하기
        getSupportActionBar().setTitle("새 메모 작성하기");
        // 액션바 뒤로가기 버튼 추가
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //액션바 배경색 변경
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFF339999));

        load= (Button) findViewById(R.id.load);
        save = (Button) findViewById(R.id.save);
        inputText = (EditText) findViewById(R.id.inputText);

        load.setOnClickListener(listener);
        save.setOnClickListener(listener);
    }
    View.OnClickListener listener = new View.OnClickListener() {

        @Override

        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.load:
                    Log.i("TAG", "load 진행");
                    FileInputStream fis = null;
                    try{
                        fis = openFileInput("memo.txt");
                        byte[] data = new byte[fis.available()];
                        while( fis.read(data) != -1){
                        }

                        inputText.setText(new String(data));
                        Toast.makeText(WriteActivity.this, "load 완료", Toast.LENGTH_SHORT).show();
                    }catch(Exception e){
                        e.printStackTrace();
                    }finally{
                        try{ if(fis != null) fis.close(); }catch(Exception e){e.printStackTrace();}
                    }
                    break;

                case R.id.save:
                    Log.i("TAG", "save 진행");
                    FileOutputStream fos = null;
                    try {
                        fos = openFileOutput("memo.txt", Context.MODE_PRIVATE);
                        String out = inputText.getText().toString();
                        fos.write(out.getBytes());
                        Toast.makeText(WriteActivity.this, "save 완료", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (fos != null) fos.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }

    };
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ // actionbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
