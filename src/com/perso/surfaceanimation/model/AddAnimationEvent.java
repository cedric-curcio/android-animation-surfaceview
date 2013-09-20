package com.perso.surfaceanimation.model;

import com.perso.surfaceanimation.engine.animation.MyAnimation;

/**
 * Event that add new animation to #SurfaceAnimator.
 * @author cedric
 *
 */
public class AddAnimationEvent extends AppEvent {
	public MyAnimation anim;
	public AddAnimationEvent(MyAnimation anim){
		this.anim = anim;
	}
}
