public class SyntaxAnalyzer {

    // yung susunod na token
    Token currentToken = null;

    // yung parseTree na gagawin
    TreeNode tree = new TreeNode("PROGRAM");

    // Initialize lex
    LexicalAnalyzer lex = null;

    //for computations
    String infix = "";


    //For outputStmt
    String outputstmt = "";

    //for booleanexpressions
    String bool="";
    String value="";

    // Constructor
    public SyntaxAnalyzer(String filename) {
        // initialize na yung Lexical Anal
        lex = new LexicalAnalyzer(filename);
    }


    //yung Symbol_Table
    Symbol_Table symbol = new Symbol_Table();

    public void error(String message, Token token, TreeNode parent) {
        System.out.println("ERROR!!! " + message + "EXPECTED AT LINE " + token.getLineNumber());
        System.exit(1);
        while (currentToken.getTokenClass() != Token.EOS
                & currentToken.getTokenClass() != Token.RSQUAREBRACKET
                & currentToken.getTokenClass() != Token.RCURLYBRACE
                & currentToken.getTokenClass() != Token.LSQUAREBRACKET
                & currentToken.getTokenClass() != Token.LCURLYBRACE
                & currentToken.getTokenClass() != Token.EOF) {
            currentToken = lex.nextToken();
        }
        parent.addChild(currentToken);
        currentToken = lex.nextToken();
        stmt(parent);
    }
    
    public void program() throws ParseError {
        System.out.println("PARSING STARTED");

        // Create the main createTree node called Program
        // programNode = new DefaultMutableTreeNode("Program");
        // Start of analysis
        currentToken = lex.nextToken();


        if (currentToken.getLexeme().equals("play")) {
            // System.out.print("FOUND LEGGO");
            tree.addChild(currentToken);

            //programNode.add(new DefaultMutableTreeNode("play"));

            currentToken = lex.nextToken();

            progbody(tree);

            if (currentToken.getTokenClass() == Token.SLEEP) {

                //programNode.add(new DefaultMutableTreeNode("sleep"));
                tree.addChild(currentToken);
                //currentToken = lex.nextToken();

                tree.toString();

            } else {
                // throw new ParseError();
            }
            System.out.println("PARSE COMPLETE");
            //programNode.add(new DefaultMutableTreeNode("play"));
            //symbol.showTable();
            tree.toString();

        } else {
            System.out.println("ERROR ON LINE " + currentToken.getLineNumber() + "\n LEGGO EXPECTED");
        }
    }

    public void progbody(TreeNode parent) {
        System.out.println("ENTERED PROGBODY");

        //programBodyNode = new DefaultMutableTreeNode("Program Body");
        //parent.add(programBodyNode);

        //create a node
        TreeNode progBodyNode = new TreeNode("PROGBODY");
        //addnode sa createTree
        parent.addChild(progBodyNode);



        stmt(progBodyNode);
        while (currentToken.getTokenClass() != Token.SLEEP) {

            if (currentToken.getTokenClass() == Token.EOS) {

                progBodyNode.addChild(currentToken);

                currentToken = lex.nextToken();

            }

            if (currentToken.getTokenClass() == Token.SLEEP|currentToken.getTokenClass()==Token.RCURLYBRACE) break;

            stmt(progBodyNode);

        }
        System.out.println("UMALIS PROGBODY");
    }

