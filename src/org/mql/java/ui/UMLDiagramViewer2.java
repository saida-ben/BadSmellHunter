package org.mql.java.ui;


import javax.swing.*;
import java.awt.*;
import java.util.List;
import org.mql.java.models.*;

public class UMLDiagramViewer2 extends JFrame {
    private static final long serialVersionUID = 1L;

    public UMLDiagramViewer2(Project project) {
        setTitle("UML Class Diagram");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null); // Absolute positioning

        drawDiagram(project);

        setVisible(true);
    }

    private void drawDiagram(Project project) {
        int x = 50, y = 50;

        for (PackageInfo pkg : project.getPackages()) {
            for (ClassInfo classInfo : pkg.getClasses()) {
                UMLClassPanel classPanel = new UMLClassPanel(classInfo);
                classPanel.setBounds(x, y, 200, classPanel.getPreferredHeight());
                add(classPanel);
                classInfo.setLocation(x + 100, y + 40); // pour tracer les flèches ensuite
                y += classPanel.getPreferredHeight() + 50;
                if (y > getHeight() - 300) {
                    y = 50;
                    x += 300;
                }
            }
        }

        // Ajouter un panneau pour dessiner les relations par-dessus
        UMLRelationPanel relationPanel = new UMLRelationPanel(project);
        relationPanel.setBounds(0, 0, getWidth(), getHeight());
        relationPanel.setOpaque(false);
        add(relationPanel);
    }
}
