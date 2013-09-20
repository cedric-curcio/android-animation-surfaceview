package com.perso.surfaceanimation.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.perso.surfaceanimation.controller.MainActivityControllerRunnable;
import com.perso.surfaceanimation.engine.GameClock;

public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback {

	private boolean mIsViewRdy;
	private int mCanvasWidth;
	private int mCanvasHeight;
	private Context mContext;
	private long mStartTime;
	private Paint mTimePaint, mClearPaint;
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
		//load the paints
		mTimePaint = new Paint();
		mTimePaint.setColor(Color.RED);
		mTimePaint.setAntiAlias(true);
		mClearPaint = new Paint();
		mClearPaint.setColor(Color.WHITE);
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
			//erase previous canvas
			canvas.drawRect(0, 0, mCanvasWidth, mCanvasHeight, mClearPaint);
			
			//write the time in left top corner
			long gameTimeLong = GameClock.getInstance().getCurrentMilli() - mStartTime;
			String gameTimeString = String.format("%tM : %tS", gameTimeLong, gameTimeLong);
			canvas.drawText(gameTimeString, 10, 15, mTimePaint);
		}		
	}

}
