package RDF;

/* Created by @author ernesto
* Available at: https://github.com/city-knowledge-graphs/java-2024/blob/main/src/main/java/lab5/Lab5_Solution.java
* https://github.com/city-knowledge-graphs/java-2024/blob/main/src/main/java/lab5/Wikidatalookup.java
* Type of Code: Java code
* 
* Updated by @author Shreyas Jadhav, Apoorva Ramaiah
* Coursework: Semantic Web and Technologies Part 2
* Coursework Group Name: Nerds
* Updated on 11 May 2024
*/


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.OWL;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.opencsv.CSVReader;

import util.WriteFile;

public class Task_RDF_3_RDF_Triples_Using_WikiData {
	
	String input_file;
	Model model;
	InfModel inf_model;
	
	String cw_ns_str;
	
	WikidataLookup wikidataLookup;
	
	List<String[]> csv_file;
	
	//Dictionary that keeps the URIs. Specially useful if accessing a remote service to get a candidate URI  to avoid repeated calls
    Map<String, String> stringToURI = new HashMap<String, String>();
    
    Map<String, Integer> column_index;
    
    I_Sub isub = new I_Sub();
    
    
    
    
public Task_RDF_3_RDF_Triples_Using_WikiData(String input_file, Map<String, Integer> column_index) throws IOException {
		
		
		this.input_file = input_file;

		this.column_index = column_index;
		
		
		//1. GRAPH INITIALIZATION
	    
        //Empty graph
		model = ModelFactory.createDefaultModel();
		
		
		//Note that this is the same namespace used in the ontology "pizza-restaurant-ontology.ttl"
        cw_ns_str= "http://www.semanticweb.org/city/in3067-inm713/2024/restaurants#";
        
        
        //Prefixes for the serialization
		model.setNsPrefix("cw", cw_ns_str);
		model.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
		model.setNsPrefix("wiki", "http://wikidata.org/resource/");
        
        
        //Load data in matric to later use an iterator		
		CSVReader reader = new CSVReader(new FileReader(input_file));
	    csv_file = reader.readAll();
	    reader.close();
		
	    wikidataLookup = new WikidataLookup();
	}
	
public void performTaskRDFusingGoogleKG() throws JsonProcessingException, IOException, URISyntaxException {
    CovertCSVToRDF(true);
}
	
protected void CovertCSVToRDF(boolean useExternalURI) throws JsonProcessingException, IOException, URISyntaxException {
	
	
	//In a large ontology one would need to find a more automatic way to use the ontology vocabulary. 
    //e.g.,  via matching. In a similar way as we match entities to a large KG like DBPedia or Wikidata
    //Since we are dealing with very manageable ontologies, we can integrate their vocabulary 
    //within the code. E.g.,: cw_ns_str + City
	
	
    
    //We modularize the transformation to RDF. The transformation is tailored to the given table, but 
    //he individual components/mappings are relatively generic (especially type and literal triples).
    
    //Mappings may required one or more columns as input and create 1 or more triples for an entity
	
	
	
	//We give subject column and target type
	mappingToCreateTypeTriple(column_index.get("restaurant"), cw_ns_str + "Restaurant", useExternalURI);
	mappingToCreateTypeTriple(column_index.get("address"), cw_ns_str + "Address", useExternalURI);
	mappingToCreateTypeTriple(column_index.get("city"), cw_ns_str + "City", useExternalURI);
	mappingToCreateTypeTriple(column_index.get("country"), cw_ns_str + "Country", useExternalURI);
	mappingToCreateTypeTriple(column_index.get("postcode"), cw_ns_str + "Postcode", useExternalURI);
	mappingToCreateTypeTriple(column_index.get("state"), cw_ns_str + "state", useExternalURI);
    mappingToCreateTypeTriple(column_index.get("menu_item"), cw_ns_str + "Food", useExternalURI);
    mappingToCreateTypeTriple(column_index.get("menu_item"), cw_ns_str + "menu_item", useExternalURI);
    mappingToCreateTypeTriple(column_index.get("item_value"), cw_ns_str + "Itemvalue", useExternalURI);
    mappingToCreateTypeTriple(column_index.get("currency"), cw_ns_str + "Currency", useExternalURI);
    mappingToCreateTypeTriple(column_index.get("item_description"), cw_ns_str + "Ingredient", useExternalURI);
    mappingToCreateTypeTriple(column_index.get("categories"), cw_ns_str + "categories", useExternalURI);
    
    
    
    
    
        
    //We give subject and object columns (they could be the same), predicate and datatype 
    mappingToCreateLiteralTriple(column_index.get("item_description"), column_index.get("item_description"), cw_ns_str + "itemName", XSDDatatype.XSDstring);
    mappingToCreateLiteralTriple(column_index.get("restaurant"), column_index.get("restaurant"), cw_ns_str + "restaurantName", XSDDatatype.XSDstring);
    mappingToCreateLiteralTriple(column_index.get("restaurant"), column_index.get("postcode"), cw_ns_str + "postcode", XSDDatatype.XSDstring);
    mappingToCreateLiteralTriple(column_index.get("menu_item"), column_index.get("item_value"), cw_ns_str + "amount", XSDDatatype.XSDstring);
  
    

    
    mappingToCreateObjectTriple(column_index.get("restaurant"), column_index.get("country"), cw_ns_str + "locatedInCountry");
    mappingToCreateObjectTriple(column_index.get("restaurant"), column_index.get("city"), cw_ns_str + "locatedInCity");
    mappingToCreateObjectTriple(column_index.get("restaurant"), column_index.get("address"), cw_ns_str + "locatedInAddress");
    mappingToCreateObjectTriple(column_index.get("restaurant"), column_index.get("state"), cw_ns_str + "locatedInState");
    mappingToCreateObjectTriple(column_index.get("restaurant"), column_index.get("menu_item"), cw_ns_str + "serves");
    
    mappingToCreateObjectTriple(column_index.get("restaurant"), column_index.get("address"), cw_ns_str + "containsAdress");
    mappingToCreateObjectTriple(column_index.get("restaurant"), column_index.get("city"), cw_ns_str + "containsCity");
    mappingToCreateObjectTriple(column_index.get("restaurant"), column_index.get("state"), cw_ns_str + "containsState");
    
    mappingToCreateObjectTriple(column_index.get("menu_item"), column_index.get("restaurant"), cw_ns_str + "servedInRestaurant");
    mappingToCreateObjectTriple(column_index.get("item_description"), column_index.get("menu_item"), cw_ns_str + "isIngredientOf");
    mappingToCreateObjectTriple(column_index.get("menu_item"), column_index.get("item_value"), cw_ns_str + "hasValue");
    
    
    
    
    
}
	
protected String processLexicalName(String name) {
	
	//Remove potential spaces and other characters not allowed in URIs
    
    //This method may need to be extended
	//Other problematic characters: 
    //{", "}", "|", "\", "^", "~", "[", "]", and "`"
	
	return name.replaceAll(" ", "_").replaceAll(",", "");
}	


protected String createURIForEntity(String name, boolean useExternalURI) throws JsonProcessingException, IOException, URISyntaxException {
    
    //We create fresh URI (default option)
    stringToURI.put(name, cw_ns_str + processLexicalName(name));
    
    if (useExternalURI) {//We connect to online KG
        String uri = getExternalKGURI(name);
        if (!uri.equals(""))
        	stringToURI.put(name, uri);
    }
   
    return stringToURI.get(name);

}
	
protected String getExternalKGURI(String name) throws JsonProcessingException, IOException, URISyntaxException {
    
    //Approximate solution: We get the entity with highest lexical similarity
    //The use of context may be necessary in some cases        
	
	final String lang="en";
    Set<KGEntity> entities = wikidataLookup.getKGEntities("name");

    double current_sim = -1.0;
    String current_uri="";
    
    for (KGEntity ent : entities) {           
        double isub_score = isub.score(name, ent.getName()); 
        if (current_sim < isub_score) {
            current_uri = ent.getId();
            current_sim = isub_score;
        }
    }
        
    return current_uri;
}
        



protected void mappingToCreateTypeTriple(int subject_column_index, String class_type_uri, boolean useExternalURI) throws JsonProcessingException, IOException, URISyntaxException {
    
	for (String[] row : csv_file) {
		
		
		
		//Ignore rows with less elements than expected
		if (row.length<column_index.size())
			continue;
		
		
		String subject = row[subject_column_index].toLowerCase();
		String subject_uri;
		
		//We use the ascii restaurant to create the fresh URI for a city in the dataset
		if (stringToURI.containsKey(subject))
			subject_uri=stringToURI.get(subject);
        else
            subject_uri=createURIForEntity(subject, useExternalURI);
		
		
		//TYPE TRIPLE    		
		Resource subject_resource = model.createResource(subject_uri);
		Resource type_resource = model.createResource(class_type_uri);
		
		model.add(subject_resource, RDF.type, type_resource);
	
	}
	
}

private boolean is_nan(String value) {
    return (!value.equals(value));
}


protected void mappingToCreateLiteralTriple(int subject_column, int object_column, String predicate, XSDDatatype datatype) {
    
	for (String[] row : csv_file) {
		
		//Ignore rows with less elements than expected
		if (row.length<column_index.size())
			continue;
		
		
		
		String subject = row[subject_column];
		String lit_value = row[object_column];
		
		if (is_nan(lit_value))
			continue;
		

        //Uri as already created
        String entity_uri = stringToURI.get(subject.toLowerCase());
            
        
        //New triple            
        Resource subject_resource = model.createResource(entity_uri);
        Property predicate_resource = model.createProperty(predicate);
        
		//Literal
        Literal lit = model.createTypedLiteral(lit_value, datatype);

		
		model.add(subject_resource, predicate_resource, lit);
		
	}
	
        
}


protected void mappingToCreateObjectTriple(int subject_column, int object_column, String predicate) {
    
	for (String[] row : csv_file) {
		
		//Ignore rows with less elements than expected
		if (row.length<column_index.size())
			continue;
		
		
		
		String subject = row[subject_column];
		String object = row[object_column];
		
		if (is_nan(object))
			continue;
		

        //Uri as already created
        String subject_uri = stringToURI.get(subject.toLowerCase());
        String object_uri = stringToURI.get(object.toLowerCase());
            
        
        //New triple            
        Resource subject_resource = model.createResource(subject_uri);
        Property predicate_resource = model.createProperty(predicate);
        Resource object_resource = model.createResource(object_uri);
        
		
		model.add(subject_resource, predicate_resource, object_resource);
		
	}
	
        
}


public void saveGraph(Model model, String file_output) throws FileNotFoundException {
    
    //SAVE/SERIALIZE GRAPH
    OutputStream out = new FileOutputStream(file_output);
    RDFDataMgr.write(out, model, RDFFormat.TURTLE);
     
  }

public static void main(String[] args) {
	
	String file = "files/IN3067-INM713_coursework_data_pizza_500.csv";
	
	//Format
	//restaurant    address    city    country    postcode    state    categories    menu_item    item_value    currency	item_description
	Map<String, Integer> column_index = new HashMap<String, Integer>();
	column_index.put("restaurant", 0);
	column_index.put("address", 1);
	column_index.put("city", 2);
	column_index.put("country", 3);
	column_index.put("postcode", 4);
	column_index.put("state", 5);
	column_index.put("categories", 6);
	column_index.put("menu_item", 7);
	column_index.put("item_value", 8);
	column_index.put("currency", 9);
	column_index.put("item_description", 10);
	
	try {
		Task_RDF_3_RDF_Triples_Using_WikiData solution = new Task_RDF_3_RDF_Triples_Using_WikiData(file, column_index);
		
		String task = "createRDF_Wikidata";

		if (task.equals("createRDF_Wikidata"))
			solution.performTaskRDFusingGoogleKG();  //Fresh entity URIs
	 
			    
		//Graph with only data
		solution.saveGraph(solution.model, file.replace(".csv", "-"+task)+".ttl");
	
		
		
	} catch (Exception e) {			
		e.printStackTrace();
	}
	
	
	
	 
	
}
}

