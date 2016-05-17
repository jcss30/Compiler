
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;


public class SyntaxAnalyzer {

    // yung susunod na token
    Token currentToken = null;

    // yung parseTree na gagawin
    TreeNode tree = new TreeNode("PROGRAM");

    // Initialize lex
    LexicalAnalyzer lex = null;

    //for computations
    String infix = "";


    //For OUTPUT
    String outputstmt = "";
    String value = "";
    // Constructor
    public SyntaxAnalyzer(String filename) {
        // initialize na yung Lexical Anal
        lex = new LexicalAnalyzer(filename);
    }


    //yung symboltable
    Symbol_Table symbol = new Symbol_Table();

    public void error(String message, Token token, TreeNode parent) {
        System.out.println("ERROR: " + message + "EXPECTED AT LINE " + token.getLineNumber());
        System.exit(1);
        while (currentToken.getTokenClass() != Token.EOS
                & currentToken.getTokenClass() != Token.RPAR
                & currentToken.getTokenClass() != Token.RCURLYBRACE
                & currentToken.getTokenClass() != Token.LPAR
                & currentToken.getTokenClass() != Token.LCURLYBRACE
                & currentToken.getTokenClass() != Token.EOF
                & currentToken.getTokenClass() != Token.SLEEP) {
            currentToken = lex.nextToken();
        }
        parent.addChild(currentToken);
        if(currentToken.getTokenClass() != Token.SLEEP){
            currentToken = lex.nextToken();
            stmt(parent);
        }

    }


