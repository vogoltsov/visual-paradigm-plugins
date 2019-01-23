package com.github.vogoltsov.vp.plugins.design.stereotype;

import com.github.vogoltsov.vp.plugins.design.template.TemplateField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
@AllArgsConstructor
@Builder
@Getter
public class StereotypeTemplate {

    @NonNull
    private String baseType;
    @NonNull
    private String name;
    @TemplateField(title = "Description")
    private String description;

    private List<TaggedValueDefinitionTemplate> taggedValues;

}
