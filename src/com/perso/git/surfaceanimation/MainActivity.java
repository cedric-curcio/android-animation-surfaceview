package com.perso.git.surfaceanimation;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.perso.git.surfaceanimation.controller.MainActivityControllerRunnable;
import com.perso.git.surfaceanimation.view.MySurfaceView;

public class MainActivity extends Activity implements OnClickListener{

	private MainActivityControllerRunnable mRunnable;
	private MySurfaceView mSurfaceView;
	private Button mPauseBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mSurfaceView = (MySurfaceView) findViewById(R.id.surfaceView1);
		mRunnable = mSurfaceView.getControllerRunnable();
		mPauseBtn = (Button)findViewById(R.id.pauseBtn);
		mPauseBtn.setOnClickListener(this);
		mPauseBtn.setText("Click to pause");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	@Override
	protected void onPause() {
		super.onPause();
		mRunnable.pause();
		mPauseBtn.setText("Click to run");
	}

	@Override
	protected void onResume(){
		super.onResume();
		mRunnable.setInBackground(false);
		//remove the pause manualy by touching the btn
	}

	@Override
	protected void onStop() {
		super.onStop();
		mRunnable.setInBackground(true);
	}

	@Override
	public void onClick(View v) {
		if(v == mPauseBtn){
			if(mRunnable.getState() == MainActivityControllerRunnable.STATE_PAUSED){
				mRunnable.unpause();
				mPauseBtn.setText("Click to pause");
			}
			else if(mRunnable.getState() == MainActivityControllerRunnable.STATE_RUNNING){
				mRunnable.pause();
				mPauseBtn.setText("Click to run");
			}
		}
		
	}


}
