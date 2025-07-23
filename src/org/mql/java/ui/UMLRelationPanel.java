package org.mql.java.ui;


import javax.swing.*;
import java.awt.*;
import org.mql.java.models.*;

public class UMLRelationPanel extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Project project;

    public UMLRelationPanel(Project project) {
        this.project = project;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (PackageInfo pkg : project.getPackages()) {
            for (ClassInfo classInfo : pkg.getClasses()) {
                int x1 = classInfo.getX();
                int y1 = classInfo.getY();

                for (Relation rel : classInfo.getRelations()) {
                    ClassInfo target = project.findClassByName(rel.getTarget());
                    if (target == null) continue;

                    int x2 = target.getX();
                    int y2 = target.getY();

                    drawArrow(g, x1, y1, x2, y2, rel.getType());
                }
            }
        }
    }

    	private void drawArrow(Graphics g, int x1, int y1, int x2, int y2, String type) {
    	    Graphics2D g2 = (Graphics2D) g;
    	    g2.setColor(getColorForType(type));
    	    g2.setStroke(new BasicStroke(2));

    	    switch (type) {
    	        case "Inheritance", "Implements" -> drawInheritanceArrow(g2, x1, y1, x2, y2);
    	        case "Composition" -> drawDiamondArrow(g2, x1, y1, x2, y2, true);
    	        case "Aggregation" -> drawDiamondArrow(g2, x1, y1, x2, y2, false);
    	        default -> drawSimpleArrow(g2, x1, y1, x2, y2, type);
    	    }
    	}
    	private void drawInheritanceArrow(Graphics2D g2, int x1, int y1, int x2, int y2) {
    	    double angle = Math.atan2(y2 - y1, x2 - x1);
    	    int size = 15;

    	    int xTip = x2, yTip = y2;
    	    int xLeft = (int)(xTip - size * Math.cos(angle - Math.PI / 6));
    	    int yLeft = (int)(yTip - size * Math.sin(angle - Math.PI / 6));
    	    int xRight = (int)(xTip - size * Math.cos(angle + Math.PI / 6));
    	    int yRight = (int)(yTip - size * Math.sin(angle + Math.PI / 6));

    	    Polygon triangle = new Polygon(
    	        new int[] { xTip, xLeft, xRight },
    	        new int[] { yTip, yLeft, yRight },
    	        3
    	    );

    	    g2.drawPolygon(triangle); // triangle vide
    	    g2.drawLine(x1, y1, (xLeft + xRight) / 2, (yLeft + yRight) / 2); // ligne sans toucher la pointe
    	}
	
    	
private void drawDiamondArrow(Graphics2D g2, int x1, int y1, int x2, int y2, boolean filled) {
    double angle = Math.atan2(y2 - y1, x2 - x1);
    int size = 12;

    // Point de départ du losange (proche de la source)
    int xBase = x1;
    int yBase = y1;

    // Points du losange (4 points)
    int xFront = (int)(xBase + size * Math.cos(angle));
    int yFront = (int)(yBase + size * Math.sin(angle));

    int xLeft = (int)(xBase + size / 2.0 * Math.cos(angle - Math.PI / 2));
    int yLeft = (int)(yBase + size / 2.0 * Math.sin(angle - Math.PI / 2));

    int xRight = (int)(xBase + size / 2.0 * Math.cos(angle + Math.PI / 2));
    int yRight = (int)(yBase + size / 2.0 * Math.sin(angle + Math.PI / 2));

    int[] xPoints = { xBase, xLeft, xFront, xRight };
    int[] yPoints = { yBase, yLeft, yFront, yRight };

    if (filled) {
        g2.fillPolygon(xPoints, yPoints, 4); // ◆ Composition
    } else {
        g2.drawPolygon(xPoints, yPoints, 4); // ◇ Agrégation
    }

    // Ligne vers la cible (depuis la pointe du losange)
    g2.drawLine(xFront, yFront, x2, y2);
}
 	
    	
    	
private void drawSimpleArrow(Graphics2D g2, int x1, int y1, int x2, int y2, String type) {
    g2.drawLine(x1, y1, x2, y2);

    double angle = Math.atan2(y2 - y1, x2 - x1);
    int len = 10;

    int xLeft = (int)(x2 - len * Math.cos(angle - Math.PI / 6));
    int yLeft = (int)(y2 - len * Math.sin(angle - Math.PI / 6));
    int xRight = (int)(x2 - len * Math.cos(angle + Math.PI / 6));
    int yRight = (int)(y2 - len * Math.sin(angle + Math.PI / 6));

    g2.drawLine(x2, y2, xLeft, yLeft);
    g2.drawLine(x2, y2, xRight, yRight);

    // Nom de la relation au milieu
    g2.setColor(Color.DARK_GRAY);
    g2.drawString(type, (x1 + x2) / 2, (y1 + y2) / 2);
}


    private Color getColorForType(String type) {
        return switch (type) {
            case "Inheritance" -> Color.BLUE;
            case "Implements" -> Color.MAGENTA;
            case "Aggregation" -> Color.ORANGE;
            case "Composition" -> Color.RED;
            case "Association" -> Color.GREEN.darker();
            case "Uses" -> Color.GRAY;
            default -> Color.BLACK;
        };
    }
}
