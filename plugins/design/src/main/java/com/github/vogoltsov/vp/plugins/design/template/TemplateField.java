package com.github.vogoltsov.vp.plugins.design.template;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TemplateField {

    String title() default "";

    Class converter() default FieldValueConverter.Identity.class;

    String getter() default "";

    String setter() default "";

}
