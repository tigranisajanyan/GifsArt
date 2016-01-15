package com.gifsart.studio.social;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
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
    public static final String FILE_TYPE_IMAGE = "image/gif";

    public static final String url1 = "http://cdn76.picsart.com/187889742003202.gif";

    private String filePath;
    private Activity activity;

    public ShareContraller(String filePath, Activity activity) {
        this.filePath = filePath;
        this.activity = activity;
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
        if (PackageContraller.isPackageExisted(activity, INSTAGRAM_PACKAGE_NAME)) {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.putExtra(Intent.EXTRA_TEXT, GifsArtConst.MY_DIR);
            share.setType(FILE_TYPE_IMAGE);
            File media = new File(filePath);
            Uri uri = Uri.fromFile(media);
            try {
                share.putExtra(Intent.EXTRA_STREAM, uri);
                share.setPackage(INSTAGRAM_PACKAGE_NAME);
                activity.startActivity(share);
            } catch (Exception e) {
                e.printStackTrace();
                //Intent intent = new Intent(Intent.ACTION_VIEW);
                //intent.setData(Uri.parse("market://details?id=" + packageName));
                //context.startActivity(intent);
            }
        } else {
            Toast.makeText(activity, "insta", Toast.LENGTH_SHORT).show();
        }
    }

    public void shareTwitter() {
        if (PackageContraller.isPackageExisted(activity, TWITTER_PACKAGE_NAME)) {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.putExtra(Intent.EXTRA_TEXT, GifsArtConst.MY_DIR);
            share.setType(FILE_TYPE_IMAGE);
            File media = new File(filePath);
            Uri uri = Uri.fromFile(media);
            try {
                share.putExtra(Intent.EXTRA_STREAM, uri);
                share.setPackage(TWITTER_PACKAGE_NAME);
                activity.startActivity(share);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(activity, "messenger", Toast.LENGTH_SHORT).show();
        }
    }

    public void shareMessenger() {
        if (PackageContraller.isPackageExisted(activity, MESSENGER_PACKAGE_NAME)) {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.putExtra(Intent.EXTRA_TEXT, GifsArtConst.MY_DIR);
            share.setType(FILE_TYPE_IMAGE);
            File media = new File(filePath);
            Uri uri = Uri.fromFile(media);
            try {
                share.putExtra(Intent.EXTRA_STREAM, uri);
                share.setPackage(MESSENGER_PACKAGE_NAME);
                activity.startActivity(share);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(activity, "messenger", Toast.LENGTH_SHORT).show();
        }
    }

    public void shareWhatsApp() {
        if (PackageContraller.isPackageExisted(activity, WHATSAPP_PACKAGE_NAME)) {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.putExtra(Intent.EXTRA_TEXT, GifsArtConst.MY_DIR);
            share.setType(FILE_TYPE_IMAGE);
            File media = new File(filePath);
            Uri uri = Uri.fromFile(media);
            try {
                share.putExtra(Intent.EXTRA_STREAM, uri);
                share.setPackage(WHATSAPP_PACKAGE_NAME);
                activity.startActivity(share);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(activity, "whatsapp", Toast.LENGTH_SHORT).show();
        }
    }

    public void shareFacebook() {
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("http://cdn78.picsart.com/186853261001202.gif"))
                .build();

        ShareApi.share(content, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Log.d("gag", result.toString());
            }

            @Override
            public void onCancel() {
                Log.d("gag", "cancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("gag", error.toString());
            }
        });

        /*if (PackageContraller.isPackageExisted(context, "com.facebook.katana")) {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.putExtra(Intent.EXTRA_TEXT, GifsArtConst.MY_DIR);
            share.setType(FILE_TYPE_IMAGE);
            File media = new File(filePath);
            Uri uri = Uri.fromFile(media);
            try {
                share.putExtra(Intent.EXTRA_STREAM, Uri.parse("http://cdn78.picsart.com/186853261001202.gif"));
                share.setPackage("com.facebook.katana");
                context.startActivity(share);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, "whatsapp", Toast.LENGTH_SHORT).show();
        }*/
    }

}
