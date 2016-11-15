package codemagic.generator.context.shared.component;

/*>>> import org.checkerframework.checker.nullness.qual.*; */
import static codemagic.util.shared.common.SanitizerUtil.sanitize;

import java.util.List;

import com.google.common.base.Verify;
import com.google.common.collect.Lists;

public class ButtonGroup<P> extends AbstractWidget<P> implements IsWidget<ButtonGroup<P>>, HasGridSystemSize, HasGridSystemOffset {

	private String gridSystemSize = "MD_5";
	private String gridSystemOffset = "";
	
	private final List<Button<ButtonGroup<P>>> buttons = Lists.newArrayList();
	
	public ButtonGroup(P parent) {
		super(parent, "org.gwtbootstrap3.client.ui.ButtonGroup", false);
	}

	
	public List<Button<ButtonGroup<P>>> getButtons() {
		return buttons;
	}
	
	public Button<ButtonGroup<P>> addButton() {
		final Button<ButtonGroup<P>> button = new Button<ButtonGroup<P>>(this);
		buttons.add( button);
		return button;
	}

	@Override
	public ButtonGroup<P> setEnable(final boolean enable) {
		internalSetEnable(enable);
		return this;
	}

	@Override
	public ButtonGroup<P> setTabIndex(final int tabIndex) {
		internalSetTabIndex(tabIndex);
		return this;
	}

	@Override
	public ButtonGroup<P> setName(final String name) {
		internalSetName(name);
		return this;
	}

	@Override
	public ButtonGroup<P> setAddInJavaCode(final boolean addInJavacode) {
		internalSetAddInJavacode(addInJavacode);
		return this;
	}

	public ButtonGroup<P> setGridSystemSize(final String gridSystemSize) {
		this.gridSystemSize = sanitize(gridSystemSize).trim() ;
		Verify.verify( !this.gridSystemSize.isEmpty()  );
		return this;
	}
	
	@Override
	public String getGridSystemSize() {
		return gridSystemSize;
	}

	public ButtonGroup<P> setGridSystemOffset(/*@Nullable*/ final String offset) {
		this.gridSystemOffset = sanitize(offset).trim();
		return this;
	}

	/* (non-Javadoc)
	 * @see codemagic.generator.context.shared.component.HasGridSystemOffset#getGridSystemOffset()
	 */
	@Override
	public String getGridSystemOffset() {
		return gridSystemOffset;
	}
	
}
