package com.gifsart.studio.social;

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
}
