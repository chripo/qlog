package com.quui.utils.event;

/**
 * Internal class which stores an <code>IListener</code> for a type
 *
 * @author maxmc
 *
 */
public class ListenerContainer {
	private Enum<? extends IEventType> _type;
	private IListener _eventlistener;

	/**
	 * @param type
	 *            the type bound to the listener
	 * @param eventlistener
	 *            the listener
	 */
	public ListenerContainer(Enum<? extends IEventType> type, IListener eventlistener) {
		_type = type;
		_eventlistener = eventlistener;
	}

	/**
	 * @return the listeners type
	 */
	public Enum<? extends IEventType> getType() {
		return _type;
	}

	/**
	 * @return the <code>IListener</code>
	 */
	public IListener getEventListener() {
		return _eventlistener;
	}

}
