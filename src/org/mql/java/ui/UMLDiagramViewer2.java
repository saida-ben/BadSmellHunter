package org.mql.java.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.io.File;
import org.mql.java.models.*;
import org.mql.java.reflection.Extractor;

public class UMLDiagramViewer2 extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTextField searchField;
    private JPanel diagramPanel;
    private Project currentProject;
    private List<UMLClassPanel> allClassPanels;
    private JLabel statusLabel;
    
    public UMLDiagramViewer2() {
        this.allClassPanels = new ArrayList<>();
        setupUI();
        setVisible(true);
    }
    
    // Constructeur avec projet (pour compatibilité)
    public UMLDiagramViewer2(Project project) {
        this.currentProject = project;
        this.allClassPanels = new ArrayList<>();
        setupUI();
        if (project != null) {
            drawDiagram(project);
        }
        setVisible(true);
    }
    
    private void setupUI() {
        setTitle("UML Class Diagram Viewer");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Créer le panneau de titre et de recherche
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);
        
        // Créer le panneau principal avec scroll
        diagramPanel = new JPanel();
        diagramPanel.setLayout(null);
        diagramPanel.setBackground(Color.WHITE);
        diagramPanel.setPreferredSize(new Dimension(2000, 1500));
        
        JScrollPane scrollPane = new JScrollPane(diagramPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);
        
        // Panneau de statut en bas
        createStatusPanel();
        
        // Message initial si pas de projet
        if (currentProject == null) {
            showWelcomeMessage();
        }
    }
    
    private void createStatusPanel() {
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(new Color(240, 240, 240));
        statusPanel.setBorder(new EmptyBorder(5, 10, 5, 10));
        
        statusLabel = new JLabel("Prêt - Aucun projet chargé");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(100, 100, 100));
        statusPanel.add(statusLabel);
        
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    private void showWelcomeMessage() {
        JLabel welcomeLabel = new JLabel("<html><div style='text-align: center;'>" +
            "<h2>Bienvenue dans UML Diagram Viewer</h2>" +
            "<p>Entrez le chemin d'un projet Java et appuyez sur Entrée</p>" +
            "<p>ou cliquez sur le bouton de navigation pour sélectionner un dossier</p>" +
            "</div></html>", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        welcomeLabel.setForeground(new Color(100, 100, 100));
        
        diagramPanel.setLayout(new BorderLayout());
        diagramPanel.add(welcomeLabel, BorderLayout.CENTER);
        diagramPanel.revalidate();
        diagramPanel.repaint();
    }
    
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(45, 45, 45));
        topPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        // Titre de l'application
        JLabel titleLabel = new JLabel("UML Class Diagram Viewer");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setIcon(createTitleIcon());
        topPanel.add(titleLabel, BorderLayout.WEST);
        
        // Panneau de recherche
        JPanel searchPanel = createSearchPanel();
        topPanel.add(searchPanel, BorderLayout.EAST);
        
        return topPanel;
    }
    
    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setBackground(new Color(45, 45, 45));
        
        // Label de recherche
        JLabel searchLabel = new JLabel("Chemin du projet:");
        searchLabel.setForeground(Color.WHITE);
        searchLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        searchPanel.add(searchLabel);
        
        // Champ de recherche stylé
        searchField = new JTextField(30);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        searchField.setBackground(Color.WHITE);
        searchField.setForeground(Color.BLACK);
        
        // Placeholder effect
        searchField.setText("C:\\path\\to\\your\\project");
        searchField.setForeground(Color.GRAY);
        
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals("C:\\path\\to\\your\\project")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("C:\\path\\to\\your\\project");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });
        
        // Ajouter la recherche avec Entrée
        searchField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    loadProject();
                }
            }
            
            @Override
            public void keyReleased(KeyEvent e) {}
        });
        
        searchPanel.add(searchField);
        
        // Bouton de navigation pour sélectionner dossier
        JButton browseButton = new JButton("📁");
        browseButton.setPreferredSize(new Dimension(45, 35));
        browseButton.setBackground(new Color(70, 130, 180));
        browseButton.setForeground(Color.WHITE);
        browseButton.setBorder(BorderFactory.createEmptyBorder());
        browseButton.setFocusPainted(false);
        browseButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        browseButton.setToolTipText("Parcourir les dossiers");
        
        browseButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                browseButton.setBackground(new Color(100, 150, 200));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                browseButton.setBackground(new Color(70, 130, 180));
            }
        });
        
        browseButton.addActionListener(e -> browseForProject());
        searchPanel.add(browseButton);
        
        // Bouton de chargement
        JButton loadButton = new JButton("⚡");
        loadButton.setPreferredSize(new Dimension(45, 35));
        loadButton.setBackground(new Color(34, 139, 34));
        loadButton.setForeground(Color.WHITE);
        loadButton.setBorder(BorderFactory.createEmptyBorder());
        loadButton.setFocusPainted(false);
        loadButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loadButton.setToolTipText("Charger le projet");
        
        loadButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loadButton.setBackground(new Color(50, 160, 50));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loadButton.setBackground(new Color(34, 139, 34));
            }
        });
        
        loadButton.addActionListener(e -> loadProject());
        searchPanel.add(loadButton);
        
        // Bouton reset
        JButton resetButton = new JButton("🔄");
        resetButton.setPreferredSize(new Dimension(35, 35));
        resetButton.setBackground(new Color(220, 20, 60));
        resetButton.setForeground(Color.WHITE);
        resetButton.setBorder(BorderFactory.createEmptyBorder());
        resetButton.setFocusPainted(false);
        resetButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        resetButton.setToolTipText("Réinitialiser");
        
        resetButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                resetButton.setBackground(new Color(240, 40, 80));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                resetButton.setBackground(new Color(220, 20, 60));
            }
        });
        
        resetButton.addActionListener(e -> resetViewer());
        searchPanel.add(resetButton);
        
        return searchPanel;
    }
    
    private void browseForProject() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("Sélectionnez le dossier du projet Java");
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            searchField.setText(selectedFile.getAbsolutePath());
            searchField.setForeground(Color.BLACK);
            loadProject();
        }
    }
    
    private void loadProject() {
        String projectPath = searchField.getText().trim();
        
        if (projectPath.isEmpty() || projectPath.equals("C:\\path\\to\\your\\project")) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez entrer un chemin de projet valide.", 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Vérifier si le chemin existe
        File projectDir = new File(projectPath);
        if (!projectDir.exists() || !projectDir.isDirectory()) {
            JOptionPane.showMessageDialog(this, 
                "Le chemin spécifié n'existe pas ou n'est pas un dossier.", 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Afficher un indicateur de chargement
        statusLabel.setText("Chargement du projet en cours...");
        statusLabel.setForeground(Color.BLUE);
        
        // Utiliser SwingWorker pour charger en arrière-plan
        SwingWorker<Project, Void> worker = new SwingWorker<Project, Void>() {
            @Override
            protected Project doInBackground() throws Exception {
                return Extractor.extractProject(projectPath);
            }
            
            @Override
            protected void done() {
                try {
                    Project project = get();
                    if (project != null && !project.getPackages().isEmpty()) {
                        currentProject = project;
                        clearDiagram();
                        drawDiagram(project);
                        statusLabel.setText("Projet chargé: " + project.getName() + 
                            " (" + getTotalClassCount(project) + " classes)");
                        statusLabel.setForeground(new Color(34, 139, 34));
                    } else {
                        statusLabel.setText("Aucune classe trouvée dans ce projet");
                        statusLabel.setForeground(Color.RED);
                        showNoClassesMessage();
                    }
                } catch (Exception e) {
                    statusLabel.setText("Erreur lors du chargement: " + e.getMessage());
                    statusLabel.setForeground(Color.RED);
                    JOptionPane.showMessageDialog(UMLDiagramViewer2.this, 
                        "Erreur lors de l'extraction du projet:\n" + e.getMessage(), 
                        "Erreur", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    private void clearDiagram() {
        allClassPanels.clear();
        diagramPanel.removeAll();
        diagramPanel.setLayout(null);
        diagramPanel.revalidate();
        diagramPanel.repaint();
    }
    
    private void showNoClassesMessage() {
        clearDiagram();
        JLabel noClassesLabel = new JLabel("<html><div style='text-align: center;'>" +
            "<h2>Aucune classe Java trouvée</h2>" +
            "<p>Vérifiez que le chemin contient des fichiers .java ou .class</p>" +
            "</div></html>", SwingConstants.CENTER);
        noClassesLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        noClassesLabel.setForeground(Color.RED);
        
        diagramPanel.setLayout(new BorderLayout());
        diagramPanel.add(noClassesLabel, BorderLayout.CENTER);
        diagramPanel.revalidate();
        diagramPanel.repaint();
    }
    
    private int getTotalClassCount(Project project) {
        int count = 0;
        for (PackageInfo pkg : project.getPackages()) {
            count += pkg.getClasses().size();
        }
        return count;
    }
    
    private void resetViewer() {
        searchField.setText("C:\\path\\to\\your\\project");
        searchField.setForeground(Color.GRAY);
        currentProject = null;
        clearDiagram();
        showWelcomeMessage();
        statusLabel.setText("Prêt - Aucun projet chargé");
        statusLabel.setForeground(new Color(100, 100, 100));
    }
    
    private Icon createTitleIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Dessiner un petit diagramme UML comme icône
                g2d.setColor(new Color(70, 130, 180));
                g2d.fillRect(x + 2, y + 2, 16, 10);
                g2d.setColor(Color.WHITE);
                g2d.drawLine(x + 4, y + 4, x + 16, y + 4);
                g2d.drawLine(x + 4, y + 6, x + 12, y + 6);
                g2d.drawLine(x + 4, y + 8, x + 14, y + 8);
                g2d.drawLine(x + 4, y + 10, x + 10, y + 10);
                
                g2d.dispose();
            }
            
            @Override
            public int getIconWidth() { return 20; }
            
            @Override
            public int getIconHeight() { return 14; }
        };
    }
    
    private void drawDiagram(Project project) {
        // Utiliser la disposition intelligente
        drawDiagramWithSmartLayout(project);
    }
    
    
    private void createClassPanels(Project project) {
        allClassPanels.clear();
        
        for (PackageInfo pkg : project.getPackages()) {
            for (ClassInfo classInfo : pkg.getClasses()) {
                UMLClassPanel classPanel = new UMLClassPanel(classInfo);
                
                // Calculer la taille préférée
                int preferredHeight = classPanel.getPreferredHeight();
                classPanel.setBounds(0, 0, 220, preferredHeight); // Position temporaire
                
                diagramPanel.add(classPanel);
                allClassPanels.add(classPanel);
            }
        }
    }
    
    private void applySmartLayout(Project project) {
        // Disposition basique améliorée avec espacement intelligent
        int margin = 60;
        int classWidth = 220;
        int horizontalSpacing = 100;
        int verticalSpacing = 80;
        
        // Grouper par package
        Map<String, List<UMLClassPanel>> packageGroups = groupPanelsByPackage(project);
        
        int currentX = margin;
        int maxY = margin;
        
        for (Map.Entry<String, List<UMLClassPanel>> entry : packageGroups.entrySet()) {
            String packageName = entry.getKey();
            List<UMLClassPanel> panels = entry.getValue();
            
            // Dessiner le titre du package
            drawPackageHeader(packageName, currentX, margin - 30);
            
            // Disposer les classes du package
            int packageMaxY = layoutPackageClasses(panels, currentX, margin, 
                                                 classWidth, verticalSpacing);
            
            maxY = Math.max(maxY, packageMaxY);
            currentX += getMaxWidthInPackage(panels) + horizontalSpacing * 2;
        }
        
        // Mettre à jour la taille préférée du diagramme
        Dimension newSize = new Dimension(currentX + margin, maxY + margin);
        diagramPanel.setPreferredSize(newSize);
    }
    
    private Map<String, List<UMLClassPanel>> groupPanelsByPackage(Project project) {
        Map<String, List<UMLClassPanel>> groups = new LinkedHashMap<>();
        
        for (PackageInfo pkg : project.getPackages()) {
            String packageName = pkg.getName().isEmpty() ? "default" : 
                                 pkg.getName().substring(pkg.getName().lastIndexOf('.') + 1);
            List<UMLClassPanel> packagePanels = new ArrayList<>();
            
            for (ClassInfo classInfo : pkg.getClasses()) {
                for (UMLClassPanel panel : allClassPanels) {
                    if (panel.getClassInfo() == classInfo) {
                        packagePanels.add(panel);
                        break;
                    }
                }
            }
            
            if (!packagePanels.isEmpty()) {
                groups.put(packageName, packagePanels);
            }
        }
        
        return groups;
    }
    
    
    private int layoutPackageClasses(List<UMLClassPanel> panels, int startX, int startY, 
                                   int classWidth, int verticalSpacing) {
        // Trier par type : interfaces en haut, puis classes abstraites, puis classes concrètes
        panels.sort((p1, p2) -> {
            ClassInfo c1 = p1.getClassInfo();
            ClassInfo c2 = p2.getClassInfo();
            
            int priority1 = getClassPriority(c1);
            int priority2 = getClassPriority(c2);
            
            if (priority1 != priority2) {
                return Integer.compare(priority1, priority2);
            }
            
            return c1.getName().compareTo(c2.getName());
        });
        
        int currentY = startY;
        int maxY = startY;
        
        for (UMLClassPanel panel : panels) {
            panel.setBounds(startX, currentY, classWidth, panel.getPreferredHeight());
            
            // Définir la location du centre pour le dessin des relations
            ClassInfo classInfo = panel.getClassInfo();
            classInfo.setLocation(startX + classWidth/2, currentY + panel.getPreferredHeight()/2);
            
            currentY += panel.getPreferredHeight() + verticalSpacing;
            maxY = Math.max(maxY, currentY);
        }
        
        return maxY;
    }
    
    private int getClassPriority(ClassInfo classInfo) {
        if (classInfo.isInterface()) {
            return 1; // Interfaces en premier
        } else if (classInfo.isAbstract()) {
            return 2; // Classes abstraites ensuite
        } else {
            return 3; // Classes concrètes en dernier
        }
    }
    
    private int getMaxWidthInPackage(List<UMLClassPanel> panels) {
        return panels.stream()
                    .mapToInt(UMLClassPanel::getWidth)
                    .max()
                    .orElse(220);
    }
    
    private void addEnhancedRelationPanel(Project project) {
        // Supprimer l'ancien panneau de relations s'il existe
        Component[] components = diagramPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof UMLRelationPanel) {
                diagramPanel.remove(comp);
                break;
            }
        }
        
        // Ajouter le nouveau panneau de relations amélioré
        UMLRelationPanel relationPanel = new UMLRelationPanel(project);
        Dimension diagramSize = diagramPanel.getPreferredSize();
        relationPanel.setBounds(0, 0, diagramSize.width, diagramSize.height);
        relationPanel.setOpaque(false);
        
        // Ajouter le panneau de relations en arrière-plan (index 0)
        diagramPanel.add(relationPanel, 0);
    }
    
    private void drawDiagramWithSmartLayout(Project project) {
        // Créer les panneaux de classes avec la nouvelle version améliorée
        createClassPanels(project);
        
        // Appliquer la disposition intelligente anti-chevauchement
        applyImprovedLayout(project);
        
        // Ajouter le panneau des relations amélioré
        addEnhancedRelationPanel(project);
        
        // Actualiser l'affichage
        diagramPanel.revalidate();
        diagramPanel.repaint();
    }
    
    
    private int layoutPackageWithAntiOverlap(List<UMLClassPanel> panels, int startX, int startY, 
                                           int maxWidth, int minVerticalSpacing) {
        // Trier par priorité de type et nom
        panels.sort((p1, p2) -> {
            ClassInfo c1 = p1.getClassInfo();
            ClassInfo c2 = p2.getClassInfo();
            
            int priority1 = getClassPriority(c1);
            int priority2 = getClassPriority(c2);
            
            if (priority1 != priority2) {
                return Integer.compare(priority1, priority2);
            }
            
            return c1.getName().compareTo(c2.getName());
        });
        
        // Calculer les positions avec espacement dynamique
        List<Rectangle> positions = new ArrayList<>();
        int currentY = startY;
        
        for (UMLClassPanel panel : panels) {
            int panelHeight = panel.getPreferredHeight();
            
            // Créer le rectangle de position
            Rectangle rect = new Rectangle(startX, currentY, maxWidth, panelHeight);
            
            // Vérifier les chevauchements et ajuster
            rect = adjustForOverlaps(rect, positions, minVerticalSpacing);
            
            // Appliquer la position
            panel.setBounds(rect.x, rect.y, rect.width, rect.height);
            positions.add(rect);
            
            // Définir la location du centre pour le dessin des relations
            ClassInfo classInfo = panel.getClassInfo();
            classInfo.setLocation(rect.x + rect.width/2, rect.y + rect.height/2);
            
            currentY = rect.y + rect.height + minVerticalSpacing;
        }
        
        // Retourner la position Y maximale
        return positions.stream()
                       .mapToInt(r -> r.y + r.height)
                       .max()
                       .orElse(currentY);
    }
    
    private Rectangle adjustForOverlaps(Rectangle newRect, List<Rectangle> existingRects, int minSpacing) {
        Rectangle adjustedRect = new Rectangle(newRect);
        boolean hasOverlap;
        int maxAttempts = 20;
        int attempts = 0;
        
        do {
            hasOverlap = false;
            attempts++;
            
            for (Rectangle existing : existingRects) {
                // Vérifier chevauchement avec marge de sécurité
                Rectangle expandedExisting = new Rectangle(
                    existing.x - minSpacing/2,
                    existing.y - minSpacing/2,
                    existing.width + minSpacing,
                    existing.height + minSpacing
                );
                
                if (adjustedRect.intersects(expandedExisting)) {
                    hasOverlap = true;
                    // Déplacer vers le bas
                    adjustedRect.y = existing.y + existing.height + minSpacing;
                    break;
                }
            }
        } while (hasOverlap && attempts < maxAttempts);
        
        return adjustedRect;
    }
    
    private int getMaxWidthInPackage(List<UMLClassPanel> panels, int defaultWidth) {
        int maxWidth = defaultWidth;
        
        for (UMLClassPanel panel : panels) {
            // Calculer la largeur nécessaire basée sur le contenu
            int contentWidth = calculateRequiredWidth(panel);
            maxWidth = Math.max(maxWidth, contentWidth);
        }
        
        return Math.min(maxWidth, 350); // Largeur maximum de 350px
    }
    
    private int calculateRequiredWidth(UMLClassPanel panel) {
        ClassInfo classInfo = panel.getClassInfo();
        int maxWidth = 200; // Minimum
        
        // Largeur basée sur le nom de la classe
        maxWidth = Math.max(maxWidth, classInfo.getName().length() * 8 + 40);
        
        // Largeur basée sur les champs
        for (FieldModel field : classInfo.getFields()) {
            String fieldText = formatFieldForWidth(field);
            maxWidth = Math.max(maxWidth, fieldText.length() * 7 + 20);
        }
        
        // Largeur basée sur les méthodes
        for (MethodInfo method : classInfo.getMethods()) {
            String methodText = formatMethodForWidth(method);
            maxWidth = Math.max(maxWidth, methodText.length() * 6 + 20);
        }
        
        return Math.min(maxWidth, 400); // Maximum 400px
    }
    
    private String formatFieldForWidth(FieldModel field) {
        StringBuilder sb = new StringBuilder();
        sb.append(getVisibilitySymbol(field.getVisibility()));
        sb.append(" ").append(field.getName());
        sb.append(" : ").append(simplifyType(field.getType()));
        return sb.toString();
    }
    
    private String formatMethodForWidth(MethodInfo method) {
        StringBuilder sb = new StringBuilder();
        sb.append(getVisibilitySymbol(method.getVisibility()));
        sb.append(" ").append(method.getName()).append("()");
        if (method.getReturnType() != null && !method.getReturnType().equals("void")) {
            sb.append(" : ").append(simplifyType(method.getReturnType()));
        }
        return sb.toString();
    }
    
    private String getVisibilitySymbol(String visibility) {
        if (visibility == null) return "~";
        switch (visibility.toLowerCase()) {
            case "public": return "+";
            case "private": return "-";
            case "protected": return "#";
            default: return "~";
        }
    }
    
    private String simplifyType(String type) {
        if (type == null) return "";
        type = type.replaceAll("<.*?>", "<>");
        if (type.contains(".")) {
            String[] parts = type.split("\\.");
            type = parts[parts.length - 1];
        }
        return type.replace("java.lang.", "").replace("java.util.", "");
    }
    
    private void drawPackageHeader(String packageName, int x, int y) {
        // Créer un label plus visible pour le nom du package
        JLabel packageLabel = new JLabel("📦 Package: " + packageName);
        packageLabel.setFont(new Font("Arial", Font.BOLD, 14));
        packageLabel.setForeground(new Color(70, 130, 180));
        packageLabel.setBounds(x, y, 400, 25);
        packageLabel.setOpaque(true);
        packageLabel.setBackground(new Color(240, 248, 255));
        packageLabel.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180), 1));
        diagramPanel.add(packageLabel);
    }
    
    private void applyImprovedLayout(Project project) {
        // Paramètres de disposition optimisés
        int margin = 60;
        int classWidth = 280;
        int horizontalSpacing = 80;
        int verticalSpacing = 70;
        int maxClassesPerRow = 4; // Limiter le nombre de classes par ligne
        
        // Récupérer toutes les classes et les mélanger pour éviter les colonnes par package
        List<UMLClassPanel> allPanels = new ArrayList<>();
        for (PackageInfo pkg : project.getPackages()) {
            for (ClassInfo classInfo : pkg.getClasses()) {
                for (UMLClassPanel panel : allClassPanels) {
                    if (panel.getClassInfo() == classInfo) {
                        allPanels.add(panel);
                        break;
                    }
                }
            }
        }
        
        // Trier par importance (interfaces, classes abstraites, puis classes concrètes)
        allPanels.sort((p1, p2) -> {
            ClassInfo c1 = p1.getClassInfo();
            ClassInfo c2 = p2.getClassInfo();
            
            int priority1 = getClassPriority(c1);
            int priority2 = getClassPriority(c2);
            
            if (priority1 != priority2) {
                return Integer.compare(priority1, priority2);
            }
            return c1.getName().compareTo(c2.getName());
        });
        
        // Disposition en grille flexible
        int currentX = margin;
        int currentY = margin;
        int maxHeightInRow = 0;
        int classesInCurrentRow = 0;
        int maxY = margin;
        
        for (UMLClassPanel panel : allPanels) {
            int panelHeight = panel.getPreferredHeight();
            
            // Passer à la ligne suivante si nécessaire
            if (classesInCurrentRow >= maxClassesPerRow) {
                currentY += maxHeightInRow + verticalSpacing;
                currentX = margin;
                classesInCurrentRow = 0;
                maxHeightInRow = 0;
            }
            
            // Vérifier si on a assez d'espace horizontalement
            if (currentX + classWidth > 1400) { // Largeur max raisonnable
                currentY += maxHeightInRow + verticalSpacing;
                currentX = margin;
                classesInCurrentRow = 0;
                maxHeightInRow = 0;
            }
            
            // Positionner la classe
            panel.setBounds(currentX, currentY, classWidth, panelHeight);
            
            // Définir la location du centre pour les relations
            ClassInfo classInfo = panel.getClassInfo();
            classInfo.setLocation(currentX + classWidth/2, currentY + panelHeight/2);
            
            // Mettre à jour les positions
            currentX += classWidth + horizontalSpacing;
            maxHeightInRow = Math.max(maxHeightInRow, panelHeight);
            classesInCurrentRow++;
            maxY = Math.max(maxY, currentY + panelHeight);
        }
        
        // Ajouter les en-têtes de packages de manière non-intrusive
        addPackageLabels(project, allPanels);
        
        // Calculer la taille finale avec marge pour la légende
        int totalWidth = Math.max(1600, currentX + margin);
        int totalHeight = maxY + margin + 200; // Espace supplémentaire en bas pour la légende
        
        Dimension newSize = new Dimension(totalWidth, totalHeight);
        diagramPanel.setPreferredSize(newSize);
    }
    
    private void addPackageLabels(Project project, List<UMLClassPanel> allPanels) {
        // Créer un map des classes par package
        Map<String, List<UMLClassPanel>> packageToPanels = new HashMap<>();
        
        for (PackageInfo pkg : project.getPackages()) {
            String packageName = pkg.getName().isEmpty() ? "default" : 
                                 pkg.getName().substring(pkg.getName().lastIndexOf('.') + 1);
            
            List<UMLClassPanel> packagePanels = new ArrayList<>();
            for (ClassInfo classInfo : pkg.getClasses()) {
                for (UMLClassPanel panel : allPanels) {
                    if (panel.getClassInfo() == classInfo) {
                        packagePanels.add(panel);
                        break;
                    }
                }
            }
            
            if (!packagePanels.isEmpty()) {
                packageToPanels.put(packageName, packagePanels);
            }
        }
        
        // Ajouter des labels de package près des classes (pas en en-tête)
        for (Map.Entry<String, List<UMLClassPanel>> entry : packageToPanels.entrySet()) {
            String packageName = entry.getKey();
            List<UMLClassPanel> panels = entry.getValue();
            
            if (!panels.isEmpty()) {
                // Trouver la position de la première classe de ce package
                UMLClassPanel firstPanel = panels.get(0);
                Rectangle bounds = firstPanel.getBounds();
                
                // Placer le label à côté de la première classe
                JLabel packageLabel = new JLabel("📁 " + packageName);
                packageLabel.setFont(new Font("Arial", Font.BOLD, 10));
                packageLabel.setForeground(new Color(70, 130, 180));
                packageLabel.setBounds(bounds.x, bounds.y - 20, 150, 16);
                packageLabel.setOpaque(true);
                packageLabel.setBackground(new Color(240, 248, 255, 200));
                packageLabel.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180), 1));
                diagramPanel.add(packageLabel);
            }
        }
    }
}