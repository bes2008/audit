package com.jn.audit.core.operation;

import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer2;
import com.jn.langx.util.struct.Entry;
import com.jn.langx.util.struct.Pair;

import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OperationAnnotationParserRegistry {
    private Map<Class<? extends Annotation>, OperationAnnotationParser<?>> parserMap = new LinkedHashMap<Class<? extends Annotation>, OperationAnnotationParser<?>>();

    public void registry(Class<? extends Annotation> annotation, OperationAnnotationParser<?> parser) {
        Preconditions.checkNotNull(annotation);
        Preconditions.checkNotNull(parser);
        parserMap.put(annotation, parser);
    }

    public List<Pair<Class<? extends Annotation>, OperationAnnotationParser<?>>> getParsers() {
        final List<Pair<Class<? extends Annotation>, OperationAnnotationParser<?>>> list = Collects.newArrayList();
        Collects.forEach(parserMap, new Consumer2<Class<? extends Annotation>, OperationAnnotationParser<?>>() {
            @Override
            public void accept(Class<? extends Annotation> key, OperationAnnotationParser<?> value) {
                list.add(new Entry<Class<? extends Annotation>, OperationAnnotationParser<?>>(key, value));
            }
        });
        return list;
    }
}
