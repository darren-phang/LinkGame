package com.example.pangd.linkgame;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dou361.dialogui.DialogUIUtils;
import com.dou361.dialogui.bean.TieBean;
import com.dou361.dialogui.listener.DialogUIItemListener;
import com.dou361.dialogui.listener.DialogUIListener;
import com.example.pangd.linkgame.FragmentSet.RankItem;
import com.example.pangd.linkgame.Other.CopyRight;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private String Username = "defaultUser";
    private String UserHeadImage = null;
    private RankInfoReceiver rankInfoReceiver;
    private IntentFilter intentFilter;
    static boolean bar_Action = true;
    public static Game_F Now_Fragmet;
    ImageView headImage;
    Uri imageUri; //图片路径
    File imageFile; //图片文件
    String imagePath;
    Bitmap bitmapdown;
    TextView username_view;
    final static int CAMERA = 1;
    final static int ICON = 2;
    final static int CAMERAPRESS = 3;
    final static int ICONPRESS = 4;
    BackgroundSound backgroundSound;

    static public Game_F getNow_Fragmet() {
        return Now_Fragmet;
    }

    static public void setBar_Action(boolean _bar_Action) {
        bar_Action = _bar_Action;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GetUserInformation();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Now_Fragmet = new Game_F();
        getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, Now_Fragmet).commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        backgroundSound = new BackgroundSound(MainActivity.this, R.raw.merry_christmas);
        setPrefenence();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        View view = navigationView.getHeaderView(0);
        username_view = view.findViewById(R.id.username);
        username_view.setText(Username);
        headImage = (ImageView) view.findViewById(R.id.imageView);
        Log.d(TAG, "onCreate: headImage: " + headImage);
        headImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<TieBean> strings = new ArrayList<>();
                strings.add(new TieBean("tack photo", 0));
                strings.add(new TieBean("chose from album", 1));
                DialogUIUtils.showMdBottomSheet(MainActivity.this, true, null,
                        strings, 1, new DialogUIItemListener() {
                            @Override
                            public void onItemClick(CharSequence text, int position) {
                                if (position == 0) {
                                    if (Build.VERSION.SDK_INT >= 23) {
                                        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                                                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERAPRESS);
                                        } else {
                                            startCamera();
                                        }

                                    } else {
                                        startCamera();
                                    }
                                } else if (position == 1) {
                                    if (Build.VERSION.SDK_INT >= 23) {
                                        if (ContextCompat.checkSelfPermission(MainActivity.this,
                                                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                                                ContextCompat.checkSelfPermission(MainActivity.this,
                                                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                            ActivityCompat.requestPermissions(MainActivity.this,
                                                    new String[]{Manifest.permission.CAMERA,
                                                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                    CAMERAPRESS);
                                        } else {
                                            startIcon();
                                        }
                                    } else {
                                        startIcon();
                                    }
                                }
                            }
                        }).show();
            }
        });
        setUserHeadImage();
        buildRegisterRank(); //注册广播
    }

    private Bitmap clip_image(Bitmap bitmap, float target_W, float target_H) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scaleWidth = target_W / width;
        float scaleHeight = target_H / height;
        Log.d(TAG, "clip_image: " + scaleWidth + " " + scaleHeight);
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("onActivityResult", "requestCode" + requestCode + "resultCode" + resultCode);
        switch (requestCode) {
            case CAMERA:
                Bitmap bitmap1 = null;
                try {
                    bitmap1 = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                    imagePath = getPath(this, imageUri);
                    Bitmap bitmapdown = clip_image(bitmap1, (float) 192, (float) 192);
                    headImage.setImageBitmap(bitmapdown);
                    saveUserInformation(Username, imagePath);
                } catch (FileNotFoundException e) {
                    imageFile = null;
                    e.printStackTrace();
                }
                Log.d(TAG, "onActivityResult: " + imagePath);
                break;
            case ICON:
                DisplayMetrics metric = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metric);
                String dst = getPath(this, data.getData());
                Log.d(TAG, "onActivityResult: dst: " + dst);
                imageFile = new File(dst);
                imagePath = dst;
                Bitmap bitmap = ThumbnailUtils.extractThumbnail(getBitmapFromFile(imageFile), 192, 192);
                bitmapdown = bitmap;
                headImage.setImageBitmap(bitmapdown);
                saveUserInformation(Username, dst);
                Log.d(TAG, "onActivityResult: " + dst);
                break;
        }
    }

    public Bitmap getBitmapFromFile(File dst) {
        if (null != dst && dst.exists()) {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            //opts.inJustDecodeBounds = false;
            opts.inSampleSize = 2;

            try {
                return BitmapFactory.decodeFile(dst.getPath(), opts);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public void startCamera() {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        imageFile = new File(path, "heard_" + Username + ".png");
        try {
            if (imageFile.exists()) {
                imageFile.delete();
            }
            imageFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //将File对象转换为Uri并启动照相程序
        imageUri = Uri.fromFile(imageFile);
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE"); //照相
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); //指定图片输出地址
        startActivityForResult(intent, CAMERA); //启动照相
    }

    public void startIcon() {
        Intent intent1 = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent1, ICON);
    }

    private void GetUserInformation() {
        FileInputStream in = null;
        BufferedReader reader = null;
        try {
            in = openFileInput("USERINFOMATION");
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            int i = 1;
            while ((line = reader.readLine()) != null) {
                if (i == 1) {
                    Username = line;
                } else {
                    UserHeadImage = line;
                }
                i += 1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void saveUserInformation(String name, String string_dir) {
        FileOutputStream outputStream = null;
        BufferedWriter writer = null;
        try {
            outputStream = openFileOutput("USERINFOMATION", Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            writer.write(name);
            writer.newLine();
            writer.write(string_dir);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setUserHeadImage() {
        if (UserHeadImage != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(UserHeadImage);
            Log.d(TAG, "setUserHeadImage: bitmap: " + bitmap);
            headImage.setImageBitmap(clip_image(bitmap, (float) 192, (float) 192));
        }

    }

    private void setPrefenence() {
        SharedPreferences preferences = getSharedPreferences(Username, MODE_PRIVATE);
        int background_music = preferences.getInt("background_music", 1);
        if (background_music == 1) {
            backgroundSound._create_background_sound();
        }
        int click_music = preferences.getInt("click_music", 1);
        Now_Fragmet.setClick_music(click_music);
    }

    private void buildRegisterRank() {
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.broadcast.RANK_INFOMATION");
        rankInfoReceiver = new RankInfoReceiver();
        registerReceiver(rankInfoReceiver, intentFilter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (!bar_Action)
            return true;
        //noinspection SimplifiableIfStatement
        if (id == R.id.sub_difficulty_1) {
            Now_Fragmet.mode = 1;
            Now_Fragmet.initView();
            return true;
        }
        if (id == R.id.sub_difficulty_2) {
            Now_Fragmet.mode = 2;
            Now_Fragmet.initView();
            return true;
        }
        if (id == R.id.sub_difficulty_3) {
            Now_Fragmet.mode = 3;
            Now_Fragmet.initView();
            return true;
        }
        if (id == R.id.sub_number_1) {
            Now_Fragmet.degree = 10;
            Now_Fragmet.initGame(true);
            return true;
        }
        if (id == R.id.sub_number_2) {
            Now_Fragmet.degree = 15;
            Now_Fragmet.initGame(true);
            return true;
        }
        if (id == R.id.sub_number_3) {
            Now_Fragmet.degree = 25;
            Now_Fragmet.initGame(true);
            return true;
        }

        if (id == R.id.action_flush) {
            Now_Fragmet.fab.performClick();
            return true;
        }

        if (id == R.id.action_review) {
            Now_Fragmet.initView();
            return true;
        }

        if (id == R.id.background_music_open) {
            start_background_Sound();
        }
        if (id == R.id.background_music_close) {
            stop_background_Sound();
        }
        if (id == R.id.click_music_open) {
            open_click_sound();
        }
        if (id == R.id.click_music_close) {
            close_click_sound();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_rename) {
            final EditText inputServer = new EditText(MainActivity.this);
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("输入新名称").setView(inputServer)
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String text = inputServer.getText().toString();
                            set_new_name(text);
                        }
                    }).show();
        } else if (id == R.id.nav_rank) {
            rankInfoReceiver.getActivityRank(Now_Fragmet.getMode(), Now_Fragmet.getDegree());
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(MainActivity.this, CopyRight.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void set_new_name(String name){
        SharedPreferences preferences = getSharedPreferences(Username, MODE_PRIVATE);
        int background_music = preferences.getInt("background_music", 1);
        int click_music = preferences.getInt("click_music", 1);
        GetUserInformation();
        Username = name;
        saveUserInformation(Username, UserHeadImage);
        SharedPreferences.Editor editor = getSharedPreferences(Username,
                MODE_PRIVATE).edit();
        editor.putInt("click_music", click_music);
        editor.putInt("background_music", background_music);
        editor.apply();
        username_view.setText(Username);
    }

    private void stop_background_Sound() {
        SharedPreferences.Editor editor = getSharedPreferences(Username,
                MODE_PRIVATE).edit();
        editor.putInt("background_music", 0);
        editor.apply();
        backgroundSound._release_background_sound();
    }

    private void start_background_Sound() {
        SharedPreferences.Editor editor = getSharedPreferences(Username,
                MODE_PRIVATE).edit();
        editor.putInt("background_music", 1);
        editor.apply();
        backgroundSound._create_background_sound();
    }

    private void close_click_sound() {
        SharedPreferences.Editor editor = getSharedPreferences(Username,
                MODE_PRIVATE).edit();
        editor.putInt("click_music", 0);
        editor.apply();
        Now_Fragmet.setClick_music(0);
    }

    private void open_click_sound() {
        SharedPreferences.Editor editor = getSharedPreferences(Username,
                MODE_PRIVATE).edit();
        editor.putInt("click_music", 1);
        editor.apply();
        Now_Fragmet.setClick_music(1);
    }

    @Override
    protected void onDestroy() {
        backgroundSound.onDestroy();
        backgroundSound = null;
        super.onDestroy();
        unregisterReceiver(rankInfoReceiver);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.commit();
    }

    /**
     * //     * @param uri The Uri to check.
     * //     * @return Whether the Uri authority is ExternalStorageProvider.
     * //
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERAPRESS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    //获取到了权限
                    startCamera();
                } else {
                    Toast.makeText(this, "对不起你没有同意该权限", Toast.LENGTH_LONG).show();
                }
                break;

            case ICONPRESS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    //获取到了权限
                    startIcon();
                } else {
                    Toast.makeText(this, "对不起你没有同意该权限", Toast.LENGTH_LONG).show();
                }
                break;

        }


    }

    class RankInfoReceiver extends BroadcastReceiver {
        private Database_RANK dbHelper;

        public RankInfoReceiver() {
            super();
            dbHelper = new Database_RANK(getApplicationContext(), "RankInfo.db", null, 2);
            dbHelper.getWritableDatabase();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            final int degreeDifficult = intent.getIntExtra("degreeDifficult", 0);
            final int numberBlock = intent.getIntExtra("numberBlock", 0);
            values.put("playerName", intent.getStringExtra("playerName"));
            values.put("costTime", intent.getDoubleExtra("costTime", 0.0));
            values.put("inputTime", getTimeNow());
            values.put("degreeDifficult", degreeDifficult);
            values.put("numberBlock", numberBlock);
            db.insert("RANK", null, values);
            DialogUIUtils.showMdAlert(MainActivity.this, "Good Job",
                    "恭喜你完成了游戏，是否查看排行榜", new DialogUIListener() {
                        @Override
                        public void onPositive() {
                            Now_Fragmet.initView(); //重置画面
                            getActivityRank(degreeDifficult, numberBlock);
                        }

                        @Override
                        public void onNegative() {
                            Now_Fragmet.initView();
                        }

                    }).show();
//            getActivityRank(degreeDifficult, numberBlock);

        }

        public void getActivityRank(int degreeDifficult, int numberBlock) {
            bar_Action = false;
            Fragment fragment_rank = new Rank_F();
            ((Rank_F) fragment_rank).setDegreeDifficult(degreeDifficult);
            ((Rank_F) fragment_rank).setNumberBlock(numberBlock);
            replaceFragment(fragment_rank);
        }

        String getTimeNow() {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date curDate = new Date(System.currentTimeMillis());//获取当前时间
            return formatter.format(curDate);
        }
    }
}

class BackgroundSound {
    private static MediaPlayer mp = null;// 声明一个MediaPlayer对象

    public void playBGSound() {
        mp.start();// 开始播放
        // 为MediaPlayer添加播放完事件监听器
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                try {
                    Thread.sleep(1000);// 线程休眠1秒钟
                    mp.start();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private Thread thread;// 声明一个线程对象

    void _release_background_sound() {
        mp.stop();
        mp.release();
        mp = null;
    }

    void _create_background_sound() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                playBGSound();// 播放背景音乐
            }
        });
        thread.start();
    }

    void onDestroy() {
        if (mp != null) {
            mp.stop();// 停止播放
            mp.release();// 释放资源
            mp = null;
        }
        if (thread != null) {
            thread = null;
        }
    }

    BackgroundSound(Context context, int rawID) {
        if (mp != null) {
            mp.release();// 释放资源
        }
        mp = MediaPlayer.create(context, R.raw.merry_christmas);
    }
}