    public void stmt(TreeNode parent) {
        System.out.println("ENTERED STATEMENT");

        TreeNode stmtNode = new TreeNode("STATEMENT");
        parent.addChild(stmtNode);
        switch (currentToken.getTokenClass()) {
            case Token.IDENTIFIER:
                Token tempToken = currentToken;
                currentToken = lex.nextToken();
                assignstmt(stmtNode, tempToken);
                break;
            case Token.NUM:
            case Token.DEC:
            case Token.LET:
            case Token.WORD:
            case Token.TRALSE:
                Token tokens = currentToken;
                currentToken = lex.nextToken();
                decstmt(stmtNode, tokens);
                break;
            case Token.STRING:
                currentToken = lex.nextToken();
                string_stmt(stmtNode);
                break;
            case Token.IN:// IO

                Token IOToken = currentToken;
                currentToken = lex.nextToken();
                in_stmt(stmtNode, IOToken);
                break;

            case Token.NOISY:
                Token OToken = currentToken;
                currentToken = lex.nextToken();
                noisy_stmt(stmtNode, OToken);
                break;
            case Token.IF:
                currentToken = lex.nextToken();
                if_stmt(stmtNode);
                break;
            case Token.SPIN:
                currentToken = lex.nextToken();
                spin_stmt(stmtNode);
                break;///dsdasdasdasd
            case Token.LCURLYBRACE:
                currentToken = lex.nextToken();
                progbody(stmtNode);
                if (currentToken.getTokenClass() == Token.RCURLYBRACE) {
                    currentToken = lex.nextToken();
                } else {
                    error("}", currentToken, parent);
                }

            case Token.RCURLYBRACE:
            case Token.SLEEP:
            case Token.EOS:
                parent.addChild(currentToken);
                currentToken = lex.nextToken();
                break;
            default:
                System.out.println("INVALID TOKEN");
                error("INVALID TOKEN ", currentToken, stmtNode);
                break;


        }
        System.out.println("EXITED STATEMENT");
    }

    public void spin_stmt(TreeNode parent) {

        System.out.println("ENTERED SPIN UNTIL");

        TreeNode spinNode = new TreeNode("SPINSTMT");
        parent.addChild(spinNode);
        spinNode.addChild("spin");

        if(currentToken.getTokenClass()==Token.THIS){
            //parent.addChild(spinNode);
            spinNode.addChild("this");
            currentToken = lex.nextToken();

        }

        if (currentToken.getTokenClass() == Token.LCURLYBRACE) {
            spinNode.addChild(currentToken);
            currentToken = lex.nextToken();

            stmt(spinNode);

            while (currentToken.getTokenClass() == Token.EOS) {
                spinNode.addChild(currentToken);
                currentToken = lex.nextToken();

                if (currentToken.getTokenClass() == Token.RCURLYBRACE)
                    break;

                stmt(spinNode);

            }

            if (currentToken.getTokenClass() == Token.RCURLYBRACE) {
                spinNode.addChild(currentToken);
                currentToken = lex.nextToken();

                if (currentToken.getTokenClass() == Token.UNTIL) {
                    spinNode.addChild(currentToken);
                    currentToken = lex.nextToken();

                    if (currentToken.getTokenClass() == Token.LSQUAREBRACKET) {
                        spinNode.addChild(currentToken);
                        currentToken = lex.nextToken();

                        booleanstmt(spinNode);

                        if (currentToken.getTokenClass() == Token.RSQUAREBRACKET) {
                            spinNode.addChild(currentToken);
                            currentToken = lex.nextToken();

                        } else {
                            // System.out.println("CLOSE PARENTHESIS EXPECTED at line " + currentToken.getLineNumber());
                            error("] EXPECTED ", currentToken, spinNode);
                        }

                    } else {
                        //System.out.println("( EXPECTED at LINE " + currentToken.getLineNumber());
                        error("[ ", currentToken, spinNode);
                    }

                } else {
                    error("UNTIL ", currentToken, spinNode);
                    //System.out.println("EXPECTED WHILE AT LINE" + currentToken.getLineNumber());
                }

            } else {
                error("} ", currentToken, spinNode);
                //System.out.println("} EXPECTED AT LINE " + currentToken.getLineNumber());
            }

        } else {
            error("{ ", currentToken, spinNode);
            //System.out.println("{ EXPECTED AT LINE" + currentToken.getLineNumber());
        }

        System.out.println("EXITED SPIN UNTIL");
    }

    public void this_noise(TreeNode parent){
        TreeNode spinNode = new TreeNode("NOISE");


        if(currentToken.getTokenClass()==Token.THIS){
            parent.addChild(spinNode);
            spinNode.addChild("this");
            currentToken = lex.nextToken();

        }
        else{

        }

    }

