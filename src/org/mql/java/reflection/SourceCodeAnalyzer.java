package org.mql.java.reflection;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mql.java.models.ClassInfo;
import org.mql.java.models.MethodInfo;
import org.mql.java.models.PackageInfo;
import org.mql.java.models.Project;

public class SourceCodeAnalyzer {

    public static void enrichWithSourceCode(Project project, String srcFolderPath) {
        List<String> javaFiles = ProjectScanner.scanJavaFiles(srcFolderPath);

        for (String filePath : javaFiles) {
            String content = readFile(filePath);
            if (content == null) continue;

            for (PackageInfo pkg : project.getPackages()) {
                for (ClassInfo cls : pkg.getClasses()) {
                    if (filePath.endsWith(cls.getName() + ".java")) {
                        for (MethodInfo method : cls.getMethods()) {
                            String methodCode = extractMethodCode(content, method.getName());
                            method.setSourceCode(methodCode);

                            // Analyse rapide pour Feature Envy
                            analyzeFeatureEnvy(method, cls.getName());
                        }
                    }
                }
            }
        }
    }

    private static String readFile(String path) {
        try {
            return new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(path)));
        } catch (Exception e) {
            return null;
        }
    }

    // ⚠️ simpliste : détecte une méthode par son nom
    private static String extractMethodCode(String content, String methodName) {
        Pattern p = Pattern.compile(methodName + "\\s*\\(.*?\\)\\s*\\{", Pattern.DOTALL);
        Matcher m = p.matcher(content);
        if (m.find()) {
            int start = m.start();
            int braceCount = 0;
            int i = m.end() - 1;
            for (; i < content.length(); i++) {
                if (content.charAt(i) == '{') braceCount++;
                else if (content.charAt(i) == '}') {
                    braceCount--;
                    if (braceCount <= 0) break;
                }
            }
            return content.substring(start, i + 1);
        }
        return "";
    }

    private static void analyzeFeatureEnvy(MethodInfo method, String className) {
        String code = method.getSourceCode();
        if (code == null) return;

        Pattern access = Pattern.compile("(\\w+)\\.(\\w+)");
        Matcher m = access.matcher(code);

        while (m.find()) {
            String obj = m.group(1);
            String member = m.group(2);

            String full = obj + "." + member;
            if (obj.equalsIgnoreCase("this") || obj.equalsIgnoreCase(className)) {
                method.addAccessedField(className + "." + member);
                method.addInvokedMethod(className + "." + member);
            } else {
                method.addAccessedField(obj + "." + member);
                method.addInvokedMethod(obj + "." + member);
            }
        }
    }
    
    
}
