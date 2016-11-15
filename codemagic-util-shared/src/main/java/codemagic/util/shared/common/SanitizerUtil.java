package codemagic.util.shared.common;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 *
 */
@GwtCompatible
public abstract class SanitizerUtil {

	private static final Logger LOGGER = Logger.getLogger(SanitizerUtil.class.getName()); 
	
	private static void log(final double value, final double defaultValue) {
		LOGGER.warning("The value is negative. Returning the default value " + defaultValue + " instead of " + value);
	}
	
	public static double defineIfNegative(final double value, final double defaultValue) {
		if (value < 0) {
			log(value, defaultValue);
			return defaultValue;
		} else {
			return value;
		}
	}

	public static long defineIfNegative(final long value, final long defaultValue) {
		if (value < 0) {
			log(value, defaultValue);
			return defaultValue;
		} else {
			return value;
		}
	}

	public static int defineIfNegative(final int value, final int defaultValue) {
		if (value < 0) {
			log(value, defaultValue);
			return defaultValue;
		} else {
			return value;
		}
	}

	public static String safeToString(final /* @Nullable */ Object o) {
		return safeToString(o, "");
	}

	public static String safeToString(final /* @Nullable */ Object o, final String dephault) {
		return o == null ? dephault : o.toString();
	}

	public static String sanitize(final /* @Nullable */ String str) {
		return sanitize(str, "");
	}

	public static String sanitize(final /* @Nullable */String str, final String defaultStr) {
		return (str == null || str.isEmpty()) ? defaultStr : str;
	}

	public static <T, K> Map<T, K> sanitize(final /* @Nullable */ Map<T, K> value, Map<T, K> defaultValue) {
		Preconditions.checkArgument(defaultValue != null, "'defaultValue' could not be null");
		return value == null ? defaultValue : value;
	}

	public static Throwable sanitize(final /* @Nullable */ Throwable value) {
		return value == null ? new Throwable("") : value; // NOPMD
	}

	public static Date sanitize(final /* @Nullable */ Date value, final Date dephault) {
		Preconditions.checkArgument(dephault != null, "'defaultValue' could not be null");
		return (value == null ? dephault : value);
	}

	public static Double sanitize(final Double value, final double defaultValue) {
		return value == null ? defaultValue : value;
	}

	public static Long sanitize(final /* @Nullable */ Long value, final long defaultStr) {
		return value == null ? defaultStr : value;
	}

	public static Integer sanitize(final /* @Nullable */ Integer value, final int defaultStr) {
		return value == null ? defaultStr : value;
	}

	@GwtCompatible(serializable = true)
	public static <T> List<T> sanitize(final /* @Nullable */ List<T> value, final List<T> defaultValue) {

		Preconditions.checkArgument(defaultValue != null, "'defaultValue' could not be null");

		return value == null ? defaultValue : value;
	}

	@GwtCompatible(serializable = true)
	public static <T> ImmutableList<T> sanitize(final /* @Nullable */ ImmutableList<T> value,
			final ImmutableList<T> defaultValue) {

		Preconditions.checkArgument(defaultValue != null, "'defaultValue' could not be null");

		return value == null ? defaultValue : value;
	}
	
	@GwtCompatible(serializable = true)
	public static <T> ImmutableSet<T> sanitize(final /* @Nullable */ ImmutableSet<T> value,
			final ImmutableSet<T> defaultValue) {

		Preconditions.checkArgument(defaultValue != null, "'defaultValue' could not be null");

		return value == null ? defaultValue : value;
	}

	public static <T> Set<T> sanitize(final /* @Nullable */ Set<T> value, final Set<T> defaultValue) {
		Preconditions.checkArgument(defaultValue != null, "'defaultValue' could not be null");

		return value == null ? defaultValue : value;
	}

	public static <T> Optional<T> sanitize(final Optional<T> content) {
		return content == null ? Optional.<T>absent() : content;
	}
	
	public static String safeGetClassName(final /* @Nullable */ Object obj) {
		if (obj == null) {
			return "null";
		} else {
			return obj.getClass().getName();
		}
	}
}
