package com.github.vogoltsov.vp.plugins.design.rule;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.diagram.shape.IBasePortUIModel;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IStyle;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Visual Paradigm API lacks certain set of functionality that is required for design rule application:
 * <ul>
 * <li>there is no way to get diagram element parent element;</li>
 * <li>when accessing elements, a new element instance is always created and returned by the API.</li>
 * </ul>
 *
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
public class DiagramElementTree {

    @Getter
    private final IDiagramUIModel diagram;
    @Getter
    private final List<Node> rootNodes;

    private final Map<String, IDiagramElement> elementMap;
    private final Map<String, String> childParentIdMap;
    private final Map<String, List<String>> parentChildIdMap;


    public DiagramElementTree(IDiagramUIModel diagram) {
        Objects.requireNonNull(diagram);
        this.diagram = diagram;
        // get all diagram elements
        IDiagramElement[] elements = diagram.toDiagramElementArray();
        // construct a (id -> element) map
        this.elementMap = Arrays.stream(elements).collect(Collectors.toMap(IDiagramElement::getId, Function.identity()));
        // construct bidirectional parent<->children id mapping
        this.parentChildIdMap = new HashMap<>();
        this.childParentIdMap = new HashMap<>();
        for (IDiagramElement element : elements) {
            IDiagramElement[] children = element.toChildArray();
            this.parentChildIdMap.put(element.getId(), Arrays.stream(children).map(IDiagramElement::getId).collect(Collectors.toList()));
            for (IDiagramElement child : children) {
                this.childParentIdMap.put(child.getId(), element.getId());
            }
        }
        // now we are able to reconstruct diagram elements as a tree
        this.rootNodes = Arrays.stream(elements)
                // filter top-level diagram elements
                .filter(iDiagramElement -> !childParentIdMap.containsKey(iDiagramElement.getId()))
                .map(iDiagramElement -> new DiagramElementTree.Node(iDiagramElement.getId(), null))
                .collect(Collectors.toList());
    }


    @SuppressWarnings("WeakerAccess")
    @Getter
    public final class Node {

        private final IDiagramElement diagramElement;
        private final IModelElement modelElement;


        private final Node parent;
        private final List<Node> children;


        private Node(String elementId, Node parent) {
            this.diagramElement = elementMap.get(elementId);
            this.modelElement = this.diagramElement.getModelElement();

            this.parent = parent;
            this.children = Collections.unmodifiableList(
                    parentChildIdMap.get(elementId).stream()
                            .map(childId -> new Node(childId, this))
                            .collect(Collectors.toList())
            );
        }

        /**
         * Returns whether this node's {@link IDiagramUIModel} has children elements,
         * which are considered "nested".
         */
        public boolean hasNestedShapes() {
            return this.children.stream().anyMatch(child -> !child.isPort());
        }

        /**
         * Returns whether the corresponding model element has a certain stereotype.
         */
        public boolean hasStereotype(String stereotype) {
            return this.modelElement.hasStereotype(stereotype);
        }


        /**
         * Returns whether this node represents a port diagram element or not.
         */
        public boolean isPort() {
            return this.diagramElement instanceof IBasePortUIModel;
        }


        /**
         * Applies a certain style to the underlying diagram element.
         */
        public void setStyle(IStyle style) {
            this.diagramElement.setStyle(style);
        }

    }
}
