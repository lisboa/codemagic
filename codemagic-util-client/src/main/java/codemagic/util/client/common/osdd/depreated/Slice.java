/**
 * osdd - stands for org.springframework.data.
 */
package codemagic.util.client.common.osdd.depreated;

import java.awt.print.Pageable;

/**
 * <pre>
 * NB: fgomes
 * ===================================== 
 * = Used for serialization in client side, using the GQuery Serialization. 
 * = See <a href="http://dev.arcbees.com/gquery/data-binding.html">data binding</a>
 * = The GQuery generate the implementation via deferred bing (GWT.create).
 * =====================================
 * </pre> 
 * A slice of data that indicates whether
 * there's a next or previous slice available. Allows to obtain a
 * {@link Pageable} to request a previous or next {@link Slice}.
 * 
 * @author Oliver Gierke
 * @since 1.8
 * 
 */
public interface Slice {

	/**
	 * Returns the number of the current {@link Slice}. Is always non-negative.
	 * 
	 * @return the number of the current {@link Slice}.
	 */
	int getNumber();

	/**
	 * Returns the size of the {@link Slice}.
	 * 
	 * @return the size of the {@link Slice}.
	 */
	int getSize();

	/**
	 * Returns the number of elements currently on this {@link Slice}.
	 * 
	 * @return the number of elements currently on this {@link Slice}.
	 */
	int getNumberOfElements();

	/**
	 * Returns whether the current {@link Slice} is the first one.
	 * 
	 * @return
	 */
	boolean isFirst();

	/**
	 * Returns whether the current {@link Slice} is the last one.
	 * 
	 * @return
	 */
	boolean isLast();
}
