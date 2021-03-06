package jp.techacademy.takuya.okitsu.contents;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.view.View;


import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener  {

    private static final int PERMISSIONS_REQUEST_CODE = 100;

    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Android 6.0 以降の場合

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED) {
                //許可されている
                getContentsInfo();
            } else {
                //許可されていないため許可ダイアログを表示
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSIONS_REQUEST_CODE);
            }
            //Android 5 系以下の場合
        } else {
            getContentsInfo();

        }

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        Button buttonNext = (Button)findViewById(R.id.button1);
        Button buttonSlideshow = (Button)findViewById(R.id.button2);
        Button buttonPrev = (Button)findViewById(R.id.button3);

        buttonNext.setOnClickListener(this);
        buttonSlideshow.setOnClickListener(this);
        buttonPrev.setOnClickListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo();
                }
                break;
            default:
                break;
        }
    }

    private void getContentsInfo() {

        //画像を取得
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,//データの種類
                null,
                null,
                null,
                null//ソート
        );


        if (cursor.moveToFirst()) {
            do {

                int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                Long id = cursor.getLong(fieldIndex);
                Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                uriList.add(imageUri);
                //indexからIDを取得、画像のURI取得
                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                imageView.setImageURI(uriList.get(0));


            }
            while (cursor.moveToNext());

        }
                cursor.close();
    }

    int showIndex = 0;
    ArrayList<Uri> uriList = new ArrayList<Uri>();

    public void onClick(View v) {
        Log.d("debug", "onClick()メソッドが呼ばれた！");

        //進むボタンを押したとき
        if(v.getId() == R.id.button1) {

                showIndex++;
                if (showIndex >= uriList.size()) {
                    showIndex = 0;
                }

                Uri imageUri = uriList.get(showIndex);
            Log.d("debug", imageUri.toString());
            Log.d("debug", "showIndex = " + showIndex);
                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                imageView.setImageURI(imageUri);


        }
        //戻るボタンを押したとき
        else if (v.getId() == R.id.button3) {

                showIndex--;
                if (showIndex >= 0) {
                    showIndex = uriList.size();
                }

                Uri imageUri = uriList.get(showIndex);

                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                imageView.setImageURI(imageUri);


        }else if (v.getId() == R.id.button2) {

            SlideTask task = new SlideTask();
            Timer timer = new Timer();
            timer.schedule(task, 0L, 2000);
        }

    }

    public class SlideTask extends TimerTask {

        public void run() {
            showIndex++;
            if (showIndex >= uriList.size()) {
                showIndex = 0;
            }


            (new android.os.Handler()).post(new Runnable() {
                @Override
                public void run() {
                    Uri imageUri = uriList.get(showIndex);
                    ImageView imageView = (ImageView) findViewById(R.id.imageView);
                    imageView.setImageURI(imageUri);
                }
            });


        }
    }

}
