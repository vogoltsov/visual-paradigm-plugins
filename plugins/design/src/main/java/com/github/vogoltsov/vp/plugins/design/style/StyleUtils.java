package com.github.vogoltsov.vp.plugins.design.style;

import com.github.vogoltsov.vp.plugins.design.template.TemplateUtils;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.diagram.IShapeUIModel;
import com.vp.plugin.diagram.format.LineStyle;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.IStyle;
import com.vp.plugin.model.IStyleSetContainer;
import com.vp.plugin.model.factory.IModelElementFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StyleUtils {

    /**
     * Apply a collection of templates to current project.
     */
    public static void apply(List<StyleTemplate> templates) {
        IProject project = ApplicationManager.instance().getProjectManager().getProject();
        // get container element for all styles in a project
        IStyleSetContainer styles = (IStyleSetContainer) project.toModelElementArray(IModelElementFactory.MODEL_TYPE_STYLE_SET_CONTAINER)[0];
        // construct style map for ease of use
        Map<String, IStyle> styleMap = Arrays.stream(styles.toStyleArray())
                .collect(Collectors.toMap(IStyle::getName, Function.identity()));
        // iterate through templates
        for (StyleTemplate template : templates) {
            // check if style needs to be created first
            IStyle style = styleMap.get(template.getName());
            if (style == null) {
                IDiagramUIModel diagram;
                IDiagramElement element = null;
                try {
                    // create a prototype diagram element
                    diagram = ApplicationManager.instance().getDiagramManager().getActiveDiagram();
                    element = diagram.createDiagramElement(template.getType());
                    // create style and copy properties from a diagram element
                    style = styles.createStyle();
                    style.setName(template.getName());
                    copy(element, style);
                } catch (Exception e) {
                    System.out.println("Style " + template.getName() + " could not be created");
                    e.printStackTrace();
                    if (style != null) {
                        try {
                            style.delete();
                        } catch (Exception e1) {
                            System.out.println("Failed to delete style");
                            e1.printStackTrace();
                        }
                    }
                    continue;
                } finally {
                    if (element != null) {
                        try {
                            element.getModelElement().delete();
                        } catch (Exception e) {
                            System.out.println("Failed to delete model element");
                            e.printStackTrace();
                        }
                    }
                }
            }
            TemplateUtils.apply(template, style);
        }
    }

    /**
     * Copies style settings from diagram element to a style.
     */
    public static void copy(IDiagramElement element, IStyle style) {
/*
        commented lines are used for setters from IStyle
        which have no corresponding properties in element,
        or these properties cannot be converted easily
*/

//        style.setQualityScore(int var1);
//        style.setQualityReason(String var1);
        if (element instanceof IShapeUIModel) {
            IShapeUIModel shape = (IShapeUIModel) element;
            style.setPrimitiveShapeType(shape.getPrimitiveShapeType());
            style.setCustomText(shape.getCustomText());
            style.setModelElementNameAlignment(shape.getModelElementNameAlignment());
            style.setFillColorType(shape.getFillColor().getType());
            style.setFillColorGradientStyle(shape.getFillColor().getGradientStyle());
            style.setFillColorTransparency(shape.getFillColor().getTransparency());
            style.setBackground(shape.getFillColor().getColor1().getRGB());
            style.setBackground2(
                    Optional.ofNullable(shape.getFillColor().getColor2())
                            .map(Color::getRGB)
                            .orElse(Color.BLACK.getRGB())
            );
        }
        style.setForeground(element.getForeground().getRGB());
        style.setFontName(element.getElementFont().getName());
        style.setFontStyle(element.getElementFont().getStyle());
        style.setFontSize(element.getElementFont().getSize());

/*
        there is no way we can programmatically convert
        LineStyle from element.getLineModel().getLineStyle()
        to these stroke properties
*/
        style.setBorderStrokeNull(toBorderStrokeNull(element.getLineModel().getLineStyle()));
        style.setBorderStrokeDashs(toBorderStrokeDashs(element.getLineModel().getLineStyle()));
        style.setBorderWeight(element.getLineModel().getWeight());
        style.setBorderColor(element.getLineModel().getColor().getRGB());
        style.setBorderTransparency(element.getLineModel().getTransparency());
//        style.setBeginArrowName(String var1);
//        style.setBeginArrowSize( int var1);
//        style.setBeginArrowWidth( int var1);
//        style.setBeginArrowHeight( int var1);
//        style.setBeginArrowUseFillColor( boolean var1);
//        style.setBeginArrowFillColor( int var1);
//        style.setEndArrowName(String var1);
//        style.setEndArrowSize( int var1);
//        style.setEndArrowWidth( int var1);
//        style.setEndArrowHeight( int var1);
//        style.setEndArrowUseFillColor( boolean var1);
//        style.setEndArrowFillColor( int var1);
//        style.setCompartmentFontName(String var1);
//        style.setCompartmentFontStyle(int var1);
//        style.setCompartmentFontSize(int var1);
//        style.setShapeLegend(IShapeLegend var1);
//        style.setColorLegend(IColorLegend var1);
    }


    /**
     * Returns value to be used in {@link IStyle#setBorderStrokeNull(boolean)} from {@link LineStyle}.
     * <br/>
     * {@code true} value disables border around the component,
     * while {@code false} renders the border according to {@link IStyle#getBorderStrokeDashs()}.
     */
    public static boolean toBorderStrokeNull(LineStyle lineStyle) {
        return lineStyle != null && lineStyle == LineStyle.None;
    }

    /**
     * Returns value to be used in {@link IStyle#setBorderStrokeDashs(String[])} from {@link LineStyle}.
     * <br/>
     * Note: I had to manually iterate through all the line styles in VP UI to get this mapping. :)
     */
    public static String[] toBorderStrokeDashs(LineStyle lineStyle) {
        switch (lineStyle) {
            case Style1:
                return null;
            case Style2:
                return new String[]{"8", "5"};
            case Style3:
                return new String[]{"1", "5"};
            case Style4:
                return new String[]{"8", "5", "1", "5"};
            case Style5:
                return new String[]{"8", "5", "1", "5", "1", "5"};
            case Style6:
                return new String[]{"8", "4", "8", "5", "1", "5"};
            case Style7:
                return new String[]{"20", "5", "4", "5"};
            case Style8:
                return new String[]{"20", "5", "4", "5", "4", "5"};
            case Style9:
                return new String[]{"4"};
            case Style10:
                return new String[]{"1", "3"};
            case Style11:
                return new String[]{"4", "3", "1", "3"};
            case Style12:
                return new String[]{"4", "3", "1", "3", "1", "3"};
            case Style13:
                return new String[]{"4", "3", "4", "3", "1", "3"};
            case Style14:
                return new String[]{"10", "3", "2", "3"};
            case Style15:
                return new String[]{"10", "3", "2", "3", "2", "3"};
            case Style16:
                return new String[]{"16", "9"};
            case Style17:
                return new String[]{"1", "9"};
            case Style18:
                return new String[]{"16", "9", "1", "9"};
            case Style19:
                return new String[]{"16", "9", "1", "9", "1", "9"};
            case Style20:
                return new String[]{"16", "9", "16", "9", "1", "9"};
            case Style21:
                return new String[]{"40", "9", "8", "9", "7", "9"};
            case Style22:
                return new String[]{"2"};
            case None:
            default:
                return null;
        }
    }

}
