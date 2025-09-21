package org.mql.java.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import org.mql.java.models.*;

public class UMLClassPanel extends JPanel {
    private ClassInfo classInfo;
    
    // Couleurs UML standard
    private static final Color CLASS_HEADER_COLOR = new Color(220, 235, 255);
    private static final Color CLASS_BODY_COLOR = new Color(245, 250, 255);
    private static final Color INTERFACE_HEADER_COLOR = new Color(255, 240, 220);
    private static final Color ABSTRACT_HEADER_COLOR = new Color(230, 255, 230);
    private static final Color BORDER_COLOR = new Color(100, 100, 100);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    
    public UMLClassPanel(ClassInfo classInfo) {
        this.classInfo = classInfo;
        setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 2));
        setBackground(CLASS_BODY_COLOR);
    }
    
    public ClassInfo getClassInfo() {
        return this.classInfo;
    }
    
    public int getPreferredHeight() {
        int baseHeight = 40; // En-tête
        int fieldCount = classInfo.getFields().size();
        int methodCount = classInfo.getMethods().size();
        
        // Section champs
        if (fieldCount > 0) {
            baseHeight += 20 + (fieldCount * 18);
        } else {
            baseHeight += 20; // Section vide mais visible
        }
        
        // Section méthodes
        if (methodCount > 0) {
            baseHeight += 20 + (methodCount * 18);
        } else {
            baseHeight += 20; // Section vide mais visible
        }
        
        return Math.max(baseHeight, 80);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        int width = getWidth();
        int y = 0;
        
        // Déterminer le type de classe
        ClassType classType = determineClassType();
        
        // Dessiner l'en-tête
        y = drawClassHeader(g2d, width, y, classType);
        
        // Dessiner la section des champs
        y = drawFieldsSection(g2d, width, y);
        
        // Dessiner la section des méthodes
        drawMethodsSection(g2d, width, y);
        
        // Dessiner les coins arrondis (effet moderne)
        drawRoundedBorder(g2d, width, getHeight());
    }
    
    private ClassType determineClassType() {
        if (classInfo.isInterface()) {
            return ClassType.INTERFACE;
        } else if (classInfo.isAbstract()) {
            return ClassType.ABSTRACT_CLASS;
        } else {
            return ClassType.CONCRETE_CLASS;
        }
    }
    
    private int drawClassHeader(Graphics2D g2d, int width, int y, ClassType classType) {
        int headerHeight = 40;
        
        // Couleur de fond de l'en-tête selon le type
        Color headerColor = getHeaderColor(classType);
        GradientPaint gradient = new GradientPaint(0, y, headerColor, 0, y + headerHeight, 
                                                  headerColor.darker());
        g2d.setPaint(gradient);
        g2d.fillRect(1, y + 1, width - 2, headerHeight - 1);
        
        // Bordure de l'en-tête
        g2d.setColor(BORDER_COLOR);
        g2d.drawLine(1, y + headerHeight, width - 1, y + headerHeight);
        
        // Stéréotype (si applicable)
        g2d.setColor(TEXT_COLOR);
        g2d.setFont(new Font("Arial", Font.ITALIC, 10));
        FontMetrics fmStereo = g2d.getFontMetrics();
        
        String stereotype = getStereotype(classType);
        if (!stereotype.isEmpty()) {
            int stereoWidth = fmStereo.stringWidth(stereotype);
            g2d.drawString(stereotype, (width - stereoWidth) / 2, y + 15);
            y += 15;
            headerHeight -= 15;
        }
        
        // Nom de la classe
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics fmName = g2d.getFontMetrics();
        
        String className = classInfo.getName();
        if (classType == ClassType.ABSTRACT_CLASS) {
            // Texte italique pour les classes abstraites
            g2d.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 12));
        }
        
        int nameWidth = fmName.stringWidth(className);
        g2d.drawString(className, (width - nameWidth) / 2, y + headerHeight - 8);
        
        return y + headerHeight;
    }
    
    private int drawFieldsSection(Graphics2D g2d, int width, int y) {
        // Ligne de séparation
        g2d.setColor(BORDER_COLOR);
        g2d.drawLine(1, y, width - 1, y);
        
        int sectionHeight = Math.max(20, classInfo.getFields().size() * 18 + 10);
        
        // Fond de la section
        g2d.setColor(CLASS_BODY_COLOR);
        g2d.fillRect(1, y + 1, width - 2, sectionHeight - 1);
        
        // Champs
        g2d.setColor(TEXT_COLOR);
        g2d.setFont(new Font("Courier New", Font.PLAIN, 11)); // Police monospace pour l'alignement
        
        int fieldY = y + 15;
        
        if (classInfo.getFields().isEmpty()) {
            // Afficher un message pour section vide
            g2d.setColor(new Color(150, 150, 150));
            g2d.setFont(new Font("Arial", Font.ITALIC, 10));
            g2d.drawString("(no fields)", 10, fieldY);
        } else {
            for (FieldModel field : classInfo.getFields()) {
                String fieldText = formatField(field);
                g2d.setColor(getVisibilityColor(field.getVisibility()));
                g2d.drawString(fieldText, 10, fieldY);
                fieldY += 18;
            }
        }
        
        return y + sectionHeight;
    }
    
    private int drawMethodsSection(Graphics2D g2d, int width, int y) {
        // Ligne de séparation
        g2d.setColor(BORDER_COLOR);
        g2d.drawLine(1, y, width - 1, y);
        
        int sectionHeight = Math.max(20, classInfo.getMethods().size() * 18 + 10);
        
        // Fond de la section
        g2d.setColor(CLASS_BODY_COLOR);
        g2d.fillRect(1, y + 1, width - 2, sectionHeight - 1);
        
        // Méthodes
        g2d.setFont(new Font("Courier New", Font.PLAIN, 11));
        
        int methodY = y + 15;
        
        if (classInfo.getMethods().isEmpty()) {
            // Afficher un message pour section vide
            g2d.setColor(new Color(150, 150, 150));
            g2d.setFont(new Font("Arial", Font.ITALIC, 10));
            g2d.drawString("(no methods)", 10, methodY);
        } else {
            for (MethodInfo method : classInfo.getMethods()) {
                String methodText = formatMethod(method);
                
                // Style selon le type de méthode
                Font methodFont = new Font("Courier New", Font.PLAIN, 11);
                if (method.isAbstract()) {
                    methodFont = new Font("Courier New", Font.ITALIC, 11);
                }
                if (method.isStatic()) {
                    methodFont = new Font("Courier New", Font.BOLD, 11);
                }
                g2d.setFont(methodFont);
                
                g2d.setColor(getVisibilityColor(method.getVisibility()));
                g2d.drawString(methodText, 10, methodY);
                methodY += 18;
            }
        }
        
        return y + sectionHeight;
    }
    
    private void drawRoundedBorder(Graphics2D g2d, int width, int height) {
        g2d.setColor(BORDER_COLOR);
        g2d.setStroke(new BasicStroke(2.0f));
        
        // Bordure avec coins légèrement arrondis
        RoundRectangle2D roundRect = new RoundRectangle2D.Double(1, 1, width - 2, height - 2, 8, 8);
        g2d.draw(roundRect);
    }
    
    private Color getHeaderColor(ClassType classType) {
        switch (classType) {
            case INTERFACE:
                return INTERFACE_HEADER_COLOR;
            case ABSTRACT_CLASS:
                return ABSTRACT_HEADER_COLOR;
            default:
                return CLASS_HEADER_COLOR;
        }
    }
    
    private String getStereotype(ClassType classType) {
        switch (classType) {
            case INTERFACE:
                return "<<interface>>";
            case ABSTRACT_CLASS:
                return "<<abstract>>";
            default:
                return "";
        }
    }
    
    private String formatField(FieldModel field) {
        StringBuilder sb = new StringBuilder();
        
        // Symbole de visibilité
        sb.append(getVisibilitySymbol(field.getVisibility()));
        
        // Nom du champ
        sb.append(" ").append(field.getName());
        
        // Type du champ
        sb.append(" : ").append(simplifyType(field.getType()));
        
        // Modificateurs
        if (field.isStatic()) {
            sb.insert(0, "{static} ");
        }
        if (field.isFinal()) {
            sb.append(" {final}");
        }
        
        return sb.toString();
    }
    
    private String formatMethod(MethodInfo method) {
        StringBuilder sb = new StringBuilder();
        
        // Symbole de visibilité
        sb.append(getVisibilitySymbol(method.getVisibility()));
        
        // Nom de la méthode
        sb.append(" ").append(method.getName());
        
        // Paramètres
        sb.append("(");
        if (method.getParameters() != null && !method.getParameters().isEmpty()) {
            for (int i = 0; i < method.getParameters().size(); i++) {
                if (i > 0) sb.append(", ");
                ParameterInfo param = method.getParameters().get(i);
                sb.append(simplifyType(param.getType()));            }
        }
        sb.append(")");
        
        // Type de retour
        if (method.getReturnType() != null && !method.getReturnType().equals("void")) {
            sb.append(" : ").append(simplifyType(method.getReturnType()));
        }
        
        // Modificateurs
        if (method.isStatic()) {
            sb.insert(0, "{static} ");
        }
        if (method.isAbstract()) {
            sb.append(" {abstract}");
        }
        
        return sb.toString();
    }
    
    private String getVisibilitySymbol(String visibility) {
        if (visibility == null) return "~";
        switch (visibility.toLowerCase()) {
            case "public":
                return "+";
            case "private":
                return "-";
            case "protected":
                return "#";
            default:
                return "~"; // package-private
        }
    }
    
    private Color getVisibilityColor(String visibility) {
        if (visibility == null) return new Color(100, 100, 100);
        switch (visibility.toLowerCase()) {
            case "public":
                return new Color(0, 150, 0); // Vert
            case "private":
                return new Color(150, 0, 0); // Rouge
            case "protected":
                return new Color(150, 100, 0); // Orange
            default:
                return new Color(100, 100, 100); // Gris
        }
    }
    
    private String simplifyType(String type) {
        if (type == null) return "";
        
        // Simplifier les types génériques
        type = type.replaceAll("<.*?>", "<>");
        
        // Raccourcir les noms de classes complètement qualifiés
        if (type.contains(".") && !type.startsWith("java.lang.")) {
            String[] parts = type.split("\\.");
            type = parts[parts.length - 1];
        }
        
        // Raccourcir les types Java communs
        type = type.replace("java.lang.", "");
        type = type.replace("java.util.", "");
        
        return type;
    }
    
    public Point getCenter() {
        return new Point(getX() + getWidth()/2, getY() + getHeight()/2);
    }
    
    // Enum pour les types de classes
    private enum ClassType {
        CONCRETE_CLASS, ABSTRACT_CLASS, INTERFACE
    }
    
    
}