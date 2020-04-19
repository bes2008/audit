package com.jn.audit.spring.webmvc;

import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.Annotation;
import java.util.List;

public interface RequestMappingAccessor<E extends Annotation> {
    E getMapping();

    void setMapping(E mapping);

    String getName();

    List<String> getValues();

    List<String> getPaths();

    List<RequestMethod> getMethods();

    List<String> params();

    List<String> headers();

    List<String> consumes();

    List<String> produces();
}
