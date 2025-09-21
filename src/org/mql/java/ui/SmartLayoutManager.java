package org.mql.java.ui;

import java.awt.*;
import java.util.*;
import java.util.List;
import org.mql.java.models.*;

public class SmartLayoutManager {
    
    private static final int CLASS_WIDTH = 220;
    private static final int MIN_CLASS_HEIGHT = 100;
    private static final int HORIZONTAL_SPACING = 80;
    private static final int VERTICAL_SPACING = 60;
    private static final int MARGIN = 50;
    
    public static void layoutClasses(Project project, java.awt.Container container) {
        List<ClassInfo> allClasses = getAllClasses(project);
        
        if (allClasses.isEmpty()) {
            return;
        }
        
        // Grouper les classes par package
        Map<String, List<ClassInfo>> packageGroups = groupByPackage(project);
        
        // Créer la hiérarchie des classes
        Map<ClassInfo, Integer> hierarchyLevels = calculateHierarchyLevels(allClasses);
        
        // Disposition intelligente
        layoutByHierarchyAndPackage(packageGroups, hierarchyLevels, container);
    }
    
    private static List<ClassInfo> getAllClasses(Project project) {
        List<ClassInfo> allClasses = new ArrayList<>();
        for (PackageInfo pkg : project.getPackages()) {
            allClasses.addAll(pkg.getClasses());
        }
        return allClasses;
    }
    
    private static Map<String, List<ClassInfo>> groupByPackage(Project project) {
        Map<String, List<ClassInfo>> packageGroups = new LinkedHashMap<>();
        
        for (PackageInfo pkg : project.getPackages()) {
            String packageName = pkg.getName().isEmpty() ? "default" : pkg.getName();
            packageGroups.put(packageName, new ArrayList<>(pkg.getClasses()));
        }
        
        return packageGroups;
    }
    
    private static Map<ClassInfo, Integer> calculateHierarchyLevels(List<ClassInfo> classes) {
        Map<ClassInfo, Integer> levels = new HashMap<>();
        Map<String, ClassInfo> classMap = new HashMap<>();
        
        // Créer une map nom -> classe
        for (ClassInfo classInfo : classes) {
            classMap.put(classInfo.getName(), classInfo);
        }
        
        // Calculer les niveaux récursivement
        for (ClassInfo classInfo : classes) {
            calculateLevel(classInfo, classMap, levels);
        }
        
        return levels;
    }
    
    private static int calculateLevel(ClassInfo classInfo, Map<String, ClassInfo> classMap, 
                                    Map<ClassInfo, Integer> levels) {
        
        if (levels.containsKey(classInfo)) {
            return levels.get(classInfo);
        }
        
        int level = 0;
        
        // Vérifier la superclasse
        if (classInfo.getSuperclass() != null) {
            ClassInfo superClass = classMap.get(classInfo.getSuperclass());
            if (superClass != null) {
                level = Math.max(level, calculateLevel(superClass, classMap, levels) + 1);
            }
        }
        
        // Vérifier les interfaces
        for (String interfaceName : classInfo.getInterfaces()) {
            ClassInfo interfaceClass = classMap.get(interfaceName);
            if (interfaceClass != null) {
                level = Math.max(level, calculateLevel(interfaceClass, classMap, levels) + 1);
            }
        }
        
        levels.put(classInfo, level);
        return level;
    }
    
    private static void layoutByHierarchyAndPackage(Map<String, List<ClassInfo>> packageGroups,
                                                   Map<ClassInfo, Integer> hierarchyLevels,
                                                   Container container) {
        
        int currentX = MARGIN;
        int maxY = MARGIN;
        
        // Disposer chaque package
        for (Map.Entry<String, List<ClassInfo>> entry : packageGroups.entrySet()) {
            String packageName = entry.getKey();
            List<ClassInfo> classesInPackage = entry.getValue();
            
            // Trier les classes par niveau hiérarchique
            classesInPackage.sort((c1, c2) -> {
                int level1 = hierarchyLevels.getOrDefault(c1, 0);
                int level2 = hierarchyLevels.getOrDefault(c2, 0);
                return Integer.compare(level1, level2);
            });
            
            // Disposer les classes de ce package
            Point packageResult = layoutPackage(packageName, classesInPackage, 
                                               hierarchyLevels, currentX, MARGIN);
            
            currentX = packageResult.x + HORIZONTAL_SPACING * 2; // Espace entre packages
            maxY = Math.max(maxY, packageResult.y);
        }
        
        // Redimensionner le container si nécessaire
        Dimension preferredSize = new Dimension(currentX + MARGIN, maxY + MARGIN);
        container.setPreferredSize(preferredSize);
    }
    
