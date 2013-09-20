package com.perso.surfaceanimation.engine.animation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import com.perso.surfaceanimation.controller.MainActivityControllerRunnable;
import com.perso.surfaceanimation.engine.GameClock;
import com.perso.surfaceanimation.model.AddAnimationEvent;
import com.perso.surfaceanimation.model.RemoveAnimationEvent;

/**
 * SurfaceAnimator manages the bitmap animation life cycle.
 * 
 * @author cedric
 *
 */
public class SurfaceAnimator {

	private static final String TAG = SurfaceAnimator.class.getSimpleName();
	private HashSet<MyAnimation> mAnimSet;
	private ArrayList<MyAnimation> mAnimToAdd;
	private long mNowTime;
	private boolean isInit;
	private MainActivityControllerRunnable mRunnable;
	private static SurfaceAnimator instance;


	public static SurfaceAnimator getInstance(){
		if(instance == null){
			instance = new SurfaceAnimator();
		}
		return instance;
	}

	private SurfaceAnimator() {
		mAnimSet = new HashSet<MyAnimation>();
		mAnimToAdd = new ArrayList<MyAnimation>();
		isInit = false;
	}
	public void init(MainActivityControllerRunnable run){
		isInit = true;
		mRunnable = run;
	}
	public void postToRegisterAnimation(MyAnimation anim){
		mRunnable.sendGameEvent(new AddAnimationEvent(anim));
	}

	//NO safe thread, use register animation if not in processing event thread 
	public void addAnimation(AddAnimationEvent event){
		mAnimToAdd.add(event.anim);
	}

	/**
	 * Must be called from processing event thread only.
	 */
	public void update(){
		mNowTime = GameClock.getInstance().getCurrentMilli();

		//remove finished anim
		Iterator<MyAnimation>itRemove = mAnimSet.iterator();
		while(itRemove.hasNext()){
			MyAnimation currentAnim = itRemove.next();
			if(currentAnim.mHasfinished == true){
				itRemove.remove();
			}
		}
		//add animation needed to be added
		if(mAnimToAdd.size()>0){
			Iterator<MyAnimation>itAdd = mAnimToAdd.iterator();
			while(itAdd.hasNext()){
				MyAnimation anim = itAdd.next();
				mAnimSet.add(anim);
				itAdd.remove();
			}
		}
		//set next frame
		for(MyAnimation anim:mAnimSet){
			if(anim.mHasStarted && !anim.mHasfinished){
				//set the image the view will display depending of the time
				if(mNowTime-anim.mWhenStart+anim.getAdvanceTime() >= anim.mDuration && !anim.mIsLoop){
					anim.mHasfinished = true;
					anim.mCurrentBitmapToDisplay = anim.mBitmaps.get(anim.mBitmaps.size() -1);
					anim.mIndexOfCurrentDisplayedBitmap = anim.mBitmaps.size() -1;
					anim.mTimeInFrame = 0;
				}
				else{
					int interval = anim.mInterval;
					int rest = (int) ((mNowTime-anim.mWhenStart+anim.getAdvanceTime())/interval);
					if(anim.mIsLoop){
						rest = rest%anim.mBitmaps.size();
					}
					else if(rest >= anim.mBitmaps.size()){
						rest = rest - 1;
					}
					anim.mTimeInFrame = ((mNowTime-anim.mWhenStart+anim.getAdvanceTime()) % interval);
					anim.mCurrentBitmapToDisplay = anim.mBitmaps.get(rest);
					anim.mIndexOfCurrentDisplayedBitmap = rest;
				}
			}
		}
	}

	public void postToUnregisterAnimation(MyAnimation anim) {
		mRunnable.sendGameEvent(new RemoveAnimationEvent(anim));
	}
	
	/**
	 * Must be call from main thread.
	 * @param event
	 */
	public void unregisterAnimation(RemoveAnimationEvent event){
		Iterator<MyAnimation>it = mAnimSet.iterator();
		while(it.hasNext()){
			MyAnimation currentAnim = it.next();
			if(currentAnim == event.anim){
				currentAnim.mHasfinished = true;
			}
		}
	}
	
	public void clearAll(){
		mAnimSet.clear();
		mAnimToAdd.clear();
	}
}
