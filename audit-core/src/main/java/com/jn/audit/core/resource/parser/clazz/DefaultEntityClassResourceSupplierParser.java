package com.jn.audit.core.resource.parser.clazz;

import com.jn.audit.core.resource.supplier.EntityResourceSupplier;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer2;
import com.jn.langx.util.function.Predicate2;
import com.jn.langx.util.struct.Holder;

import java.util.List;

/**
 * 推荐单例使用
 *
 * @param <T>
 */
public class DefaultEntityClassResourceSupplierParser<T> implements EntityClassResourceSupplierParser<T> {
    private static List<EntityClassResourceSupplierParser> delegates = Collects.newArrayList(AnnotatedEntityResourceSupplierParser.DEFAULT_INSTANCE, FieldNameEntityResourceSupplierParser.DEFAULT_INSTANCE);
    public static final DefaultEntityClassResourceSupplierParser DEFAULT_INSTANCE = new DefaultEntityClassResourceSupplierParser();

    @Override
    public EntityResourceSupplier<T> parse(Class<T> entityClass) {
        Holder<EntityResourceSupplier> supplierHolder = new Holder<EntityResourceSupplier>();
        Collects.forEach(delegates, new Consumer2<Integer, EntityClassResourceSupplierParser>() {
            @Override
            public void accept(Integer index, EntityClassResourceSupplierParser delegate) {
                supplierHolder.set((EntityResourceSupplier) delegate.parse(entityClass));
            }
        }, new Predicate2<Integer, EntityClassResourceSupplierParser>() {
            @Override
            public boolean test(Integer index, EntityClassResourceSupplierParser delegate) {
                return !supplierHolder.isNull();
            }
        });

        return supplierHolder.get();
    }
}
