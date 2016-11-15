package codemagic.util.client.common.event;

import static codemagic.util.shared.common.SanitizerUtil.sanitize;

import com.google.common.base.Optional;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;

import codemagic.util.client.common.StringUtilClient;

public abstract class AbstractNativeEventHandler {
	
	/** The handler for native event*/
	private final NativePreviewHandler handler;
	
	private Optional<HandlerRegistration> handlerRegistration = Optional.absent(); 
	
	
	/**
	 * 
	 * @param presenter
	 *            The presenter that catch the native event. This allows, for
	 *            example, add shortcuts (ctrl+z, for example), define ENTER key
	 *            as default action, etc.
	 */
	public AbstractNativeEventHandler(final MyPresenter presenter) {
		assert presenter != null : "Presenter cannot be null";
		
		handler = new NativePreviewHandler() {
			@Override
			public void onPreviewNativeEvent(final NativePreviewEvent event) {
				if ( presenter.isVisible() && haveInterestInThisEvent(event) ) {
					if ( ! sanitize(presenter.logNativeEventMessage()).trim().isEmpty()) {
						GWT.log(StringUtilClient.wrap(presenter.logNativeEventMessage())	);
					}
				    event.consume();
				    event.getNativeEvent().stopPropagation();
				    event.getNativeEvent().preventDefault();
					presenter.onNativeEvent();
				}
			}
		};
	}

	public void register() {
		if (! handlerRegistration.isPresent()) {
			handlerRegistration = Optional.of( Event.addNativePreviewHandler(handler) );
		}
		
	}
	
	public void unRegister() {
		if (handlerRegistration.isPresent()) {
			handlerRegistration.get().removeHandler();
			handlerRegistration = Optional.absent();
		}
	}

	public abstract boolean haveInterestInThisEvent(NativePreviewEvent event);
	
	   //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
	  // The presenter that will catch     //
	 //  the native event                 //
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
	public static interface MyPresenter {
		
		/**
		 * An event is catched only if the presenter is visible. Otherwise, the
		 * event is ignored.  
		 */
		boolean isVisible();
		
		/**
		 * 
		 * @return The message to be logged when the event is catched. For debug
		 *         purposes only. In production, this log is removed. If null or
		 *         empty, nothing is logged.
		 */
		/*@Nullable*/ String logNativeEventMessage();
		
		
		/**
		 * The action to be executed when the event is catched.
		 */
		void onNativeEvent();
	}
}
