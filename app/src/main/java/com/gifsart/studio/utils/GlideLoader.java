package com.gifsart.studio.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.bumptech.glide.GenericTransitionOptions.withNoTransition;

public class GlideLoader {
	private Context context;
	private RequestOptions displayOptions;
	private RequestBuilder<Drawable> tumbnailBuilder;
	private RequestManager fullRequest;
	private ThreadPoolAsyncTask<Void,Void,?> downloadTask;
	private Executor executor;


	public GlideLoader(Context context){
		this.context = context;
		fullRequest = Glide.with(context);
		executor = Executors.newSingleThreadExecutor();
	}


	public Glide getGlideInstance(){
		return Glide.get(context);
	}

	public RequestManager getGlideManager(){
		return Glide.with(context);
	}

	public void loadWithParams(String url, final ImageView target, RequestOptions fullOps) {
			fullRequest.asDrawable().load(url).thumbnail(0.6f).apply(fullOps).transition(withNoTransition()).into(target);
	}


	public void loadWithParamsAsDrawable(String url, final ImageView target, RequestOptions fullOps,@Nullable RequestListener<Drawable> listener) {
			if(listener!=null) {
				fullRequest.asDrawable().load(url).transition(withNoTransition()).thumbnail(0.6f).listener(listener).apply(fullOps).into(target);
			}else{
				fullRequest.asDrawable().load(url).transition(withNoTransition()).thumbnail(0.6f).apply(fullOps).into(target);
			}
	}
	public void loadWithParamsAsDrawableNoThumbnail(String url, final ImageView target, RequestOptions fullOps,@Nullable RequestListener<Drawable> listener) {
			if(listener!=null) {
				fullRequest.asDrawable().load(url).transition(withNoTransition()).listener(listener).apply(fullOps).into(target);
			}else{
				fullRequest.asDrawable().load(url).apply(fullOps).into(target);
			}
	}
	public void loadWithParamsAsGifDrawable(String url, final ImageView target, RequestOptions fullOps, RequestListener<GifDrawable> listener) {
			if(listener!=null) {
				fullRequest.asGif().load(url).thumbnail(0.6f).listener(listener).apply(fullOps).into(target);
			}else{
				fullRequest.asGif().load(url).apply(fullOps.diskCacheStrategy(DiskCacheStrategy.RESOURCE)).into(target);
			}
	}

	public void loadWithParams(String url, final ImageView target, RequestListener<Bitmap> listener, RequestOptions fullOps) {
			fullRequest.asBitmap().load(url).listener(listener).apply(fullOps).into(new SimpleTarget<Bitmap>() {
				@Override
				public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
					if (bitmap != null && !bitmap.isRecycled()) {
						target.setImageBitmap(bitmap);
					}
				}
			});

	}

	public void loadTargetWithParamsAsBitmap(String url, final ImageView imageView, RequestOptions fullOps) {
		fullRequest.asBitmap().load(url).thumbnail(0.6f).apply(fullOps).transition(withNoTransition()).into(new BitmapImageViewTarget(imageView) {
				@Override
				public void onResourceReady(Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
					if (resource != null && !resource.isRecycled()) {
						imageView.setImageBitmap(resource);
					}
				}
			});
	}public void loadTargetWithParamsAsBitmapNoThumbnail(String url, final ImageView imageView, RequestOptions fullOps) {
		fullRequest.asBitmap().load(url).apply(fullOps).transition(withNoTransition()).into(new BitmapImageViewTarget(imageView) {
			@Override
			public void onResourceReady(Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
				if (resource != null && !resource.isRecycled()) {
					imageView.setImageBitmap(resource);
				}
			}
		});
	}

	public void loadWithParams(String url,  RequestListener<File> listener) {
			fullRequest.downloadOnly().load(url).listener(listener);
	}


	public void setDisplayOptions(RequestOptions displayOptions) {
		this.displayOptions = displayOptions;
	}

	public void setTumbnailBuilder(RequestBuilder<Drawable> tumbnailBuilder) {
		this.tumbnailBuilder = tumbnailBuilder;
	}

	public Context with(){
		return this.context;
	}

	public void setFullRequest(RequestManager fullRequest) {
		this.fullRequest = fullRequest;
	}

	public void loadAndGetAsync(final String url, final RequestOptions options, final GlideDownloadListener listener, boolean isImage ){
		if(isImage){
			downloadTask = new ThreadPoolAsyncTask<Void,Void,Bitmap>() {
				@Override
				protected Bitmap doInBackground(Void... params) {
					try{
							Bitmap bmp = fullRequest
									.asBitmap()
									.load(url)
									.apply(options)
									.into(-1, -1)
									.get();
							 if(bmp !=null && !bmp.isRecycled( )){
								return bmp;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					}

					return null;
				}

				@Override
				protected void onPreExecute() {
					super.onPreExecute();
				}

				@Override
				protected void onPostExecute(Bitmap bitmap) {
					super.onPostExecute(bitmap);
					if(bitmap!=null){
						listener.downloadSuccess(bitmap);
					}else{
						listener.downloadFailed();
					}
				}

				@Override
				protected void onCancelled() {
					super.onCancelled();
					listener.downloadFailed();
				}
			};
			downloadTask.executeOnExecutor(executor);
		}

	}



	public interface GlideDownloadListener<T> {
		 void downloadFailed();
		 void downloadSuccess(T target);
	}
}
