package codemagic.util.client.common;

import static codemagic.util.shared.common.SanitizerUtil.sanitize;

import java.util.logging.Logger;

import com.arcbees.chosen.client.gwt.ChosenListBox;

public abstract class ChosenUtil {

	private static final Logger LOGGER = Logger.getLogger( ChosenUtil.class.getName() );
	
	
	public static int findIndexByText(final String text, final ChosenListBox chznListBox) {
		
		ensureThatChooseIsNotNull(chznListBox);
		
		for (int i=0; i<chznListBox.getItemCount(); i++) {
		    if (chznListBox.getItemText(i).equals(text)) {
		        return i;
		    }
		}
		return -1;
	}

	/**
	 * Change the current select text.
	 * 
	 * Note: This method already call the chznListBox.update(); Therefore, the
	 * client does not need to call this method. Otherwise, two update events
	 * will be fired.
	 * 
	 * @param itemText
	 *            The item text whose index should be selected. If this text is
	 *            empty neither not found, the index 0 (first element) is
	 *            selected.
	 * @param chznListBox
	 *            The {@link ChosenListBox} where the text is searched for.
	 * 
	 * @return int
	 * 
	 *         <pre>
	 *         -1 if the text is blank (null, tab or only space), -2 text not
	 *         found non_negative if the text was found
	 *         </pre>
	 */
	public static int selectByText(final /*@Nullable*/ String itemText, final ChosenListBox chznListBox) {
		ensureThatChooseIsNotNull(chznListBox);
		
		if (chznListBox.getItemCount() < 1) {
			
			LOGGER.finer( StringUtilClient.wrap("The ChosenListBox is empty. Retuning the error -3") );
			chznListBox.update();
			return -3;
		}
			
		final String sanitizedText = sanitize(itemText).trim();
		
		if (sanitizedText.isEmpty()) {
			chznListBox.setSelectedIndex(0); //this method already call the update
			LOGGER.finer( StringUtilClient.format2("The searched text is empty. Selecting the first ChosenListBox item: '%s'", chznListBox.getItemText(0)));
			return -1; //empty text
		}
			
		final int index = findIndexByText(sanitizedText, chznListBox);
   		if (index > -1) {
   			chznListBox.setSelectedIndex(index); //this method already call the update
   			return index; 
   		} else {
   			chznListBox.setSelectedIndex(0); //this method already call the update
   			LOGGER.finer( StringUtilClient.format2("The text '%s' was not found in the chznListBox. Selecting the first ChosenListBox item: '%s'", itemText, chznListBox.getItemText(0)) );
   			return -2; //text not found
   		}
	}
	
	private static void ensureThatChooseIsNotNull(final ChosenListBox chznListBox) {
		assert chznListBox != null : "chznListBox cannot not be null.";
	}
}
