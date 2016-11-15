package codemagic.generator.context.shared.component;

import static codemagic.util.shared.common.SanitizerUtil.sanitize;

import java.util.List;

import com.google.common.base.Verify;
import com.google.common.collect.Lists;

import codemagic.generator.context.shared.util.OtherUtil;

public class Breadcrumb<P> extends AbstractWidget<P>implements IsWidget<Breadcrumb<P>>, HasGridSystemSize, HasGridSystemOffset {

	private final List<Item> items = Lists.newArrayList();
	private String gridSystemSize = "MD_5";
	private String gridSystemOffset = "";
	
	public Breadcrumb(P parent) {
		super(parent, "org.gwtbootstrap3.client.ui.Breadcrumbs", true);
	}
	
	public Breadcrumb<P> addItem(final String text) {
		addItem(text, "");
		return this;
	}
	
	public Breadcrumb<P> addItem(final String text, final String targetHistoryTokenLiteral) {
		final Item newItem = new Item(text, targetHistoryTokenLiteral);
		items.add(newItem);
		return this;
	}
	
	
	public List<Item> getItems() {
		return items;
	}

	@Override
	public Breadcrumb<P> setEnable(final boolean enable) {
		internalSetEnable(enable);
		return this;
	}

	@Override
	public Breadcrumb<P> setTabIndex(final int tabIndex) {
		 internalSetTabIndex(tabIndex);
		return this;
	}

	@Override
	public Breadcrumb<P> setName(final String name) {
		internalSetName(name);
		return this;
	}
	
	public static class Item {
		private final String text;
		private final String escapedText;  
		private final String targetHistoryTokenLiiteral;

		/**
		 * 
		 * @param text
		 *            The text to be showed by this item within the breadcrumb
		 * @param targetHistoryToken
		 *            If empty, a ListItem will be generate. Otherwise, a
		 *            AnchorListItem.
		 */
		public Item(final String text, final /*@Nullable*/ String targetHistoryToken) {
			this.targetHistoryTokenLiiteral = sanitize(targetHistoryToken).trim();
			this.text = sanitize(text).trim();
			this.escapedText = OtherUtil.escapeParamDelimiters(text);
			
			Verify.verify( !this.text.isEmpty(), "The text to be showed in the breadcrumb cannot be null");
		}

		public String getText() {
			return text;
		}

		/**
		 * Escape the parameters delimiters.
		 * 
		 * @return For an input like ${abc}, returns _abc_. This is safe to add
		 *         to uibinder file (xml).
		 */
		public String getEscapedText() {
			return escapedText;
		}

		public String getTargetHistoryTokenLiiteral() {
			return targetHistoryTokenLiiteral;
		}
	}

	@Override
	public Breadcrumb<P> setAddInJavaCode(boolean value) {
		internalSetAddInJavacode(value);
		return this;
	}

	public Breadcrumb<P> setGridSystemSize(final String size) {
		this.gridSystemSize = sanitize(size).trim();
		Verify.verify( !this.gridSystemSize.isEmpty(), "The size cannot be null. Should be [MD|LG]_1, [MD|LG]_2, .. [MD|LG]_12");
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getGridSystemSize() {
		return gridSystemSize;
	}

	public Breadcrumb<P> setGridSystemOffset(/*@Nullable*/ final String offset) {
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
