package codemagic.widget.client.common.util;

/*>>> import org.checkerframework.checker.nullness.qual.Nullable; */
import static codemagic.util.shared.common.SanitizerUtil.sanitize;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.base.button.AbstractButton;

import com.google.common.base.Optional;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;

import codemagic.util.client.common.StringUtilClient;

public abstract class WidgetUtil {

	/**
	 * Go back to normal sate: Signals to the user that the operations has
	 * finished
	 * 
	 * @param button
	 *            The {@link Button} whose state should back to the normal.
	 */
	public static void changeToNormalState(final AbstractButton button) {
		ensureThatButtonIsNotNull(button);
		button.state().reset();
	}
	
	/**
	 * Change to loading state: Signals to the user that the operation is
	 * started.
	 * 
	 * @param button
	 *            The {@link Button} whose state should change to loading state.
	 */
	public static void changeToLoadingState(final AbstractButton button) {
		ensureThatButtonIsNotNull(button);
		button.state().loading();
	}

	/**
	 * 
	 * @param hasText The widget that implement the {@link HasText} interface. If null, this method do nothing.
	 * @param newText
	 */
	public static void setText( final Optional<Widget> hasText, /*@Nullable*/ String newText) {
		
		assert hasText != null : "Do not use null. Use Optional.absent() instead.";
				
		if ( !hasText.isPresent()) {
			GWT.log( StringUtilClient.wrap( "The element hasText is null. Nothing to do." ) );
			return;
		}
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																				
		assert hasText.get() instanceof HasText : "The widget should implement a HastText. Otherwise, there is no setText() to be called.";

		GWT.log( StringUtilClient.format2( "The text will be changed from '%s' to '%s'" , ((HasText) hasText.get()).getText(), newText ) );
		
		newText = sanitize(newText).trim();
		((HasText) hasText.get()).setText(newText);
		
		GWT.log( StringUtilClient.format2( "The text was changed to '%s'" , ((HasText) hasText.get()).getText() ) );
	}

	  //~~~~~~~~~~~~// 
	 // Utilities  //
	//~~~~~~~~~~~~// 
	private static void ensureThatButtonIsNotNull(final AbstractButton button) {
		assert button != null : "button cannot be null";
	}
}
