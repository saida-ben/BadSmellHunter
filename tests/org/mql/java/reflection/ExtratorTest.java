package org.mql.java.reflection;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.mql.java.models.ClassInfo;
import org.mql.java.models.Project;

public class ExtratorTest {

	@Test
	public void convertToQualifiedNameTest() {
        String baseDir = "C:\\projects\\myapp\\bin";
        String filePath = "C:\\projects\\myapp\\bin\\com\\example\\MyClass.class";
        
        String qualifiedName = Extractor.convertToQualifiedName(filePath, baseDir);
        assertEquals("com.example.MyClass", qualifiedName);

	}
	
	@Test
	public void ExtractProjectInvalidPathTest() {
		Project prj = Extractor.extractProject("C:\\\\nonexistent");
		assertNotNull(prj);
		assertEquals(0, prj.getPackages().size());
	}
	
	@Test 
	public void ExtractClassTest() throws NoSuchMethodException, SecurityException, IllegalAccessException, InvocationTargetException {
	    Class<?> clazz = Extractor.class;
	    Method method = Extractor.class.getDeclaredMethod("extractClass", Class.class);
	    method.setAccessible(true);

	    ClassInfo classInfo = (ClassInfo) method.invoke(null, clazz);

	    assertEquals("Extractor", classInfo.getName());
	    assertEquals(5, classInfo.getMethods().size());
	    assertEquals(0, classInfo.getFields().size());
	}
}
