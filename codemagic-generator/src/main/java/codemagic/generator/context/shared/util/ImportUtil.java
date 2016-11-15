package codemagic.generator.context.shared.util;

import java.util.Set;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

public abstract class ImportUtil {

	@SafeVarargs
	public static Set<String> mergeNotEmpty(Set<String>... sets) {
		return Sets.newHashSet(Iterables.filter(Iterables.concat(sets), PredicateUtil.notEmpty));
	}
}
