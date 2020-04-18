package com.jn.audit.core.operation;

import com.jn.audit.core.operation.repository.OperationRepositoryParser;
import com.jn.audit.core.operation.method.OperationMethodAnnotationParser;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.Collects;

import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OperationParserRegistry {
    private Map<Class<? extends Annotation>, OperationMethodAnnotationParser<?>> annotationParserMap = new LinkedHashMap<Class<? extends Annotation>, OperationMethodAnnotationParser<?>>();
    private Map<String, OperationRepositoryParser> repositoryParserMap = new LinkedHashMap<String, OperationRepositoryParser>();

    public void registry(OperationMethodAnnotationParser<? extends Annotation> parser) {
        Preconditions.checkNotNull(parser);
        annotationParserMap.put(parser.getAnnotation(), parser);
    }

    public void registry(OperationRepositoryParser parser) {
        Preconditions.checkNotNull(parser);
        repositoryParserMap.put(parser.getName(), parser);
    }

    public List<OperationMethodAnnotationParser<? extends Annotation>> getAnnotationParsers() {
        return Collects.asList(annotationParserMap.values());
    }

    public OperationMethodAnnotationParser<? extends Annotation> getAnnotationParser(Class<? extends Annotation> annotationClass) {
        return annotationParserMap.get(annotationClass);
    }

    public OperationRepositoryParser getRepositoryParser(String parserName) {
        return repositoryParserMap.get(parserName);
    }

    public List<OperationRepositoryParser> getRepositoryParsers() {
        return Collects.asList(repositoryParserMap.values());
    }

}
