package com.dearzs.upload.uploadimage.utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程分发类（单例）
 */
public class ThreadPool {
	/**队列**/
	private BlockingQueue<Runnable> queue;
	/**线程池实例**/
	private ThreadPoolExecutor executor;
	/**本类实例**/
	private volatile static ThreadPool mThreadPool;

	private ThreadPool() {
	}
	
	/**获取线程分发器实例**/
	public static ThreadPool getInstence(){
		if(mThreadPool == null){
			synchronized (ThreadPool.class) {
				if(mThreadPool == null){
					mThreadPool = new ThreadPool();
				}	
			}
		}
		return mThreadPool;
	}

	public void execute(Runnable runnable) {
		if (queue == null) {
			queue = new LinkedBlockingQueue<Runnable>();
		}
		if (executor == null) {
			executor = new ThreadPoolExecutor(3, 5, 0L, TimeUnit.MILLISECONDS, queue);
		}
		executor.execute(runnable);
	}
}
