package com.gifsart.studio.utils;


public abstract class ThreadPoolAsyncTask<Params, Progress, Result> extends ModernAsyncTask<Params, Progress, Result> {

	public void runAsyncTask(Params... params) {
		//if (Build.VERSION.SDK_INT >= 11)
			executeOnExecutor(ModernAsyncTask.THREAD_POOL_EXECUTOR, params);
		//else execute(params);
		
	}
}