    //gusto ko lang magkaerror :D
    public void program() throws ParseError {
        System.out.println("PARSING STARTED");

        // Create the main createTree node called Program
        // programNode = new DefaultMutableTreeNode("Program");
        // Start of analysis
        currentToken = lex.nextToken();


        if (currentToken.getLexeme().equals("play")) {
            // System.out.print("FOUND PLAY");
            tree.addChild(currentToken);

            //programNode.add(new DefaultMutableTreeNode("PLAY"));

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
            //programNode.add(new DefaultMutableTreeNode("PLAY"));
            symbol.showTable();

        } else {
            System.out.println("ERROR ON LINE " + currentToken.getLineNumber() + "\n PLAY EXPECTED");
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
        while (currentToken.getTokenClass() == Token.EOS | currentToken.getTokenClass() != Token.SLEEP) {

            if (currentToken.getTokenClass() == Token.EOS) {

                progBodyNode.addChild(currentToken);

                currentToken = lex.nextToken();

            } else {
                error(";", currentToken, progBodyNode);
            }

            if (currentToken.getTokenClass() == Token.SLEEP) break;
            stmt(progBodyNode);

        }
        System.out.println("EXITED PROGBODY");
    }

    public void stmt(TreeNode parent) {
        System.out.println("ENTERED STATEMENT");

        //stmtNode = new DefaultMutableTreeNode("Statement");
        //parent.add(stmtNode);


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
                Token tempTokens = currentToken;
                currentToken = lex.nextToken();
                decstmt(stmtNode, tempTokens);
                break;
            case Token.STRING:
                currentToken = lex.nextToken();
                string_stmt(stmtNode, "");
                break;
            case Token.IN:// IO

                //Token IOToken = currentToken;
                currentToken = lex.nextToken();
                string_stmt(stmtNode, "");
                break;

            case Token.NOISY:
                //Token OToken = currentToken;
                currentToken = lex.nextToken();
                string_stmt(stmtNode, "");
                break;
            case Token.IF:
                currentToken = lex.nextToken();
                if_Stmt(stmtNode);
                break;
            case Token.SPIN:
                currentToken = lex.nextToken();
                spin_stmt(stmtNode);
                break;///dsdasdasdasd
            case Token.LCURLYBRACE:
                currentToken = lex.nextToken();
                progbody(parent);
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

        System.out.println("ENTERED SPIN");

        TreeNode spinNode = new TreeNode("SPINSTMT");
        parent.addChild(spinNode);
        spinNode.addChild("spin");

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

                    if (currentToken.getTokenClass() == Token.LCURLYBRACE) {
                        spinNode.addChild(currentToken);
                        currentToken = lex.nextToken();

                        booleanstmt(spinNode);

                        if (currentToken.getTokenClass() == Token.EOS) {
                            spinNode.addChild(currentToken);
                            currentToken = lex.nextToken();

                        }

                        if (currentToken.getTokenClass() == Token.RCURLYBRACE) {
                            spinNode.addChild(currentToken);
                            currentToken = lex.nextToken();
                            stmt(spinNode);

                        } else {
                            // System.out.println("CLOSE PARENTHESIS EXPECTED at line " + currentToken.getLineNumber());
                            error(") EXPECTED ", currentToken, spinNode);
                        }

                    } else {
                        //System.out.println("( EXPECTED at LINE " + currentToken.getLineNumber());
                        error("( ", currentToken, spinNode);
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

        System.out.println("EXITED SPIN");
    }

    public void string_stmt(TreeNode parent, String strings) {
        System.out.println("ENTERED STRING STMT");
        TreeNode stringNode = new TreeNode("STRINGEXPR");
        parent.addChild(stringNode);
        outputstmt = "";
        if (!strings.equals("")) {
            outputstmt += StackExpr.postfixEvaluation(StackExpr.infixToPostfix(strings));
        }


        if (currentToken.getTokenClass() == Token.IDENTIFIER |
                currentToken.getTokenClass() == Token.DECIMAL |
                currentToken.getTokenClass() == Token.STRING |
                currentToken.getTokenClass() == Token.INT) {
            switch (currentToken.getTokenClass()) {
                case Token.IDENTIFIER:
                    if (symbol.checkIdentifier(currentToken)) {
                        //add yung value ng IDENTIFIER sa outputstmt
                        outputstmt += symbol.getValue(currentToken.getLexeme()).value;
                    } else {
                        System.out.println("ERROR! - IDENTIFIER" + currentToken.getLexeme() + "NOT INITIALIZED");
                        System.exit(0);
                    }
                    break;
                case Token.DECIMAL:
                case Token.INT:
                case Token.STRING:
                    outputstmt += currentToken.getLexeme();
                    break;
            }

            stringNode.addChild(currentToken);
            currentToken = lex.nextToken();

            while (currentToken.getTokenClass() == Token.CONCAT) {
                stringNode.addChild(currentToken);
                currentToken = lex.nextToken();
                if (currentToken.getTokenClass() == Token.IDENTIFIER |
                        currentToken.getTokenClass() == Token.DECIMAL |
                        currentToken.getTokenClass() == Token.STRING |
                        currentToken.getTokenClass() == Token.INT) {


                    switch (currentToken.getTokenClass()) {
                        case Token.IDENTIFIER:
                            if (symbol.checkIdentifier(currentToken)) {
                                //add yung value ng IDENTIFIER sa outputstmt
                                outputstmt += symbol.getValue(currentToken.getLexeme()).value;
                            } else {
                                System.out.println("ERROR! - IDENTIFIER" + currentToken.getLexeme() + "NOT INITIALIZED");
                                System.exit(0);
                            }
                            break;
                        case Token.DECIMAL:
                        case Token.INT:
                        case Token.STRING:
                            outputstmt += currentToken.getLexeme();
                            break;
                    }
                    stringNode.addChild(currentToken);
                    currentToken = lex.nextToken();
                }
            }

        }
        else if(currentToken.getTokenClass() == Token.INOP){
            stringNode.addChild(currentToken);
            currentToken = lex.nextToken();
            string_stmt(stringNode, "");
        }
        else {
            error("NUMBER OR STRING", currentToken, stringNode);
        }


        System.out.println("EXITED STRING STMT");

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

            if(currentToken.getTokenClass() == Token.TRUE | currentToken.getTokenClass() == Token.FALSE){
                relationalstmt(assignmentNode);
            }
            else {
                exprStmt(assignmentNode);
            }
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


        if (currentToken.getTokenClass() == Token.LPAR) {
            // infix+=" (";
            IONode.addChild(currentToken);
            currentToken = lex.nextToken();
            string_stmt(IONode, "");
            if (currentToken.getTokenClass() == Token.RPAR) {

                IONode.addChild(currentToken);
                currentToken = lex.nextToken();
            }
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
            currentToken = lex.nextToken();
            string_stmt(IONode, "");
            System.out.println("OUTPUT--------" + outputstmt);
        }
        else if(currentToken.getTokenClass() == Token.IDENTIFIER){
            IONode.addChild(currentToken);
            currentToken = lex.nextToken();
            string_stmt(IONode, "");
        }
        else {
            error("STRING EXPR", currentToken, IONode);
        }


        System.out.println("EXITED IO");

    }

    public void if_Stmt(TreeNode parent) {
        System.out.println("ENTERED IF ");

        TreeNode ifKungNode = new TreeNode("IFSTMT");

        parent.addChild(ifKungNode);
        ifKungNode.addChild(new Token("", "if", Token.IF, Token.IF));

        if (currentToken.getTokenClass() == Token.LSQUAREBRACKET) {
            ifKungNode.addChild(currentToken);
            currentToken = lex.nextToken();

            booleanstmt(ifKungNode);

            if (currentToken.getTokenClass() == Token.RSQUAREBRACKET) {
                ifKungNode.addChild(currentToken);
                currentToken = lex.nextToken();

                if (currentToken.getTokenClass() == Token.LCURLYBRACE) {
                    ifKungNode.addChild(currentToken);

                    currentToken = lex.nextToken();

                    stmt(ifKungNode);

                    while (currentToken.getTokenClass() == Token.EOS) {

                        ifKungNode.addChild(currentToken);

                        currentToken = lex.nextToken();
                        if (currentToken.getTokenClass() == Token.RCURLYBRACE) break;
                        stmt(ifKungNode);

                    }

                    if (currentToken.getTokenClass() == Token.RCURLYBRACE) {
                        ifKungNode.addChild(currentToken);
                        currentToken = lex.nextToken();

                        while (currentToken.getTokenClass() == Token.ELSEIF) {

                            currentToken = lex.nextToken();
                            elsif(ifKungNode);
                        }

                        if (currentToken.getTokenClass() == Token.ELSE) {
                            //ifKungNode.addChild(currentToken);
                            currentToken = lex.nextToken();
                            else_stmt(ifKungNode);
                        }


                    } else {
                        error("}", currentToken, ifKungNode);
                        System.out.println("} EXPECTED AFTER STATEMENTS A");
                    }

                } else {
                    error("{", currentToken, ifKungNode);
                    System.out.print("{ EXPECTED AT LINE " + currentToken.getLineNumber());
                }

            } else {
                error(")", currentToken, ifKungNode);
                System.out.println(") EXPECTED AT LINE " + currentToken.getLineNumber());
            }

        } else {
            error("(", currentToken, ifKungNode);
            System.out.println("( EXPECTED AT LINE " + currentToken.getLineNumber());
        }
        System.out.println("EXITED IF KUNG");
    }

    public void else_stmt(TreeNode parent) {//else if
        System.out.println("ENTERED ELSE");
        TreeNode elseNode = new TreeNode("ELSE");
        parent.addChild(elseNode);
        elseNode.addChild("else");
        //elsifNode = new DefaultMutableTreeNode("ELSIF");
        // parent.add(elsifNode);

        if (currentToken.getTokenClass() == Token.LCURLYBRACE) {
            elseNode.addChild(currentToken);
            //elsifNode.add(new DefaultMutableTreeNode(currentToken.getLexeme()));
            currentToken = lex.nextToken();

            stmt(elseNode);

            while (currentToken.getTokenClass() == Token.EOS) {
                elseNode.addChild(currentToken);
                currentToken = lex.nextToken();

                if (currentToken.getTokenClass() == Token.RCURLYBRACE)break;
                stmt(elseNode);
            }

            if (currentToken.getTokenClass() == Token.RCURLYBRACE) {
                elseNode.addChild(currentToken);
                //elsifNode.add(new DefaultMutableTreeNode(currentToken.getLexeme()));
                currentToken = lex.nextToken();
                stmt(elseNode);
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

                        if (currentToken.getTokenClass() == Token.ELSE) {
                            elsifNode.addChild(currentToken);
                            currentToken = lex.nextToken();
                            else_stmt(elsifNode);
                        } else {
                            elsifNode.addChild(currentToken);
                            currentToken = lex.nextToken();
                            elsif(elsifNode);
                        }

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

        if (currentToken.getTokenClass() == Token.LPAR) {
            booleanStmtNode.addChild(currentToken);
            currentToken = lex.nextToken();
            //booleanStmtNode.add(new DefaultMutableTreeNode(currentToken.getLexeme()));
            booleanstmt(booleanStmtNode);

            if (currentToken.getTokenClass() == Token.RPAR) {
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

        if (currentToken.getTokenClass() == Token.LPAR) {
            relationalStmtNode.addChild(currentToken);
            currentToken = lex.nextToken();
            //booleanStmtNode.add(new DefaultMutableTreeNode(currentToken.getLexeme()));
            relationalstmt(relationalStmtNode);

            if (currentToken.getTokenClass() == Token.RPAR) {
                //booleanStmtNode.add(new DefaultMutableTreeNode(currentToken.getLexeme()));
                relationalStmtNode.addChild(currentToken);
                currentToken = lex.nextToken();
            } else {

                error(")", currentToken, relationalStmtNode);
                System.out.println(") EXPECTED AT LINE " + currentToken.getLineNumber());
            }
        }

        if (currentToken.getTokenClass() == Token.TRUE
                | currentToken.getTokenClass() == Token.FALSE) {
            relationalStmtNode.addChild(currentToken);
            currentToken = lex.nextToken();
        } else {
            exprStmt(relationalStmtNode);
            if (currentToken.getTokenClass() == Token.GREATEROREQUAL
                    | currentToken.getTokenClass() == Token.GREATER
                    | currentToken.getTokenClass() == Token.LESSOREQUAL
                    | currentToken.getTokenClass() == Token.LESS
                    | currentToken.getTokenClass() == Token.ISEQUAL
                    | currentToken.getTokenClass() == Token.NOTEQUAL) {
                relationalStmtNode.addChild(currentToken);
                currentToken = lex.nextToken();
                exprStmt(relationalStmtNode);

            } else {

                error("RELATIONAL OP", currentToken, relationalStmtNode);
                System.out.println("RELATIONAL OPERATOR EXPECTED");
            }
        }
        System.out.println("EXITED RELATIONAL STMT");
    }

    public void exprStmt(TreeNode parent) {
        System.out.println("ENTERED EXPRESSION STATEMENT");

        TreeNode exprStmtNode = new TreeNode("EXPRSTMT");

        parent.addChild(exprStmtNode);

        if (currentToken.getTokenClass() == Token.LPAR) {
            infix += " (";
            exprStmtNode.addChild(currentToken);
            //exprStmtNode.add(new DefaultMutableTreeNode(currentToken.getLexeme()));

            currentToken = lex.nextToken();

            exprStmt(exprStmtNode);

            if (currentToken.getTokenClass() == Token.RPAR) {
                infix += " )";

                exprStmtNode.addChild(currentToken);

                //exprStmtNode.add(new DefaultMutableTreeNode(currentToken.getLexeme()));

                currentToken = lex.nextToken();

            } else {

                error("(", currentToken, exprStmtNode);

            }
        }

        operand(exprStmtNode);
        while (currentToken.getTokenClass() == Token.ADD | currentToken.getTokenClass() == Token.SUBTRACT) {
            if (currentToken.getTokenClass() == Token.ADD) infix += " +";
            else infix += " -";

            exprStmtNode.addChild(currentToken);
            currentToken = lex.nextToken();
            operand(exprStmtNode);
        }

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

            switch (currentToken.getTokenClass()) {
                case Token.MULTIPLY:
                    infix += " *";
                    break;

                case Token.DIVIDE:
                    infix += " *";
                    break;

                case Token.REMAINDER:
                    infix += " *";
                    break;

                case Token.INTDIV:
                    infix += " *";
                    break;
            }


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
            string_stmt(factorNode, infix);
           // currentToken = lex.nextToken();
        } else {
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
            infix += " " + currentToken.getLexeme();

            idNode.addChild(currentToken);
            currentToken = lex.nextToken();
        }
        else if(currentToken.getTokenClass() == Token.TRUE | currentToken.getTokenClass() == Token.FALSE){
            idNode.addChild(currentToken);
            currentToken = lex.nextToken();
            relationalstmt(idNode);
        }
        else if (tokenClass == Token.IDENTIFIER) {

            //checheck niya kung nasa symbol table yung id
            if (symbol.checkIdentifier(currentToken)) {

                //kukunin yung value ng id
                infix += " " + symbol.getValue(currentToken.getLexeme()).value;
                currentToken = lex.nextToken();
            } else {
                System.out.println("ERROR! - IDENTIFIER " + currentToken.getLexeme() + " NOT INITIALIZED");
                System.exit(0);
            }

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
