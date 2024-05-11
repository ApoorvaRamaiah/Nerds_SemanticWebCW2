package RDF;


/* Created by @author ernesto
* Available at: https://github.com/city-knowledge-graphs/java-2024/blob/main/src/main/java/lab5/GoogleKGLookup.java
* Type of Code: Java code
* 
* Updated by @author Shreyas Jadhav, Apoorva Ramaiah
* Coursework: Semantic Web and Technologies Part 2
* Coursework Group Name: Nerds
* Updated on 11 May 2024
*/

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport; 


import com.jayway.jsonpath.JsonPath;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;




public class GoogleKGLookup {
	
	//https://github.com/schemaorg/schemaorg
	//Doc: https://developers.google.com/knowledge-graph/reference/rest/v1/
	//https://developers.google.com/knowledge-graph/
	//https://console.developers.google.com/apis/api/kgsearch.googleapis.com/credentials?project=sem-tab
	String API_key = "AIzaSyA6Bf9yuMCCPh7vpElzrfBvE2ENCVWr-84";

	
	public TreeSet<KGEntity> getEntities(String query, String num_hits, Set<String> types, Set<String> languages, double minScore) {
		
		TreeSet<KGEntity> entities = new TreeSet<KGEntity>();
		
		try {
			
		      HttpTransport httpTransport = new NetHttpTransport();
		      HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
		      JSONParser parser = new JSONParser();
		      GenericUrl url = new GenericUrl("https://kgsearch.googleapis.com/v1/entities:search");
		      url.put("query", query);
		      url.put("limit", num_hits);
		      url.put("indent", "true");
		      
		      url.put("types", types);
		      
		      url.put("languages", languages);
		      
		      url.put("key", API_key);
		      
		      //System.out.println(url);
		      
		      
		      HttpRequest request = requestFactory.buildGetRequest(url);
		      HttpResponse httpResponse = request.execute();
		      JSONObject response = (JSONObject) parser.parse(httpResponse.parseAsString());
		      JSONArray elements = (JSONArray) response.get("itemListElement");
		      
		      double score;
		      
		      
		      System.out.println("Number of canddiate elements: " + elements.size());
		      
		      System.out.println(elements);
		      
		      
		      for (Object element : elements) {
		    	  
		    	  
		    	  score = Double.parseDouble(JsonPath.read(element, "$.resultScore").toString());
		    	  
		    	  if (score>minScore){ //filter by google score. 
		    	  
			    	  KGEntity entity = new KGEntity();
				    	
			    	  entity.setId(JsonPath.read(element, "$.result.@id").toString());
			    	  entity.setName(JsonPath.read(element, "$.result.name").toString());
			    	  entity.setDescription(JsonPath.read(element, "$.result.description").toString());
				    	
			    	  for (Object type : (JSONArray)JsonPath.read(element, "$.result.@type")) {
			    		  if (!type.toString().equals("Thing"))
			    			  entity.addType(type.toString());  
			    	  }
				    	
			    	  entity.setScore(score);
				    	  
			    	  //System.out.println(JsonPath.read(element, "$.result.@id").toString());
				      //System.out.println(JsonPath.read(element, "$.result.name").toString());
				      //System.out.println(JsonPath.read(element, "$.result.description").toString());
				        
				      ///System.out.println(JsonPath.read(element, "$.result.@type").toString());
			        
			        
			    	  entities.add(entity);
		    	  }
		        
		      }
		    } catch (Exception ex) {
		      ex.printStackTrace();
		    
		    }
		
		
		
		for (KGEntity entity : entities){
			
			System.out.println(entity.toString());
			
		}
		
		
		
		return entities;
		
	}

	
	
	  public static void main(String[] args) {

		  Set<String> types = new HashSet<String>();
		  types.add("Person");
		  types.add("Event");
		  
		  String query;
		  query ="Chicago Bulls";
		  query = "Congo";

		  GoogleKGLookup lk = new GoogleKGLookup();
		  
		  lk.getEntities(query, "10", types, Collections.emptySet(), 0.0);
		  
		  
	  }
	  

	
	
	
	
	

}
