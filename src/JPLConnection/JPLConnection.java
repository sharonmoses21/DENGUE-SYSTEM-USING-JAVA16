package JPLConnection;

import org.jpl7.Query;
import org.jpl7.Term;

import java.util.LinkedHashMap;

public class JPLConnection {
    //prolog file name
    private final String PROLOG_FILE = "Dengue/RulesForQuickSort.pl";
    
    //Check the connection of java to prolog file
    public void consult() throws Exception{
        //construct the connection query
        String queryText = "consult('" + PROLOG_FILE + "')";
        Query consultQuery = new Query(queryText);

        //Execute the query and throw exception if failed
        if(!consultQuery.hasSolution()){
            throw new Exception("The consult process is failed.");
        }
        
        consultQuery.close();
    }

    //Execute the query and return result-
    public Term sortingQuery(String queryText) {
        return new Query(queryText).allSolutions()[0].get("SortedResult");
    }
    
    //Convert result returned from Prolog to map
    public LinkedHashMap<String, Term> convertSortingResultToMap
        (Term term, LinkedHashMap<String,Term> sortedResultMap) {
        //Firstly, put a new data into the map
        sortedResultMap.put(term.arg(1).arg(1).toString().replace("'", ""), term.arg(1).arg(2));

        //Secondary, check the condition
        //if fulfilling, then recursive call
        //Else, return the final result
        return((term.arg(2).arity()!=0)
                ?convertSortingResultToMap(term.arg(2),sortedResultMap)
                :sortedResultMap);
    }
}


