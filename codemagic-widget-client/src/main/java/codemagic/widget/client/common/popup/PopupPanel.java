package codemagic.widget.client.common.popup;

import org.gwtbootstrap3.client.ui.ModalComponent;
import org.gwtbootstrap3.client.ui.base.modal.ModalContent;
import org.gwtbootstrap3.client.ui.base.modal.ModalDialog;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;

public class PopupPanel extends com.google.gwt.user.client.ui.PopupPanel {

	private final ModalContent content = new ModalContent();
	private final ModalDialog dialog = new ModalDialog();
	private ModalHeader header = new ModalHeader();

	// Close the popup
	final ClickHandler clickCloseButtonHandler = new ClickHandler() {
		@Override
		public void onClick(final ClickEvent event) {
		   hide();	
		}
	};

	public PopupPanel() {
		super();
		setGlassStyleName("fade in modal-backdrop");
		content.add(header);
		dialog.add(content);
		
		// z-index of PopupPanel should be greater than the z-index used by the glass div (1400)
		dialog.getElement().getStyle().setZIndex(2000);

		// Close the popup
		header.getCloseButton().addClickHandler(clickCloseButtonHandler);
		
		myAdd(dialog);//NOPMD
	}
	
	@Override
	public void setTitle(final String title) {
		header.setTitle(title);
	}

	@Override
	public 	void add(final Widget w) {
		myAdd(w);
	}

	// Constructors must invoke only methods that are final or private.
	// Reference: https://www.securecoding.cert.org/confluence/display/java/MET05-J.+Ensure+that+constructors+do+not+call+overridable+methods
	private void myAdd(final Widget w) {
		// User can supply own ModalHeader
		if (w instanceof ModalHeader) {
			header.removeFromParent();
			header = (ModalHeader) w;
		}

		if (w instanceof ModalComponent) {
			content.add(w);
		} else {
			super.add(w);
		}
	}
}
