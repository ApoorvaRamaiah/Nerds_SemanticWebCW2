package SPARQL;


/* Created by @author ernesto
* Available at: https://github.com/city-knowledge-graphs/java-2024/blob/main/src/main/java/lab5/Lab5_Solution.java
* 
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
import java.util.HashMap;
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

public class Task_2_3_SPARQL_5 {
	
	String input_file;
	Model model;
	InfModel inf_model;
	
	String cw_ns_str;
		
	List<String[]> csv_file;

    Map<String, String> stringToURI = new HashMap<String, String>();
    
    Map<String, Integer> column_index;
    
    I_Sub isub = new I_Sub();
    
    
public Task_2_3_SPARQL_5(String input_file, Map<String, Integer> column_index) throws IOException {
		
		
		this.input_file = input_file;

		this.column_index = column_index;
		
		
		model = ModelFactory.createDefaultModel();
		

        cw_ns_str= "http://www.semanticweb.org/city/in3067-inm713/2024/restaurants#";
        
      
		model.setNsPrefix("cw", cw_ns_str);
		model.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
		model.setNsPrefix("dbr", "http://dbpedia.org/resource/");
        
  	
		CSVReader reader = new CSVReader(new FileReader(input_file));
	    csv_file = reader.readAll();
	    reader.close();
		
		
	}
	
public void performTaskRDF() throws JsonProcessingException, IOException, URISyntaxException {
    CovertCSVToRDF(false);
}
	
protected void CovertCSVToRDF(boolean useExternalURI) throws JsonProcessingException, IOException, URISyntaxException {
	
	
			//Type Triples
			mappingToCreateTypeTriple(column_index.get("restaurant"), cw_ns_str + "Restaurant", useExternalURI);
			mappingToCreateTypeTriple(column_index.get("address"), cw_ns_str + "Address", useExternalURI);
			mappingToCreateTypeTriple(column_index.get("city"), cw_ns_str + "City", useExternalURI);
			mappingToCreateTypeTriple(column_index.get("country"), cw_ns_str + "Country", useExternalURI);
			mappingToCreateTypeTriple(column_index.get("postcode"), cw_ns_str + "Postcode", useExternalURI);
			mappingToCreateTypeTriple(column_index.get("state"), cw_ns_str + "State", useExternalURI);
		    mappingToCreateTypeTriple(column_index.get("menu_item"), cw_ns_str + "Food", useExternalURI);
		    mappingToCreateTypeTriple(column_index.get("menu_item"), cw_ns_str + "MenuItem", useExternalURI);
		    mappingToCreateTypeTriple(column_index.get("item_value"), cw_ns_str + "ItemValue", useExternalURI);
		    mappingToCreateTypeTriple(column_index.get("currency"), cw_ns_str + "Currency", useExternalURI);
		    mappingToCreateTypeTriple(column_index.get("item_description"), cw_ns_str + "Ingredient", useExternalURI);
		    mappingToCreateTypeTriple(column_index.get("categories"), cw_ns_str + "Categories", useExternalURI);
		    
		    
		    
		    //Literal Triples
		    mappingToCreateLiteralTriple(column_index.get("restaurant"), column_index.get("restaurant"), cw_ns_str + "restaurantName", XSDDatatype.XSDstring);
		    mappingToCreateLiteralTriple(column_index.get("restaurant"), column_index.get("postcode"), cw_ns_str + "postcode", XSDDatatype.XSDstring);
		    
		    mappingToCreateLiteralTriple(column_index.get("menu_item"), column_index.get("item_value"), cw_ns_str + "amount", XSDDatatype.XSDstring);
		    mappingToCreateLiteralTriple(column_index.get("menu_item"), column_index.get("menu_item"), cw_ns_str + "itemName", XSDDatatype.XSDstring);
		    
		    mappingToCreateLiteralTriple(column_index.get("item_value"), column_index.get("item_value"), cw_ns_str + "amount", XSDDatatype.XSDdouble);
		    
		    
		  
		    //Object Triples
		    mappingToCreateObjectTriple(column_index.get("restaurant"), column_index.get("country"), cw_ns_str + "locatedInCountry");
		    mappingToCreateObjectTriple(column_index.get("restaurant"), column_index.get("city"), cw_ns_str + "locatedInCity");
		    mappingToCreateObjectTriple(column_index.get("restaurant"), column_index.get("address"), cw_ns_str + "locatedInAddress");
		    mappingToCreateObjectTriple(column_index.get("restaurant"), column_index.get("state"), cw_ns_str + "locatedInState");
		    mappingToCreateObjectTriple(column_index.get("restaurant"), column_index.get("menu_item"), cw_ns_str + "servesMenuItem");
		    
		   
		    mappingToCreateObjectTriple(column_index.get("menu_item"), column_index.get("restaurant"), cw_ns_str + "servedInRestaurant");
		    mappingToCreateObjectTriple(column_index.get("menu_item"), column_index.get("item_value"), cw_ns_str + "hasValue");
		    mappingToCreateObjectTriple(column_index.get("menu_item"), column_index.get("item_description"), cw_ns_str + "hasIngredient");

		    
		    mappingToCreateObjectTriple(column_index.get("item_description"), column_index.get("menu_item"), cw_ns_str + "isIngredientOf");
		    
		    mappingToCreateObjectTriple(column_index.get("country"), column_index.get("address"), cw_ns_str + "containsAdress");
		    mappingToCreateObjectTriple(column_index.get("country"), column_index.get("city"), cw_ns_str + "containsCity");
		    mappingToCreateObjectTriple(column_index.get("country"), column_index.get("currency"), cw_ns_str + "amountCurrency");
		    
		    mappingToCreateObjectTriple(column_index.get("address"), column_index.get("state"), cw_ns_str + "containsState");
		    mappingToCreateObjectTriple(column_index.get("address"), column_index.get("city"), cw_ns_str + "containsCity");
		    
		    mappingToCreateObjectTriple(column_index.get("city"), column_index.get("country"), cw_ns_str + "locatedInCountry");
		    
		    mappingToCreateObjectTriple(column_index.get("item_value"), column_index.get("currency"), cw_ns_str + "amountCurrency");
	    
	    
    
    
    
}
	
protected String processLexicalName(String restaurant) {

	
	return restaurant.replaceAll(" ", "_").replaceAll(",", "");
}	


protected String createURIForEntity(String restaurant, boolean useExternalURI) throws JsonProcessingException, IOException, URISyntaxException {
   
    stringToURI.put(restaurant, cw_ns_str + processLexicalName(restaurant));
    
   
    return stringToURI.get(restaurant);

}
	

protected void mappingToCreateTypeTriple(int subject_column_index, String class_type_uri, boolean useExternalURI) throws JsonProcessingException, IOException, URISyntaxException {
    
	for (String[] row : csv_file) {
		
		
		if (row.length<column_index.size())
			continue;
		
		
		String subject = row[subject_column_index].toLowerCase();
		String subject_uri;
		

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
		

		if (row.length<column_index.size())
			continue;
		
		String subject = row[subject_column];
		String lit_value = row[object_column];
		
		if (is_nan(lit_value))
			continue;
		

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
		
	
		if (row.length<column_index.size())
			continue;
		
		String subject = row[subject_column];
		String object = row[object_column];
		
		if (is_nan(object))
			continue;
		

        String subject_uri = stringToURI.get(subject.toLowerCase());
        String object_uri = stringToURI.get(object.toLowerCase());
            
        
        //New triple            
        Resource subject_resource = model.createResource(subject_uri);
        Property predicate_resource = model.createProperty(predicate);
        Resource object_resource = model.createResource(object_uri);
        
		
		model.add(subject_resource, predicate_resource, object_resource);
		
	}
	
        
}


public void performSPARQLQuery(Model model, String file_query_out) {
	
	WriteFile writer = new WriteFile(file_query_out);
	
	
	String queryStr = 
			   "PREFIX cw: <http://www.semanticweb.org/city/in3067-inm713/2024/restaurants#>\n" +
			   "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
			   "SELECT ?menu_item (COUNT(?restaurant) AS ?restaurantCount) WHERE {\n" +
			   "?menu_item rdf:type cw:Menu_item .\n"+
			   "?menu_item cw:servedInRestaurant ?restaurant .\n"+"} GROUP BY ?menu_item HAVING(COUNT(?restaurant)>0) ORDER BY COUNT(?restaurant) ?menu_item";
	
	
    Query q = QueryFactory.create(queryStr);
	
	QueryExecution qe =
			QueryExecutionFactory.create(q, model);
	try {
		ResultSet res = qe.execSelect();
			
		int solutions = 0;
			
		while( res.hasNext()) {
			solutions++;
			QuerySolution soln = res.next();
			RDFNode menu_item = soln.get("?menu_item");
			RDFNode restaurantCount = soln.get("?restaurantCount");

	
			writer.writeLine(menu_item.toString()+","+restaurantCount.toString());
			
			
		}
		System.out.println(solutions + " results satisfying the query.");
		    
	} finally {
		qe.close();
	}
	
	writer.closeBuffer();

			
}


public void performReasoning(String ontology_file) {
   
	System.out.println("Data triples from CSV: '" + model.listStatements().toSet().size() + "'.");
	              
    Dataset dataset = RDFDataMgr.loadDataset(ontology_file);
    model.add(dataset.getDefaultModel().listStatements().toList());
    
    
    System.out.println("Triples including ontology: '" + model.listStatements().toSet().size() + "'.");
    
    Reasoner reasoner = ReasonerRegistry.getOWLMiniReasoner();		
	inf_model = ModelFactory.createInfModel(reasoner, model);
	
    
    System.out.println("Triples after reasoning: '" + inf_model.listStatements().toSet().size() + "'.");
    
    
}



public void saveGraph(Model model, String file_output) throws FileNotFoundException {
    
    //SAVE/SERIALIZE GRAPH
    OutputStream out = new FileOutputStream(file_output);
    RDFDataMgr.write(out, model, RDFFormat.TURTLE);
     
  }

public static void main(String[] args) {
	
	String file = "files/50_unique_data.csv";
	
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
		Task_2_3_SPARQL_5 solution = new Task_2_3_SPARQL_5(file, column_index);
		
		String task = "Sparl_Query_5_Solution";

		if (task.equals("Sparl_Query_5_Solution"))
			solution.performTaskRDF();  //Fresh entity URIs
	 
			    
		solution.performReasoning("files/pizza-restaurants-ontology.ttl");
	
		solution.saveGraph(solution.inf_model, file.replace("50_unique_data.csv", ""+task)+"-reasoning.ttl");
			    
		solution.performSPARQLQuery(solution.inf_model, file.replace("50_unique_data.csv", ""+task)+".csv");	
		
		
	} catch (Exception e) {			
		e.printStackTrace();
	}
	
	
	
	 
	
}
}