    private static Point layoutPackage(String packageName, List<ClassInfo> classes,
                                     Map<ClassInfo, Integer> hierarchyLevels,
                                     int startX, int startY) {
        
        // Grouper par niveau hiérarchique
        Map<Integer, List<ClassInfo>> levelGroups = new TreeMap<>();
        for (ClassInfo classInfo : classes) {
            int level = hierarchyLevels.getOrDefault(classInfo, 0);
            levelGroups.computeIfAbsent(level, k -> new ArrayList<>()).add(classInfo);
        }
        
        int currentY = startY;
        int maxX = startX;
        
        // Disposer niveau par niveau
        for (Map.Entry<Integer, List<ClassInfo>> levelEntry : levelGroups.entrySet()) {
            int level = levelEntry.getKey();
            List<ClassInfo> classesAtLevel = levelEntry.getValue();
            
            // Centrer les classes de ce niveau
            int totalWidth = classesAtLevel.size() * CLASS_WIDTH + 
                           (classesAtLevel.size() - 1) * HORIZONTAL_SPACING;
            int levelStartX = startX + Math.max(0, (maxX - startX - totalWidth) / 2);
            
            int currentX = levelStartX;
            int maxHeightAtLevel = 0;
            
            // Positionner chaque classe du niveau
            for (ClassInfo classInfo : classesAtLevel) {
                UMLClassPanel panel = findClassPanel(classInfo);
                if (panel != null) {
                    int height = panel.getPreferredHeight();
                    panel.setBounds(currentX, currentY, CLASS_WIDTH, height);
                    classInfo.setLocation(currentX + CLASS_WIDTH/2, currentY + height/2);
                    
                    currentX += CLASS_WIDTH + HORIZONTAL_SPACING;
                    maxHeightAtLevel = Math.max(maxHeightAtLevel, height);
                }
            }
            
            maxX = Math.max(maxX, currentX - HORIZONTAL_SPACING);
            currentY += maxHeightAtLevel + VERTICAL_SPACING;
        }
        
        return new Point(maxX, currentY);
    }
    
    private static UMLClassPanel findClassPanel(ClassInfo classInfo) {
        // Cette méthode devrait être appelée avec une référence au container
        // Pour simplifier, on retourne null ici - à adapter selon votre architecture
        return null;
    }
    
    // Méthode utilitaire pour éviter les chevauchements
    public static void avoidOverlaps(List<Rectangle> bounds, int containerWidth, int containerHeight) {
        boolean hasOverlap;
        int maxIterations = 100;
        int iteration = 0;
        
        do {
            hasOverlap = false;
            iteration++;
            
            for (int i = 0; i < bounds.size() && !hasOverlap; i++) {
                for (int j = i + 1; j < bounds.size(); j++) {
                    Rectangle rect1 = bounds.get(i);
                    Rectangle rect2 = bounds.get(j);
                    
                    if (rect1.intersects(rect2)) {
                        hasOverlap = true;
                        
                        // Déplacer rect2 pour éviter le chevauchement
                        if (rect1.getCenterX() < rect2.getCenterX()) {
                            // Déplacer rect2 vers la droite
                            rect2.x = rect1.x + rect1.width + HORIZONTAL_SPACING;
                        } else {
                            // Déplacer rect2 vers la gauche
                            rect2.x = rect1.x - rect2.width - HORIZONTAL_SPACING;
                        }
                        
                        // Vérifier les limites du container
                        if (rect2.x < 0) {
                            rect2.x = MARGIN;
                            rect2.y += rect1.height + VERTICAL_SPACING;
                        } else if (rect2.x + rect2.width > containerWidth) {
                            rect2.x = MARGIN;
                            rect2.y += rect1.height + VERTICAL_SPACING;
                        }
                        
                        break;
                    }
                }
            }
        } while (hasOverlap && iteration < maxIterations);
    }
    
