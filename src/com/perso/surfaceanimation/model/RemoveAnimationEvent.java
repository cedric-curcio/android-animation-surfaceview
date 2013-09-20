package com.perso.surfaceanimation.model;

import com.perso.surfaceanimation.engine.animation.MyAnimation;

/**
 * Event that remove previously added animation from #SurfaceAnimator.
 * @author cedric
 *
 */
public class RemoveAnimationEvent extends AppEvent {
	public MyAnimation anim;
	public RemoveAnimationEvent(MyAnimation anim){
		this.anim = anim;
	}
}
