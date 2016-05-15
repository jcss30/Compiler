public class Identifier {
    String type = null;
    int index;
    int intvalue;


    public int scope;

    public Identifier(){
        this.type = null;
        this.index = 0;
        this.intvalue = 0;


    }
    public void setType(String i){
        type = i;

    }
    public void setintValue(int i){
        intvalue = i;

    }
    public void setIndex(int i){
        index = i;
    }

    public String getType(){
        return type;
    }
    public int getIntValue(){
        return intvalue;
    }
    public int getIndex(){
        return index;
    }

    void setintValue(double answer) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}