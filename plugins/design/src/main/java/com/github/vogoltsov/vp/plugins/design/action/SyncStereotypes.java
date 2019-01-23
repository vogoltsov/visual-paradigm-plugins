package com.github.vogoltsov.vp.plugins.design.action;

import com.github.vogoltsov.vp.plugins.design.stereotype.StereotypeTemplate;
import com.github.vogoltsov.vp.plugins.design.stereotype.StereotypeUtils;
import com.github.vogoltsov.vp.plugins.design.stereotype.TaggedValueDefinitionTemplate;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.model.ITaggedValueDefinition;
import com.vp.plugin.model.factory.IModelElementFactory;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
public class SyncStereotypes implements VPActionController {

    @Override
    public void performAction(VPAction vpAction) {
        List<StereotypeTemplate> templates = new LinkedList<>();
        templates.add(StereotypeTemplate.builder()
                .baseType(IModelElementFactory.MODEL_TYPE_COMPONENT)
                .name("container")
                .taggedValues(Collections.singletonList(
                        TaggedValueDefinitionTemplate.builder()
                                .name("image")
                                .type(ITaggedValueDefinition.TYPE_TEXT)
                                .build()
                ))
                .build());
        templates.add(StereotypeTemplate.builder()
                .baseType(IModelElementFactory.MODEL_TYPE_COMPONENT)
                .name("database")
                .build());
        templates.add(StereotypeTemplate.builder()
                .baseType(IModelElementFactory.MODEL_TYPE_COMPONENT)
                .name("executable")
                .build());
        templates.add(StereotypeTemplate.builder()
                .baseType(IModelElementFactory.MODEL_TYPE_COMPONENT)
                .name("external")
                .build());
        templates.add(StereotypeTemplate.builder()
                .baseType(IModelElementFactory.MODEL_TYPE_COMPONENT)
                .name("microservice")
                .build());
        templates.add(StereotypeTemplate.builder()
                .baseType(IModelElementFactory.MODEL_TYPE_COMPONENT)
                .name("volume")
                .build());
        StereotypeUtils.apply(templates);
    }

    @Override
    public void update(VPAction vpAction) {
    }

}
