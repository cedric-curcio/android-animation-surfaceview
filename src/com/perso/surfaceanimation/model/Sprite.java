package com.perso.surfaceanimation.model;

import java.util.HashMap;

import com.perso.surfaceanimation.engine.animation.MyAnimation;

import android.graphics.Bitmap;

/**
 * Represent the base object to be represented on surface view.
 * 
 * @author cedric
 *
 */
public class Sprite {
	private int mX; //mandatory
	private int mY; //mandatory
	private int mWidth; //mandatory
	private int mHeight; //mandatory
	private float mAngle; //mandatory
	private Bitmap mDefaultImage; //mandatory
	private HashMap<String, MyAnimation> mAnimMap; //optional
	private MyAnimation mCurrentAnimation; //optionnal
	private int mSpeed; //optionnal
	
	public Sprite(int x, int y, int w, int h, float angle, Bitmap image){
		this.mX = x;
		this.mY = y;
		this.mWidth = w;
		this.mHeight = h;
		this.mAngle = angle;
		this.mDefaultImage = image;
	}

	public int getX() {
		return mX;
	}

	public void setX(int mX) {
		this.mX = mX;
	}
	
	public int getY() {
		return mY;
	}


	public void setY(int mY) {
		this.mY = mY;
	}
	
	public int getWidth() {
		return mWidth;
	}

	public void setWidth(int w) {
		this.mWidth = w;
	}
	
	public int getHeight() {
		return mHeight;
	}

	public void setHeight(int h) {
		this.mHeight = h;
	}

	public float getAngle() {
		return mAngle;
	}

	public void setAngle(float mAngle) {
		this.mAngle = mAngle;
	}

	public Bitmap getDefaultImage() {
		return mDefaultImage;
	}

	public void setDefaultImage(Bitmap mDefaultImage) {
		this.mDefaultImage = mDefaultImage;
	}

	public HashMap<String, MyAnimation> getAnimMap() {
		return mAnimMap;
	}

	public void setAnimMap(HashMap<String, MyAnimation> mAnimMap) {
		this.mAnimMap = mAnimMap;
	}
	
	public MyAnimation getCurrentAnimation(){
		return mCurrentAnimation;
	}
	
	public void setCurrenAnimation(MyAnimation anim){
		mCurrentAnimation = anim;
	}

	public int getSpeed() {
		return mSpeed;
	}
	
	public void setSpeed(int speed){
		mSpeed = speed;
	}
	
}
