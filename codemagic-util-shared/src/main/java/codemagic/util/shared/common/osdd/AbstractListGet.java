package codemagic.util.shared.common.osdd;

import static codemagic.util.shared.common.SanitizerUtil.defineIfNegative;
import static codemagic.util.shared.common.SanitizerUtil.sanitize;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;

/**
 * Base type to fetch a Page{@literal<}T{@literal>}
 *
 * @param <T>
 *            The object type of fetched objects.
 */
@JsonIgnoreProperties(ignoreUnknown = true) 
public abstract class AbstractListGet<T extends Serializable> implements Page<T> {

	private static final long serialVersionUID = 1L;
	private int number = 0; 
	private int size = 0;
	private int numberOfElements = 0;
	private List<T> content = Lists.newArrayList();
	private int totalPages = 0;
	private long totalElements = 0;
	
	@Override
	public List<T> getContent() {
		return content;
	}

	@Override
	public int getNumber() {
		return number;
	}

	@Override
	public int getNumberOfElements() {
		return numberOfElements;
	}

	@Override
	public int getSize() {
		return size;
	}

	@Override
	public long getTotalElements() {
		return totalElements;
	}

	@Override
	public int getTotalPages() {
		return totalPages;
	}

	@JsonIgnore
	@Override
	public boolean hasContent() {
		return !content.isEmpty();
	}

	
	@JsonIgnore
	@Override
	public boolean hasNext() {
		return getNumber() + 1 < getTotalPages();
	}

	@JsonIgnore
	@Override
	public boolean hasPrevious() {
		return getNumber() > 0;
	}

	@JsonIgnore
	@Override
	public boolean isFirst() {
		return !hasPrevious();
	}

	@JsonIgnore
	@Override
	public boolean isLast() {
		return !hasNext();
	}

	public void setContent(final List<T> content) {
		this.content = sanitize(content, Lists.<T>newArrayList());
	}

	public void setNumber(final int number) {
		this.number = defineIfNegative(number, 0);
	}

	public void setNumberOfElements(final int numberOfElements) {
		this.numberOfElements = defineIfNegative(numberOfElements, 0);
	}

	public void setSize(final int size) {
		this.size = defineIfNegative(size,0);
	}

	public void setTotalElements(final long totalElements) {
		this.totalElements = defineIfNegative(totalElements, 0);
	}

	public void setTotalPages(final int totalPages) {
		this.totalPages = defineIfNegative(totalPages, 0);
	}
}
