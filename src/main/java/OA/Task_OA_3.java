package OA;

/*
* Created by @author ernesto
* Available at: https://github.com/city-knowledge-graphs/java-2024/blob/main/src/main/java/lab5/Lab5_Solution.java
* Type of Code: Java code
* Code used: performReasoning(), savegraph()
* 
* 
* Updated by @author Shreyas Jadhav, Apoorva Ramaiah
* Coursework: Semantic Web and Technologies Part 2
* Coursework Group Name: Nerds
* Updated on 11 May 2024
*/



import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDFS;

public class Task_OA_3 {
	
	OntModel model;
	InfModel inf_model;
	public void loadOntologyFromURL(String sourceURL) {
		
		model = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM );
        model.read(sourceURL, "RDF/XML" );

        
        System.out.println("Number of classes: " + model.listNamedClasses().toList().size());
        
        
	}
	
	public Task_OA_3(String city, String pizza) throws FileNotFoundException{
       
		String pizza_restaurants = "files/PizzaOnto.ttl";
		String output_file = "files/Task_2.3_OA_1_Ontology_Alignment.ttl";
		model = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM );
        model.read(pizza_restaurants, "RDF/XML" );
		
	        
	    model.setNsPrefix("pizza", pizza);
	    model.setNsPrefix("city", city);
	    
		    
	    String PizzaRestaurant = "http://www.semanticweb.org/city/in3067-inm713/2024/restaurants#";
	    String pizzaOnto= "http://www.co-ode.org/ontologies/pizza/pizza.owl#";
	        
		String[][] EquivalentClasses = {
                {"Country", "Country"},
                {"Food", "Food"},
                {"Seafood", "FishTopping"},
                {"Meat", "MeatTopping"},
                {"Seafood", "FishTopping"},
                {"Ham", "HamTopping"},
                {"Anchovies", "AnchoviesTopping"},
                {"Onions", "OnionTopping"},
                {"Artichokes", "ArtichokeTopping"},
                {"AmericanPizza", "American"},
                {"Capers", "CaperTopping"},
                {"Cheese", "CheeseTopping"},
                {"Fruit", "FruitTopping"},
                {"Garlic", "GarlicTopping"},
                {"Olives", "OliveTopping"},
                {"GoatCheese", "GoatsCheeseTopping"},
                {"Gorgonzola", "GorgonzolaTopping"},
                {"GreenPepper", "GreenPepperTopping"},
                {"JalapenoPepper", "JalapenoPepperTopping"},
                {"MargheritaPizza", "Margherita"},
                {"Meat", "MeatTopping"},
                {"MeatPizza", "MeatyPizza"},
                {"Mozzarella", "MozzarellaTopping"},
                {"Mushroom", "MushroomTopping"},
                {"MushroomPizza", "Mushroom"},
                {"NamedPizza", "NamedPizza"},
                {"Pizza", "Pizza"},
                {"Rosemary", "RosemaryTopping"},
                {"Sauce", "SauceTopping"},
                {"Spinach", "SpinachTopping"},
                {"Tomato", "TomatoTopping"},
                {"VegetarianPizza", "VegetarianPizza"},
                {"Pepper", "PepperTopping"},
                {"Chicken", "ChickenTopping"}};
		
		  
		
		for (String[] equivalence : EquivalentClasses) {
			
			Resource subject_resource = model.createResource(PizzaRestaurant+equivalence[0]);
	        
	        Resource object_resource = model.createResource(pizzaOnto+equivalence[1]);
	        
	        model.add(subject_resource, OWL.equivalentClass, object_resource);
			
	        
        };
        
        System.out.println("The graph contains '" + model.listStatements().toSet().size() + "' Equivalent Classes triples.");
        
        OutputStream out = new FileOutputStream(output_file);
        RDFDataMgr.write(out, model, RDFFormat.TTL);
        
        String[][] equivalentSubProperties = {
        		{"IsIngredientOf", "IsIngredientOf"},
                {"HasIngredient", "HasIngredient"}};
		        
		for (String[] equivalence : equivalentSubProperties) {
					
					Resource subject_resource = model.createResource(PizzaRestaurant+equivalence[0]);
			       
			        Resource object_resource = model.createResource(pizzaOnto+equivalence[1]);
			        
			        model.add(subject_resource, OWL.equivalentProperty, object_resource);
					
		        };
        
       
		        System.out.println("The graph contains '" + model.listStatements().toSet().size() + "' Equivalent Sub Properties triples.");
        
    
        OutputStream outt = new FileOutputStream(output_file);
        RDFDataMgr.write(outt, model, RDFFormat.TTL);
    }
	
	public Set<String> getRDFSLabelsForClass(OntClass cls) {
		
		final NodeIterator labels = cls.listPropertyValues(RDFS.label);
		
		Set<String> labels_set =  new HashSet<String>();
		
		while( labels.hasNext() ) {
		    final RDFNode labelNode = labels.next();
		    final Literal label = labelNode.asLiteral();
		   
		    labels_set.add(label.getString());
		}
		
		return labels_set;
		
	}

	public void iterateOverLabels() {
	
	for (Iterator<OntClass> i =  model.listClasses(); i.hasNext(); ) {
  	  OntClass c = i.next();
  	  if (!c.isAnon()) {  
  		  System.out.println(c.getURI() );
  		  System.out.println("\t" + c.getLocalName());  
  		  System.out.println("\t" + getRDFSLabelsForClass(c) ); 
  		  
  	  }
	}
}
  	  
