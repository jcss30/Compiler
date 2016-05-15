
public class Token {
    static final int IDENTIFIER = 150;
    static final int KEYWORD = 151;
    static final int ERROR = 152;
    static final int ADD = 153;                 //'+'
    static final int SUBTRACT = 154;            //'-'
    static final int DIVIDE = 155;              //'/'
    static final int MULTIPLY = 156;            //'*'
    static final int EXPONENT = 157;            //'^'
    static final int INTDIV = 158;        //'|'
    static final int REMAINDER = 159;           //'%'
    static final int SEPARATOR = 160;           //','
    static final int EOS = 161;                 //';'
    static final int LPAR = 162;                //'('
    static final int RPAR = 163;                //')'
    static final int LCURLYBRACE = 164;              //'{'
    static final int RCURLYBRACE = 165;              //'}'
    static final int LSQUAREBRACKET = 166;             //'['
    static final int RSQUAREBRACKET = 167;            //']'
    static final int EQUAL = 168;               //'='
    static final int GREATER = 169;             //'>'
    static final int LESS = 170;                //'<'
    static final int CONCAT = 171;             //'&'
    static final int COMMENT = 172;             //'#'
    static final int STRING = 173;              //'"'
    static final int TRALSE = 174;          
    static final int LOGICAL = 175;
    static final int INOP = 176;
    static final int NOTEQUAL = 177;
    static final int GREATEROREQUAL = 178;
    static final int LESSOREQUAL = 179;
    static final int ISEQUAL = 180;
    static final int DECIMAL = 181;
    static final int INT = 182;
    static final int EOF = 183;
    static final int PLAY = 184;
    static final int SLEEP = 185;
    static final int ELSE = 186;
    static final int IF = 187;
    static final int ELSEIF = 188;
    static final int STOP = 189;
    static final int SPIN = 190;
    static final int UNTIL = 191;
    static final int NOISY = 192;
    static final int IN = 193;
    static final int FUN = 194;
    static final int NUM = 195;
    static final int DEC = 196;
    static final int LET = 197;
    static final int WORD = 198;
    static final int NOISE = 199;
    static final int TRUE = 200;
    static final int FALSE = 201;
    static final int NOT = 202;
    static final int NL = 203;
    static final int INCREMENT = 204;
    static final int DECREMENT = 205;

    private String token; // Type of of token
    private String lexeme; // The lexeme
    private int lineNumber;
    private int tokenClass;

    private static int counter = 0;

    public Token(String token, String lexeme, int lineNumber, int tokenClass) {
        this.token = token;
        this.lexeme = lexeme;
        this.lineNumber = lineNumber;
        this.tokenClass = tokenClass;
        System.out.println(toString());
    }

    // Returns the type of the token
    public String getTokenType() {
        return token;
    }

    // Returns the lexeme of the token
    public String getLexeme() {
        return lexeme;
    }

    public int getLineNumber() {
        return lineNumber;
    }

        public int getTokenClass(){
        return tokenClass;
    }

    // Returns a string representation of the token
    public String toString() {

        return
                getTokenType() + "\t\t\t" + getLexeme() + "\tlineNumber:" + getLineNumber();
    }
}