package com.perso.surfaceanimation.engine;

import android.util.Log;

/**
 * A clock that doesn't count the paused time.
 * 
 * @author cedric
 *
 */
public class GameClock {

	private static final String TAG = GameClock.class.getSimpleName();
	private long mLastPauseTime;
	private long mDeltaPausedTime;
	private boolean mIsPaused;

	private GameClock(){
		mDeltaPausedTime = 0;
		mLastPauseTime = 0;
		mIsPaused = false;
	}
	private static GameClock instance;
	public static GameClock getInstance(){
		if(instance == null){
			instance = new GameClock();
		}
		return instance;
	}

	public static void destroy(){
		instance = null;
	}

	public void init() {
		mDeltaPausedTime = 0;
		mLastPauseTime = 0;
		mIsPaused = false;
	}

	/**
	 * Return the time without the paused time.
	 * @return
	 */
	public long getCurrentMilli(){
		long now = System.currentTimeMillis();
		if(mIsPaused){
			return mLastPauseTime - mDeltaPausedTime ;
		}
		return now - mDeltaPausedTime;
	}

	/**
	 * Mark the last time the game was paused with system clock.
	 */
	public void pause(){
		Log.i(TAG, "pause clock");
		mLastPauseTime = System.currentTimeMillis();
		mIsPaused = true;
	}

	/**
	 * Record all the time between the last time paused and now.
	 */
	public void unPause() {
		Log.i(TAG, "unpause clock");
		long mLastUnpauseTime = System.currentTimeMillis();
		mDeltaPausedTime += (mLastUnpauseTime - mLastPauseTime);
		mIsPaused = false;
		// true elapsed time = now - all the pause
		//			long trueElapsedTime = (mLastUnpauseTime) - mDeltaPausedTime ;
	}
}
