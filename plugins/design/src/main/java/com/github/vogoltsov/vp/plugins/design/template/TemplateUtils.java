package com.github.vogoltsov.vp.plugins.design.template;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Simple templating engine for objects.
 *
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
public class TemplateUtils {

    private static Map<Class, FieldValueConverter<Object, Object>> converters = new ConcurrentHashMap<>();


    /**
     * Compares target object against the template and returns field differences.
     */
    public static List<FieldValueDiff> compare(Object template, Object target) {
        Field[] fields = template.getClass().getDeclaredFields();
        return Arrays.stream(fields)
                .map(field -> compare(template, target, field))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private static FieldValueDiff compare(Object template, Object target, Field field) {
        try {
            String fieldName = field.getName();
            String fieldGetterName = getterName(fieldName);
            // skip non-annotated template fields
            TemplateField templateField = field.getAnnotation(TemplateField.class);
            if (templateField == null) {
                return null;
            }
            // if value is not set in template, we don't need to enforce it
            Object templateFieldValue = template.getClass().getMethod(fieldGetterName).invoke(template);
            if (templateFieldValue == null) {
                return null;
            }
            // get target field value
            String targetGetterName = !templateField.getter().isEmpty() ? templateField.getter() : fieldGetterName;
            Object targetFieldValue = target.getClass().getMethod(targetGetterName).invoke(target);
            Object convertedTargetFieldValue = converter(templateField.converter()).fromVPValue(targetFieldValue);
            // compare values
            if (Objects.equals(templateFieldValue, convertedTargetFieldValue)) {
                return null;
            }
            // return difference
            return new FieldValueDiff(
                    template,
                    fieldName,
                    templateField.title(),
                    templateFieldValue,
                    targetFieldValue
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Applies supplied template to the target object.
     */
    public static void apply(Object template, Object target) {
        try {
            Field[] fields = template.getClass().getDeclaredFields();
            Arrays.stream(fields).forEach(field -> apply(template, target, field));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void apply(Object template, Object target, Field field) {
        try {
            String fieldName = field.getName();
            String fieldGetterName = getterName(fieldName);
            String fieldSetterName = setterName(fieldName);
            // skip non-annotated template fields
            TemplateField templateField = field.getAnnotation(TemplateField.class);
            if (templateField == null) {
                return;
            }
            // if value is not set in template, we don't need to enforce it
            Object templateFieldValue = template.getClass().getMethod(fieldGetterName).invoke(template);
            if (templateFieldValue == null) {
                return;
            }
            // set target field value
            String targetGetterName = !templateField.getter().isEmpty() ? templateField.getter() : fieldGetterName;
            String targetSetterName = !templateField.setter().isEmpty() ? templateField.setter() : fieldSetterName;
            Object convertedTemplateValue = converter(templateField.converter()).toVPValue(templateFieldValue);
            target.getClass().getMethod(
                    targetSetterName,
                    // use getter return type in setter signature
                    target.getClass().getMethod(targetGetterName).getReturnType()
            ).invoke(target, convertedTemplateValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static FieldValueConverter<Object, Object> converter(Class converterClazz) {
        FieldValueConverter<Object, Object> converter = converters.get(converterClazz);
        if (converter == null) {
            try {
                //noinspection unchecked
                converter = (FieldValueConverter<Object, Object>) converterClazz.getConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Failed to instantiate value converter", e);
            }
            converters.put(converterClazz, converter);
        }
        return converter;
    }

    private static String getterName(String fieldName) {
        return "get" + capitalize(fieldName);
    }

    private static String setterName(String fieldName) {
        return "set" + capitalize(fieldName);
    }

    private static String capitalize(String fieldName) {
        return Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
    }

}
