package codemagic.util.client.common;

import static com.google.gwt.query.client.GQuery.$;

/*>>> import org.checkerframework.checker.nullness.qual.*; */
import com.google.gwt.core.shared.GWT;
import com.google.gwt.query.client.Function;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.IsWidget;


public abstract class SlotUtil {

	public static void setIfNotExists(final ComplexPanel panel, final /*@Nullable*/ IsWidget content, final String slotName) {
         setIfNotExists(null, panel, content, slotName, false);		
	}
	
	
	// @formatter:off
	/**
	 * 
	 * @param applyEffectTarget
	 *            The {@link ComplexPanel} that should be hide/show with
	 *            fadin/fadout. This is the outermost panel.
	 * @param panel
	 *            The {@link ComplexPanel} where the content should be put.
	 * @param content
	 * @param slotName
	 * @param forceSet
	 */
	public static void setIfNotExists(/*@Nullable*/ final ComplexPanel applyEffectTarget, final ComplexPanel panel, final /*@Nullable*/ IsWidget content, final String slotName, final boolean forceSet) {
		if (content == null) {
			hideWithEffects(applyEffectTarget);
			panel.clear();
		} else if (panel.getWidgetIndex(content) < 0 || forceSet) { // Checks if the widget already is in panel. If not, it will be added.
			panel.clear();
			hideQuietly(applyEffectTarget);
			panel.add(content);
			showWithEffects(applyEffectTarget);
			GWT.log(StringUtilClient.format2("The widget [%s] was added to Slot '%s'", content.getClass().getName(), slotName));
		} else {
			GWT.log(StringUtilClient.format2("The widget [%s] already is added to Slot '%s'. Nothing to do", content.getClass().getName(), slotName));
		}
	}
	// @formatter:on


	private static void hideQuietly(/*@Nullable*/ final ComplexPanel applyEffectTarget) {
		if (applyEffectTarget != null) {
			$(applyEffectTarget).css("opacity","0"); 
		}
	}
	
	private static void showWithEffects(/*@Nullable*/ final ComplexPanel applyEffectTarget) {
		if (applyEffectTarget != null) {
			$(applyEffectTarget).animate("opacity:1");
		}
	}

	private static void hideWithEffects(/*@Nullable*/ final ComplexPanel applyEffectTarget) {
		if (applyEffectTarget != null) {
			if ( !applyEffectTarget.isVisible()) { return; } 
			$(applyEffectTarget).css("opacity","1");
			$(applyEffectTarget).animate("opacity:0", new Function(){
				@Override
				public void f() {
					applyEffectTarget.setVisible(false);
				}
			});
		}
	}
}
