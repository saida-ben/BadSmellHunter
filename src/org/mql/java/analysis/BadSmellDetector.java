package org.mql.java.analysis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.mql.java.models.ClassInfo;
import org.mql.java.models.MethodInfo;
import org.mql.java.models.PackageInfo;
import org.mql.java.models.Project;

public class BadSmellDetector {

    public BadSmellDetector() {}
    
    public static List<String> detect(Project project) {
        List<String> smells = new Vector<>();
        
        for(PackageInfo pkg : project.getPackages()) {
            for(ClassInfo clsInfo : pkg.getClasses()) {
                detectClassBadSmells(clsInfo, smells);
                detectMethodBadSmells(clsInfo, pkg, smells);
            }
        }
        
        detectDuplicateCode(project, smells);
        return smells;
    }
    

    private static void detectDuplicateCode(Project project, List<String> smells) {
        List<MethodInfo> allMethods = new Vector<>();
        Map<MethodInfo, ClassInfo> methodToClassMap = new HashMap<>();

        for(PackageInfo pkg : project.getPackages()) {
            for(ClassInfo cls : pkg.getClasses()) {
                for(MethodInfo method : cls.getMethods()) {
                    allMethods.add(method);
                    methodToClassMap.put(method, cls);
                }
            }
        }
        
        for(int i = 0; i < allMethods.size(); i++) {
            MethodInfo meth1 = allMethods.get(i);
            if(shouldSkipMethod(meth1)) continue;
            
            String body1 = cleanMethodBody(meth1.getBody());
            if(body1 == null || body1.length() < 20) continue; // Ignorer les corps trop courts
            
            for(int j = i + 1; j < allMethods.size(); j++) {
                MethodInfo meth2 = allMethods.get(j); // Correction: utiliser j au lieu de i
                if(shouldSkipMethod(meth2)) continue;
                
                String body2 = cleanMethodBody(meth2.getBody());
                if(body2 == null || body2.length() < 20) continue;
                
                // Calcul de similarité plus intelligent
                double similarity = calculateSimilarity(body1, body2);
                if(similarity > 0.8) { // 80% de similarité
                    ClassInfo class1 = methodToClassMap.get(meth1);
                    ClassInfo class2 = methodToClassMap.get(meth2);
                    
                    if(!class1.equals(class2)) {
                        smells.add(String.format(
                            "Duplicate code (%.0f%%) between %s.%s and %s.%s",
                            similarity * 100,
                            class1.getName(), meth1.getName(),
                            class2.getName(), meth2.getName()
                        ));
                    }
                }
            }
        }
    }

    private static double calculateSimilarity(String str1, String str2) {
        // Implémentation simplifiée de la distance de Levenshtein
        int maxLen = Math.max(str1.length(), str2.length());
        if(maxLen == 0) return 0.0;
        
        int distance = levenshteinDistance(str1, str2);
        return 1.0 - (double)distance / maxLen;
    }

    private static int levenshteinDistance(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];

        for(int i = 0; i <= a.length(); i++) {
            for(int j = 0; j <= b.length(); j++) {
                if(i == 0) {
                    dp[i][j] = j;
                } else if(j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = min(
                        dp[i-1][j-1] + (a.charAt(i-1) == b.charAt(j-1) ? 0 : 1),
                        dp[i-1][j] + 1,
                        dp[i][j-1] + 1
                    );
                }
            }
        }
        return dp[a.length()][b.length()];
    }

    private static int min(int a, int b, int c) {
        return Math.min(Math.min(a, b), c);
    }

    private static String cleanMethodBody(String code) {
        if(code == null) return "";
        return code.replaceAll("//.*|/\\*(.|\\R)*?\\*/", "") // Supprime commentaires
                  .replaceAll("\\s+", " ") // Garde un espace entre les tokens
                  .replaceAll("@\\w+", "") // Supprime annotations
                  .trim();
    }    
 
    
    private static boolean shouldSkipMethod(MethodInfo method) {
        return isSpecialMethod(method.getName()) || 
               method.getBody() == null || 
               method.getBody().trim().isEmpty() ||
               method.countLines() < 5; // Ignorer les méthodes très courtes
    }

    private static boolean isSpecialMethod(String methodName) {
        return methodName.equals("toString") || 
               methodName.equals("equals") || 
               methodName.equals("hashCode") ||
               methodName.startsWith("get") || 
               methodName.startsWith("set") ||
               methodName.startsWith("is") || // Pour les getters boolean
               methodName.equals("clone") ||
               methodName.equals("compareTo");
    }
    
    
    
    private static void detectClassBadSmells(ClassInfo clsInfo, List<String> smells) {
        if(clsInfo.getMethods().size() > 20 || clsInfo.getFields().size() > 15) {
            smells.add("Large class: " + clsInfo.getName());
        }
        
        if(clsInfo.getFields().size() >= 5 && clsInfo.getMethods().size() <= 3) {
            smells.add("Data class: " + clsInfo.getName());
        }
        
        if(clsInfo.getMethods().size() >= 20 && clsInfo.getRelations().size() >= 5) {
            smells.add("God class: " + clsInfo.getName());
        }
        
        if(clsInfo.getMethods().size() <= 3 && clsInfo.getFields().size() <= 2) {
            smells.add("Lazy class: " + clsInfo.getName());
        }
    }
    
    private static void detectMethodBadSmells(ClassInfo clsInfo, PackageInfo pkg, List<String> smells) {
        for(MethodInfo meth : clsInfo.getMethods()) {
            detectLongMethod(clsInfo, meth, smells);
            detectFeatureEnvy(clsInfo, meth, smells);
        }
    }
    
    private static void detectLongMethod(ClassInfo clsInfo, MethodInfo method, List<String> smells) {
        boolean tooLong = method.countLines() > 40;
        boolean tooManyParams = method.getParameters().size() > 5;
        
        if(tooLong || tooManyParams) {
            StringBuilder reason = new StringBuilder();
            if(tooLong) reason.append("Too many lines; ");
            if(tooManyParams) reason.append("Too many parameters; ");
            smells.add("Long method: " + clsInfo.getName() + "." + method.getName() + 
                      " [" + reason.toString().trim() + "]");
        }
    }
    
    private static void detectFeatureEnvy(ClassInfo clsInfo, MethodInfo method, List<String> smells) {
        int selfAccess = 0;
        int otherAccess = 0;
        
        for(String field : method.getAccessedFields()) {
            if(field.startsWith(clsInfo.getName() + ".")) {
                selfAccess++;
            } else {
                otherAccess++;
            }
        }
        
        for(String invokedMethod : method.getInvokedMethods()) {
            if(invokedMethod.startsWith(clsInfo.getName() + ".")) {
                selfAccess++;
            } else {
                otherAccess++;
            }
        }
        
        if(otherAccess > selfAccess * 2 && otherAccess > 3) {
            smells.add("Feature Envy: " + clsInfo.getName() + "." + method.getName() + 
                      " uses more foreign classes members than its own");
        }
    }
}