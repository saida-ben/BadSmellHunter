package org.mql.java.analysis;

import java.util.*;

import org.mql.java.models.*;

public class BadSmellDetector2 {

    // Seuils pour les métriques
    private static final int LONG_METHOD_LINES = 40;
    private static final int LONG_METHOD_PARAMS = 5;
    private static final int LARGE_CLASS_METHODS = 20;
    private static final int LARGE_CLASS_FIELDS = 15;
    private static final int DATA_CLASS_FIELDS = 5;
    private static final int GOD_CLASS_METHODS = 20;
    private static final int GOD_CLASS_RELATIONS = 5;
    private static final int LAZY_CLASS_METHODS = 3;
    private static final int LAZY_CLASS_FIELDS = 2;

    public static List<BadSmell> detect(Project project) {
        List<BadSmell> smells = new Vector<>();
        
        for (PackageInfo pkg : project.getPackages()) {
            for (ClassInfo cls : pkg.getClasses()) {
                detectClassSmells(cls, smells);
                detectMethodSmells(cls, smells);
            }
        }
        
        detectDuplicateCode(project, smells);
        return smells;
    }

    // ===================== Détection de classe =====================
    private static void detectClassSmells(ClassInfo cls, List<BadSmell> smells) {
        int numMethods = cls.getMethods().size();
        int numFields = cls.getFields().size();
        int numRelations = cls.getRelations().size();

        if (numMethods > LARGE_CLASS_METHODS || numFields > LARGE_CLASS_FIELDS) {
            smells.add(new BadSmell(
                "Large Class",
                cls.getName(),
                "Contains " + numMethods + " methods and " + numFields + " fields (too big)"
            ));        
        }
        if (numFields >= DATA_CLASS_FIELDS && numMethods <= 3) {
            smells.add(new BadSmell(
                "Data Class",
                cls.getName(),
                "Has " + numFields + " fields but only " + numMethods + " methods (acts like a data container)"
            ));        
        }
        if (numMethods >= GOD_CLASS_METHODS && numRelations >= GOD_CLASS_RELATIONS) {
            smells.add(new BadSmell(
                "God Class",
                cls.getName(),
                "Has " + numMethods + " methods and " + numRelations + " relations (too many responsibilities)"
            ));  
        }
        if (numMethods <= LAZY_CLASS_METHODS && numFields <= LAZY_CLASS_FIELDS) {
            smells.add(new BadSmell(
                "Lazy Class",
                cls.getName(),
                "Has only " + numMethods + " methods and " + numFields + " fields (does too little)"
            ));  
        }
    }

    // ===================== Détection de méthode =====================
    private static void detectMethodSmells(ClassInfo cls, List<BadSmell> smells) {
        for (MethodInfo meth : cls.getMethods()) {
            detectLongMethod(cls, meth, smells);
            detectFeatureEnvy(cls, meth, smells);
        }
    }

    private static void detectLongMethod(ClassInfo cls, MethodInfo method, List<BadSmell> smells) {
        boolean tooLong = method.countLines() > LONG_METHOD_LINES;
        boolean tooManyParams = method.getParameters().size() > LONG_METHOD_PARAMS;

        if (tooLong || tooManyParams) {
            StringBuilder reason = new StringBuilder();
            if (tooLong) reason.append("Method has ").append(method.countLines()).append(" lines (too long). ");
            if (tooManyParams) reason.append("Method has ").append(method.getParameters().size()).append(" parameters (too many).");

            smells.add(new BadSmell(
                "Long Method",
                cls.getName() + "." + method.getName(),
                reason.toString().trim()
            ));
        }
    }

    private static void detectFeatureEnvy(ClassInfo cls, MethodInfo method, List<BadSmell> smells) {
        int selfAccess = 0, otherAccess = 0;

        for (String field : method.getAccessedFields()) {
            if (field.startsWith(cls.getName() + ".")) selfAccess++;
            else otherAccess++;
        }

        for (String invoked : method.getInvokedMethods()) {
            if (invoked.startsWith(cls.getName() + ".")) selfAccess++;
            else otherAccess++;
        }

        if (otherAccess > selfAccess * 2 && otherAccess > 3) {
            smells.add(new BadSmell(
                "Feature Envy",
                cls.getName() + "." + method.getName(),
                "Accesses external classes " + otherAccess + " times vs own class " + selfAccess + " times"
            )); 
        }
    }

    // ===================== Détection du code dupliqué =====================
    private static void detectDuplicateCode(Project project, List<BadSmell> smells) {
        List<MethodInfo> allMethods = new ArrayList<>();
        Map<MethodInfo, ClassInfo> methodToClass = new HashMap<>();

        for (PackageInfo pkg : project.getPackages()) {
            for (ClassInfo cls : pkg.getClasses()) {
                for (MethodInfo method : cls.getMethods()) {
                    if (shouldSkipMethod(method)) continue;
                    allMethods.add(method);
                    methodToClass.put(method, cls);
                }
            }
        }

        for (int i = 0; i < allMethods.size(); i++) {
            MethodInfo m1 = allMethods.get(i);
            String body1 = cleanMethodBody(m1.getBody());
            if (body1.length() < 20) continue;

            for (int j = i + 1; j < allMethods.size(); j++) {
                MethodInfo m2 = allMethods.get(j);
                String body2 = cleanMethodBody(m2.getBody());
                if (body2.length() < 20) continue;

                double similarity = calculateSimilarity(body1, body2);
                if (similarity > 0.8 && !methodToClass.get(m1).equals(methodToClass.get(m2))) {
                    smells.add(new BadSmell(
                        "Duplicate Code",
                        methodToClass.get(m1).getName() + "." + m1.getName() + 
                        " <-> " + 
                        methodToClass.get(m2).getName() + "." + m2.getName(), 
                        String.format("Code similarity: %.0f%%", similarity * 100)
                    ));
                }
            }
        }
    }

    // ===================== Utilitaires =====================
    private static String cleanMethodBody(String code) {
        if (code == null) return "";
        return code.replaceAll("//.*|/\\*(.|\\R)*?\\*/", "")
                   .replaceAll("\\s+", " ")
                   .replaceAll("@\\w+", "")
                   .trim();
    }

    private static boolean shouldSkipMethod(MethodInfo m) {
        return m.getBody() == null || m.getBody().trim().isEmpty() || m.countLines() < 5 ||
               isSpecialMethod(m.getName());
    }

    private static boolean isSpecialMethod(String name) {
        return name.equals("toString") || name.equals("equals") || name.equals("hashCode") ||
               name.equals("clone") || name.equals("compareTo") ||
               name.startsWith("get") || name.startsWith("set") || name.startsWith("is");
    }

    private static double calculateSimilarity(String s1, String s2) {
        int max = Math.max(s1.length(), s2.length());
        if (max == 0) return 0.0;
        int dist = levenshteinDistance(s1, s2);
        return 1.0 - (double) dist / max;
    }

    private static int levenshteinDistance(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i <= a.length(); i++)
            for (int j = 0; j <= b.length(); j++)
                if (i == 0) dp[i][j] = j;
                else if (j == 0) dp[i][j] = i;
                else dp[i][j] = Math.min(Math.min(dp[i-1][j-1] + (a.charAt(i-1) == b.charAt(j-1) ? 0 : 1),
                                                 dp[i-1][j] + 1),
                                         dp[i][j-1] + 1);
        return dp[a.length()][b.length()];
    }
}
