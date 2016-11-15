package codemagic.util.client.common.event;

import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event.NativePreviewEvent;

public class EnterNativeEventHandler extends AbstractNativeEventHandler {

	public EnterNativeEventHandler(final MyPresenter presenter) {
		super(presenter);
	}
	
	@Override
	public boolean haveInterestInThisEvent(final NativePreviewEvent event) {
		// For ENTER, there are three events: keydown, keyup and presskey
		// Only keyup are used. 
		return event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER && BrowserEvents.KEYUP.equals(event.getNativeEvent().getType());
	}
}
