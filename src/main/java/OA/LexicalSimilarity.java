package OA;

/* Created by @author ernesto
* Available at: https://github.com/city-knowledge-graphs/java-2024/blob/main/src/main/java/lab5/LexicalSimilarity.java
* Type of Code: Java code
* 
* Updated by @author Shreyas Jadhav, Apoorva Ramaiah
* Coursework: Semantic Web and Technologies Part 2
* Coursework Group Name: Nerds
* Updated on 11 May 2024
*/

import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.apache.commons.text.similarity.LevenshteinDistance;
//import org.apache.commons.text.similarity.CosineSimilarity;

public class LexicalSimilarity {
	
	
	public LexicalSimilarity() {
		
		//Reference: https://commons.apache.org/proper/commons-text/apidocs/org/apache/commons/text/similarity/
		//Another potential library: https://github.com/tdebatty/java-string-similarity
		
		JaroWinklerSimilarity jw_sim = new JaroWinklerSimilarity();
		LevenshteinDistance l_dist = new LevenshteinDistance();
		I_Sub isub = new I_Sub();
	
		
		System.out.println("Jaro Winkler Similarity: " + jw_sim.apply("Congo", "Republic of Congo"));
		System.out.println("Levenshtein Distance: " + l_dist.apply("Congo", "Republic of Congo"));
		
		System.out.println("I-sub Similarity: " + isub.score("Congo", "Republic of Congo"));
		
		
	}
	
	
	
	public static void main(String[] args) {
		new LexicalSimilarity();
	}

}
