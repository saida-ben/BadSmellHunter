package org.mql.java.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.List;
import java.util.ArrayList;
import org.mql.java.models.*;

public class UMLRelationPanel extends JPanel {
    private Project project;
    private static final int ARROW_SIZE = 10;
    private static final int DIAMOND_SIZE = 8;
    
    // Couleurs pour différents types de relations
    private static final Color INHERITANCE_COLOR = new Color(0, 100, 0);
    private static final Color ASSOCIATION_COLOR = new Color(0, 0, 150);
    private static final Color AGGREGATION_COLOR = new Color(150, 75, 0);
    private static final Color COMPOSITION_COLOR = new Color(150, 0, 0);
    private static final Color DEPENDENCY_COLOR = new Color(128, 128, 128);
    
    public UMLRelationPanel(Project project) {
        this.project = project;
        setOpaque(false);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Activer l'antialiasing pour des lignes lisses
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setStroke(new BasicStroke(2.0f));
        
        // Dessiner toutes les relations
        drawInheritanceRelations(g2d);
        drawInterfaceImplementations(g2d);
        drawAssociationRelations(g2d);
        drawDependencyRelations(g2d);
        
        // Ajouter une légende
        drawLegend(g2d);
    }
    
    private void drawInheritanceRelations(Graphics2D g2d) {
        g2d.setColor(INHERITANCE_COLOR);
        
        for (PackageInfo pkg : project.getPackages()) {
            for (ClassInfo classInfo : pkg.getClasses()) {
                if (classInfo.getSuperclass() != null) {
                    ClassInfo superClass = findClassByName(classInfo.getSuperclass());
                    if (superClass != null) {
                        Point from = getClassCenter(classInfo);
                        Point to = getClassCenter(superClass);
                        
                        if (from != null && to != null) {
                            // Dessiner la ligne
                            drawLine(g2d, from, to);
                            // Dessiner la flèche triangle (héritage)
                            drawInheritanceArrow(g2d, from, to);
                            // Ajouter le label
                            drawRelationLabel(g2d, from, to, "extends", INHERITANCE_COLOR);
                        }
                    }
                }
            }
        }
    }
    
