package codemagic.util.shared.common.osdd;

import static codemagic.util.shared.common.SanitizerUtil.sanitize;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.base.Optional;

/**
 * Base type to fetch one single object.
 * 
 * Note: Add annotations @JsonCreator and @JsonProperty("content") on child constructor to keep the
 * immutability of theses classes.
 *
 * @param <T>
 *            The object type of fetched object.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AbstractOneGet<T extends Serializable> implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Optional<T> content;

	public AbstractOneGet(final Optional<T> content) {
		this.content = sanitize(content);
	}

	public Optional<T> getContent() {
		return content;
	}
}
