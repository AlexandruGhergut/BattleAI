package Database;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Dragos-Alexandru
 */
public class DummyDatabaseClass {

    public static void main(String[] args) {
        
            DatabaseHandler X = DatabaseHandler.getInstance();
        
        X.pushPlayer("jucator1", "jucator1");
        X.pushPlayer("jucator2", "jucator2");
        X.pushPlayer("jucator3", "jucator3");
        X.pushPlayer("jucator4", "jucator4");
        X.pushPlayer("jucator5", "jucator5");

        List<String> L = new ArrayList<String>();
        L.add("jucator1");
        L.add("jucator2");
        L.add("jucator3");

        MatchDatabase M = new MatchDatabase("jucator1", 3, new Double(12));
        M.pushMatch(L);
        M = new MatchDatabase("jucator2", 3, new Double(2));
        M.pushMatch(L);
        M = new MatchDatabase("jucator3", 3, new Double(2));
        M.pushMatch(L);
        M = new MatchDatabase("jucator1", 3, new Double(2));
        M.pushMatch(L);
        List<MatchDatabase> Ll = X.getWonMatches("jucator1");
        
        for(MatchDatabase it: Ll){
            it.afis();
        }
        
        System.out.println("Toate meciurile");
        X.afisM();
    
    }
}
