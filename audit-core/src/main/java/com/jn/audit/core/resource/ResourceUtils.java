package com.jn.audit.core.resource;

import com.jn.audit.core.model.Resource;
import com.jn.langx.util.Emptys;

public class ResourceUtils {
    /**
     * 判断是否 有 resourceName
     */
    public static boolean isValid(Resource resource) {
        if (Emptys.isEmpty(resource)) {
            return false;
        }
        return Emptys.isNotEmpty(resource.getResourceName());
    }

    /**
     * 判断是否 有 resourceId ,但没有 resourceName
     */
    public static boolean isOnlyResourceId(Resource resource) {
        if (!isValid(resource)) {
            if (Emptys.isEmpty(resource)) {
                return false;
            }
            return Emptys.isNotEmpty(resource.getResourceId());
        }
        return false;
    }
}
