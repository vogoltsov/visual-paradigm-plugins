package com.github.vogoltsov.vp.plugins.design.template;

import java.awt.Color;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
public class ColorFieldValueConverter implements FieldValueConverter<Color, Integer> {

    @Override
    public Color fromVPValue(Integer vpValue) {
        return new Color(vpValue);
    }

    @Override
    public Integer toVPValue(Color templateValue) {
        return templateValue.getRGB();
    }

}
