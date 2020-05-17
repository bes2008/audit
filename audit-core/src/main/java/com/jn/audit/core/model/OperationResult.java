package com.jn.audit.core.model;

import com.jn.langx.Delegatable;
import com.jn.langx.util.enums.base.CommonEnum;
import com.jn.langx.util.enums.base.EnumDelegate;

import java.io.Serializable;

public enum OperationResult implements CommonEnum, Delegatable<EnumDelegate>, Serializable {
    SUCCESS(1, "success", "成功"),
    FAIL(0, "fail", "失败");

    public static final long serialVersionUID = 1L;

    private OperationResult(int code, String name, String displayText) {

    }

    private EnumDelegate delegate;

    @Override
    public int getCode() {
        return delegate.getCode();
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public String getDisplayText() {
        return delegate.getDisplayText();
    }

    @Override
    public EnumDelegate getDelegate() {
        return delegate;
    }

    @Override
    public void setDelegate(EnumDelegate delegate) {
        this.delegate = delegate;
    }
}
