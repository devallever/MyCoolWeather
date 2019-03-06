package com.allever.mycoolweather.modules.setting.ui;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.allever.mycoolweather.R;
import com.allever.mycoolweather.modules.setting.event.BGEvent;
import com.allever.mycoolweather.utils.Constant;
import com.allever.mycoolweather.utils.OkHttpUtil;
import com.allever.mycoolweather.utils.SharePreferenceUtil;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.io.FileNotFoundException;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * Created by allever on 17-5-15.
 */

public class SettingFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener,
        Preference.OnPreferenceClickListener {

    private static final int CHOSE_PHOTO = 1000;

    private static final String TAG = "SettingFragment";

    private Preference choosePicPreference;

    private Handler mHandler = new Handler();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting);
        findPreference("pref_bg").setOnPreferenceChangeListener(this);
        findPreference("pref_notification").setOnPreferenceChangeListener(this);
        findPreference("pref_update_frequency").setOnPreferenceChangeListener(this);
        findPreference("pref_update").setOnPreferenceClickListener(this);

        choosePicPreference = findPreference("pref_choose_pic");
        choosePicPreference.setOnPreferenceClickListener(this);

    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        switch (key){
            case "pref_update":
                //Log.d(TAG, "onPreferenceClick: pref_update");
                String url = Constant.SERVER_BASE_URL + "UpdateServlet";
                OkHttpUtil.checkUpdateVersion(url, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result = response.body().string();
                        Gson gson = new Gson();
                        ResponseData responseData = gson.fromJson(result,ResponseData.class);
                        if (responseData.code == 1){
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AlertDialog builder = new AlertDialog.Builder(getActivity())
                                            .setTitle("提示")
                                            .setMessage("有可更新版本：" + responseData.data.version_name +
                                                    "\n" + responseData.data.description)
                                            .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    new Thread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            DownloadManager downloadManager = (DownloadManager) getActivity()
                                                                    .getSystemService(Context.DOWNLOAD_SERVICE);
                                                            DownloadManager.Request request = new DownloadManager
                                                                    .Request(Uri.parse(Constant.SERVER_BASE_URL + responseData.data.path ));
                                                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                                            request.setTitle("极简天气");
                                                            request.setDestinationInExternalFilesDir(getActivity(), Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath(), "simpleWeather" + responseData.data.version_name+".apk");
                                                            Log.d(TAG, "download path =  " + Constant.SERVER_BASE_URL + responseData.data.path);
                                                            long id = downloadManager.enqueue(request);
                                                        }
                                                    }).start();
                                                    //downloadManager.addCompletedDownload("极简天气","正在下载",false,"",Constant.SERVER_BASE_URL + responseData.data.path,)
                                                }
                                            })
                                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            }).show();
                                }
                            });

                        }else{
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(),"无更新",Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                });
                break;
            case "pref_choose_pic":
                if (preference.getSharedPreferences().getBoolean("pref_bg",false)){
                    //选择图片
                    openAlbum();
                }else {
                    Toast.makeText(getActivity(),"先打开设置背景",Toast.LENGTH_LONG).show();
                }
                break;

        }
        return true;
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();
        switch (key){
            case "pref_notification":
                Log.d(TAG, "onPreferenceChange: pref_notification");
                break;
            case "pref_bg":
                Log.d(TAG, "onPreferenceChange: pref_bg");
                if ((boolean)newValue){
                    //choosePicPreference.setSelectable(true);
                    /***
                    //运行时权限
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(getActivity(),new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                    }else{
                        openAlbum();
                    }
                     */
                }else {
                    //choosePicPreference.setSelectable(false);
                    //通知主界面刷新背景
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            EventBus.getDefault().post(new BGEvent());
                        }
                    },1000);

                }
                break;
            case "pref_update_frequency":
                String stringValue = newValue.toString();
                // For list preferences, look up the correct display value in the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                int[] updateValues = getActivity().getResources().getIntArray(R.array.setting_array_update_frequency_values);

                // Set the summary to reflect the new value.
                preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
                SharePreferenceUtil.setUpdateFrequency(getActivity(), Integer.valueOf(newValue.toString()));
                Log.d(TAG, "onPreferenceChange: pref_update_frequency = " + SharePreferenceUtil.getUpdateFrequency(getActivity()));
                break;
        }
        Log.d(TAG, "onPreferenceChange: newValue = " + newValue);
        return true;
    }

    private void openAlbum(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOSE_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else {
                    Toast.makeText(getActivity(),"you denied the permission",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case CHOSE_PHOTO:
                if (resultCode == getActivity().RESULT_OK){
                    //判断系统版本号
                    if (Build.VERSION.SDK_INT >= 19){
                        handleImageOnKitkat(data);
                    }else {
                        handleImageBeforeKitkat(data);
                    }
                }else {
                    //
                    //((SwitchPreference)findPreference("pref_bg")).setChecked(false);
                    EventBus.getDefault().post(new BGEvent());
                }
                break;
        }
    }

    @TargetApi(19)
    private void handleImageOnKitkat(Intent data){
        String imagePath = null;
        Uri uri = data.getData();
        Log.d(TAG, "uri = " + data.getData());
        //快图浏览      content://com.alensw.PicFolder.FileProvider/document/%2Fstorage%2Femulated%2F0%2Foutput_image.jpg
        //最近/Image   content://com.android.providers.media.documents/document/image%3A161
        //下载         content://com.android.providers.downloads.documents
        //文件浏览器    file:///sdcard/0SDcard/P/WeiXin/001b24dc4bf4107dd4e858~01.jpg
        //谷歌照片      content://com.google.android.apps.photos.contentprovider/-1/1/content%3A%2F%2Fmedia%2Fexternal%2Fimages%2Fmedia%2F271/ORIGINAL/NONE/1895548347
        if (DocumentsContract.isDocumentUri(getActivity(),uri)){
            //如果是Document类型的Uri 则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            Log.d(TAG, "docId = " + docId);
            //docId = image:3232
            Log.d(TAG, "uri.getAuthority() = " + uri.getAuthority());
            //uri.getAuthority() = com.android.providers.media.documents
            if ("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                Log.d(TAG, "selection = " + selection);
                //selection = _id=3232
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }
        }else if ("content".equalsIgnoreCase(uri.getScheme())){
            //如果是content类型的uri
            imagePath = getImagePath(uri,null);
        }else if ("file".equalsIgnoreCase(uri.getScheme())){
            //如果是file类型的uri
            imagePath = uri.getPath();
        }
        SharePreferenceUtil.saveBackgroundImagePath(getActivity(),imagePath);
        EventBus.getDefault().post(new BGEvent());
        //sendEvent;
        //displayImage(imagePath);
    }

    private void handleImageBeforeKitkat(Intent data){
        Uri uri = data.getData();
        String imagePath = getImagePath(uri,null);
        SharePreferenceUtil.saveBackgroundImagePath(getActivity(),imagePath);
        EventBus.getDefault().post(new BGEvent());
        //sendEvent to modify bg
        //displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection){
        String path = null;
        //通过uri和selection获取真实图片路径
        Cursor cursor = getActivity().getContentResolver().query(uri,null,selection,null,null);
        if (cursor != null){
            if (cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }


    private class ResponseData{
        int code;
        String message;
        Version data;
    }
    private class Version{
        int version_code;
        String version_name;
        String description;
        String path;
    }


}
