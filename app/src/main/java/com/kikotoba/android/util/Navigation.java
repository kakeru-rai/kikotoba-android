package com.kikotoba.android.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.kikotoba.android.SettingsActivity;

/**
 * Created by raix on 2017/03/12.
 */

public class Navigation {
    public static void goSettings(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    //メール起動
    public static void goMail(Context context, String toAddr, String subject, String body){
        Uri uri;
        if( toAddr!=null ) uri = Uri.parse("mailto:" + toAddr);
        else uri = Uri.parse("mailto:");

        // インテントのインスタンス生成
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);

        // インテントにアクション及び送信情報をセット
        intent.setAction(Intent.ACTION_SENDTO);
        if( subject!=null ) intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if( body!=null ) intent.putExtra(Intent.EXTRA_TEXT, body);

        // メール起動
        try{
            context.startActivity(intent);
        }catch(ActivityNotFoundException e){
            e.printStackTrace();
        }
    }

}
