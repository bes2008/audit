package com.jn.audit.core.resource.valuegetter;

public class LiteralValueGetter<V> implements ValueGetter<V, V> {
    @Override
    public V get(V input) {
        return input;
    }
}
