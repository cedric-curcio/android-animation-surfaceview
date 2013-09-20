package com.perso.surfaceanimation.engine.animation;

import java.util.ArrayList;

import android.graphics.Bitmap;

import com.perso.surfaceanimation.engine.GameClock;

/**
 * Basic animation that meant displaying an array bitmap
 * during a mDuration (or in loop). User can specify the started frame,
 * the starting time for a frame (usefull to chain and synchronize 2 animations)
 * 
 * GameAnimator update the values.
 * 
 * possible update/upgrade : change the current frame while playing the animation
 * 
 * @author cedric
 *
 */
public class MyAnimation {

	public long mDuration; 
	public long mWhenStart;
	public boolean mHasStarted;
	public boolean mHasfinished;
	public boolean mIsLoop;
	public ArrayList<Bitmap> mBitmaps; //bitmaps composing the animation
	public Bitmap mCurrentBitmapToDisplay;
	public int mIndexOfCurrentDisplayedBitmap;
	public long mTimeInFrame;//calculated in game animator
	private long mAdvanceTime = 0; //defined by user - add this "delay" to make the next frame arrive faster 
	private int mStartingFrame = 0;
	public int mInterval;
	public String mName;

	public MyAnimation(String name, long duration, boolean loop, Bitmap... bitmaps) {
		mDuration = duration;
		mIsLoop = loop;
		mBitmaps = new ArrayList<Bitmap>();
		for(Bitmap b:bitmaps){
			mBitmaps.add(b);
		}
		this.mName = name;
		mHasStarted = false;
		mHasfinished = false;
		mInterval = (int) (duration/bitmaps.length);
	}

	public MyAnimation(String name, long duration, boolean loop,
			ArrayList<Bitmap> bitmapsList) {
		mDuration = duration;
		mIsLoop = loop;
		mBitmaps = bitmapsList;
		mInterval = (int) (duration/bitmapsList.size());
		this.mName = name;
	}

	/**
	 * start the animation now
	 */
	public void start() {
		mHasStarted = true;
		mHasfinished = false;
		mWhenStart = GameClock.getInstance().getCurrentMilli();
		mCurrentBitmapToDisplay = mBitmaps.get(mStartingFrame);
		mIndexOfCurrentDisplayedBitmap = mStartingFrame;
		mTimeInFrame = mDuration/mBitmaps.size();
	}

	public MyAnimation copy() {
		return new MyAnimation(mName, mDuration,mIsLoop, mBitmaps);
	}

	public void stop() {
		mHasfinished = true;
	}

	public long getAdvanceTime(){
		return mAdvanceTime;
	}

	public void setAdvanceTime(long time){
		mAdvanceTime = time;
	}

	public void setStartFrame(int frame){
		mStartingFrame = frame;
	}
	@Override
	public String toString(){
		if(mName == null){
			return "no Name";
		}
		return mName;
	}

}
