package codemagic.generator.context.types;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class TabdIndexHolder {
	
	private static final ThreadLocal<AtomicInteger> thl = new ThreadLocal<AtomicInteger>() {
		@Override
		protected AtomicInteger initialValue() {
			return new AtomicInteger(0);
		};
	}; 
	
	public static int addAndGet(final int delta) {
		return thl.get().addAndGet(delta);
	}
	
	public static int next() {
		return thl.get().incrementAndGet();
	}
	
	public static int set(final int value) {
		thl.get().set(value);
		return value;
	}
	
	public static int get() {
		return thl.get().get();
	}
	
	public static void reset() {
		thl.get().set(0);
	}
	
	public static void clear() {
		thl.remove();
	}
}
