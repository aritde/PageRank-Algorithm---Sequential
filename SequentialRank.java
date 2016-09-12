
import java.io.*;
import java.util.*;
public class SequentialRank {
    // adjacency matrix read from file
    private Map<Integer, ArrayList<Integer>> outdegree = new HashMap<Integer, ArrayList<Integer>>();
    private Map<Integer, ArrayList<Integer>> indegree = new HashMap<Integer, ArrayList<Integer>>();
    // input file name
    private String inputFile;
    // output file name
    private String outputFile;
    // number of iterations
    private int iterations ;
    // damping factor
    private double df  ;
    // number of URLs
    private int size = 0;
    // calculating rank values
    private TreeMap<Integer, Double> rankValues = new TreeMap<Integer, Double>();
    
    /* Sorting the treeMap by values n decreasing order*/
    
    public static <K, V extends Comparable<V>> Map<K, V> 
    sortByValues(final Map<K, V> map) {
    Comparator<K> valueComparator = 
             new Comparator<K>() {
      public int compare(K k1, K k2) {
        int compare = 
              map.get(k2).compareTo(map.get(k1));
        if (compare == 0) 
          return 1;
        else 
          return compare;
      }
    };
 
    Map<K, V> sortedByValues = 
      new TreeMap<K, V>(valueComparator);
    sortedByValues.putAll(map);
    return sortedByValues;
  }
     /**
     * Parse the command line arguments and update the instance variables. Command line arguments are of the form
     * <input_file_name> <output_file_name> <num_iters> <damp_factor>
     */
  
    public void parseArgs(String[] args) {
	if(args.length!=4)
		System.out.println("Incorrect Number of arguments");
	inputFile = args[0];
	outputFile = args[1];
	iterations = Integer.parseInt(args[2]);
	df = Double.parseDouble(args[3]);
    }

