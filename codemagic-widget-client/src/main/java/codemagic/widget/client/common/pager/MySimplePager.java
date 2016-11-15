package codemagic.widget.client.common.pager;

import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.view.client.HasRows;
import com.google.gwt.view.client.Range;

/**
 * Fix the pager "last page bug":
 * http://stackoverflow.com/questions/6057141/simplepager-row-count-is-working-
 * incorrectly
 *
 */
public class MySimplePager extends SimplePager {

	/**
	 * @see SimplePager#SimplePager(TextLocation, boolean, int, boolean)
	 */
	public MySimplePager(final TextLocation location, final boolean showFastForwardButton, final boolean showLastPageButton) {
		super(location, showFastForwardButton, showLastPageButton);
	}

	/**
	 * @see http://stackoverflow.com/a/8015681
	 */
	@Override
	public void setPageStart(int index) {
		final HasRows display = getDisplay();
		if (display != null) {
			Range range = display.getVisibleRange();
			int pageSize = range.getLength();
			final int newIndex = Math.max(0, index);
			if (newIndex != range.getStart()) {
				display.setVisibleRange(newIndex, pageSize);
			}
		}
	}
}
