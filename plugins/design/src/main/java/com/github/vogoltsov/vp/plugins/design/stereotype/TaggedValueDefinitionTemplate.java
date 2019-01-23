package com.github.vogoltsov.vp.plugins.design.stereotype;

import com.github.vogoltsov.vp.plugins.design.template.TemplateField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
@AllArgsConstructor
@Builder
@Getter
public class TaggedValueDefinitionTemplate {

    @NonNull
    private String name;
    @TemplateField(title = "Description")
    private String description;

    @TemplateField(title = "Value Type")
    @NonNull
    private Integer type;

    @TemplateField(title = "Default Value")
    private String defaultValue;

}
