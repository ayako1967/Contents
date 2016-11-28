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
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE = 100;

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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],int[] grantResults) {
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

                //indexからIDを取得、画像のURI取得
                int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                Long id = cursor.getLong(fieldIndex);
                Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,id);

            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            imageView.setImageURI(imageUri);

        }
        cursor.close();
    }
}
