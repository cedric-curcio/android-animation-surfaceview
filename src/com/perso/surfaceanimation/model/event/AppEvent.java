package com.perso.surfaceanimation.model.event;

import com.perso.surfaceanimation.engine.GameClock;

/**
 * Parent of all apps events.
 * @author cedric
 *
 */
public class AppEvent {
	public long eventTime;
	public AppEvent() {
		eventTime = GameClock.getInstance().getCurrentMilli();
	}
}