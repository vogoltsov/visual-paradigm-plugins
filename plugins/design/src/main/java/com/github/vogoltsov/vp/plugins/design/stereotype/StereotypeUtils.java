package com.github.vogoltsov.vp.plugins.design.stereotype;

import com.github.vogoltsov.vp.plugins.design.template.TemplateUtils;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.IStereotype;
import com.vp.plugin.model.ITaggedValueDefinition;
import com.vp.plugin.model.ITaggedValueDefinitionContainer;
import com.vp.plugin.model.factory.IModelElementFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StereotypeUtils {

    /**
     * Apply a collection of templates to current project.
     */
    public static void apply(List<StereotypeTemplate> templates) {
        IProject project = ApplicationManager.instance().getProjectManager().getProject();
        // get all stereotypes from project
        IModelElement[] stereotypes = project.toAllLevelModelElementArray(IModelElementFactory.MODEL_TYPE_STEREOTYPE);
        // construct stereotype map for ease of use
        Map<String, IStereotype> stereotypeMap = Arrays.stream(stereotypes)
                .map(IStereotype.class::cast)
                .collect(Collectors.toMap(IStereotype::getName, Function.identity()));
        // iterate through templates
        for (StereotypeTemplate template : templates) {
            // check if stereotype needs to be created first
            IStereotype stereotype = stereotypeMap.get(template.getName());
            if (stereotype == null) {
                stereotype = IModelElementFactory.instance().createStereotype();
                stereotype.setName(template.getName());
                stereotype.setBaseType(template.getBaseType());
            }
            if (!stereotype.canModify()) {
                System.out.println("Cannot apply template to stereotype '" + stereotype.getName() + "':" +
                        " destination stereotype is not modifiable");
                continue;
            }
            if (!Objects.equals(template.getBaseType(), stereotype.getBaseType())) {
                System.out.println("Cannot apply template to stereotype '" + stereotype.getName() + "':" +
                        " base type differs");
                continue;
            }
            // apply template to stereotype
            TemplateUtils.apply(template, stereotype);
            // apply template to tagged values
            if (template.getTaggedValues() != null) {
                apply(template.getTaggedValues(), stereotype);
            }
        }
    }

    private static void apply(List<TaggedValueDefinitionTemplate> templates, IStereotype stereotype) {
        // create tagged value definition container, if it does not exist
        ITaggedValueDefinitionContainer taggedValueDefinitions = stereotype.getTaggedValueDefinitions();
        if (taggedValueDefinitions == null) {
            taggedValueDefinitions = IModelElementFactory.instance().createTaggedValueDefinitionContainer();
            stereotype.setTaggedValueDefinitions(taggedValueDefinitions);
        }
        // construct tagged value definition map for ease of use
        Map<String, ITaggedValueDefinition> taggedValueDefinitionMap = Arrays
                .stream(taggedValueDefinitions.toTaggedValueDefinitionArray())
                .collect(Collectors.toMap(ITaggedValueDefinition::getName, Function.identity()));
        // iterate through templates
        for (TaggedValueDefinitionTemplate template : templates) {
            // create tagged value definition if needed
            ITaggedValueDefinition taggedValueDefinition = taggedValueDefinitionMap.get(template.getName());
            if (taggedValueDefinition == null) {
                taggedValueDefinition = taggedValueDefinitions.createTaggedValueDefinition();
                taggedValueDefinition.setName(template.getName());
            }
            // apply template to tagged value definition
            TemplateUtils.apply(template, taggedValueDefinition);
        }
    }

}