    private void drawInterfaceImplementations(Graphics2D g2d) {
        g2d.setColor(INHERITANCE_COLOR);
        Stroke oldStroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 
                                      0, new float[]{5, 5}, 0)); // Ligne pointillée
        
        for (PackageInfo pkg : project.getPackages()) {
            for (ClassInfo classInfo : pkg.getClasses()) {
                for (String interfaceName : classInfo.getInterfaces()) {
                    ClassInfo interfaceClass = findClassByName(interfaceName);
                    if (interfaceClass != null) {
                        Point from = getClassCenter(classInfo);
                        Point to = getClassCenter(interfaceClass);
                        
                        if (from != null && to != null) {
                            drawLine(g2d, from, to);
                            drawInheritanceArrow(g2d, from, to);
                            drawRelationLabel(g2d, from, to, "implements", INHERITANCE_COLOR);
                        }
                    }
                }
            }
        }
        
        g2d.setStroke(oldStroke);
    }
    
    private void drawAssociationRelations(Graphics2D g2d) {
        g2d.setColor(ASSOCIATION_COLOR);
        
        for (PackageInfo pkg : project.getPackages()) {
            for (ClassInfo classInfo : pkg.getClasses()) {
                // Analyser les champs pour détecter les associations
                for (FieldModel field : classInfo.getFields()) {
                    RelationType relationType = analyzeFieldRelation(field);
                    if (relationType != RelationType.NONE) {
                        ClassInfo targetClass = findClassByFieldType(field.getType());
                        if (targetClass != null && !targetClass.getName().equals(classInfo.getName())) {
                            Point from = getClassCenter(classInfo);
                            Point to = getClassCenter(targetClass);
                            
                            if (from != null && to != null) {
                                drawRelationByType(g2d, from, to, relationType, field.getName());
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void drawDependencyRelations(Graphics2D g2d) {
        g2d.setColor(DEPENDENCY_COLOR);
        Stroke oldStroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 
                                      0, new float[]{3, 3}, 0));
        
        for (PackageInfo pkg : project.getPackages()) {
            for (ClassInfo classInfo : pkg.getClasses()) {
                // Analyser les méthodes pour les dépendances
                for (MethodInfo method : classInfo.getMethods()) {
                    // Cette partie nécessiterait une analyse plus poussée du code source
                    // pour détecter les paramètres et types de retour
                }
            }
        }
        
        g2d.setStroke(oldStroke);
    }
    
    private void drawRelationByType(Graphics2D g2d, Point from, Point to, RelationType type, String label) {
        switch (type) {
            case ASSOCIATION:
                g2d.setColor(ASSOCIATION_COLOR);
                drawLine(g2d, from, to);
                drawSimpleArrow(g2d, from, to);
                drawRelationLabel(g2d, from, to, label, ASSOCIATION_COLOR);
                break;
                
            case AGGREGATION:
                g2d.setColor(AGGREGATION_COLOR);
                drawLine(g2d, from, to);
                drawDiamondArrow(g2d, from, to, false); // Diamant vide
                drawRelationLabel(g2d, from, to, label + " (aggregation)", AGGREGATION_COLOR);
                break;
                
            case COMPOSITION:
                g2d.setColor(COMPOSITION_COLOR);
                drawLine(g2d, from, to);
                drawDiamondArrow(g2d, from, to, true); // Diamant plein
                drawRelationLabel(g2d, from, to, label + " (composition)", COMPOSITION_COLOR);
                break;
        }
    }
    
    private RelationType analyzeFieldRelation(FieldModel field) {
        String type = field.getType().toLowerCase();
        String name = field.getName().toLowerCase();
        
        // Détecter la composition (relation forte)
        if (name.contains("component") || name.contains("part") || 
            type.contains("list<") || type.contains("arraylist<") || 
            type.contains("set<") || type.contains("collection<")) {
            return RelationType.COMPOSITION;
        }
        
        // Détecter l'agrégation (relation faible)
        if (name.contains("has") || name.contains("contains") || 
            name.contains("manager") || name.contains("reference")) {
            return RelationType.AGGREGATION;
        }
        
        // Association simple si c'est un objet personnalisé
        if (isCustomClass(field.getType())) {
            return RelationType.ASSOCIATION;
        }
        
        return RelationType.NONE;
    }
    
    private boolean isCustomClass(String type) {
        // Exclure les types primitifs et classes de base Java
        if (type.matches("(int|long|double|float|boolean|char|byte|short)")) return false;
        if (type.startsWith("java.") || type.startsWith("javax.")) return false;
        if (type.equals("String") || type.equals("Object")) return false;
        
        // Si c'est une classe du projet, c'est une association
        return findClassByName(type) != null;
    }
    
    private void drawLine(Graphics2D g2d, Point from, Point to) {
        // Calculer les points d'intersection avec les bordures des classes
        Point adjustedFrom = adjustPointToBorder(from, to, 100, 50); // largeur et hauteur approximatives
        Point adjustedTo = adjustPointToBorder(to, from, 100, 50);
        
        g2d.drawLine(adjustedFrom.x, adjustedFrom.y, adjustedTo.x, adjustedTo.y);
    }
    
    
    private void drawInheritanceArrow(Graphics2D g2d, Point from, Point to) {
        Point adjustedTo = adjustPointToBorder(to, from, 100, 50);
        
        double angle = Math.atan2(from.y - adjustedTo.y, from.x - adjustedTo.x);
        
        // Triangle pour l'héritage
        int[] xPoints = new int[3];
        int[] yPoints = new int[3];
        
        xPoints[0] = adjustedTo.x;
        yPoints[0] = adjustedTo.y;
        xPoints[1] = (int) (adjustedTo.x + ARROW_SIZE * Math.cos(angle + Math.PI/6));
        yPoints[1] = (int) (adjustedTo.y + ARROW_SIZE * Math.sin(angle + Math.PI/6));
        xPoints[2] = (int) (adjustedTo.x + ARROW_SIZE * Math.cos(angle - Math.PI/6));
        yPoints[2] = (int) (adjustedTo.y + ARROW_SIZE * Math.sin(angle - Math.PI/6));
        
        // Triangle vide (héritage)
        g2d.setColor(Color.WHITE);
        g2d.fillPolygon(xPoints, yPoints, 3);
        g2d.setColor(INHERITANCE_COLOR);
        g2d.drawPolygon(xPoints, yPoints, 3);
    }
    
    private void drawSimpleArrow(Graphics2D g2d, Point from, Point to) {
        Point adjustedTo = adjustPointToBorder(to, from, 100, 50);
        
        double angle = Math.atan2(from.y - adjustedTo.y, from.x - adjustedTo.x);
        
        // Flèche simple
        int x1 = (int) (adjustedTo.x + ARROW_SIZE * Math.cos(angle + Math.PI/6));
        int y1 = (int) (adjustedTo.y + ARROW_SIZE * Math.sin(angle + Math.PI/6));
        int x2 = (int) (adjustedTo.x + ARROW_SIZE * Math.cos(angle - Math.PI/6));
        int y2 = (int) (adjustedTo.y + ARROW_SIZE * Math.sin(angle - Math.PI/6));
        
        g2d.drawLine(adjustedTo.x, adjustedTo.y, x1, y1);
        g2d.drawLine(adjustedTo.x, adjustedTo.y, x2, y2);
    }
    
    private void drawDiamondArrow(Graphics2D g2d, Point from, Point to, boolean filled) {
        Point adjustedFrom = adjustPointToBorder(from, to, 100, 50);
        
        double angle = Math.atan2(to.y - adjustedFrom.y, to.x - adjustedFrom.x);
        
        // Diamant
        int[] xPoints = new int[4];
        int[] yPoints = new int[4];
        
        int centerX = (int) (adjustedFrom.x + DIAMOND_SIZE * Math.cos(angle));
        int centerY = (int) (adjustedFrom.y + DIAMOND_SIZE * Math.sin(angle));
        
        xPoints[0] = adjustedFrom.x;
        yPoints[0] = adjustedFrom.y;
        xPoints[1] = (int) (centerX + DIAMOND_SIZE/2 * Math.cos(angle + Math.PI/2));
        yPoints[1] = (int) (centerY + DIAMOND_SIZE/2 * Math.sin(angle + Math.PI/2));
        xPoints[2] = (int) (adjustedFrom.x + 2 * DIAMOND_SIZE * Math.cos(angle));
        yPoints[2] = (int) (adjustedFrom.y + 2 * DIAMOND_SIZE * Math.sin(angle));
        xPoints[3] = (int) (centerX + DIAMOND_SIZE/2 * Math.cos(angle - Math.PI/2));
        yPoints[3] = (int) (centerY + DIAMOND_SIZE/2 * Math.sin(angle - Math.PI/2));
        
        if (filled) {
            // Diamant plein (composition)
            g2d.fillPolygon(xPoints, yPoints, 4);
        } else {
            // Diamant vide (agrégation)
            g2d.setColor(Color.WHITE);
            g2d.fillPolygon(xPoints, yPoints, 4);
            g2d.setColor(AGGREGATION_COLOR);
        }
        g2d.drawPolygon(xPoints, yPoints, 4);
    }
   
    private Point getClassCenter(ClassInfo classInfo) {
        if (classInfo.getLocation() != null) {
            return classInfo.getLocation();
        }
        return null;
    }
    
    private ClassInfo findClassByName(String className) {
        for (PackageInfo pkg : project.getPackages()) {
            for (ClassInfo classInfo : pkg.getClasses()) {
                if (classInfo.getName().equals(className) || 
                    classInfo.getName().endsWith("." + className)) {
                    return classInfo;
                }
            }
        }
        return null;
    }
    
    private ClassInfo findClassByFieldType(String type) {
        // Nettoyer le type (enlever génériques, tableaux, etc.)
        String cleanType = type.replaceAll("<.*>", "").replaceAll("\\[\\]", "");
        if (cleanType.contains(".")) {
            cleanType = cleanType.substring(cleanType.lastIndexOf(".") + 1);
        }
        return findClassByName(cleanType);
    }
    
    // Enum pour les types de relations
    private enum RelationType {
        NONE, ASSOCIATION, AGGREGATION, COMPOSITION, DEPENDENCY
    }
    
    
    
    // Améliorer le positionnement des labels de relations
    private void drawRelationLabel(Graphics2D g2d, Point from, Point to, String label, Color color) {
        // Calculer le point milieu avec un petit décalage pour éviter les chevauchements
        int midX = (from.x + to.x) / 2;
        int midY = (from.y + to.y) / 2;
        
        // Décaler le label pour éviter qu'il soit sur la ligne
        double angle = Math.atan2(to.y - from.y, to.x - from.x);
        int offsetX = (int)(15 * Math.sin(angle));
        int offsetY = (int)(-15 * Math.cos(angle));
        
        midX += offsetX;
        midY += offsetY;
        
        // Fond semi-transparent pour le label avec plus d'espace
        FontMetrics fm = g2d.getFontMetrics();
        Rectangle2D bounds = fm.getStringBounds(label, g2d);
        
        int padding = 4;
        int rectX = midX - (int)bounds.getWidth()/2 - padding;
        int rectY = midY - (int)bounds.getHeight()/2 - padding;
        int rectWidth = (int)bounds.getWidth() + padding * 2;
        int rectHeight = (int)bounds.getHeight() + padding * 2;
        
        // Fond avec bordure
        g2d.setColor(new Color(255, 255, 255, 230));
        g2d.fillRoundRect(rectX, rectY, rectWidth, rectHeight, 6, 6);
        
        g2d.setColor(new Color(200, 200, 200, 150));
        g2d.drawRoundRect(rectX, rectY, rectWidth, rectHeight, 6, 6);
        
        // Texte du label
        g2d.setColor(color);
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        g2d.drawString(label, midX - (int)bounds.getWidth()/2, midY + (int)bounds.getHeight()/4);
    }
    
    // Améliorer le calcul des points d'intersection pour éviter les chevauchements
    private Point adjustPointToBorder(Point center, Point target, int width, int height) {
        double dx = target.x - center.x;
        double dy = target.y - center.y;
        
        if (Math.abs(dx) < 5 && Math.abs(dy) < 5) {
            // Si les points sont trop proches, éviter la division par zéro
            return new Point(center.x, center.y);
        }
        
        double angle = Math.atan2(dy, dx);
        
        // Calculer l'intersection avec le rectangle avec une marge
        double halfWidth = (width / 2.0) - 5; // Marge de 5px
        double halfHeight = (height / 2.0) - 5;
        
        double x, y;
        if (Math.abs(dx) / halfWidth > Math.abs(dy) / halfHeight) {
            // Intersection avec le côté gauche ou droit
            x = center.x + Math.signum(dx) * halfWidth;
            y = center.y + (x - center.x) * Math.tan(angle);
        } else {
            // Intersection avec le côté haut ou bas
            y = center.y + Math.signum(dy) * halfHeight;
            if (Math.abs(Math.cos(angle)) > 0.01) { // Éviter division par zéro
                x = center.x + (y - center.y) / Math.tan(angle);
            } else {
                x = center.x;
            }
        }
        
        return new Point((int)x, (int)y);
    }
private void drawLegend(Graphics2D g2d) {
        // Calculer la position en bas du diagramme
        Dimension diagramSize = getSize();
        int legendWidth = 800;
        int legendHeight = 100;
        
        // Position en bas, centrée horizontalement
        int legendX = Math.max(20, (diagramSize.width - legendWidth) / 2);
        int legendY = diagramSize.height - legendHeight - 20;
        
        // S'assurer que la légende ne dépasse pas
        if (legendX + legendWidth > diagramSize.width - 20) {
            legendX = diagramSize.width - legendWidth - 20;
        }
        
        // Fond de la légende avec ombre
        g2d.setColor(new Color(0, 0, 0, 50)); // Ombre
        g2d.fillRoundRect(legendX + 3, legendY + 3, legendWidth, legendHeight, 15, 15);
        
        g2d.setColor(new Color(255, 255, 255, 250));
        g2d.fillRoundRect(legendX, legendY, legendWidth, legendHeight, 15, 15);
        
        // Bordure de la légende
        g2d.setColor(new Color(100, 100, 100));
        g2d.setStroke(new BasicStroke(2.0f));
        g2d.drawRoundRect(legendX, legendY, legendWidth, legendHeight, 15, 15);
        
        // Titre centré
        g2d.setColor(new Color(50, 50, 50));
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        FontMetrics titleFm = g2d.getFontMetrics();
        String title = "Relations UML";
        int titleWidth = titleFm.stringWidth(title);
        g2d.drawString(title, legendX + (legendWidth - titleWidth) / 2, legendY + 25);
        
        // Ligne séparatrice
        g2d.setColor(new Color(150, 150, 150));
        g2d.drawLine(legendX + 20, legendY + 35, legendX + legendWidth - 20, legendY + 35);
        
        // Relations disposées horizontalement
        int startX = legendX + 30;
        int startY = legendY + 55;
        int itemWidth = 150; // Augmenté pour plus d'espace
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 11));
        
        // Héritage
        drawLegendItem(g2d, startX, startY, "Héritage", INHERITANCE_COLOR, 
                      LegendItemType.INHERITANCE);
        
        // Implémentation
        drawLegendItem(g2d, startX + itemWidth, startY, "Interface", INHERITANCE_COLOR, 
                      LegendItemType.IMPLEMENTATION);
        
        // Association
        drawLegendItem(g2d, startX + itemWidth * 2, startY, "Association", ASSOCIATION_COLOR, 
                      LegendItemType.ASSOCIATION);
        
        // Agrégation
        drawLegendItem(g2d, startX + itemWidth * 3, startY, "Agrégation", AGGREGATION_COLOR, 
                      LegendItemType.AGGREGATION);
        
        // Composition
        drawLegendItem(g2d, startX + itemWidth * 4, startY, "Composition", COMPOSITION_COLOR, 
                      LegendItemType.COMPOSITION);
    }
    
    private void drawLegendItem(Graphics2D g2d, int x, int y, String label, Color color, 
                               LegendItemType type) {
        // Dessiner la ligne/symbole
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(2.0f));
        
        int symbolY = y - 5;
        
        switch (type) {
            case INHERITANCE:
                g2d.drawLine(x, symbolY, x + 30, symbolY);
                drawInheritanceArrow(g2d, new Point(x + 30, symbolY), new Point(x, symbolY));
                break;
                
            case IMPLEMENTATION:
                g2d.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 
                                              0, new float[]{3, 3}, 0));
                g2d.drawLine(x, symbolY, x + 30, symbolY);
                drawInheritanceArrow(g2d, new Point(x + 30, symbolY), new Point(x, symbolY));
                g2d.setStroke(new BasicStroke(2.0f)); // Remettre ligne continue
                break;
                
            case ASSOCIATION:
                g2d.drawLine(x, symbolY, x + 30, symbolY);
                drawSimpleArrow(g2d, new Point(x + 30, symbolY), new Point(x, symbolY));
                break;
                
            case AGGREGATION:
                g2d.drawLine(x + 12, symbolY, x + 30, symbolY);
                drawDiamondArrow(g2d, new Point(x, symbolY), new Point(x + 30, symbolY), false);
                break;
                
            case COMPOSITION:
                g2d.drawLine(x + 12, symbolY, x + 30, symbolY);
                drawDiamondArrow(g2d, new Point(x, symbolY), new Point(x + 30, symbolY), true);
                break;
        }
        
        // Dessiner le label avec plus d'espace
        g2d.setColor(new Color(50, 50, 50));
        g2d.drawString(label, x + 45, y); // Augmenté de 35 à 45 pour plus d'espace
    }
    
    private enum LegendItemType {
        INHERITANCE, IMPLEMENTATION, ASSOCIATION, AGGREGATION, COMPOSITION
    }   
 
}