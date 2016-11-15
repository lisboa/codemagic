package codemagic.util.shared.common.collection.builder;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class ImmutableMapBuilder<K,V> {
	
	private final Map<K,V> map;
	
	public ImmutableMapBuilder(final int initialCapacity) {
		this.map = new HashMap<K,V>(initialCapacity);
	}
	
	public ImmutableMapBuilder<K,V> put(final K k, final V v) {
		map.put(k, v);
		return this;
	}
	
	public ImmutableMap<K,V> build() {
		return ImmutableMap.copyOf(map);
	}

}
