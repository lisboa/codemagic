package codemagic.util.shared.common;

import java.util.LinkedHashMap;
import java.util.Map;

//@formatter:off
/**
 * <a href="http://thinking-in-code.blogspot.com.br/2010/05/creating-bounded-lru-cache-with.html">Creating a bounded LRU Cache with LinkedHashMap</a>
 * @param <KEY>
 * @param <TYPE>
 */
//@formatter:on
public class BoundedLruCache<KEY, TYPE> extends LinkedHashMap<KEY, TYPE> {
	 
	private static final long serialVersionUID = 1L;

	private static  final int MY_DEFAULT_INITIAL_CAPACITY = 100;
 
    private static final float MY_DEFAULT_LOAD_FACTOR = 0.75f;
 
    private final int bound;
 
    public BoundedLruCache() {
    	this(100);
    }
    
    public BoundedLruCache(final int bound) {
        super(MY_DEFAULT_INITIAL_CAPACITY, MY_DEFAULT_LOAD_FACTOR, true);
        this.bound = bound;
    }
 
    @Override
    protected boolean removeEldestEntry(Map.Entry<KEY, TYPE> eldest) {
        return size() > bound;
    }
}