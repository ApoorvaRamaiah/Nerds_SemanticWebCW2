package OA;

/*
* Created by @author ernesto
* Available at: https://github.com/city-knowledge-graphs/java-2024/blob/main/src/main/java/lab8/CompareWithReference.java
* Type of Code: Java code
* 
* Updated by @author Shreyas Jadhav, Apoorva Ramaiah
* Coursework: Semantic Web and Technologies Part 2
* Coursework Group Name: Nerds
* Updated on 11 May 2024
*/


import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.RDFDataMgr;

public class Task_OA_2 {

	public Task_OA_2(String reference_mappings, String system_mappings) {
		
		Dataset reference_dataset = RDFDataMgr.loadDataset(reference_mappings);
		Model reference_model = reference_dataset.getDefaultModel();
		StmtIterator iter_ref = reference_model.listStatements();
		
		Dataset system_dataset = RDFDataMgr.loadDataset(system_mappings);
		Model system_model = system_dataset.getDefaultModel();
		StmtIterator iter_syst = system_model.listStatements();
		
	           
		int tp=0;		
		int fp=0;
		int fn=0;
       
        try {
            
        	while ( iter_syst.hasNext() ) {
                Statement stmt = iter_syst.next();
                if (reference_model.contains(stmt))
                	tp++;
                else
                	fp++;
           
            }       	
        	
        	
        	while ( iter_ref.hasNext() ) {
                Statement stmt = iter_ref.next();
                if (!system_model.contains(stmt))                	
                	fn++;
           
            }
        } finally {
            if ( iter_ref != null ) iter_ref.close();
            if ( iter_syst != null ) iter_syst.close();
        }
		
        
        //System.out.println(tp + " " + tp2);
        //System.out.println(fp);
        //System.out.println(fn);
        double precision = (double)tp/(double)(fp+tp);
        double recall = (double)tp/(double)(fn+tp);
        double f_score = (2*precision*recall)/(precision+recall);
        System.out.println("Comparing '" + system_mappings + "' with '" + reference_mappings);
        System.out.println("\tPrecision: " + precision);
        System.out.println("\tRecall: " + recall);
        System.out.println("\tF-Score: " + f_score);
		
	}
	
	public static void main(String[] args) {
	
		
		//Pizza
		new Task_OA_2("files/reference-mappings-pizza.ttl", "files/Task_2.4_OA_1_Ontology_Alignment.ttl");
		
	}
		
}
