package com.perso.surfaceanimation.engine;

import java.util.ArrayList;

import com.perso.surfaceanimation.model.Sprite;

public class MyPhysicsEngine {
	private static final String TAG = MyPhysicsEngine.class.getSimpleName();
	private int mDt = 0;//in milli
	private ArrayList<Sprite> mSpriteToUpdateList;
	private ArrayList<Sprite> mSpriteToAddList;
	private ArrayList<Sprite> mSpriteToRemoveList;
	
	private MyPhysicsEngine(){
		mSpriteToUpdateList = new ArrayList<Sprite>();
		mSpriteToAddList = new ArrayList<Sprite>();
		mSpriteToRemoveList = new ArrayList<Sprite>();
	}
	private static MyPhysicsEngine instance;
	public static MyPhysicsEngine getInstance(){
		if(instance == null){
			instance = new MyPhysicsEngine();
		}
		return instance;
	}

	public static void destroy(){
		instance = null;
	}
	
	public void setDt(int dt){
		this.mDt = dt;
	}
	
	public void addSpriteToEngine(Sprite s){
		mSpriteToAddList.add(s);
	}
	
	public void removeSpriteFromEngine(Sprite s){
		mSpriteToRemoveList.add(s);
	}
	
	public void updateSprite(){
		float xTmp = 0, yTmp = 0;
		//clean what need to be clean first
		if(mSpriteToAddList.size()>0){
			for(Sprite s:mSpriteToAddList){
				mSpriteToUpdateList.add(s);
			}
			mSpriteToAddList.clear();
		}
		if(mSpriteToRemoveList.size()>0){
			for(Sprite s:mSpriteToRemoveList){
				mSpriteToUpdateList.add(s);
			}
			mSpriteToRemoveList.clear();
		}
		if(mSpriteToUpdateList.size()>0){
			for(Sprite s:mSpriteToUpdateList){
				xTmp = (float) (s.getSpeed() * Math.cos(s.getAngle()) * mDt/1000);
				yTmp = (float) (s.getSpeed() * Math.sin(s.getAngle()) * mDt/1000);
				
				s.setX( (int) (s.getX() + xTmp));
				s.setY( (int) (s.getY() + yTmp));
				
			}
		}
		
	}
}
