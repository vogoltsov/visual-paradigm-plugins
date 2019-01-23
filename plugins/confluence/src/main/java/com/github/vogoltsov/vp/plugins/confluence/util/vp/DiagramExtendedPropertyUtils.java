package com.github.vogoltsov.vp.plugins.confluence.util.vp;

import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.ITaggedValue;
import com.vp.plugin.model.ITaggedValueContainer;
import com.vp.plugin.model.factory.IModelElementFactory;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
public class DiagramExtendedPropertyUtils {

    private static final String PROPERTY_CONTENT_ID = "confluence.page.id";
    private static final String PROPERTY_ATTACHMENT_ID = "confluence.attachment.id";


    public static String getDiagramConfluencePageId(IDiagramUIModel diagram) {
        ITaggedValue taggedValue = getTaggedValue(diagram, PROPERTY_CONTENT_ID, false);
        return taggedValue != null ? taggedValue.getValueAsString() : null;
    }

    public static String getDiagramConfluenceAttachmentId(IDiagramUIModel diagram) {
        ITaggedValue taggedValue = getTaggedValue(diagram, PROPERTY_ATTACHMENT_ID, false);
        return taggedValue != null ? taggedValue.getValueAsString() : null;
    }

    public static void setDiagramConfluencePageId(IDiagramUIModel diagram, String pageId) {
        setTaggedValue(diagram, PROPERTY_CONTENT_ID, pageId);
    }

    public static void setDiagramConfluenceAttachmentId(IDiagramUIModel diagram, String attachmentId) {
        setTaggedValue(diagram, PROPERTY_ATTACHMENT_ID, attachmentId);
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
        IModelElement extendedPropertiesModelElement = diagram.getParentModel().getChildByName(extendedPropertiesModelElementName);
        if (extendedPropertiesModelElement == null && create) {
            // create diagram element
            extendedPropertiesModelElement = diagram.getParentModel().createChild(IModelElementFactory.MODEL_TYPE_NOTE);
            extendedPropertiesModelElement.setName(extendedPropertiesModelElementName);
            extendedPropertiesModelElement.setDescription("Diagram extended properties");
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


    private DiagramExtendedPropertyUtils() {
    }

}
