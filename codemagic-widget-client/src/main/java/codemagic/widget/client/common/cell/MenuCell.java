package codemagic.widget.client.common.cell;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import codemagic.util.shared.common.contract.HasId;


/**
 * 
 * Create a menu with three clickable icons: edit, duplicate and delete.
 * This cell uses the icons from http://fontawesome.io/cheatsheet/. Therefore,
 * this icons SHOULD be available in the page where this cell is rendered.
 * 
 * Note: If you are using the GwtBootstrap3, then this icons already is
 * available in the page.
 *
 * @param <T> The data passed to this cell.
 * <b>Use example</b>
 * 
 */

public class MenuCell<T extends HasId> extends AbstractCell<T> {

	public static final String RIGHT_PADDING = "5px";
	
	private static Templates templates = GWT.create(Templates.class);

	private /*@Nullable*/ ClickHandler<T> clickHandler = null;

    public interface ClickHandler<T> {
    	void onClickEvent(ClickEvent<T> event, EventType type);
    }

	public static class ClickEvent<T> {
		private final T content;
		private final int rowIndex;
		
		public ClickEvent(final T data, final int rowIndex) {
			this.content = data;
			this.rowIndex = rowIndex;
		}
		
		public T getContent() {
			return content;
		}
		public int getRowIndex() {
			return rowIndex;
		}
	}
	
	public enum EventType {
	    EDIT,
	    DUPLICATE,
	    DELETE
	}
	
    interface Templates extends SafeHtmlTemplates{
    	
	  @Template("<div data-id=\"{0}\"><i style=\"cursor:pointer;margin-right:" + RIGHT_PADDING + "\" data-action=\"js-edit\" class=\"fa fa-edit\" aria-label=\"edit script\"> </i><i style=\"cursor:pointer;margin-right:" + RIGHT_PADDING + "\" data-action=\"js-dup\" class=\"fa fa-copy d-hand\" aria-label=\"duplicate script\"> </i><i style=\"cursor:pointer;\" data-action=\"js-del\" class=\"fa fa-remove hand\" aria-label=\"delete script\"> </i></div>")	
      SafeHtml menu(long id);	
    	
    }
    
    public MenuCell() {
    	super(BrowserEvents.CLICK);
    }
    
    public MenuCell<T> addEditClickHandler(final ClickHandler<T> handler) {
    	this.clickHandler = handler;
    	return this;
    }
    
	@Override
	public void render(final Context context, /*@Nullable*/ final T value, final SafeHtmlBuilder sb) {
		
		if (value == null) {
			return;
		}

		sb.append(templates.menu(value.getId()));
	}
	
	@Override
	public void onBrowserEvent(final Context context,
			final Element parent, final T value, final NativeEvent event,
			final ValueUpdater<T> valueUpdater) {
		
		super.onBrowserEvent(context, parent, value, event, valueUpdater);

		final EventTarget target = event.getEventTarget();
		final Element el = Element.as(target);
		final String actionType = el.getAttribute("data-action");
		
		// Get the div that wrap the icons
		final Element divEl = el.getParentElement();

		// Should be a div, otherwise the user did not has clicked on the icon. 
		if (divEl == null) {
			return;
		}
		
		if (clickHandler == null) {
			return;
		}
			
		event.stopPropagation(); 
		
		if ("js-edit".equals(actionType)) {
			clickHandler.onClickEvent( new ClickEvent<T>(value, context.getIndex())  , EventType.EDIT);
			
		} else if ("js-dup".equals(actionType)) {
			clickHandler.onClickEvent( new ClickEvent<T>(value, context.getIndex()), EventType.DUPLICATE);
			
		} else if ("js-del".equals(actionType)){
			clickHandler.onClickEvent(new ClickEvent<T>(value, context.getIndex()), EventType.DELETE);
		}
			
		// GWT.log( "el=" + el.toString());
		// GWT.log( "action = " + actionType);
		
		// GWT.log(event.getType());
		
		//GWT.log( target.toString() );
		
	}

}
