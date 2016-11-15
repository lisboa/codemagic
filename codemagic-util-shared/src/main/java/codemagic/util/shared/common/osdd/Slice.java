package codemagic.util.shared.common.osdd;

import java.io.Serializable;
import java.util.List;

/**
 * ***********************
 * Copy from Spring data
 * ***********************
 * 
 * A slice of data that indicates whether there's a next or previous slice available. Allows to obtain a
 * {@link org.springframework.data.domain.Pageable} to request a previous or next {@link Slice}.
 * 
 * @author Oliver Gierke
 * @since 1.8
 */
public interface Slice<T extends Serializable> extends Serializable {

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
	 * Returns the page content as {@link List}.
	 * 
	 * @return
	 */
	List<T> getContent();

	/**
	 * Returns whether the {@link Slice} has content at all.
	 * 
	 * @return
	 */
	boolean hasContent();

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

	/**
	 * Returns if there is a next {@link Slice}.
	 * 
	 * @return if there is a next {@link Slice}.
	 */
	boolean hasNext();

	/**
	 * Returns if there is a previous {@link Slice}.
	 * 
	 * @return if there is a previous {@link Slice}.
	 */
	boolean hasPrevious();
}

