package codemagic.generator.context.shared.util;

import static codemagic.util.shared.common.SanitizerUtil.sanitize;

import com.google.common.base.Predicate;

public class PredicateUtil {

	public static Predicate<String> notEmpty = new Predicate<String>() {

		@Override
		public boolean apply(final String input) {
			return !sanitize(input).trim().isEmpty();
		}
	}; 
}
