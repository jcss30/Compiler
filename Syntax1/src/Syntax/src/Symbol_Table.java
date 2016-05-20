import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Jude on 5/13/2016.
 */

public class Symbol_Table {
    Identifier ide = new Identifier();
    static int counter = 0;
    private static LinkedHashMap<String, SymbolEntry> linkedHashMap = new LinkedHashMap<>();


    public static boolean checkIdentifier(Token token){
        return linkedHashMap.containsKey(token.getLexeme());
    }


    public static boolean checkIdentifier(String name, Identifier id){

        return linkedHashMap.containsKey(name);

    }
    public static SymbolEntry getValue(String name){
        return linkedHashMap.get(name);
    }

    public static SymbolEntry getValue(Token token){
        return getValue(token.getLexeme());
    }
    public static void addToTable(SymbolEntry SymbolEntry){
        linkedHashMap.put(SymbolEntry.getId(), SymbolEntry);
        //System.out.println("nailagay na sa table");
    }

    public static void addToTable(String name, SymbolEntry symbol){
        counter++;
        //id.setIndex(counter);
        linkedHashMap.put(name,  symbol);
    }

    public static void addToTable(Token token){
        counter++;
        linkedHashMap.put(token.getLexeme(), new SymbolEntry(token.getLexeme(),token.getTokenType(),token.getLineNumber()));
      //  linkedHashMap.put(token.getLexeme(), new SymbolEntry(token.getLexeme(),token.getTokenType(),token.getLineNumber()));
        //TODO linkedHashMap.put(token.getLexeme(),token.getTokenClass());

    }

    public static void showTable(){

        System.out.println("----------------SYMBOL TABLE----------------");
        Iterator<Map.Entry<String, SymbolEntry>> itr = linkedHashMap.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry<String, SymbolEntry> entry = itr.next();

            SymbolEntry list = entry.getValue();
            System.out.println( list.toString());
        }
    }


}