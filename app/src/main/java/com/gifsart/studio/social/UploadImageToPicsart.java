package com.gifsart.studio.social;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;

import com.gifsart.studio.utils.AnimatedProgressDialog;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * Created by Tigran on 11/24/15.
 */
public class UploadImageToPicsart extends AsyncTask<Void, Integer, JSONObject> {

    private static final String API_KEY = "c985ce04-f6dd-448e-95f2-6a58d0d50431";

    private static final String UPLOAD_PHOTO_TO_PICSART_GENERAL = "https://api.picsart.com/photos/add.json?key=";
    private static final String UPLOAD_PHOTO_TO_PICSART_AVATAR = "https://api.picsart.com/users/photo/add.json?key=";

    private InputStream is = null;
    private volatile JSONObject jObj = null;
    private String json = "";
    private static boolean cancelFlag = false;
    private String filePath;
    private String uploadedImageUrl;
    private PHOTO_IS photo_is;
    private String userApiKey;
    private Context context;
    private ImageUploaded imageUploaded;
    private AnimatedProgressDialog animatedProgressDialog;
    private String title;
    private PHOTO_PUBLIC photo_public = PHOTO_PUBLIC.PUBLIC;

    public UploadImageToPicsart(Context context, String userApiKey, String filePath, String title, PHOTO_PUBLIC photo_public, PHOTO_IS photo_is) {
        this.filePath = filePath;
        this.photo_is = photo_is;
        this.userApiKey = userApiKey;
        this.title = title;
        this.photo_public = photo_public;
        this.context = context;

    }

    public static void setCancelFlag(boolean cancelFlag) {
        UploadImageToPicsart.cancelFlag = cancelFlag;
    }


    @Override
    protected void onPreExecute() {
        animatedProgressDialog = new AnimatedProgressDialog(context);
        animatedProgressDialog.show();
        Log.d("Upload", " Uploading Picture...");
    }

    @Override
    protected synchronized JSONObject doInBackground(Void... params) {

        final int[] iter = new int[1];

        Looper.prepare();

        try {
            final File file = new File(filePath);
            final long[] totalSize = new long[1];
            totalSize[0] = file.length();
            final HttpClient httpClient = new DefaultHttpClient();
            String url = "";
            //MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            MultiPartEntityMod multipartContent = null;
            try {
                multipartContent = new MultiPartEntityMod(new ProgressListener() {
                    @Override
                    public boolean doneFlag(boolean b) {
                        return false;
                    }

                    @Override
                    public void transferred(long num) {
                        long checker = -1;
                        if (UploadImageToPicsart.this.isCancelled()) {
                            httpClient.getConnectionManager().shutdown();
                        }
                        if (num != checker && num % 2 == 0) {
                            checker = num;
                            publishProgress((int) ((num / (float) totalSize[0] * 100)), iter[0]);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            multipartContent.addPart("file", new FileBody(file));
            //multipartContent.addPart("file", new ByteArrayBody(file));

            if (photo_is == PHOTO_IS.AVATAR) {
                url = UPLOAD_PHOTO_TO_PICSART_AVATAR + userApiKey;
            } else if (photo_is == PHOTO_IS.GENERAL) {
                url = UPLOAD_PHOTO_TO_PICSART_GENERAL + userApiKey;
                multipartContent.addPart("title", new StringBody(title, ContentType.DEFAULT_TEXT));
                multipartContent.addPart("is_public", new StringBody("" + photo_public.ordinal(), ContentType.DEFAULT_TEXT));
                multipartContent.addPart("mature", new StringBody("0", ContentType.DEFAULT_TEXT));
            }

            totalSize[0] = multipartContent.getContentLength();

            HttpPost httpPost = new HttpPost(url);
            HttpContext httpContext = new BasicHttpContext();
            httpPost.setEntity(multipartContent);
            HttpResponse response = null;
            try {
                response = httpClient.execute(httpPost, httpContext);
            } catch (IllegalStateException e) {
                return null;
            }
            HttpEntity httpEntity = response.getEntity();
            is = httpEntity.getContent();


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();

            json = sb.toString();
            Log.e("JSONStr", json);
        } catch (Exception e) {
            e.getMessage();
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
        try {
            jObj = new JSONObject(json);
        } catch (Exception e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
        iter[0]++;
        // Return JSON String
        return jObj;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        if (cancelFlag) {
            UploadImageToPicsart.this.cancel(true);
            cancelFlag = false;
        }
        Log.d("Uploaded", "photo #" + progress[1] + " done " + progress[0] + "%");
    }

    @Override
    protected void onPostExecute(JSONObject sResponse) {
        if (sResponse != null) {
            try {
                Log.d("response Upload", sResponse.toString());
                if (ErrorHandler.statusIsError(sResponse)) {
                    imageUploaded.uploadIsDone(false, "error");
                } else {
                    uploadedImageUrl = sResponse.getString("url");
                    imageUploaded.uploadIsDone(true, "success");
                }
            } catch (Exception e) {
                Log.e(e.getClass().getName(), e.getMessage(), e);
                imageUploaded.uploadIsDone(false, "error");
            }
        }
        animatedProgressDialog.dismiss();
    }

    private static class MultiPartEntityMod extends MultipartEntity {

        private final ProgressListener listener;

        public MultiPartEntityMod(final ProgressListener listener) {
            super();
            this.listener = listener;
        }

        public MultiPartEntityMod(final HttpMultipartMode mode, final ProgressListener listener) {
            super(mode);
            this.listener = listener;
        }

        public MultiPartEntityMod(HttpMultipartMode mode, final String boundary, final Charset charset, final ProgressListener listener) {
            super(mode, boundary, charset);
            this.listener = listener;
        }

        @Override
        public void writeTo(final OutputStream outstream) throws IOException {
            super.writeTo(new CountingOutputStream(outstream, this.listener));
        }


        /**
         * This class serves for counting progress of transferred streams.
         */
        private class CountingOutputStream extends FilterOutputStream {

            private final ProgressListener listener;
            private long transferred;

            public CountingOutputStream(final OutputStream out, final ProgressListener listener) {
                super(out);
                this.listener = listener;
                this.transferred = 0;
            }

            public void write(byte[] b, int off, int len) throws IOException {
                out.write(b, off, len);
                this.transferred += len;
                this.listener.transferred(this.transferred);
            }

            public void write(int b) throws IOException {
                out.write(b);
                this.transferred++;
                this.listener.transferred(this.transferred);
            }
        }
    }

    public interface ProgressListener {
        boolean doneFlag(boolean b);

        void transferred(long num);

    }

    public interface ImageUploaded {
        void uploadIsDone(boolean uploaded, String messege);
    }

    public void setOnUploadedListener(ImageUploaded listener) {
        this.imageUploaded = listener;
    }

    public String getUploadedImageUrl() {
        return uploadedImageUrl;
    }

    public enum PHOTO_IS {
        AVATAR,
        GENERAL
    }

    public enum PHOTO_PUBLIC {
        PRIVATE,
        PUBLIC

    }


}
