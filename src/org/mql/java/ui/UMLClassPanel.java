package org.mql.java.ui;


import javax.swing.*;
import java.awt.*;
import org.mql.java.models.*;

public class UMLClassPanel extends JPanel {
    private ClassInfo classInfo;

    public UMLClassPanel(ClassInfo classInfo) {
        this.classInfo = classInfo;
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }

    public int getPreferredHeight() {
        return 60 + (classInfo.getFields().size() + classInfo.getMethods().size()) * 18;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int y = 15;
        g.drawString("Class: " + classInfo.getName(), 10, y);
        y += 20;
        g.drawLine(0, y, getWidth(), y);
        y += 15;

        for (FieldModel field : classInfo.getFields()) {
            g.drawString("- " + field.getName() + " : " + field.getType(), 10, y);
            y += 15;
        }

        g.drawLine(0, y, getWidth(), y);
        y += 15;

        for (MethodInfo method : classInfo.getMethods()) {
            g.drawString("+ " + method.getName() + "()", 10, y);
            y += 15;
        }
    }

	public Point getCenter() {
		// TODO Auto-generated method stub
		return null;
	}
}
