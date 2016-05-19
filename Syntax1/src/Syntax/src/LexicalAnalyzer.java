
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class LexicalAnalyzer {

    private BufferedReader reader; // Reader
    private char currentChar; // The current character being scanned
    public static int line = 1;
    private static final char EOF = (char) (-1);

    // End of file character
    public LexicalAnalyzer(String file) {
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Read the first character
        currentChar = read();
    }

    private char read() {
        try {
            return (char) (reader.read());
        } catch (IOException e) {
            e.printStackTrace();
            return EOF;
        }
    }

    // Checks if a character is a digit
    private boolean isNumeric(char c) {
        if (c >= '0' && c <= '9') {
            return true;
        }

        return false;
    }

    //Checks if character is an alphabet

    public boolean isAlpha(char c) {
        if (c >= 'a' && c <= 'z') {
            return true;
        }
        if (c >= 'A' && c <= 'Z') {
            return true;
        }
        return false;

    }

    public Token nextToken() {

        String currentState = "LA"; // Initial currentState

        int numBuffer = 0; // A buffer for number literals

        String alphaBuffer = "";

        int decBuffer = 0;

        boolean skipped = false;

        while (true) {
            if (currentChar == EOF && !skipped) {
                skipped = true;

            } else if (skipped) {

                try {

                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            switch (currentState) {
                // Controller
                case "LA":
                    switch (currentChar) {

                        case '\n':
                            line++;

                        case ' ': // Whitespaces

                        case '\r':
                        case '\t':
                            currentChar = read();

                            continue;
                        case '"':
                            currentChar = read();
                            currentState = "string";
                            alphaBuffer = "";
                            continue;
                            //EOS
                        case ';':
                            currentChar = read();
                            return new Token("EOS", ";", line, Token.EOS);
                        //Operators
                        case '+':
                            currentChar = read();
                            currentState = "++";
                            continue;

                        case '-':
                            currentChar = read();
                            currentState = "--";
                            continue;

                        case '*':
                            currentChar = read();
                            return new Token("Multiply", "*", line, Token.MULTIPLY);

                        case '^':
                            currentChar = read();
                            return new Token("Exponent", "^", line, Token.EXPONENT);

                        case '/':
                            currentChar = read();
                            return new Token("Division", "/", line, Token.DIVIDE);

                        case '|':
                            currentChar = read();
                            return new Token("Int Divide", "|", line, Token.INTDIV);

                        case '%':
                            currentChar = read();
                            return new Token("Remainder", "%", line, Token.REMAINDER);

                        case ',':
                            currentChar = read();
                            return new Token("ParameterSep", ",", line, Token.SEPARATOR);
                        case '(':
                            currentChar = read();
                            return new Token("OpenParenth", "(", line, Token.LPAR);
                        case ')':
                            currentChar = read();
                            return new Token("CloseParenth", ")", line, Token.RPAR);
                        case '{':
                            currentChar = read();
                            return new Token("LeftBrace", "{", line, Token.LCURLYBRACE);
                        case '}':
                            currentChar = read();
                            return new Token("RightBrace", "}", line, Token.RCURLYBRACE);
                        case '[':
                            currentChar = read();
                            return new Token("LeftBracket", "[", line, Token.LSQUAREBRACKET);
                        case ']':
                            currentChar = read();
                            return new Token("RightBracket", "]", line, Token.RSQUAREBRACKET);
                        case '&':
                            currentChar = read();
                            return new Token("Concat", "&", line, Token.CONCAT);
                        case '$':
                            currentChar = read();
                            return new Token("In", "$", line, Token.INOP);
                        case '=':
                            currentChar = read();
                            currentState = "==";
                            continue;


                        case '!':
                            currentChar = read();
                            currentState = "!=";
                            continue;

                        case '>':
                            currentChar = read();
                            currentState = ">=";
                            continue;

                        case '<':
                            currentChar = read();
                            currentState = "<=";
                            continue;

                        case '#':
                            currentChar = read();
                            currentState = "##";
                            alphaBuffer = "";
                            continue;

                        default:
                            currentState = "P1"; // Check the next possibility
                            continue;
                    }

                    // Integer - Start
                case "P1":

                    if (isNumeric(currentChar)) {
                        numBuffer = 0; // Reset the buffer.
                        numBuffer += (currentChar - '0');

                        currentState = "P2";

                        currentChar = read();

                    } else {
                        currentState = "P3"; //doesnot start with number or symbol go to case 5
                    }
                    continue;

                    // Integer - Body
                case "P2":
                    if (isNumeric(currentChar)) {
                        numBuffer *= 10;
                        numBuffer += (currentChar - '0');

                        currentChar = read();

                    } else if (currentChar == '.') {

                        currentChar = read();

                        currentState = "P4"; //has decimal point go to case 4

                    } else {
                        return new Token("Number", "" + numBuffer, line, Token.INT);
                    }

                    continue;

                    //decimal-start
                case "P4":
                    if (isNumeric(currentChar)) {
                        decBuffer = 0;
                        decBuffer += (currentChar - '0');
                        currentState = "P5";
                        currentChar = read();

                    } else {
                        return new Token("ERROR", "Invalid input: " + numBuffer + ".", line, Token.ERROR);
                    }
                    continue;
                    //decimal body
                case "P5":
                    if (isNumeric(currentChar)) {
                        decBuffer *= 10;
                        decBuffer += (currentChar - '0');

                        currentChar = read();
                    } else {
                        return new Token("Decimal", "" + numBuffer + "." + decBuffer, line, Token.DECIMAL);
                    }
                    continue;

                    //identifier -start
                case "P3":

                    if (isAlpha(currentChar) || currentChar == '_') {
                        //play
                        if (currentChar == 'p') {
                            alphaBuffer += currentChar;
                            currentChar = read(); //p

                            if (currentChar == 'l') {
                                alphaBuffer += currentChar;
                                currentChar = read(); //pl

                                if (currentChar == 'a') {
                                    alphaBuffer += currentChar;
                                    currentChar = read(); //pla

                                    if (currentChar == 'y') {
                                        alphaBuffer += currentChar;
                                        currentChar = read(); //play
                                        //System.out.println(alphaBuffer);
                                        return new Token("ReservedWord", alphaBuffer, line, Token.PLAY);

                                    }
                                }

                            }
                        } //sleep, stop, spin
                        else if (currentChar == 's') {
                            alphaBuffer += currentChar;
                            currentChar = read(); //s

                            if (currentChar == 'l') {
                                alphaBuffer += currentChar;
                                currentChar = read(); //sl

                                if (currentChar == 'e') {
                                    alphaBuffer += currentChar;
                                    currentChar = read(); //sle

                                    if (currentChar == 'e') {
                                        alphaBuffer += currentChar;
                                        currentChar = read(); //slee
                                        if (currentChar == 'p') {
                                            alphaBuffer += currentChar;
                                            currentChar = read(); //sleep
                                            //System.out.println(alphaBuffer);
                                            return new Token("ReservedWord", alphaBuffer, line, Token.SLEEP);
                                        }

                                    }
                                }

                            } else if (currentChar == 't') {
                                alphaBuffer += currentChar;
                                currentChar = read(); //st

                                if (currentChar == 'o') {
                                    alphaBuffer += currentChar;
                                    currentChar = read(); //sto

                                    if (currentChar == 'p') {
                                        alphaBuffer += currentChar;
                                        currentChar = read(); //stop
                                        //System.out.println(alphaBuffer);
                                        return new Token("Keyword", alphaBuffer, line, Token.STOP);
                                    }
                                }
                            } else if (currentChar == 'p') {
                                alphaBuffer += currentChar;
                                currentChar = read(); //sp

                                if (currentChar == 'i') {
                                    alphaBuffer += currentChar;
                                    currentChar = read(); //spi

                                    if (currentChar == 'n') {
                                        alphaBuffer += currentChar;
                                        currentChar = read(); //spin
                                        //System.out.println(alphaBuffer);
                                        return new Token("Keyword", alphaBuffer, line, Token.SPIN);
                                    }
                                }

                            }
                        } //if, in
                        else if (currentChar == 'i') {
                            alphaBuffer += currentChar;
                            currentChar = read(); //i

                            if (currentChar == 'f') {
                                alphaBuffer += currentChar;
                                currentChar = read(); //if
                                return new Token("Keyword", alphaBuffer, line, Token.IF);
                            }

                            if (currentChar == 'n') {
                                alphaBuffer += currentChar;
                                currentChar = read(); //in
                                return new Token("Keyword", alphaBuffer, line, Token.IN);
                            }
                        }
                        //elsif, else
                        if (currentChar == 'e') {
                            alphaBuffer += currentChar;
                            currentChar = read(); //e

                            if (currentChar == 'l') {
                                alphaBuffer += currentChar;
                                currentChar = read(); //el

                                if (currentChar == 's') {
                                    alphaBuffer += currentChar;
                                    currentChar = read(); //els
                                    if (currentChar == 'i') {
                                        alphaBuffer += currentChar;
                                        currentChar = read(); //elsi
                                        if (currentChar == 'f') {
                                            alphaBuffer += currentChar;
                                            currentChar = read(); //elsif
                                            return new Token("Keyword", alphaBuffer, line, Token.ELSEIF);
                                        }
                                    }
                                    if (currentChar == 'e') {
                                        alphaBuffer += currentChar;
                                        currentChar = read(); //else
                                        return new Token("Keyword", alphaBuffer, line, Token.ELSE);
                                    }
                                }
                            }
                        } //until
                        else if (currentChar == 'u') {
                            alphaBuffer += currentChar;
                            currentChar = read(); //u

                            if (currentChar == 'n') {
                                alphaBuffer += currentChar;
                                currentChar = read(); //un

                                if (currentChar == 't') {
                                    alphaBuffer += currentChar;
                                    currentChar = read(); //unt
                                    if (currentChar == 'i') {
                                        alphaBuffer += currentChar;
                                        currentChar = read(); //unti
                                        if (currentChar == 'l') {
                                            alphaBuffer += currentChar;
                                            currentChar = read(); //until
                                            return new Token("Keyword", alphaBuffer, line, Token.UNTIL);
                                        }
                                    }
                                }
                            }
                        } //noisy,num,not
                        else if (currentChar == 'n') {
                            alphaBuffer += currentChar;
                            currentChar = read(); //n

                            if (currentChar == 'o') {
                                alphaBuffer += currentChar;
                                currentChar = read(); //no

                                if (currentChar == 'i') {
                                    alphaBuffer += currentChar;
                                    currentChar = read(); //noi
                                    if (currentChar == 's') {
                                        alphaBuffer += currentChar;
                                        currentChar = read(); //nois
                                        if (currentChar == 'y') {
                                            alphaBuffer += currentChar;
                                            currentChar = read(); //noisy
                                            return new Token("Keyword", alphaBuffer, line, Token.NOISY);
                                        }
                                    }
                                }
                            } else if (currentChar == 'u') {
                                alphaBuffer += currentChar;
                                currentChar = read(); //nu
                                if (currentChar == 'm') {
                                    alphaBuffer += currentChar;
                                    currentChar = read(); //num
                                    return new Token("Keyword", alphaBuffer, line, Token.NUM);
                                }
                            }
                        }
                        //NOT
                        else if(currentChar == 'N'){
                            alphaBuffer += currentChar;
                            currentChar = read(); //N
                            if (currentChar == 'O') {
                                alphaBuffer += currentChar;
                                currentChar = read(); //NO
                                if (currentChar == 'T') {
                                    alphaBuffer += currentChar;
                                    currentChar = read(); //NOT
                                    return new Token("Logical", alphaBuffer, line, Token.NOT);
                                }
                            }
                        }
                        //AND
                        else if(currentChar == 'A'){
                            alphaBuffer += currentChar;
                            currentChar = read(); //A
                            if (currentChar == 'N') {
                                alphaBuffer += currentChar;
                                currentChar = read(); //AN
                                if (currentChar == 'D') {
                                    alphaBuffer += currentChar;
                                    currentChar = read(); //AND
                                    return new Token("Logical", alphaBuffer, line, Token.AND);
                                }
                            }
                        }
                        //OR
                        else if(currentChar == 'O'){
                            alphaBuffer += currentChar;
                            currentChar = read(); //O
                            if (currentChar == 'R') {
                                alphaBuffer += currentChar;
                                currentChar = read(); //OR

                                return new Token("Logical", alphaBuffer, line, Token.OR);

                            }
                        }
                        //fun, false
                        else if (currentChar == 'f') {
                            alphaBuffer += currentChar;
                            currentChar = read(); //f

                            if (currentChar == 'a') {
                                alphaBuffer += currentChar;
                                currentChar = read(); //fa

                                if (currentChar == 'l') {
                                    alphaBuffer += currentChar;
                                    currentChar = read(); //fal
                                    if (currentChar == 's') {
                                        alphaBuffer += currentChar;
                                        currentChar = read(); //fals
                                        if (currentChar == 'e') {
                                            alphaBuffer += currentChar;
                                            currentChar = read(); //false
                                            return new Token("Tralse", alphaBuffer, line, Token.FALSE);
                                        }
                                    }
                                }
                            } else if (currentChar == 'u') {
                                alphaBuffer += currentChar;
                                currentChar = read(); //fu

                                if (currentChar == 'n') {
                                    alphaBuffer += currentChar;
                                    currentChar = read(); //fun
                                    return new Token("Keyword", alphaBuffer, line, Token.FUN);
                                }
                            }

                        } else if (currentChar == 'd') {
                            alphaBuffer += currentChar;
                            currentChar = read(); //d

                            if (currentChar == 'e') {
                                alphaBuffer += currentChar;
                                currentChar = read(); //de
                                if (currentChar == 'c') {
                                    alphaBuffer += currentChar;
                                    currentChar = read(); //dec
                                    return new Token("Keyword", alphaBuffer, line, Token.DEC);
                                }
                            }
                        } else if (currentChar == 'l') {
                            alphaBuffer += currentChar;
                            currentChar = read(); //l

                            if (currentChar == 'e') {
                                alphaBuffer += currentChar;
                                currentChar = read(); //le
                                if (currentChar == 't') {
                                    alphaBuffer += currentChar;
                                    currentChar = read(); //let
                                    return new Token("Keyword", alphaBuffer, line, Token.LET);
                                }
                            }

                        } else if (currentChar == 'w') {
                            alphaBuffer += currentChar;
                            currentChar = read(); //w
                            if (currentChar == 'o') {
                                alphaBuffer += currentChar;
                                currentChar = read(); //wo

                                if (currentChar == 'r') {
                                    alphaBuffer += currentChar;
                                    currentChar = read(); //wor
                                    if (currentChar == 'd') {
                                        alphaBuffer += currentChar;
                                        currentChar = read(); //word
                                        return new Token("Keyword", alphaBuffer, line, Token.WORD);
                                    }
                                }
                            }
                        } else if (currentChar == 't') {
                            alphaBuffer += currentChar;
                            currentChar = read(); //t
                            if (currentChar == 'r') {
                                alphaBuffer += currentChar;
                                currentChar = read(); //tr

                                if (currentChar == 'a') {
                                    alphaBuffer += currentChar;
                                    currentChar = read(); //tra
                                    if (currentChar == 'l') {
                                        alphaBuffer += currentChar;
                                        currentChar = read(); //tral
                                        if (currentChar == 's') {
                                            alphaBuffer += currentChar;
                                            currentChar = read(); //trals
                                            if (currentChar == 'e') {
                                                alphaBuffer += currentChar;
                                                currentChar = read(); //tralse
                                                return new Token("Keyword", "" + alphaBuffer, line, Token.TRALSE);
                                            }
                                        }
                                    }
                                } else if (currentChar == 'u') {
                                    alphaBuffer += currentChar;
                                    currentChar = read(); //tru
                                    if (currentChar == 'e') {
                                        alphaBuffer += currentChar;
                                        currentChar = read(); //true
                                        return new Token("Tralse", "" + alphaBuffer, line, Token.TRUE);
                                    }
                                }

                            } else if (currentChar == 'h') {
                                alphaBuffer += currentChar;
                                currentChar = read(); //th
                                if (currentChar == 'i') {
                                    alphaBuffer += currentChar;
                                    currentChar = read(); //thi
                                    if (currentChar == 's') {
                                        alphaBuffer += currentChar;
                                        currentChar = read(); //this
                                        return new Token("NoiseWord", "" + alphaBuffer, line, Token.NOISE);
                                    }
                                }
                            }

                        }

                        currentState = "P6";
                        continue;
                    } else {

                        currentChar = read();
                        return new Token("ERROR", "Invalid input:" + currentChar + alphaBuffer, line, Token.ERROR);
                    }

                case "==":

                    if (currentChar == '=') {

                        currentChar = read();
                        return new Token("isEqTo", "==", line, Token.ISEQUAL);
                    } else {

                        return new Token("ArEq", "=", line, Token.EQUAL);
                    }

                    //identifier - Body
                case "P6":
                    if ((isAlpha(currentChar) || isNumeric(currentChar) || currentChar == '_')) {

                        alphaBuffer += currentChar;
                        currentChar = read();

                    } else {
                        return new Token("Identifier", "" + alphaBuffer, line, Token.IDENTIFIER);
                    }
                    continue;

                case "++":
                    if (currentChar == '+') {
                        currentChar = read();
                        return new Token("Increment", "++", line, Token.INCREMENT);
                    } else {
                        return new Token("Addition", "+", line, Token.ADD);
                    }
                case "--":
                    if (currentChar == '-') {
                        currentChar = read();
                        return new Token("Decrement", "--", line, Token.DECREMENT);
                    } else {
                        return new Token("Subtraction", "-", line, Token.SUBTRACT);
                    }

                    //if !=
                case "!=":
                    if (currentChar == '=') {
                        currentChar = read();
                        return new Token("NotEq", "!=", line, Token.NOTEQUAL);
                    } else {
                        return new Token("ERROR", "Invalid input: !", line, Token.ERROR);
                    }

                case "string":
                    if (currentChar == '"') {
                        currentChar = read();
                        return new Token("String", "" + alphaBuffer + "", line, Token.STRING);
                    } else if (currentChar == '\n' || currentChar == EOF) {
                        currentChar = read();
                        return new Token("ERROR", "Invalid word literal", line, Token.ERROR);
                    } else {
                        alphaBuffer += currentChar;
                        currentChar = read();
                    }
                    continue;

                case "P7":
                    if (currentChar == '#') {
                        currentState = "P8";
                        currentChar = read();
                    }
                    continue;
                case "P8":
                    if (currentChar == '\n') {

                        currentState = "LA";
                    }
                    currentChar = read();
                    continue;

                case "<=":
                    if (currentChar == '=') {
                        currentChar = read();
                        return new Token("LessThanEq", "<=", line, Token.LESSOREQUAL);
                    } else {
                        return new Token("LessThan", "<", line, Token.LESS);
                    }
                case ">=":
                    if (currentChar == '=') {
                        currentChar = read();
                        return new Token("GreaterThanEq", ">=", line, Token.GREATEROREQUAL);
                    } else {

                        return new Token("GreaterThan", ">", line, Token.GREATER);
                    }
                case "##":
                    if (currentChar == '#') {
                        currentChar = read();
                        currentState = "LA";
                        //return new Token("Comment", "" + alphaBuffer + "", line, Token.COMMENT);
                    } else {
                       // alphaBuffer += currentChar;
                        currentChar = read();
                    }
                    continue;

            }
        }
    }
}
