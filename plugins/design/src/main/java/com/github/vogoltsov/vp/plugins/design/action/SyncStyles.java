package com.github.vogoltsov.vp.plugins.design.action;

import com.github.vogoltsov.vp.plugins.design.style.StyleTemplate;
import com.github.vogoltsov.vp.plugins.design.style.StyleUtils;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.diagram.format.LineStyle;
import com.vp.plugin.model.IStyle;
import com.vp.plugin.model.factory.IModelElementFactory;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
public class SyncStyles implements VPActionController {

    @Override
    public void performAction(VPAction vpAction) {
        List<StyleTemplate> templates = new ArrayList<>();
        templates.add(StyleTemplate.builder()
                .type(IModelElementFactory.MODEL_TYPE_COMPONENT)
                .name("UML Component - Generic - Parent Component")
                .modelElementNameAlignment(IStyle.MODEL_ELEMENT_NAME_ALIGNMENT_TOP_CENTER)
                .background(new Color(122, 207, 245))
                .fillColorTransparency(80)
                .borderStrokeNull(false)
                .borderStrokeDashs(StyleUtils.toBorderStrokeDashs(LineStyle.Style3))
                .build());
        templates.add(StyleTemplate.builder()
                .type(IModelElementFactory.MODEL_TYPE_COMPONENT)
                .modelElementNameAlignment(IStyle.MODEL_ELEMENT_NAME_ALIGNMENT_MIDDLE_CENTER)
                .name("UML Component - Generic - External Component")
                .background(Color.LIGHT_GRAY)
                .build());
        templates.add(StyleTemplate.builder()
                .type(IModelElementFactory.MODEL_TYPE_COMPONENT)
                .modelElementNameAlignment(IStyle.MODEL_ELEMENT_NAME_ALIGNMENT_MIDDLE_CENTER)
                .name("UML Component - Database Server")
                .background(new Color(255, 255, 192))
                .build());
        templates.add(StyleTemplate.builder()
                .type(IModelElementFactory.MODEL_TYPE_COMPONENT)
                .modelElementNameAlignment(IStyle.MODEL_ELEMENT_NAME_ALIGNMENT_MIDDLE_CENTER)
                .name("UML Component - Data Volume")
                .background(new Color(192, 192, 255))
                .build());
        templates.add(StyleTemplate.builder()
                .type(IModelElementFactory.MODEL_TYPE_COMPONENT)
                .modelElementNameAlignment(IStyle.MODEL_ELEMENT_NAME_ALIGNMENT_MIDDLE_CENTER)
                .name("UML Component - Application Container")
                .background(new Color(192, 255, 192))
                .build());
        StyleUtils.apply(templates);
    }

    @Override
    public void update(VPAction vpAction) {
    }

}
