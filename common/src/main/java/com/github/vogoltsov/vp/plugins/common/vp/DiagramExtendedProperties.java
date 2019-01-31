package com.github.vogoltsov.vp.plugins.common.vp;

import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.ITaggedValue;
import com.vp.plugin.model.ITaggedValueContainer;
import com.vp.plugin.model.factory.IModelElementFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Objects;

/**
 * {@link IDiagramUIModel} supports only certain subset of properties (see {@code PROPERTY_XXX} constants in {@link IDiagramUIModel}).
 * There is no standard way to set plugin-defined properties (e.g. external id when exporting diagram to a server).
 * <br/>
 * This class provides a way to persistently store and access diagram extended attributes
 * in a special model element of type {@link IModelElementFactory#MODEL_TYPE_NOTE} using the Tagged Values API.
 *
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DiagramExtendedProperties {


    /**
     * Get diagram extended property.
     */
    public static String getProperty(IDiagramUIModel diagram, String name) {
        ITaggedValue taggedValue = getTaggedValue(diagram, name, false);
        return taggedValue != null ? taggedValue.getValueAsString() : null;
    }

    /**
     * Set diagram extended property.
     * If {@code value == null} it is effectively removed from extended property set.
     */
    public static void setProperty(IDiagramUIModel diagram, String name, String value) {
        setTaggedValue(diagram, name, value);
    }


    private static ITaggedValue getTaggedValue(IDiagramUIModel diagram, String name, boolean create) {
        ITaggedValueContainer taggedValues = getTaggedValues(diagram, create);
        if (taggedValues == null) {
            return null;
        }
        ITaggedValue taggedValue = taggedValues.getTaggedValueByName(name);
        if (taggedValue == null && create) {
            taggedValue = taggedValues.createTaggedValue();
            taggedValue.setName(name);
        }
        return taggedValue;
    }

    private static void setTaggedValue(IDiagramUIModel diagram, String name, String value) {
        ITaggedValue taggedValue = getTaggedValue(diagram, name, value != null);
        if (taggedValue == null) {
            return;
        }
        if (value != null) {
            taggedValue.setValue(value);
        } else {
            taggedValue.getTaggedValues().removeTaggedValue(taggedValue);
        }
    }

    private static ITaggedValueContainer getTaggedValues(IDiagramUIModel diagram, boolean create) {
        String extendedPropertiesModelElementName = "_diagram." + diagram.getId() + ".extended.properties";
        // model element
        IModelElement parentModelElement = diagram.getParentModel();
        IModelElement extendedPropertiesModelElement;
        if (parentModelElement != null) {
            extendedPropertiesModelElement = parentModelElement.getChildByName(extendedPropertiesModelElementName);
        } else {
            extendedPropertiesModelElement = Arrays.stream(
                    diagram.getProject().toModelElementArray(IModelElementFactory.MODEL_TYPE_NOTE)
            )
                    .filter(modelElement -> modelElement.getParent() == null)
                    .filter(modelElement -> Objects.equals(modelElement.getName(), extendedPropertiesModelElementName))
                    .findAny()
                    .orElse(null);

        }
        if (extendedPropertiesModelElement == null && create) {
            // create diagram element
            extendedPropertiesModelElement = IModelElementFactory.instance().createNOTE();
            extendedPropertiesModelElement.setName(extendedPropertiesModelElementName);
            extendedPropertiesModelElement.setDescription("Diagram extended properties");
            if (parentModelElement != null) {
                parentModelElement.addChild(extendedPropertiesModelElement);
            }
        }
        // get tagged values
        ITaggedValueContainer taggedValues = null;
        if (extendedPropertiesModelElement != null) {
            taggedValues = extendedPropertiesModelElement.getTaggedValues();
            if (taggedValues == null && create) {
                taggedValues = IModelElementFactory.instance().createTaggedValueContainer();
                extendedPropertiesModelElement.setTaggedValues(taggedValues);
            }
        }
        return taggedValues;
    }

}
