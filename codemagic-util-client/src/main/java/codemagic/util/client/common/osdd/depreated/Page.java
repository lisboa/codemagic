package codemagic.util.client.common.osdd.depreated;

/**
 *<pre>
 * NB: fgomes
 * ===================================== 
 * = Used for serialization in client side, using the GQuery Serialization. 
 * = See <a href="http://dev.arcbees.com/gquery/data-binding.html">data binding</a>
 * = The GQuery generate the implementation via deferred bing (GWT.create).
 * =====================================
 * 
 * A page is a sublist of a list of objects. It allows gain information about the position of it in the containing
 * entire list.
 * 
 * @param <T>
 * @author Oliver Gierke
 */
public interface Page extends Slice {

	/**
	 * Returns the number of total pages.
	 * 
	 * @return the number of toral pages
	 */
	int getTotalPages();

	/**
	 * Returns the total amount of elements.
	 * 
	 * @return the total amount of elements
	 */
	int getTotalElements();
}
