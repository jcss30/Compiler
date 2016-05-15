public class SymbolEntry {

    //para malaman kung gano kalalim sa scope
    public int level;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    //mga attributes ng entry
    public String id;
    public String type;
    public Object value;


    //pag wala pang data type na nailalagay
    public SymbolEntry(String id){
        this.id=id;
    }

    public SymbolEntry(String id, String type){
        this.id = id;
        this.type=type;
    }
    public SymbolEntry(String id, String type, Object value){
        this.id = id;
        this.type=type;
        this.value = value;
    }

    public String toString(){
        return "id: "+id+"\t\t value:"+value;
    }

}