    /* The below function loadInput() reads the input file and does the following :
     * Splits the contents of each row in an array of Strings
     * Populates the outdegree TreeMap by checking whether the key already exists or not.
     *     - If the key exists, the corresponding value(which is a list of edges going out from 
     *     	 the node under consideration) is updated onto the outdegree TreeMap
     *       Eg : For the sample pagerank.input file:
     *       Key :4 | Value :[1,3,5]
     *     - If the key does not exist, a new key with an empty list as the value is added to the TreeMap.
     */
    public void loadInput() throws IOException {
    	try
	{
		/** Reads the input file line by line and populates the outdegree map */
		FileReader reader = new FileReader(new File(inputFile));
    		BufferedReader bufferedReader = new BufferedReader(reader);
    		String line;
    		while((line = bufferedReader.readLine())!=null)
    		{
    			String[] columns = line.split(" ");
    			int link = Integer.parseInt(columns[0]);
    			if (!outdegree.containsKey(link))
    			{
    				outdegree.put(link,new ArrayList<Integer>());
    			}
    			// edgelist : outdegree list
    			ArrayList<Integer> edgeList = new ArrayList<>();
    			for ( int i=1;i<columns.length;i++)
    				edgeList.add(Integer.parseInt(columns[i]));
    			outdegree.put(link, edgeList);
    		}
    		/** Populates the dangling node values(which is an empty list) by making the node point to all other nodes
    		*  in the graph */ 
    	
    		Iterator<Map.Entry<Integer,ArrayList<Integer>>> iterDanglingNode = outdegree.entrySet().iterator();
    		while(iterDanglingNode.hasNext())
    		{
    			Map.Entry<Integer,ArrayList<Integer>> eachPair = /*(Map.Entry<Integer,ArrayList<Integer>>)*/iterDanglingNode.next();
    			ArrayList<Integer> tempValue = eachPair.getValue();
    			/** If the size of arraylist of edges is zero, add all the keys to the graph */
    			if(tempValue.size()==0)
    			{
    				outdegree.put(eachPair.getKey(),new ArrayList<Integer>(outdegree.keySet()));
			}
    			
    		}
    		/** Populates the indegree Map */
    	
    		Iterator<Map.Entry<Integer,ArrayList<Integer>>> it = outdegree.entrySet().iterator();
        	while (it.hasNext()) {
        		Map.Entry<Integer,ArrayList<Integer>> pair =/*(Map.Entry<Integer,ArrayList<Integer>>)*/it.next();
        		int nodeUnderConsideration = pair.getKey();
        		if(!indegree.containsKey(nodeUnderConsideration))
            		{
        			indegree.put(pair.getKey(), new ArrayList<>());
            		}
        		Iterator<Map.Entry<Integer,ArrayList<Integer>>> temp_it=outdegree.entrySet().iterator();
        		ArrayList<Integer> indegreeList = new ArrayList<>();
        		while(temp_it.hasNext())
        		{
        			Map.Entry<Integer,ArrayList<Integer>> inner_pair=temp_it.next();
        			int currentNode=inner_pair.getKey();
        			List<Integer> valuesOfOtherNodes = inner_pair.getValue(); 
        			Iterator<Integer> outDegreeIterator = valuesOfOtherNodes.iterator();
        			while(outDegreeIterator.hasNext())
        			{
        				int value = outDegreeIterator.next();
        				if(value==nodeUnderConsideration)
        					indegreeList.add(currentNode);
        			}                                      
        		}
        		indegree.put(pair.getKey(),indegreeList);
        	}
        
        	/** Populates the initial rank of all nodes in the graph */

        	Iterator<Map.Entry<Integer,ArrayList<Integer>>> iteratorInitialRank = indegree.entrySet().iterator();
        	int size = indegree.size();
        	double initRank = 1.0/size;
        	while(iteratorInitialRank.hasNext())
        	{
        		Map.Entry<Integer,ArrayList<Integer>> indegreePair=iteratorInitialRank.next();
        		if(!rankValues.containsKey(indegreePair.getKey()))
        		{
        			rankValues.put(indegreePair.getKey(),initRank);
        		}
        	}
 	      }
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
    /**
     * The below function calculates the page rank for 'iterations' number of iterations.
     */
    
    public void calculatePageRank() {
      	for (int i=0;i<iterations;i++)
    	{
    		TreeMap<Integer,Double> rankCopy = new TreeMap<>();
    		Iterator<Map.Entry<Integer,Double>> rankIterator = rankValues.entrySet().iterator();
    		while(rankIterator.hasNext())
    		{
    			double value=0.0;
        		double newRank = 0.0;
        		Map.Entry<Integer,Double> rankPair = rankIterator.next();
    			List<Integer> indegreeList = indegree.get(rankPair.getKey());
    			Iterator<Integer> indegreeIterator = indegreeList.iterator();
    			/** This loop calculates the sum of Rank(Outdegree Node)/Outdegree_count_of_the_node_under_consideration */
			while(indegreeIterator.hasNext())
    			{
    				int node =  indegreeIterator.next();
				ArrayList<Integer> outdegreeList = outdegree.get(node);
    				value += rankValues.get(node)/outdegreeList.size();
    			}
			/** Incorporates the damping factor */
    			newRank = ((1-df)/indegree.size())+(df*(value));
    			rankCopy.put(rankPair.getKey(), newRank);
    		}
		/** Updates the rankvalues of all nodes changed in the current iteration */
    		Iterator<Map.Entry<Integer,Double>> rankValueUpdate = rankCopy.entrySet().iterator();
    		while(rankValueUpdate.hasNext())
    		{
    			Map.Entry<Integer,Double> pair = rankValueUpdate.next();
    			int node = pair.getKey();
    			if(rankValues.containsKey(node))
    				rankValues.put(node,pair.getValue());
    		}
    	}
       	/**  Sorts the results in decreasing order after desired number of iterations. */
	rankValues = (TreeMap<Integer, Double>) sortByValues(rankValues);
    }

    /**
     * Prints the pagerank values to the output file. Print only the first 10 values to console.
     */
    public void printValues() throws IOException {
	try
	{
		PrintWriter writer = new PrintWriter(outputFile, "UTF-8");
		int count =0;
		Iterator<Map.Entry<Integer,Double>> indet = rankValues.entrySet().iterator();
		while(indet.hasNext() && count<10)
		{
	        	Map.Entry<Integer,Double> ipar = indet.next();
                	writer.println("Page : " + ipar.getKey() + " = " + ipar.getValue());
			count++;
        	}
		writer.close();
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
    }

    public static void main(String[] args) throws IOException {
        SequentialRank sequentialPR = new SequentialRank();
	// parse input from command line
        sequentialPR.parseArgs(args);
	// load data from input file to necc data strutures
        sequentialPR.loadInput();
	// calculates pagerank
        sequentialPR.calculatePageRank();
	//Prints output to output file
        sequentialPR.printValues();
    }
}
