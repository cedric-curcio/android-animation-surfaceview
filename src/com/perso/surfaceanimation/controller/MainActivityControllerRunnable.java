package com.perso.surfaceanimation.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

import com.perso.git.surfaceanimation.R;
import com.perso.surfaceanimation.engine.GameClock;
import com.perso.surfaceanimation.engine.MyPhysicsEngine;
import com.perso.surfaceanimation.engine.animation.MyAnimation;
import com.perso.surfaceanimation.engine.animation.SurfaceAnimator;
import com.perso.surfaceanimation.model.Sprite;
import com.perso.surfaceanimation.model.event.AddAnimationEvent;
import com.perso.surfaceanimation.model.event.AppEvent;
import com.perso.surfaceanimation.view.MySurfaceView;

/**
 * Manage the main activity logic through a loop a event.
 * Actualy it just draws the surface view.
 * 
 * @author cedric
 *
 */
public class MainActivityControllerRunnable implements Runnable {

	private static final String TAG = MainActivityControllerRunnable.class.getSimpleName();
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

	/** sprite to show on surface view	 */
	private ArrayList<Sprite> mSpriteToDisplayList; //the list of sprite to display;
	//use the two next list as a temp container to store the sprite during the loop,
	//at the end of loop add all sprite from mSpriteToAddList to mSpriteToDisplayList
	//and remove all from mSpriteToDisplayList that are in mSpriteToRemoveList
	//then clean both mSpriteToAddList and mSpriteToRemoveList
//	private ArrayList<Sprite> mSpriteToAddList; 
//	private ArrayList<Sprite> mSpriteToRemoveList;
	
	public MainActivityControllerRunnable(Context c, MySurfaceView v, SurfaceHolder surfaceHolder){
		mContext = c;
		mSurfaceView = v;
		mSurfaceHolder = surfaceHolder;
		mSpriteToDisplayList = new ArrayList<Sprite>();
//		mSpriteToAddList = new ArrayList<Sprite>();
//		mSpriteToRemoveList = new ArrayList<Sprite>();
		
		SurfaceAnimator.getInstance().init(this);

		//let's test an animation with some sprites
		initScenario();


		mIsControllerReady = true;
	}

	/**
	 * For the demo we will make a train moving from east to west, train smoke will be animated.
	 * The train will move with an event we will repeat every loop turn before event management.
	 */
	private void initScenario() {
		//we will use a train and his smoke
		//train won't be animated but his smoke yes
		Resources res = mContext.getResources();
		Sprite train = new Sprite(0, 100, 48, 48, 0, 
				BitmapFactory.decodeResource(res, R.drawable.loco_we_1_small));
		Sprite smoke = new Sprite(0, 76, 48, 96, 0,
				BitmapFactory.decodeResource(res, R.drawable.smoke_we_1_small));
		train.setSpeed(50);
		smoke.setSpeed(50);
		//make the smoke animation
		ArrayList<Bitmap> bitmapList = new ArrayList<Bitmap>();
		bitmapList.add(BitmapFactory.decodeResource(res, R.drawable.smoke_we_1_small));
		bitmapList.add(BitmapFactory.decodeResource(res, R.drawable.smoke_we_2_small));
		bitmapList.add(BitmapFactory.decodeResource(res, R.drawable.smoke_we_3lives_small));

		String animationName = "SMOKE_EW_HAPPY_ANIMATION";
		MyAnimation smokeAnimation = new MyAnimation(animationName, 1500, true, bitmapList);
//		HashMap<String, MyAnimation> animMap = new HashMap<String, MyAnimation>();
//		animMap.put(animationName, smokeAnimation);
//		smoke.setAnimMap(animMap);
		smoke.setCurrenAnimation(smokeAnimation);

		//add the animation to animator, since the thread haven't started we can use the addAnimation method
		SurfaceAnimator.getInstance().addAnimation(smokeAnimation);
		//let's start it
		smokeAnimation.start();
		
		mSpriteToDisplayList.add(train);
		mSpriteToDisplayList.add(smoke);
		MyPhysicsEngine.getInstance().addSpriteToEngine(train);
		MyPhysicsEngine.getInstance().addSpriteToEngine(smoke);
	}

	@Override
	public void run() {
		Canvas canvas = null;
		long sleepTime;
		while(mRun){
			int dt = (int) (System.currentTimeMillis()-mNowTime);//different time between 2 loop turns
//			Log.d(TAG, "DT = "+dt);
			MyPhysicsEngine.getInstance().setDt(dt);
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
				mDelay = 500; //save battery in background
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
		//update physical engine
		MyPhysicsEngine.getInstance().updateSprite();
	
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
	
	public ArrayList<Sprite> getSpriteToDisplay(){
		return mSpriteToDisplayList;
	}

	/**
	 * post an event in the queue
	 * @param event
	 */
	public void sendGameEvent(AppEvent event) {
		mEventQueue.add(event);
	}

}