    public void decstmt(TreeNode parent, Token token) {
        System.out.println("Entered Declaration");


        value="";
        Token datatype = currentToken;

        TreeNode declareNode = new TreeNode("DECLARATION");
        parent.addChild(declareNode);
        //while (currentToken.getTokenClass() != Token.EOS) {
        if (currentToken.getTokenClass() == Token.IDENTIFIER) {

            SymbolEntry symbolEntry = new SymbolEntry(currentToken.getLexeme(),datatype.getTokenType(),"");

            symbol.addToTable(symbolEntry);

            declareNode.addChild(currentToken);
            currentToken = lex.nextToken();

            if (currentToken.getTokenClass() == Token.EQUAL) {

                declareNode.addChild(currentToken);
                assignstmt(declareNode, token);
                // currentToken = lex.nextToken();
                //exprStmt(declareNode);
            }
            if (currentToken.getTokenClass() == Token.SEPARATOR) {
                declareNode.addChild(currentToken);
                currentToken = lex.nextToken();
            }
            if(currentToken.getTokenClass() == Token.INCREMENT | currentToken.getTokenClass() == Token.DECREMENT){
                declareNode.addChild(currentToken);
                currentToken = lex.nextToken();
            }
        } else {
            System.out.println("IDENTIFIER EXPECTED");
        }
        // }

        System.out.println("Exited Declaration");
    }

    public void string_stmt(TreeNode parent) {
        System.out.println("ENTERED STRING STMT");
        TreeNode stringNode = new TreeNode("STRINGEXPR");
        parent.addChild(stringNode);
        outputstmt = "";

        if (currentToken.getTokenClass() == Token.IDENTIFIER |
                currentToken.getTokenClass() == Token.DECIMAL |
                currentToken.getTokenClass() == Token.STRING |
                currentToken.getTokenClass() == Token.INT) {

            stringNode.addChild(currentToken);
            currentToken = lex.nextToken();

            while (currentToken.getTokenClass() == Token.CONCAT) {
                stringNode.addChild(currentToken);
                currentToken = lex.nextToken();
                if (currentToken.getTokenClass() == Token.IDENTIFIER |
                        currentToken.getTokenClass() == Token.DECIMAL |
                        currentToken.getTokenClass() == Token.STRING |
                        currentToken.getTokenClass() == Token.INT) {


                    stringNode.addChild(currentToken);
                    currentToken = lex.nextToken();
                }
            }

        } else {
            error("NUMBER OR STRING", currentToken, stringNode);
        }


        System.out.println("EXITED STRING STMT");

    }

    public void assignstmt(TreeNode parent, Token token) {
        System.out.println("Entered Assignment");

        TreeNode assignmentNode = new TreeNode("ASSIGNMENT");
        parent.addChild(assignmentNode);
        TreeNode IDENTIFIER = new TreeNode("IDENTIFIER");
        assignmentNode.addChild(IDENTIFIER);
        IDENTIFIER.addChild(token);


        //Temporary storage ng identifier
        String id = token.getLexeme();

        //STRING NA ICOCOMPUTE
        infix = "";


        if (currentToken.getTokenClass() == Token.EQUAL) {

            assignmentNode.addChild(currentToken);

            currentToken = lex.nextToken();
            //exprStmt(assignmentNode);

            if (currentToken.getTokenClass() == Token.TRUE | currentToken.getTokenClass() == Token.FALSE) {
                relationalstmt(assignmentNode);
            } else {
                exprStmt(assignmentNode);
            }
           /* double value;
            System.out.println(infix);
            Symbol_TableEntry Symbol_TableEntry;
            try {
                String postfix = StackExpr.infixToPostfix(infix.trim());
                
                System.out.println(postfix);
                try {
                    value = Integer.parseInt(StackExpr.postfixEvaluation(postfix));
                 Symbol_TableEntry = new Symbol_TableEntry(id, 0, value);
                    symbol.addToTable(Symbol_TableEntry);
                    return;
                } catch (NumberFormatException nfe) {
                    value = Double.parseDouble(StackExpr.postfixEvaluation(postfix));
                    Symbol_TableEntry = new Symbol_TableEntry(id, 0, value);
                    symbol.addToTable(Symbol_TableEntry);
                    return;
                }
            } catch (Exception e) {
                Symbol_TableEntry Symbol_TableEntrys = new Symbol_TableEntry(id, 0, outputstmt);
                symbol.addToTable(Symbol_TableEntrys);
                return;
            }*/

            //lagay sa symbol table
            double value;
            System.out.println(infix);
            try {
                String postfix = StackExpr.infixToPostfix(infix);
                System.out.println(postfix);
                try {
                    value = Integer.parseInt(StackExpr.postfixEvaluation(postfix));
                } catch (NumberFormatException nfe) {
                    value = Double.parseDouble(StackExpr.postfixEvaluation(postfix));
                }
            } catch (Exception e) {
                //SymbolEntry symbolEntry = new SymbolEntry(id, 0, outputstmt);
                //symbol.addToTable(symbolEntry);
                return;
            }

            //lagay sa symbol table
            //SymbolEntry symbolEntry = new SymbolEntry(id, 0, value);
            //symbol.addToTable(symbolEntry);
        }


        System.out.println("Exited Assignment");
    }

