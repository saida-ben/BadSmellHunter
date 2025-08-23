package org.mql.java.output;

import java.io.File;
import java.util.List;

import org.mql.java.models.BadSmell;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonGenerator {

	public static void exportBadSmellsToJson(List<BadSmell> smells, String filePath) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
			
			mapper.writeValue(new File(filePath), smells);
			
			System.out.println("Résultats exportés dans : " + filePath);
		} catch (Exception e) {
            e.printStackTrace();
		}
	}
}