public void iterateOverPizzaOnologyClasses() {
	
	Set<String> pizza_classes =  new HashSet<String>();
		for (Iterator<OntClass> i =  model.listClasses(); i.hasNext(); ) {
      	  OntClass c = i.next();
      	  if (!c.isAnon()) { 
      		pizza_classes.add(c.getLocalName());
     
     		  
      	  }
      	
      }
		System.out.println("Numer of classes in pizza ontology: "+pizza_classes.size());
	}

public void iterateOverPizzaRestaurantClasses() {
	
	Set<String> pizza_restaurant_classes =  new HashSet<String>();
		for (Iterator<OntClass> i =  model.listClasses(); i.hasNext(); ) {
      	  OntClass c = i.next();
      	  if (!c.isAnon()) { 
      		  
      		pizza_restaurant_classes.add(c.getLocalName());
     
      	
      	  }
      	
      }
		System.out.println("Numer of classes in pizza restaurant: "+pizza_restaurant_classes.size());
	}


	public void performReasoning(String ontology_file) throws FileNotFoundException {
	    
		
		System.out.println("Data triples from CSV: '" + model.listStatements().toSet().size() + "'.");
		        

	    Dataset dataset = RDFDataMgr.loadDataset(ontology_file);
	    model.add(dataset.getDefaultModel().listStatements().toList());
	    
	    
	    System.out.println("Triples including ontology: '" + model.listStatements().toSet().size() + "'.");
	    
	    Reasoner reasoner = ReasonerRegistry.getOWLMiniReasoner();		
		inf_model = ModelFactory.createInfModel(reasoner, model);
		
	    
	    System.out.println("Triples after reasoning: '" + inf_model.listStatements().toSet().size() + "'.");
	    
	    String output_file = "files/Task_2.4_OA_3_Reasoner_Output.ttl";
	    OutputStream outt = new FileOutputStream(output_file);
        RDFDataMgr.write(outt, model, RDFFormat.TTL);
	    
	}


public void saveGraph(Model model, String file_output) throws FileNotFoundException {
    
   
    OutputStream out = new FileOutputStream(file_output);
    RDFDataMgr.write(out, model, RDFFormat.TURTLE);
     
  }	

	public static void main(String[] args) {
		
		
		try {
		String city = "http://www.semanticweb.org/city/in3067-inm713/2024/restaurants#";
     

        String pizza= "http://www.co-ode.org/ontologies/pizza/pizza.owl#";
        

        String pizza_ontology = "files/pizza-ontology/pizza.ttl";
        String pizza_restaurants_onotolgy = "files/pizza-restaurants-ontology.ttl";
        String output_file = "files/output_file.ttl";

       
  
       Task_OA_3 computeEquivalences = new Task_OA_3(city, pizza);
        
     
        computeEquivalences.loadOntologyFromURL(pizza_ontology);
        computeEquivalences.iterateOverPizzaOnologyClasses();
        computeEquivalences.loadOntologyFromURL(pizza_restaurants_onotolgy);
        computeEquivalences.iterateOverPizzaRestaurantClasses();
        computeEquivalences.performReasoning("files/Task_2.4_OA_1_Ontology_Alignment.ttl");
    
       
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

}
}