    public void in_stmt(TreeNode parent, Token token) {
        System.out.println("ENTERED INPUT");

        TreeNode IONode = new TreeNode("INPUT");
        parent.addChild(IONode);
        IONode.addChild(token);


        if (currentToken.getTokenClass() == Token.INOP) {
            // infix+=" (";
            IONode.addChild(currentToken);
            currentToken = lex.nextToken();

            if(currentToken.getTokenClass() == Token.IDENTIFIER){
                IONode.addChild(currentToken);
                currentToken = lex.nextToken();
            }
            //string_stmt(IONode);
        } else {
            error("STRING EXPR", currentToken, IONode);
        }

        System.out.println("EXITED IO");

    }

    public void noisy_stmt(TreeNode parent, Token token) {
        System.out.println("ENTERED OUTPUT");


        TreeNode IONode = new TreeNode("OUTPUT");

        parent.addChild(IONode);
        IONode.addChild(token);


        if (currentToken.getTokenClass() == Token.STRING) {
            // infix+=" (";
            IONode.addChild(currentToken);
            // currentToken = lex.nextToken();
            string_stmt(IONode);
            System.out.println("OUTPUT--------" + outputstmt);
        }
        else if(currentToken.getTokenClass() == Token.IDENTIFIER){
            IONode.addChild(currentToken);
            //currentToken = lex.nextToken();
            string_stmt(IONode);
        }
        else {
            error("STRING EXPR", currentToken, IONode);
        }


        System.out.println("EXITED IO");

    }

    public void if_stmt(TreeNode parent) {
        System.out.println("ENTERED IF");

        TreeNode ifNode = new TreeNode("IFSTMT");

        parent.addChild(ifNode);
        ifNode.addChild(new Token("", "if", Token.IF, Token.IF));

        if (currentToken.getTokenClass() == Token.LSQUAREBRACKET) {
            ifNode.addChild(currentToken);
            currentToken = lex.nextToken();

            booleanstmt(ifNode);

            if (currentToken.getTokenClass() == Token.RSQUAREBRACKET) {
                ifNode.addChild(currentToken);
                currentToken = lex.nextToken();

                if (currentToken.getTokenClass() == Token.LCURLYBRACE) {
                    ifNode.addChild(currentToken);

                    currentToken = lex.nextToken();

                    progbody(ifNode);


                    //statements(ifNode);

                    if (currentToken.getTokenClass() == Token.RCURLYBRACE) {
                        ifNode.addChild(currentToken);
                        currentToken = lex.nextToken();

                        while (currentToken.getTokenClass() == Token.ELSEIF) {

                            currentToken = lex.nextToken();
                            elsif(ifNode);
                        }

                        if (currentToken.getTokenClass() == Token.ELSE) {
                            //ifNode.addChild(currentToken);
                            currentToken = lex.nextToken();
                            else_stmt(ifNode);
                        }


                    } else {
                        error("}", currentToken, ifNode);
                        System.out.println("} EXPECTED AFTER STATEMENTS A");
                    }

                } else {
                    error("{", currentToken, ifNode);
                    System.out.print("{ EXPECTED AT LINE " + currentToken.getLineNumber());
                }

            } else {
                error(")", currentToken, ifNode);
                System.out.println(") EXPECTED AT LINE " + currentToken.getLineNumber());
            }

        } else {
            error("(", currentToken, ifNode);
            System.out.println("( EXPECTED AT LINE " + currentToken.getLineNumber());
        }
        System.out.println("EXITED IF");
    }