    // Méthode pour calculer la disposition optimale par force-directed layout
    public static void forceDirectedLayout(List<ClassInfo> classes, 
                                         Map<ClassInfo, ClassInfo> relationships,
                                         int width, int height, int iterations) {
        
        Map<ClassInfo, Point> positions = new HashMap<>();
        Map<ClassInfo, Point> velocities = new HashMap<>();
        
        // Initialisation aléatoire
        Random random = new Random();
        for (ClassInfo classInfo : classes) {
            positions.put(classInfo, new Point(
                MARGIN + random.nextInt(width - 2 * MARGIN),
                MARGIN + random.nextInt(height - 2 * MARGIN)
            ));
            velocities.put(classInfo, new Point(0, 0));
        }
        
        double temperature = 100.0;
        double coolingRate = 0.95;
        
        for (int iter = 0; iter < iterations; iter++) {
            // Calculer les forces répulsives
            for (ClassInfo class1 : classes) {
                Point pos1 = positions.get(class1);
                Point vel1 = velocities.get(class1);
                
                for (ClassInfo class2 : classes) {
                    if (class1 == class2) continue;
                    
                    Point pos2 = positions.get(class2);
                    double dx = pos1.x - pos2.x;
                    double dy = pos1.y - pos2.y;
                    double distance = Math.sqrt(dx * dx + dy * dy);
                    
                    if (distance < 1) distance = 1;
                    
                    // Force répulsive
                    double force = (CLASS_WIDTH * CLASS_WIDTH) / (distance * distance);
                    vel1.x += (int)(force * dx / distance);
                    vel1.y += (int)(force * dy / distance);
                }
            }
            
            // Calculer les forces attractives pour les relations
            for (Map.Entry<ClassInfo, ClassInfo> rel : relationships.entrySet()) {
                ClassInfo from = rel.getKey();
                ClassInfo to = rel.getValue();
                
                Point pos1 = positions.get(from);
                Point pos2 = positions.get(to);
                Point vel1 = velocities.get(from);
                Point vel2 = velocities.get(to);
                
                double dx = pos2.x - pos1.x;
                double dy = pos2.y - pos1.y;
                double distance = Math.sqrt(dx * dx + dy * dy);
                
                if (distance > 0) {
                    // Force attractive
                    double force = distance / 100.0;
                    vel1.x += (int)(force * dx / distance);
                    vel1.y += (int)(force * dy / distance);
                    vel2.x -= (int)(force * dx / distance);
                    vel2.y -= (int)(force * dy / distance);
                }
            }
            
            // Appliquer les vitesses avec limitation de température
            for (ClassInfo classInfo : classes) {
                Point pos = positions.get(classInfo);
                Point vel = velocities.get(classInfo);
                
                double speed = Math.sqrt(vel.x * vel.x + vel.y * vel.y);
                if (speed > temperature) {
                    vel.x = (int)(vel.x * temperature / speed);
                    vel.y = (int)(vel.y * temperature / speed);
                }
                
                pos.x = Math.max(MARGIN, Math.min(width - CLASS_WIDTH - MARGIN, pos.x + vel.x));
                pos.y = Math.max(MARGIN, Math.min(height - MIN_CLASS_HEIGHT - MARGIN, pos.y + vel.y));
                
                // Réduire la vitesse (friction)
                vel.x = (int)(vel.x * 0.8);
                vel.y = (int)(vel.y * 0.8);
            }
            
            // Refroidissement
            temperature *= coolingRate;
        }
        
        // Appliquer les positions finales
        for (ClassInfo classInfo : classes) {
            Point finalPos = positions.get(classInfo);
            classInfo.setLocation(finalPos.x + CLASS_WIDTH/2, finalPos.y + MIN_CLASS_HEIGHT/2);
        }
    }
}