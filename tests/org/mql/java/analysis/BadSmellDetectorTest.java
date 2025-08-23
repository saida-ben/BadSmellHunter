package org.mql.java.analysis;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mql.java.models.BadSmell;
import org.mql.java.models.Project;
import org.mql.java.reflection.Extractor;


public class BadSmellDetectorTest {

 

	static List<BadSmell> smells;
	
	@BeforeAll
	public static void init() {
		String projectPath = "C:\\Users\\benza\\Documents\\eclipse_workspce\\prj_reflexion"; 
		Project project = Extractor.extractProject(projectPath);
		smells = BadSmellDetector2.detect(project);
	}
	
	@Test
	public void detectLongMethodTest() {		
        boolean foundLongMethod = smells.stream()
                .anyMatch(s -> s.getType().equals("Long Method"));
        
        assertTrue(foundLongMethod, "Le détecteur devrait identifier une Long Method");
	}
	
	@Test
	public void detectFeatureEnvy() {
		
        boolean foundFeatureEnvy = smells.stream()
                .anyMatch(s -> s.getType().equals("Feature Envy"));
        
        assertFalse(foundFeatureEnvy, "Le détecteur devrait identifier une Feature Envy");
	}
	
	@Test
	public void detectLargeClassTest() {
		
        boolean foundLargeClass = smells.stream()
                .anyMatch(s -> s.getType().equals("Large Class"));
        
        assertTrue(foundLargeClass, "Le détecteur devrait identifier une Large Class");
	}

	
	@Test
	public void detectDataClassTest() {
        boolean foundDataClass = smells.stream()
                .anyMatch(s -> s.getType().equals("Data Class"));
        
        assertTrue(foundDataClass, "Le détecteur devrait identifier une Data Class");
	}
	
	@Test
	public void detectLazyClassTest() {
        boolean foundLazyClass = smells.stream()
                .anyMatch(s -> s.getType().equals("Lazy Class"));
        
        assertTrue(foundLazyClass, "Le détecteur devrait identifier une Lazy Class");
	}
}
