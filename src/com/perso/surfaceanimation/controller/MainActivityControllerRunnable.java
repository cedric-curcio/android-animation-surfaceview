package com.perso.surfaceanimation.controller;

import java.util.concurrent.ConcurrentLinkedQueue;

import android.content.Context;
import android.graphics.Canvas;
import android.view.SurfaceHolder;

import com.perso.surfaceanimation.engine.GameClock;
import com.perso.surfaceanimation.engine.animation.SurfaceAnimator;
import com.perso.surfaceanimation.model.AddAnimationEvent;
import com.perso.surfaceanimation.model.AppEvent;
import com.perso.surfaceanimation.view.MySurfaceView;

/**
 * Manage the main activity logic through a loop a event.
 * Actualy it just draws the surface view.
 * 
 * @author cedric
 *
 */
public class MainActivityControllerRunnable implements Runnable {

	/**
	 * State of app.
	 */
	public static final int STATE_LOADING = 0;
	public static final int STATE_PAUSED = 1;
	public static final int STATE_RUNNING = 2;

	public static final int REFRESH_RATE = 33; //in millisecond

	/**
	 * The base time to wait between 2 loop calls,
	 *  change this if you are in a state when you don't need frequent refresh
	 */
	private int mDelay = REFRESH_RATE; 
	private int mState; //state of the app
	private boolean mRun = false; //state of the thread
	private boolean mIsControllerReady;
	private boolean mIsInBackground;//tell if the app has the focus or not
	private boolean mHasbeenLaunched = false;
	private long mNowTime;
	private MySurfaceView mSurfaceView;
	private Context mContext;
	private SurfaceHolder mSurfaceHolder; //needed to get the canvas to draw on surfaceview

	/** Queue for AppEvents */
	private ConcurrentLinkedQueue<AppEvent> mEventQueue = new ConcurrentLinkedQueue<AppEvent>();

	public MainActivityControllerRunnable(Context c, MySurfaceView v, SurfaceHolder surfaceHolder){
		mContext = c;
		mSurfaceView = v;
		mSurfaceHolder = surfaceHolder;
		SurfaceAnimator.getInstance().init(this);
		mIsControllerReady = true;
	}

	@Override
	public void run() {
		Canvas canvas = null;
		long sleepTime;
		while(mRun){
			mNowTime = System.currentTimeMillis();

			if(mState == STATE_LOADING){
				checkLoading();
			}
			else if(mState == STATE_RUNNING){
				//.. nothing atm, we will process event and animation later
				updateController();
			}

			//draw if the thread isn't in background
			if(!mIsInBackground){
				mDelay = REFRESH_RATE;
				try {
					canvas = mSurfaceHolder.lockCanvas(null);
					mSurfaceView.drawing(canvas);

				} finally {
					if (canvas != null) {
						mSurfaceHolder.unlockCanvasAndPost(canvas);
					}
				}
			}
			else {
				mDelay = 500; //save battery
			}
			sleepTime = mDelay-(System.currentTimeMillis() - mNowTime);
			if(sleepTime>0){
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	private void updateController() {
		while(true){
			AppEvent event = mEventQueue.poll();
			if(event == null){
				break;
			}
			else if(event instanceof AddAnimationEvent){
				SurfaceAnimator.getInstance().addAnimation((AddAnimationEvent) event);
			}
			//create your events here
		}
		//update animation here
		SurfaceAnimator.getInstance().update();
	}

	public void pause(){
		if(mState == STATE_RUNNING){
			GameClock.getInstance().pause();
			mState = STATE_PAUSED;
		}
	}

	public void unpause(){
		if(mState == STATE_PAUSED){
			GameClock.getInstance().unPause();
			mState = STATE_RUNNING;
		}
	}

	private void checkLoading() {
		if(mSurfaceView.isReady() && mIsControllerReady){
			setState(STATE_RUNNING);
		}
	}

	public int getState(){
		return mState;
	}

	public void setState(int state){
		mState = state;
	}

	public boolean isInBackground() {
		return mIsInBackground;
	}

	public void setInBackground(boolean b) {
		mIsInBackground = b;		
	}

	public boolean hasBeenLaunch() {
		return mHasbeenLaunched;
	}

	public void setHasBeenLaunch() {
		mHasbeenLaunched = true;		
	}

	public void setRunning(boolean b) {
		mRun = b;		
	}

	/**
	 * post an event in the queue
	 * @param event
	 */
	public void sendGameEvent(AppEvent event) {
		mEventQueue.add(event);
	}

}
