package com.jn.audit.core.model;

import com.jn.langx.configuration.Configuration;
import com.jn.langx.util.Objects;
import com.jn.langx.util.Strings;
import com.jn.langx.util.hash.HashCodeBuilder;

import java.io.Serializable;

/**
 * may be in any style: xml, yaml, database
 */
public class OperationDefinition implements Configuration, Serializable {
    public static final long serialVersionUID = 1L;
    private String id; // {required} the id , also the method full name
    private String code; // {required}  the operate code
    private String name; // {required}  the operate name
    private String type; // {optional}  the operate type
    private String description;// {optional} the operate description
    private OperationImportance importance;  // {optional} the importance

    public String getCode() {
        return Strings.isEmpty(code) ? this.id : this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OperationImportance getImportance() {
        return importance;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }


    public void setImportance(OperationImportance importance) {
        this.importance = importance;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OperationDefinition that = (OperationDefinition) o;

        if(!Objects.equals(id, that.id)){
            return false;
        }
        if(!Objects.equals(name, that.name)){
            return false;
        }
        if(!Objects.equals(code, that.code)){
            return false;
        }
        if(!Objects.equals(type, that.type)){
            return false;
        }
        if(!Objects.equals(importance, that.importance)){
            return false;
        }
        if(!Objects.equals(description, that.description)){
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .with(id)
                .with(name)
                .with(code)
                .with(type)
                .with(importance)
                .with(description)
                .build();
    }
}
