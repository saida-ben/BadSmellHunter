package org.mql.java.reflection;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProjectScanner {
	
    public static List<String> scanJavaFiles(String directoryPath) {
        
    	List<String> javaFiles = new ArrayList<>();
        File directory = new File(directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                if (file.isDirectory()) {
                    javaFiles.addAll(scanJavaFiles(file.getAbsolutePath()));
                } else if (file.getName().endsWith(".java")) {
                    javaFiles.add(file.getAbsolutePath());
                }
            }
        }
        return javaFiles;
    }
    
    
}
