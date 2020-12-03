package com.jn.audit.entityloader.resttemplate;

import com.jn.audit.core.exception.IllegalResourceDefinition;
import com.jn.audit.core.model.ResourceDefinition;
import com.jn.audit.core.resource.idresource.EntityLoader;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Arrs;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.MapAccessor;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Function2;
import com.jn.langx.util.function.Predicate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class SpringRestTemplateEntityLoader implements EntityLoader<Object> {

    private static Pattern restTemplateVariablePattern = Pattern.compile("\\{\\w+(\\.[\\w\\-]+)*}");
    private static Pattern httpUrlVariablePattern = Pattern.compile("\\$\\{\\w+(\\.[\\w\\-]+)*}");
    private Environment environment;
    private HttpRequestProvider httpRequestProvider = new DefaultHttpRequestProvider();
    private ParameterizedResponseClassProvider parameterizedResponseClassProvider = new DefaultParameterizedResponseClassProvider();
    private ResourceEntityExtractor resourceEntityExtractor = new DefaultResourceEntityExtractor();
    private RestTemplateProvider restTemplateProvider;
    private String name = "rest";

    @Override
    public List<Object> load(final ResourceDefinition resourceDefinition, final List<Serializable> ids) {
        final MapAccessor mapAccessor = resourceDefinition.getDefinitionAccessor();
        final boolean httpBatchMode = mapAccessor.getBoolean("httpBatchMode", false);

        final List<Object> entities = Collects.emptyArrayList();
        Collects.forEach(Collects.asList(Arrs.range(ids.size())), new Consumer<Integer>() {
            @Override
            public void accept(Integer index) {
                String url = findHttpUrl(resourceDefinition);
                url = replaceAuditVariables(url, resourceDefinition, ids, httpBatchMode, index);
                Map<String, Object> urlVariables = findSpringRestUrlVariables(url, resourceDefinition, ids, httpBatchMode, index);

                HttpMethod httpMethod = findHttpMethod(resourceDefinition);
                HttpEntity httpEntity = httpRequestProvider.get(url, httpMethod, resourceDefinition, ids.get(index));
                ParameterizedTypeReference responseEntityClass = parameterizedResponseClassProvider.get(url, httpMethod, resourceDefinition);
                RestTemplate restTemplate = restTemplateProvider.get(url, httpMethod, resourceDefinition);
                Preconditions.checkNotNull(restTemplate, "the restTemplate is null");
                ResponseEntity responseEntity = restTemplate.exchange(url, httpMethod, httpEntity, responseEntityClass, urlVariables);
                List<Object> objs = extractResult(responseEntity);
                entities.addAll(objs);
            }
        }, new Predicate<Integer>() {
            @Override
            public boolean test(Integer index) {
                return httpBatchMode;
            }
        });
        return entities;
    }


    protected List<Object> extractResult(ResponseEntity responseEntity) {
        Object obj = resourceEntityExtractor.extract(responseEntity);
        if(Objs.isEmpty(obj)){
            return null;
        }
        if (obj instanceof Collection) {
            return Collects.asList(obj);
        } else {
            return Collects.newArrayList(obj);
        }
    }


    protected String findHttpUrl(final ResourceDefinition resourceDefinition) {
        final MapAccessor mapAccessor = resourceDefinition.getDefinitionAccessor();
        String url = mapAccessor.getString("httpUrl");
        if (Emptys.isEmpty(url)) {
            throw new IllegalResourceDefinition("the httpUrl property is undefined in the resource definition");
        }
        return url;
    }

    protected String replaceAuditVariables(
            @NonNull String url,
            @NonNull final ResourceDefinition resourceDefinition,
            @NonNull final List<Serializable> ids,
            @NonNull final boolean httpBatchMode,
            @NonNull final int index) {
        final MapAccessor mapAccessor = resourceDefinition.getDefinitionAccessor();
        url = StringTemplates.format(url, httpUrlVariablePattern, new Function2<String, Object[], String>() {
            public String apply(String variable, final Object[] args) {
                if (variable.startsWith("${") && variable.endsWith("}")) {
                    variable = variable.substring(2, variable.length() - 1);
                }
                if (variable.equals("resourceId")) {
                    if (httpBatchMode) {
                        return Strings.join(",", ids);
                    }
                    return ids.get(index).toString();
                }
                String variableValue = environment.getProperty(variable);
                if (Emptys.isEmpty(variableValue)) {
                    variableValue = mapAccessor.getString(variable);
                }
                return variableValue;
            }
        }, Emptys.EMPTY_OBJECTS);

        return url;
    }

    protected Map<String, Object> findSpringRestUrlVariables(
            @NonNull String url,
            @NonNull final ResourceDefinition resourceDefinition,
            @NonNull final List<Serializable> ids,
            @NonNull final boolean httpBatchMode,
            @NonNull final int index) {

        final Map<String, Object> urlVariables = new HashMap<String, Object>();
        StringTemplates.format(url, restTemplateVariablePattern, new Function2<String, Object[], String>() {
            public String apply(String variable, final Object[] args) {
                String variableValue = null;
                if (variable.startsWith("{") && variable.endsWith("}")) {
                    variable = variable.substring(1, variable.length() - 1);
                }
                if (variable.equals("resourceId")) {
                    if (httpBatchMode) {
                        variableValue = Strings.join(",", ids);
                    } else {
                        variableValue = ids.get(index).toString();
                    }
                }
                if (Emptys.isEmpty(variableValue)) {
                    variableValue = environment.getProperty(variable);
                }
                if (Emptys.isEmpty(variableValue)) {
                    variableValue = (String) resourceDefinition.get(variable);
                }
                if (Emptys.isNotEmpty(variableValue)) {
                    urlVariables.put(variable, variableValue);
                }
                return variableValue;
            }
        }, Emptys.EMPTY_OBJECTS);
        return urlVariables;
    }

    protected HttpMethod findHttpMethod(ResourceDefinition resourceDefinition) {
        MapAccessor mapAccessor = resourceDefinition.getDefinitionAccessor();
        String httpMethod = mapAccessor.getString("httpMethod", "GET").toUpperCase();
        return HttpMethod.resolve(httpMethod);
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public void setResourceEntityExtractor(ResourceEntityExtractor resourceEntityExtractor) {
        if (resourceEntityExtractor != null) {
            this.resourceEntityExtractor = resourceEntityExtractor;
        }
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (Emptys.isNotEmpty(name)) {
            this.name = name;
        }
    }

    public void setHttpRequestProvider(HttpRequestProvider httpRequestProvider) {
        if (httpRequestProvider != null) {
            this.httpRequestProvider = httpRequestProvider;
        }
    }

    public void setParameterizedResponseClassProvider(ParameterizedResponseClassProvider parameterizedResponseClassProvider) {
        this.parameterizedResponseClassProvider = parameterizedResponseClassProvider;
    }

    public void setRestTemplateProvider(RestTemplateProvider restTemplateProvider) {
        this.restTemplateProvider = restTemplateProvider;
    }

}
