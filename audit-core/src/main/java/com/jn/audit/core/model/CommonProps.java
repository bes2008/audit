package com.jn.audit.core.model;

import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.MapAccessor;

import java.io.Serializable;
import java.util.Map;

/**
 * Extended properties
 * 便于个性化扩展的属性
 */
public class CommonProps implements Serializable {
    public static final long serialVersionUID = 1L;

    protected Map<String, Object> props;

    public Map<String, Object> getProps() {
        return props;
    }
    public MapAccessor getPropsAccessor() {
        return props == null ? new MapAccessor(Collects.<String, Object>emptyHashMap()) : new MapAccessor(props);
    }
    public void setProps(Map<String, Object> props) {
        this.props = props;
    }
}
