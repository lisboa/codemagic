package codemagic.generator.context.shared.component;

/*>>> import org.checkerframework.checker.nullness.qual.*; */
import static codemagic.util.shared.common.SanitizerUtil.sanitize;

import com.google.common.base.Verify;

/**
 * Held a (1) Widget, (2) an event name and (3) the action to be executed in
 * response to this event. This action can be a nameToken to where to go or
 * (TODO create an option to add java code here.)<br />
 * <b>How to use it</b>
 * 
 * <pre>
 *  1. Build a list of Happened for each event type: one list for click events, other for mouseHover, etc.
 *  2. Then, use each list in the templates. For example, A list of clicakble Widgets can be used
 *  to build the handlers for click events:
 *  
 *  <code>
 *    ${foreach clickables clicakble}
 *    &#64;UiHandler("${clickable.name}")
 *    public void on${clickable.Name}(final ClickEvent event) {
 *      //do something
 *    }
 *    ${end}
 *  </code>
 * </pre>
 * 
 * The [subject]Context is responsible for building theses list as needed.
 */
public class Happened<P> {

	private final AbstractWidget<P> widget;
	private final String happenedName;
	private final String actionCode;
	
	public Happened(final AbstractWidget<P> widget, final String happenedName, /*@Nullable*/ final String actionCode) {
		this.widget = widget;
		this.happenedName = sanitize(happenedName).trim();
		this.actionCode = sanitize(actionCode).trim();
		
		Verify.verify( widget != null, "The widget cannot be null");
		Verify.verify( !this.happenedName.isEmpty(), "The happened name, used to build the event name, cannot be null.");
	}

	public AbstractWidget<P> getWidget() {
		return widget;
	}

	public String getHappenedName() {
		return happenedName;
	}


	/**
	 * The java code to be executed when this event occurr. In the example
	 * bellow, the action is navegate to home page. Note that the code is append
	 * as is to generated code.
	 * 
	 * <code>
	 *    "PlaceUtil.goto(uHome);"
	 * </code>
	 * 
	 * @return The java code to be executed when this event occurr.
	 */
	public String getActionCode() {
		return actionCode;
	}
}
