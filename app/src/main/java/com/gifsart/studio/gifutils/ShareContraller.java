package com.gifsart.studio.gifutils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.gifsart.studio.utils.GifsArtConst;
import com.gifsart.studio.utils.PackageContraller;

import java.io.File;

/**
 * Created by Tigran on 10/30/15.
 */
public class ShareContraller {

    public static final String INSTAGRAM_PACKAGE_NAME = "com.instagram.android";
    public static final String TWITTER_PACKAGE_NAME = "com.twitter.android";
    public static final String GMAIL_PACKAGE_NAME = "com.google.android.gm";
    public static final String WHATSAPP_PACKAGE_NAME = "com.whatsapp";
    public static final String MESSENGER_PACKAGE_NAME = "com.facebook.orca";
    public static final String FILE_TYPE_IMAGE = "image/*";

    private String filePath;
    private Context context;

    public ShareContraller(String filePath, Context context) {
        this.filePath = filePath;
        this.context = context;
    }

    public void shareGif(ShareGifType shareGifType) {
        switch (shareGifType) {
            case FACEBOOK:
                break;
            case INSTAGRAM:
                shareInstagram();
                break;
            case TWITTER:
                shareTwitter();
                break;
            case WHATSAPP:
                shareWhatsApp();
                break;
            case MESSENGER:
                shareMessenger();
                break;
            default:
                break;
        }
    }


    enum ShareGifType {
        FACEBOOK,
        INSTAGRAM,
        TWITTER,
        WHATSAPP,
        MESSENGER
    }

    public void shareInstagram() {
        if (PackageContraller.isPackageExisted(context, INSTAGRAM_PACKAGE_NAME)) {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.putExtra(Intent.EXTRA_TEXT, GifsArtConst.MY_DIR);
            share.setType(FILE_TYPE_IMAGE);
            File media = new File(filePath);
            Uri uri = Uri.fromFile(media);
            try {
                share.putExtra(Intent.EXTRA_STREAM, uri);
                share.setPackage(INSTAGRAM_PACKAGE_NAME);
                context.startActivity(share);
            } catch (Exception e) {
                e.printStackTrace();
                //Intent intent = new Intent(Intent.ACTION_VIEW);
                //intent.setData(Uri.parse("market://details?id=" + packageName));
                //context.startActivity(intent);
            }
        } else {
            Toast.makeText(context, "insta", Toast.LENGTH_SHORT).show();
        }
    }

    public void shareTwitter() {
        if (PackageContraller.isPackageExisted(context, TWITTER_PACKAGE_NAME)) {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.putExtra(Intent.EXTRA_TEXT, GifsArtConst.MY_DIR);
            share.setType(FILE_TYPE_IMAGE);
            File media = new File(filePath);
            Uri uri = Uri.fromFile(media);
            try {
                share.putExtra(Intent.EXTRA_STREAM, uri);
                share.setPackage(TWITTER_PACKAGE_NAME);
                context.startActivity(share);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, "messenger", Toast.LENGTH_SHORT).show();
        }
    }

    public void shareMessenger() {
        if (PackageContraller.isPackageExisted(context, MESSENGER_PACKAGE_NAME)) {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.putExtra(Intent.EXTRA_TEXT, GifsArtConst.MY_DIR);
            share.setType(FILE_TYPE_IMAGE);
            File media = new File(filePath);
            Uri uri = Uri.fromFile(media);
            try {
                share.putExtra(Intent.EXTRA_STREAM, uri);
                share.setPackage(MESSENGER_PACKAGE_NAME);
                context.startActivity(share);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, "messenger", Toast.LENGTH_SHORT).show();
        }
    }

    public void shareWhatsApp() {
        if (PackageContraller.isPackageExisted(context, WHATSAPP_PACKAGE_NAME)) {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.putExtra(Intent.EXTRA_TEXT, GifsArtConst.MY_DIR);
            share.setType(FILE_TYPE_IMAGE);
            File media = new File(filePath);
            Uri uri = Uri.fromFile(media);
            try {
                share.putExtra(Intent.EXTRA_STREAM, uri);
                share.setPackage(WHATSAPP_PACKAGE_NAME);
                context.startActivity(share);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, "whatsapp", Toast.LENGTH_SHORT).show();
        }
    }

    public void shareGifTo(String packageName){
        if (PackageContraller.isPackageExisted(context, packageName)) {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.putExtra(Intent.EXTRA_TEXT, GifsArtConst.MY_DIR);
            share.setType(FILE_TYPE_IMAGE);
            File media = new File(filePath);
            Uri uri = Uri.fromFile(media);
            try {
                share.putExtra(Intent.EXTRA_STREAM, uri);
                share.setPackage(packageName);
                context.startActivity(share);
            } catch (Exception e) {
                e.printStackTrace();
                //Intent intent = new Intent(Intent.ACTION_VIEW);
                //intent.setData(Uri.parse("market://details?id=" + packageName));
                //context.startActivity(intent);
            }
        } else {
            Toast.makeText(context, "insta", Toast.LENGTH_SHORT).show();
        }
    }


    /*

 D/gagag: {"status":"success","key":"82e91e1d-e3c6-4eec-8de5-2320a2130fc3","id":186308506001102,"name":"","username":"509549572540494","photo":"https://graph.facebook.com/509549572540494/picture?type=normal","streams_count":0,"provider":"facebook","likes_count":0,"photos_count":0,"following_count":5,"followers_count":0,"mature":false,"username_changed":false,"is_verified":false,"connections":[{"provider":"facebook","id":"509549572540494","token":"CAAHqVtEZAcJsBAHcxhaZCZBP3K3PmC5EZBqetfZCJ6v6YYtkO7Iea0fY4FJLZCs2oLoFHQDV28983Net3Oib5oOHKoxe3AMZAmPsxLoaLPKHHSqtmtzewcEPzclkG1CJiPHmxkKNe4ntK6Dfw6GOZBpOxt8oyzxy6uSMbiq4I0BSQF9pHdkEl5ZC55noZAun8bDJ8q63z8DtL6MhcrTGR7Bf3I","_id":"5658129aa621647c4027d95e","settings":{"enable_action_follow":true,"enable_action_add":true,"enable_action_comment":true,"enable_action_like":true,"enable_action_all":true}}],"type":"user","permissions":[],"location":{"place":"","street":"","city":"","state":"","zip":"","country":""},"registered":false}
 I/WebViewFactory: Loading com.google.android.webview version 46.0.2490.76 (code 249007600)
 I/cr.library_loader: Time to load native libraries: 1 ms (timestamps 6441-6442)
 I/cr.library_loader: Expected native library version number "46.0.2490.76", actual native library version number "46.0.2490.76"
 D/gagag: {"status":"error","reason":"username_already_exist","message":"Username already exists"}


     */

}
