package com.github.vogoltsov.vp.plugins.design.style;

import com.github.vogoltsov.vp.plugins.design.template.ColorFieldValueConverter;
import com.github.vogoltsov.vp.plugins.design.template.TemplateField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.awt.Color;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
@AllArgsConstructor
@Builder
@Getter
public class StyleTemplate {

    @NonNull
    private String type;
    @NonNull
    private String name;

    @TemplateField(title = "Foreground Color", converter = ColorFieldValueConverter.class)
    private Color foreground;
    @TemplateField(title = "Font Name")
    private String fontName;
    @TemplateField(title = "Caption Placement")
    private Integer modelElementNameAlignment;

    @TemplateField(title = "Background Color", converter = ColorFieldValueConverter.class)
    private Color background;
    @TemplateField(title = "Secondary Background Color", converter = ColorFieldValueConverter.class)
    private Color background2;
    @TemplateField(title = "Background Color Transparency")
    private Integer fillColorTransparency;

    @TemplateField(title = "Hide Border", getter = "isBorderStrokeNull")
    private Boolean borderStrokeNull;
    @TemplateField(title = "Border Style")
    private String[] borderStrokeDashs;

}