    public void else_stmt(TreeNode parent) {//else 
        System.out.println("ENTERED ELSE");
        TreeNode elseNode = new TreeNode("ELSE");
        parent.addChild(elseNode);
        elseNode.addChild("else");
        //elseNode = new DefaultMutableTreeNode("OR KAYA");
        // parent.add(elseNode);

        if (currentToken.getTokenClass() == Token.LCURLYBRACE) {
            elseNode.addChild(currentToken);
            //elseNode.add(new DefaultMutableTreeNode(currentToken.getLexeme()));
            currentToken = lex.nextToken();

            stmt(elseNode);

            while (currentToken.getTokenClass() == Token.EOS) {
                elseNode.addChild(currentToken);
                currentToken = lex.nextToken();

                if (currentToken.getTokenClass() == Token.RCURLYBRACE) {
                    // System.out.println("UNEXPECTED END OF FILE");
                    break;
                }
                stmt(elseNode);
            }

            if (currentToken.getTokenClass() == Token.RCURLYBRACE) {
                elseNode.addChild(currentToken);
                //elseNode.add(new DefaultMutableTreeNode(currentToken.getLexeme()));
                currentToken = lex.nextToken();
            } else {
                error("}", currentToken, elseNode);
                System.out.println("} EXPECTED AT LINE" + currentToken.getLineNumber());
            }
        } else {
            error("{", currentToken, elseNode);
            System.out.println("{ EXPECTED AT LINE" + currentToken.getLineNumber());
        }
        System.out.println("EXITED ELSE");

    }

    public void elsif(TreeNode parent) {
        System.out.println("ENTERED ELSIF");

        TreeNode elsifNode = new TreeNode("ELSIF");
        parent.addChild(elsifNode);
        elsifNode.addChild("elsif");
        if (currentToken.getTokenClass() == Token.LSQUAREBRACKET) {
            elsifNode.addChild(currentToken);
            currentToken = lex.nextToken();

            booleanstmt(elsifNode);

            if (currentToken.getTokenClass() == Token.RSQUAREBRACKET) {
                elsifNode.addChild(currentToken);


                currentToken = lex.nextToken();

                if (currentToken.getTokenClass() == Token.LCURLYBRACE) {
                    elsifNode.addChild(currentToken);

                    currentToken = lex.nextToken();

                    stmt(elsifNode);

                    while (currentToken.getTokenClass() == Token.EOS) {

                        currentToken = lex.nextToken();

                        if (currentToken.getTokenClass() == Token.RCURLYBRACE) {
                            //System.out.println("UNEXPECTED END OF FILE");
                            break;
                        }
                        stmt(elsifNode);
                    }

                    if (currentToken.getTokenClass() == Token.RCURLYBRACE) {
                        elsifNode.addChild(currentToken);
                        currentToken = lex.nextToken();


                    } else {
                        error("}", currentToken, elsifNode);
                        System.out.println("} EXPECTED AT LINE" + currentToken.getLineNumber());
                    }

                } else {

                    error("{", currentToken, elsifNode);
                    System.out.println("{ EXPECTED AT LINE" + currentToken.getLineNumber());
                }

            } else {

                error(")", currentToken, elsifNode);
                System.out.println(") EXPECTED AT LINE" + currentToken.getLineNumber());
            }
        } else {

            error("(", currentToken, elsifNode);
            System.out.println("( EXPECTED AT LINE" + currentToken.getLineNumber());
        }


        System.out.println("EXITED ELSIF");
    }

