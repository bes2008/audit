package com.jn.audit.core.resource.valuegetter;

import com.jn.audit.core.resource.valuegetter.ValueGetter;

public class CompositeValueGetter implements ValueGetter {
    private ValueGetter valueGetter;

    public CompositeValueGetter(ValueGetter valueGetter) {
        this.valueGetter = valueGetter;
    }

    @Override
    public Object get(Object input) {
        return valueGetter.get(input);
    }
}
