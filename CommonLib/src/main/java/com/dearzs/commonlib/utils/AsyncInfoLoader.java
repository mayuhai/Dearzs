package com.dearzs.commonlib.utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程分发类（单例）
 * @version 1.0
 */
public class AsyncInfoLoader {
	/**队列**/
	private BlockingQueue<Runnable> queue;
	/**线程池实例**/
	public ThreadPoolExecutor executor;
	/**本类实例**/
	private volatile static AsyncInfoLoader asyncInfo;

	private AsyncInfoLoader() {
		queue = new LinkedBlockingQueue<Runnable>();
		executor = new ThreadPoolExecutor(3, 5, 180, TimeUnit.SECONDS, queue);
	}
	
	/**获取线程分发器实例**/
	public static AsyncInfoLoader getInstance(){
		if(asyncInfo == null){
			synchronized (AsyncInfoLoader.class) {
				if(asyncInfo == null){
					asyncInfo = new AsyncInfoLoader();	
				}	
			}
		}
		return asyncInfo;
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