    //FOR THE LOGICAL AND RELATIONAL AMBIGUITY
    public void booleanstmt(TreeNode parent) {
        System.out.println("ENTERED BOOLEAN STMT");
        TreeNode booleanStmtNode = new TreeNode("BOOLEANSTMT");
        parent.addChild(booleanStmtNode);
        if (currentToken.getTokenClass() == Token.NOT) {

            //booleanStmtNode.add(new DefaultMutableTreeNode(currentToken.getLexeme()));

            booleanStmtNode.addChild(currentToken);

            currentToken = lex.nextToken();
        }

        if (currentToken.getTokenClass() == Token.LSQUAREBRACKET) {
            booleanStmtNode.addChild(currentToken);
            currentToken = lex.nextToken();
            //booleanStmtNode.add(new DefaultMutableTreeNode(currentToken.getLexeme()));
            booleanstmt(booleanStmtNode);

            if (currentToken.getTokenClass() == Token.RSQUAREBRACKET) {
                //booleanStmtNode.add(new DefaultMutableTreeNode(currentToken.getLexeme()));
                booleanStmtNode.addChild(currentToken);
                currentToken = lex.nextToken();
            } else {

                error(")", currentToken, booleanStmtNode);
                System.out.println(") EXPECTED AT LINE " + currentToken.getLineNumber());
            }
        }


        relationalstmt(booleanStmtNode);
        while (currentToken.getTokenClass() == Token.AND | currentToken.getTokenClass() == Token.OR) {
            //booleanStmtNode.add(new DefaultMutableTreeNode(currentToken.getLexeme()));
            booleanStmtNode.addChild(currentToken);
            currentToken = lex.nextToken();
            relationalstmt(booleanStmtNode);

        }

        System.out.println("EXITED BOOLEAN STMT");
    }

    public void relationalstmt(TreeNode parent) {
        System.out.println("ENTERED RELATIONAL STMT");
        TreeNode relationalStmtNode = new TreeNode("RELATIONALSTMT");
        parent.addChild(relationalStmtNode);

        if (currentToken.getTokenClass() == Token.LSQUAREBRACKET) {
            relationalStmtNode.addChild(currentToken);
            currentToken = lex.nextToken();
            //booleanStmtNode.add(new DefaultMutableTreeNode(currentToken.getLexeme()));
            relationalstmt(relationalStmtNode);

            if (currentToken.getTokenClass() == Token.RSQUAREBRACKET) {
                //booleanStmtNode.add(new DefaultMutableTreeNode(currentToken.getLexeme()));
                relationalStmtNode.addChild(currentToken);
                currentToken = lex.nextToken();
            } else {

                error(")", currentToken, relationalStmtNode);
                System.out.println(") EXPECTED AT LINE " + currentToken.getLineNumber());
            }
        }

        if (currentToken.getTokenClass() == Token.TRUE
                | currentToken.getTokenClass() == Token.FALSE
                | currentToken.getTokenClass()==Token.IDENTIFIER ) {
            relationalStmtNode.addChild(currentToken);
            currentToken = lex.nextToken();


            if (currentToken.getTokenClass() == Token.GREATEROREQUAL
                    | currentToken.getTokenClass() == Token.GREATER
                    | currentToken.getTokenClass() == Token.LESSOREQUAL
                    | currentToken.getTokenClass() == Token.LESS
                    | currentToken.getTokenClass() == Token.ISEQUAL
                    | currentToken.getTokenClass() == Token.NOTEQUAL) {
                relationalStmtNode.addChild(currentToken);
                currentToken = lex.nextToken();
                exprStmt(relationalStmtNode);

            }




        }// else {
        //}
        System.out.println("EXITED RELATIONAL STMT");
    }

    public void exprStmt(TreeNode parent) {
        System.out.println("ENTERED EXPRESSION STATEMENT");

        TreeNode exprStmtNode = new TreeNode("EXPRSTMT");

        parent.addChild(exprStmtNode);

        operand(exprStmtNode);
        while (currentToken.getTokenClass() == Token.ADD | currentToken.getTokenClass() == Token.SUBTRACT) {
            if (currentToken.getTokenClass() == Token.ADD) infix += " +";
            else infix += " -";

            exprStmtNode.addChild(currentToken);
            currentToken = lex.nextToken();
            operand(exprStmtNode);
        }

        //infix="";
        System.out.println("EXITED EXPRESSION STATEMENT");

    }

