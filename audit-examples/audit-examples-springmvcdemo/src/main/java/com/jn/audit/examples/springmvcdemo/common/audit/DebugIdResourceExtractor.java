package com.jn.audit.examples.springmvcdemo.common.audit;

import com.jn.audit.core.AuditRequest;
import com.jn.audit.core.model.Resource;
import com.jn.audit.core.resource.AbstractIdResourceExtractor;
import com.jn.audit.examples.springmvcdemo.common.dao.UserDao;
import com.jn.audit.examples.springmvcdemo.common.model.User;
import com.jn.langx.invocation.MethodInvocation;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DebugIdResourceExtractor extends AbstractIdResourceExtractor<User, HttpServletRequest, MethodInvocation> {
    @Override
    public List<Serializable> findIds(AuditRequest<HttpServletRequest, MethodInvocation> wrappedRequest) {
        return null;
    }

    private UserDao userDao;

    @Override
    public List<User> findEntities(List<Serializable> ids) {
        final List<User> users = new ArrayList<>();
        Collects.forEach(ids, new Consumer<Serializable>() {
            @Override
            public void accept(Serializable serializable) {
                User user = userDao.selectById((String) serializable);
                if (user != null) {
                    users.add(user);
                }
            }
        });
        return users;
    }

    @Override
    public Resource extractResource(HttpServletRequest httpServletRequest, User user) {
        Resource resource = new Resource();
        resource.setResourceId(user.getId());
        return null;
    }
}
