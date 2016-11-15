package codemagic.util.shared.common;

/*>>> import org.checkerframework.checker.nullness.qual.*; */
import com.google.common.base.Optional;

public abstract class OptionalUtil {

	/**
	 * Used to harmonize the use of {@link Optional} with external API's that
	 * expect to receive raw object (including null) instead of {@link Optional}
	 * . This should be the exception, because preferably the API, when
	 * possible, should use optional values.
	 * 
	 * @param optional
	 * @return The underline object or null if there is no object.
	 */
	public static <T> /*@Nullable*/ T unWrap(final Optional<T> optional) {
		if (optional.isPresent()) {
			return optional.get();
		} else {
			return null;
		}
	}
}