    public void operand(TreeNode parent) {
        System.out.println("ENTERED OPERAND STATEMENT");
        TreeNode operandNode = new TreeNode("OPERAND");
        parent.addChild(operandNode);
        term(operandNode);
        while (currentToken.getTokenClass() == Token.EXPONENT) {
            infix += " ^";
            parent.addChild(currentToken);
            currentToken = lex.nextToken();
            term(operandNode);
        }


        System.out.println("EXITED OPERAND STATEMENT");
    }

    public void term(TreeNode parent) {
        System.out.println("ENTERED TERM");

        TreeNode termNode = new TreeNode("TERM");
        parent.addChild(termNode);

        factor(termNode);
        while (currentToken.getTokenClass() == Token.MULTIPLY
                | currentToken.getTokenClass() == Token.DIVIDE
                | currentToken.getTokenClass() == Token.REMAINDER
                | currentToken.getTokenClass() == Token.INTDIV) {

            termNode.addChild(currentToken);
            currentToken = lex.nextToken();
            factor(termNode);
        }

        System.out.println("EXITED TERM");

    }

    public void factor(TreeNode parent) {

        System.out.println("ENTERED FACTOR");

        TreeNode factorNode = new TreeNode("factor");
        parent.addChild(factorNode);

        if (currentToken.getTokenClass() == Token.INT | currentToken.getTokenClass() == Token.DECIMAL
                | currentToken.getTokenClass() == Token.IDENTIFIER) {
            id(factorNode);
            //currentToken = lex.nextToken(); //redundant kasi

        } else if (currentToken.getTokenClass() == Token.STRING) {
            string_stmt(factorNode);
            //currentToken = lex.nextToken();
        }else if (currentToken.getTokenClass() == Token.LPAR) {
            infix += " (";
            factorNode.addChild(currentToken);
            //exprStmtNode.add(new DefaultMutableTreeNode(currentToken.getLexeme()));

            currentToken = lex.nextToken();

            exprStmt(factorNode);

            if (currentToken.getTokenClass() == Token.RPAR) {
                infix += " )";

                factorNode.addChild(currentToken);

                //exprStmtNode.add(new DefaultMutableTreeNode(currentToken.getLexeme()));

                currentToken = lex.nextToken();

            } else {

                error("(", currentToken, factorNode);

            }
        }
        else {
            error("IDENTIFIER", currentToken, parent);
            System.out.println("NUMBER OR IDENTIFIER EXPECTED AT LINE:" + currentToken.getLineNumber());
        }

        System.out.println("EXITED FACTOR");

    }


    public void id(TreeNode parent) {
        System.out.println("ENTERED ID");
        TreeNode idNode = new TreeNode("ID");
        parent.addChild(idNode);
        int tokenClass = currentToken.getTokenClass();

        if (tokenClass == Token.DECIMAL | tokenClass == Token.INT) {
            //infix += " " + currentToken.getLexeme();
            //System.out.println("INFIX:"+infix);

            idNode.addChild(currentToken);
            currentToken = lex.nextToken();
        } else if (tokenClass == Token.IDENTIFIER) {

            //checheck niya kung nasa symbol table yung id
            // if (symbol.checkIdentifier(currentToken)) {

            //kukunin yung value ng id
            //infix += " " + symbol.getValue(currentToken.getLexeme()).value;
            currentToken = lex.nextToken();
            //kung string ba;
            if(currentToken.getTokenClass()==Token.CONCAT){
                currentToken = lex.nextToken();
                string_stmt(parent);
            }
            // } else {
            //       System.out.println("ERROR! - IDENTIFIER" + currentToken.getLexeme() + "NOT INITIALIZED");
            //     System.exit(0);
            //  }

        } else {
            error("ID", currentToken, idNode);
            System.out.println("INVALID ID");
        }
        System.out.println("EXITED ID");
    }

}

class ParseError extends Exception {
    public ParseError(String message) {
        super(message);
    }

    public ParseError() {
        super();
    }
}