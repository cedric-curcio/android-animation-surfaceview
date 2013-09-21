package com.perso.surfaceanimation.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.perso.surfaceanimation.controller.MainActivityControllerRunnable;
import com.perso.surfaceanimation.engine.GameClock;
import com.perso.surfaceanimation.engine.animation.MyAnimation;
import com.perso.surfaceanimation.model.Sprite;

/**
 * SurfaceView is the component we want to test here.
 * @author cedric
 *
 */
public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback {

	private boolean mIsViewRdy;
	private int mCanvasWidth;
	private int mCanvasHeight;
	private long mStartTime;
	private Paint mTimePaint, mClearPaint, spritePaint;
	private Rect mRectHolder;
	private SurfaceHolder mSurfaceHolder;
	private MainActivityControllerRunnable mControllerRunnable;
	private Thread mControllerThread;


	public MySurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mSurfaceHolder = getHolder();
		mSurfaceHolder.addCallback(this);
		//load the controller
		mControllerRunnable = new MainActivityControllerRunnable(context, this, mSurfaceHolder);
		mControllerThread = new Thread(mControllerRunnable);

		//load the paints and rect
		mTimePaint = new Paint();
		mTimePaint.setColor(Color.RED);
		mTimePaint.setAntiAlias(true);
		mClearPaint = new Paint();
		mClearPaint.setColor(Color.WHITE);
		spritePaint = new Paint();
		spritePaint.setColor(Color.RED);
		mRectHolder = new Rect();
		//all is ready for the view
		mIsViewRdy = true;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		synchronized (mSurfaceHolder) {
			mCanvasWidth = width;
			mCanvasHeight = height;
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		mControllerRunnable.setRunning(true);
		if(!mControllerThread.isAlive()){
			if(mControllerRunnable.isInBackground()){
				mControllerRunnable.setInBackground(false);
			}
			if(!mControllerRunnable.hasBeenLaunch()){
				mControllerThread.start();
				mControllerRunnable.setHasBeenLaunch();
			}
		}
		mStartTime = GameClock.getInstance().getCurrentMilli();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
	}

	public MainActivityControllerRunnable getControllerRunnable(){
		return mControllerRunnable;
	}

	public boolean isReady() {
		return mIsViewRdy;
	}
	/**
	 * Let's draw the spent time since the surface creation.
	 */
	public void drawing(Canvas canvas) {
		synchronized (mSurfaceHolder) {
			if(!mControllerRunnable.isInBackground()){
				//erase previous canvas
				canvas.drawRect(0, 0, mCanvasWidth, mCanvasHeight, mClearPaint);

				//write the time in left top corner
				long gameTimeLong = GameClock.getInstance().getCurrentMilli() - mStartTime;
				String gameTimeString = String.format("%tM : %tS", gameTimeLong, gameTimeLong);
				canvas.drawText(gameTimeString, 10, 15, mTimePaint);

				//draw the sprite
				ArrayList<Sprite> spriteList = mControllerRunnable.getSpriteToDisplay();
				if(spriteList != null && spriteList.size()>0){
					for(Sprite s:spriteList){
						if(s != null){
							mRectHolder.left = s.getX() - s.getWidth()/2;
							mRectHolder.right = s.getX() + s.getWidth()/2;
							mRectHolder.top = s.getY() - s.getHeight()/2;
							mRectHolder.bottom= s.getY() + s.getHeight()/2;

							//if it has an animation show it
							MyAnimation anim = s.getCurrentAnimation();
							if(anim != null){
								canvas.drawBitmap(anim.mCurrentBitmapToDisplay, null, mRectHolder, spritePaint);
							}
							//else show it's default Bitmap
							else{
								canvas.drawBitmap(s.getDefaultImage(), null, mRectHolder, spritePaint);
							}
						}
					}
				}
			}		
		}
	}

}
