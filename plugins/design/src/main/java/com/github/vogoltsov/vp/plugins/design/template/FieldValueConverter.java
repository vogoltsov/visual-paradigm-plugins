package com.github.vogoltsov.vp.plugins.design.template;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
public interface FieldValueConverter<D, V> {

    D fromVPValue(V vpValue);

    V toVPValue(D templateValue);


    /**
     * Identity converter does not convert anything.
     */
    class Identity implements FieldValueConverter<Object, Object> {

        @Override
        public Object fromVPValue(Object vpValue) {
            return vpValue;
        }

        @Override
        public Object toVPValue(Object templateValue) {
            return templateValue;
        }

    }

}
