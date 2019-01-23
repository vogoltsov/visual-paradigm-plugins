package com.github.vogoltsov.vp.plugins.design.template;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
@AllArgsConstructor
@Getter
public class FieldValueDiff {

    @NonNull
    private Object template;

    @NonNull
    private String fieldName;
    @NonNull
    private String fieldTitle;

    @NonNull
    private Object templateValue;
    @NonNull
    private Object currentValue;

}
