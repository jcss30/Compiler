
import javax.swing.*;

public class Syntax {

    // yung susunod na token
    Token currentToken = null;

    // yung parse tree na gagawin
    JTree parseTree = null;
    TreeNode tree = new TreeNode("PROGRAM");

    LexicalAnalyzer lex = null;

    String infix = "";
    String output = "";


    //Symbol table
    Symbol_Table symbol_table = new Symbol_Table();


    String value="";


    //// Constructor
    public Syntax(String filename) {
        // initialize na yung Lexical Anal
        lex = new LexicalAnalyzer(filename);
    }

    public void program() throws ParseError {
        System.out.println("PARSING STARTED");

        // Create the main tree node called Program
        // programNode = new DefaultMutableTreeNode("Program");
        // Start of analysis
        currentToken = lex.nextToken();

        if (currentToken.getLexeme().equals("play")) {
            tree.addChild(currentToken);
            currentToken = lex.nextToken();

            progbody(tree);

            if (currentToken.getTokenClass() == Token.SLEEP) {

                tree.addChild(currentToken);
            }
            System.out.println("PARSE COMPLETE");
            System.out.println(tree.toString());

            symbol_table.showTable();
        } else {

            System.out.println("ERROR ON LINE " + currentToken.getLineNumber() + "\n PLAY EXPECTED");
            ErrorRecover();
        }
        //progbody(tree);
    }

    public void progbody(TreeNode parent) {
        System.out.println("ENTERED PROGBODY");

        //create a node
        TreeNode progBodyNode = new TreeNode("PROGBODY");
        parent.addChild(progBodyNode);

        statement(progBodyNode);
        while (currentToken.getTokenClass() == Token.EOS & currentToken.getTokenClass() != Token.SLEEP) {

            progBodyNode.addChild(currentToken);

            currentToken = lex.nextToken();

            if (currentToken.getTokenClass() == Token.SLEEP) {
                break;
            }

            statement(progBodyNode);

            if (currentToken == null) {
                //return new Token("ReservedWord", alphaBuffer, line, Token.SLEEP);
                currentToken = new Token("ReservedWord", "sleep", lex.line, Token.SLEEP);
                break;
            }

        }
        System.out.println("EXITED PROGBODY");
    }

    public void statement(TreeNode parent) {
        System.out.println("ENTERED STATEMENT");

        TreeNode stmtNode = new TreeNode("STATEMENT");
        parent.addChild(stmtNode);
        while (currentToken.getTokenClass() == Token.NL) {
            currentToken = lex.nextToken();

            if (currentToken == null) {
                break;
            }
        }
        //currentToken=lex.nextToken();

        switch (currentToken.getTokenClass()) {
            case Token.IDENTIFIER:
                Token tempToken = currentToken;
                currentToken = lex.nextToken();
                assignstmt(parent, tempToken,"");
                break;
            case Token.NUM:
            case Token.DECIMAL:
            case Token.LET:
            case Token.WORD:
            case Token.TRALSE:
            case Token.DEC:
                Token tempTokens = currentToken;
                currentToken = lex.nextToken();
                decstmt(stmtNode, tempTokens);
                break;
            case Token.STRING:
                //currentToken = lex.nextToken();
                string_stmt(stmtNode, "");
                break;
            case Token.IN:// IO
            case Token.NOISY:
                //currentToken = lex.nextToken();
                IO(stmtNode);
                break;
            case Token.IF:
                currentToken = lex.nextToken();
                ifStmt(stmtNode);
                break;
            case Token.UNTIL:
            case Token.SPIN:
                currentToken = lex.nextToken();
                spinstmt(stmtNode);
                break;
            case Token.SLEEP:
                break;

            default:

                System.out.println("INVALID TOKEN: " + currentToken.getTokenType());
                ErrorRecover();

        }
        System.out.println("EXITED STATEMENT");
    }

    public void assignstmt(TreeNode parent, Token token, String datatype) {
        System.out.println("ENTERED ASSIGN STMT");

        TreeNode assignstmt = new TreeNode("assignment");
        parent.addChild(assignstmt);
        parent.addChild(token);

        String id = token.getLexeme();

        infix = "";

        if (currentToken.getTokenClass() == Token.EQUAL) {

            assignstmt.addChild(currentToken);

            currentToken = lex.nextToken();

            if(currentToken.getTokenClass() == Token.TRUE || currentToken.getTokenClass() == Token.FALSE){

                //if(currentToken)
                booleanstmt(assignstmt);
            }
            else {
                exprStmt(assignstmt);
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
                //SymbolEntry symbolEntry = new SymbolEntry(id, 0, output);
                //this.symbolTable.addToTable(symbolEntry);
                return;
            }

            //lagay sa symbolEntry table
           // SymbolEntry symbolEntry = new SymbolEntry(id, 0, value);


            //this.symbolTable.addToTable(symbolEntry);
        }
        System.out.println("EXITED ASSIGNMENT");

    }

    public void spinstmt(TreeNode parent) {

        System.out.println("ENTERED SPIN");

        TreeNode spinNode = new TreeNode("Spin");
        parent.addChild(spinNode);
        int count = 0;

        //Go to SPIN statement
        if (currentToken.getTokenClass() == Token.LCURLYBRACE) {
            spinNode.addChild(currentToken);
            currentToken = lex.nextToken();

            statement(spinNode);

            while (currentToken.getTokenClass() == Token.EOS) {

                spinNode.addChild(currentToken);
                currentToken = lex.nextToken();
                //statements sa loob ni spin

                spinstmt(spinNode);

            }

            // }
            if (currentToken.getTokenClass() == Token.RCURLYBRACE) {
                spinNode.addChild(currentToken);
                currentToken = lex.nextToken();
                //UNTIL
                if (currentToken.getTokenClass() == Token.UNTIL) {
                    spinNode.addChild(currentToken);
                    currentToken = lex.nextToken();

                    //UNTIL{
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
                            }
                        }

                } else {
                    System.out.println("EXPECTED UNTIL AT LINE " + currentToken.getLineNumber());
                    ErrorRecover();
                }

            }

        } //else if noise word
        else if (currentToken.getTokenClass() == Token.NOISE) {
            spinNode.addChild(currentToken);
            currentToken = lex.nextToken();
            count++;
            if (count >= 1) {
                System.out.println("{ Expected at line " + currentToken.getLineNumber());
                ErrorRecover();
            } else {
                spinstmt(spinNode);
            }
        } else {
            System.out.println("{ EXPECTED AT LINE " + currentToken.getLineNumber());
            ErrorRecover();
        }
        /*
         if (currentToken.getTokenClass() == Token.SPIN) {
         spinNode.addChild(currentToken);
         currentToken = lex.nextToken();

         if (currentToken.getTokenClass() == Token.LCURLYBRACE) {
         spinNode.addChild(currentToken);
         currentToken = lex.nextToken();

         while (currentToken.getTokenClass() != Token.RCURLYBRACE) {
         statement(spinNode);
         }


         if (currentToken.getTokenClass() == Token.RCURLYBRACE) {
         System.out.println("****");
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
         }
         }
         } else {
         System.out.println("EXPECTED UNTIL AT LINE" + currentToken.getLineNumber());
         ErrorRecover();
         }
         }
         else
         {
         statement(spinNode);
         }


         if (currentToken.getTokenClass() == Token.RCURLYBRACE) {
         spinNode.addChild(currentToken);
         currentToken = lex.nextToken();

         }
         } else {
         //error("}", currentToken,spinNode);
         System.out.println("} EXPECTED AT LINE " + currentToken.getLineNumber());
         ErrorRecover();
         }

         } else {
         //error("{", currentToken,spinNode);
         System.out.println("{ EXPECTED AT LINE" + currentToken.getLineNumber());
         ErrorRecover();
         }
         */
        System.out.println("EXITED SPIN THIS");
    }

    public void string_stmt(TreeNode parent, String string) {
        System.out.println("ENTERED STRING STMT");
        TreeNode stringNode = new TreeNode("String");
        parent.addChild(stringNode);
        output = "";

        if (!string.equals("")) {
            output += StackExpr.postfixEvaluation(StackExpr.infixToPostfix(string));
        }
        if (currentToken.getTokenClass() == Token.IDENTIFIER
                | currentToken.getTokenClass() == Token.DEC
                | currentToken.getTokenClass() == Token.STRING
                | currentToken.getTokenClass() == Token.NUM) {

            switch (currentToken.getTokenClass()) {
                case Token.IDENTIFIER:
                    if (symbol_table.checkIdentifier(currentToken)) {

                        //add yung value ng variable sa outputstmt
                        output += symbol_table.getValue(currentToken.getLexeme()).value;
                        currentToken = lex.nextToken();


                    } else {
                        System.out.println("ERROR! - VARIABLE " + currentToken.getLexeme() + " NOT INITIALIZED");
                        System.exit(0);
                    }
                    break;
                case Token.DEC:
                case Token.INT:
                case Token.STRING:
                    output += currentToken.getLexeme();
                    break;
            }

            stringNode.addChild(currentToken);
            currentToken = lex.nextToken();
            //currentToken = lex.nextToken();

            while (currentToken.getTokenClass() == Token.CONCAT) {
                stringNode.addChild(currentToken);
                currentToken = lex.nextToken();
                if (currentToken.getTokenClass() == Token.IDENTIFIER|
                        currentToken.getTokenClass() == Token.DEC |
                        currentToken.getTokenClass() == Token.STRING |
                        currentToken.getTokenClass() == Token.INT) {


                    switch (currentToken.getTokenClass()) {
                        case Token.IDENTIFIER:
                            if (symbol_table.checkIdentifier(currentToken)) {
                                //add yung value ng variable sa outputstmt
                                output += symbol_table.getValue(currentToken.getLexeme()).value;
                            } else {
                                System.out.println("ERROR! - VARIABLE" + currentToken.getLexeme() + "NOT INITIALIZED");
                                System.exit(0);
                            }
                            break;
                        case Token.DEC:
                        case Token.INT:
                        case Token.STRING:
                            output += currentToken.getLexeme();
                            break;
                    }
                    stringNode.addChild(currentToken);
                    currentToken = lex.nextToken();
                }
            }

        } else {
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
        while (currentToken.getTokenClass() != Token.EOS) {
            if (currentToken.getTokenClass() == Token.IDENTIFIER) {

                SymbolEntry symbolEntry = new SymbolEntry(currentToken.getLexeme(),datatype.getTokenType(),"");

                symbol_table.addToTable(symbolEntry);

                declareNode.addChild(currentToken);
                currentToken = lex.nextToken();

                if (currentToken.getTokenClass() == Token.EQUAL) {

                    declareNode.addChild(currentToken);
                    assignstmt(declareNode, token, "");
                    currentToken = lex.nextToken();
                    //exprStmt(declareNode);
                }
                if (currentToken.getTokenClass() == Token.SEPARATOR) {
                    declareNode.addChild(currentToken);
                    currentToken = lex.nextToken();
                }
            } else {
                System.out.println("IDENTIFIER EXPECTED");
                ErrorRecover();
            }
        }

        System.out.println("Exited Declaration");
    }

    public void IO(TreeNode parent) {
        System.out.println("ENTERED IO");

        TreeNode IONode = new TreeNode("ioNode");
        parent.addChild(IONode);

        IONode.addChild(currentToken);
        //ioNode = new DefaultMutableTreeNode("IO");
        //parent.add(ioNode);

        currentToken = lex.nextToken();

        if (currentToken.getTokenClass() == Token.IDENTIFIER
                | currentToken.getTokenClass() == Token.STRING) {
            IONode.addChild(currentToken);
            string_stmt(IONode, "");
        } else if (currentToken.getTokenClass() == Token.INOP) {
            IONode.addChild(currentToken);
            currentToken = lex.nextToken();
            string_stmt(IONode, "");
        }
        //stmt(IONode);
        System.out.println("EXITED IO");

    }

    public void ifStmt(TreeNode parent) {
        System.out.println("ENTERED IF");
        TreeNode ifNode = new TreeNode("IF");

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

                    statement(ifNode);

                    if (currentToken.getTokenClass() == Token.EOS) {
                        
                        while (currentToken.getTokenClass() == Token.EOS) {
                            ifNode.addChild(currentToken);
                            currentToken = lex.nextToken();
                            // if (currentToken.getTokenClass() == Token.LCURLYBRACE) {
                            //   break;
                            // }

                            statement(ifNode);

                        }

                    } else {
                        System.out.println("; EXPECTED");
                        ErrorRecover();

                    }

                    if (currentToken.getTokenClass() == Token.RCURLYBRACE) {
                        ifNode.addChild(currentToken);
                        currentToken = lex.nextToken();

                        while (currentToken.getTokenClass() == Token.ELSEIF) {
                            ifNode.addChild(currentToken);
                            currentToken = lex.nextToken();
                            elsif(ifNode);
                        }

                        if (currentToken.getTokenClass() == Token.ELSE) {
                            ifNode.addChild(currentToken);
                            currentToken = lex.nextToken();
                            elsestmt(ifNode);
                        } else {
                            ifNode.addChild(currentToken);
                            currentToken = lex.nextToken();
                            statement(ifNode);
                        }

                    } else {
                        //error("}", currentToken,ifNode);
                        System.out.println("} EXPECTED AFTER STATEMENTS");
                        ErrorRecover();
                    }

                } else {
                    //error("{", currentToken,ifNode);
                    System.out.print("{ EXPECTED AT LINE " + currentToken.getLineNumber());
                    ErrorRecover();
                }

            } else {
                //error("]", currentToken,ifNode);
                System.out.println("] EXPECTED AT LINE " + currentToken.getLineNumber());
                ErrorRecover();
            }

        } else {
            //error("[", currentToken,ifNode);
            System.out.println("[ EXPECTED AT LINE " + currentToken.getLineNumber());
            ErrorRecover();
        }
        System.out.println("EXITED IF ");
    }

    public void elsestmt(TreeNode parent) {
        System.out.println("ENTERED ELSE");
        TreeNode elseNode = new TreeNode("ELSE");
        parent.addChild(elseNode);

        //elseNode = new DefaultMutableTreeNode("ELSE");
        // parent.add(elseNode);
        if (currentToken.getTokenClass() == Token.LCURLYBRACE) {
            elseNode.addChild(currentToken);
            //elseNode.add(new DefaultMutableTreeNode(currentToken.getLexeme()));
            currentToken = lex.nextToken();

            statement(elseNode);

            while (currentToken.getTokenClass() != Token.EOS) {
                elseNode.addChild(currentToken);
                currentToken = lex.nextToken();

                if (currentToken.getTokenClass() == Token.RCURLYBRACE) {
                    // System.out.println("UNEXPECTED END OF FILE");
                    break;
                }
                statement(elseNode);
            }

            if (currentToken.getTokenClass() == Token.RCURLYBRACE) {
                elseNode.addChild(currentToken);
                currentToken = lex.nextToken();
                statement(elseNode);
            } else {
                System.out.println("} EXPECTED AT LINE" + currentToken.getLineNumber());
                ErrorRecover();
            }
        } else {
            System.out.println("{ EXPECTED AT LINE" + currentToken.getLineNumber());
            ErrorRecover();
        }
        System.out.println("EXITED ELSE");

    }

    public void elsif(TreeNode parent) {
        System.out.println("ENTERED ELSIF");

        TreeNode elsifNode = new TreeNode("Elsif");
        parent.addChild(elsifNode);

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

                    statement(elsifNode);

                    while (currentToken.getTokenClass() == Token.EOS) {
                        elsifNode.addChild(currentToken);
                        currentToken = lex.nextToken();

                        if (currentToken.getTokenClass() == Token.RCURLYBRACE) {
                            //System.out.println("UNEXPECTED END OF FILE");
                            break;
                        }
                        statement(elsifNode);
                    }

                    if (currentToken.getTokenClass() == Token.RCURLYBRACE) {
                        elsifNode.addChild(currentToken);
                        currentToken = lex.nextToken();
                        if (currentToken.getTokenClass() == Token.ELSE) {
                            elsifNode.addChild(currentToken);
                            currentToken = lex.nextToken();
                            elsestmt(elsifNode);
                        } else {
                            elsifNode.addChild(currentToken);
                            currentToken = lex.nextToken();
                            elsif(elsifNode);
                        }

                    } else {
                        System.out.println("} EXPECTED AT LINE" + currentToken.getLineNumber());
                        ErrorRecover();
                    }

                } else {
                    System.out.println("{ EXPECTED AT LINE" + currentToken.getLineNumber());
                    ErrorRecover();
                }

            } else {
                System.out.println("] EXPECTED AT LINE" + currentToken.getLineNumber());
                ErrorRecover();
            }
        } else {
            System.out.println("[ EXPECTED AT LINE" + currentToken.getLineNumber());
            ErrorRecover();
        }

        System.out.println("EXITED ELSIF");
    }

    //Logical nad Relational
    public void booleanstmt(TreeNode parent) {
        System.out.println("ENTERED BOOLEAN STMT");
        TreeNode booleanNode = new TreeNode("BOOLEAN STMT");
        parent.addChild(booleanNode);
        if (currentToken.getTokenClass() == Token.LSQUAREBRACKET) {
            currentToken = lex.nextToken();
            booleanNode.addChild(currentToken);
            booleanstmt(booleanNode);
            if (currentToken.getTokenClass() == Token.RSQUAREBRACKET) {
                booleanNode.addChild(currentToken);
                currentToken = lex.nextToken();
            } else {
                System.out.println("] EXPECTED AT LINE " + currentToken.getLineNumber());
                ErrorRecover();
            }
        }

        if (currentToken.getTokenClass() == Token.NOT) {
            booleanNode.addChild(currentToken);
            currentToken = lex.nextToken();
        }

        if(currentToken.getTokenClass() == Token.TRUE || currentToken.getTokenClass() == Token.FALSE){
            booleanNode.addChild(currentToken);
            currentToken = lex.nextToken();
        }
        else {
            relational(booleanNode);
        }
        while (currentToken.getTokenClass() == Token.LOGICAL) {
            booleanNode.addChild(currentToken);
            currentToken = lex.nextToken();
            relational(booleanNode);

        }

        System.out.println("EXITED BOOLEAN STMT");
    }

    public void relational(TreeNode parent) {
        System.out.println("ENTERED RELATIONAL STMT");
        TreeNode relationalNode = new TreeNode("Relational");
        parent.addChild(relationalNode);

        if (currentToken.getTokenClass() == Token.TRUE
                | currentToken.getTokenClass() == Token.FALSE) {
            relationalNode.addChild(currentToken);
            currentToken = lex.nextToken();
        } else {
            exprStmt(relationalNode);
            if (currentToken.getTokenClass() == Token.GREATEROREQUAL
                    | currentToken.getTokenClass() == Token.GREATER
                    | currentToken.getTokenClass() == Token.LESSOREQUAL
                    | currentToken.getTokenClass() == Token.LESS
                    | currentToken.getTokenClass() == Token.ISEQUAL
                    | currentToken.getTokenClass() == Token.NOTEQUAL) {
                relationalNode.addChild(currentToken);
                currentToken = lex.nextToken();
                exprStmt(relationalNode);

            } else {
                System.out.println("RELATIONAL OPERATOR EXPECTED");
                ErrorRecover();
            }
        }
        System.out.println("EXITED RELATIONAL STMT");
    }

    public void exprStmt(TreeNode parent) {
        System.out.println("ENTERED EXPRESSION STATEMENT");

        TreeNode exprNode = new TreeNode("EXPRSTMT");
        parent.addChild(exprNode);
        if (currentToken.getTokenClass() == Token.LPAR) {
            infix += "(";
            exprNode.addChild(currentToken);

            currentToken = lex.nextToken();

            exprStmt(exprNode);

            if (currentToken.getTokenClass() == Token.RPAR) {
                infix += ")";
                exprNode.addChild(currentToken);
                currentToken = lex.nextToken();
            }
        }

        operand(exprNode);
        while (currentToken.getTokenClass() == Token.ADD | currentToken.getTokenClass() == Token.SUBTRACT
                | currentToken.getTokenClass() == Token.INCREMENT
                | currentToken.getTokenClass() == Token.DECREMENT) {
            if (currentToken.getTokenClass() == Token.ADD) infix += " +";
            else infix += " -";

            exprNode.addChild(currentToken);
            currentToken = lex.nextToken();
            operand(exprNode);
        }

        System.out.println("EXITED EXPRESSION STATEMENT");

    }

    public void operand(TreeNode parent) {
        System.out.println("ENTERED OPERAND STATEMENT");
        TreeNode operandNode = new TreeNode("Operand");
        parent.addChild(operandNode);

        term(operandNode);

        while (currentToken.getTokenClass() == Token.EXPONENT) {
            infix += "^";
            parent.addChild(currentToken);
            currentToken = lex.nextToken();
            term(operandNode);
        }
        System.out.println("EXITED OPERAND STATEMENT");
    }

    public void concat_stmt(TreeNode parent) {
        System.out.println("CONCAT");

        TreeNode concatNode = new TreeNode("Concat");

        parent.addChild(concatNode);

        factor(concatNode);

        while (currentToken.getTokenClass() == Token.EXPONENT) {
            parent.addChild(currentToken);
            currentToken = lex.nextToken();
            term(concatNode);
        }

        System.out.println("EXITED CONCAT");
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

        TreeNode factorNode = new TreeNode("Factor");
        parent.addChild(factorNode);

        if (currentToken.getTokenClass() == Token.INT | currentToken.getTokenClass() == Token.DECIMAL
                |currentToken.getTokenClass() == Token.IDENTIFIER)
        {
            value+=currentToken.getLexeme();


            infix+= " "+currentToken.getLexeme();


            factorNode.addChild(currentToken);
            currentToken = lex.nextToken();
        } else if(currentToken.getTokenClass()==Token.IDENTIFIER){

            currentToken=lex.nextToken();

            if(currentToken.getTokenClass()==Token.DECREMENT|currentToken.getTokenClass()==Token.INCREMENT){
                lex.nextToken();
            }



        } else if (currentToken.getTokenClass() == Token.STRING) {
            factorNode.addChild(currentToken);
            //currentToken = lex.nextToken();
            string_stmt(factorNode, "");
        } else if (currentToken.getTokenClass() == Token.TRUE | currentToken.getTokenClass() == Token.FALSE) {
            factorNode.addChild(currentToken);
            booleanstmt(factorNode);
        } else {
            System.out.println("NUMBER OR IDENTIFIER EXPECTED AT LINE:" + currentToken.getLineNumber());
            ErrorRecover();
        }

        System.out.println("EXITED FACTOR");

    }

    public void error(String message, Token token, TreeNode parent) {

        while (currentToken.getTokenClass() == Token.EOS
                & currentToken.getTokenClass() == Token.RCURLYBRACE
                & currentToken.getTokenClass() == Token.RPAR
                & currentToken.getTokenClass() == Token.RSQUAREBRACKET) {
            currentToken = lex.nextToken();
        }
        parent.addChild(currentToken);
        currentToken = lex.nextToken();
        System.out.println(message + " at line " + token.getLineNumber());
    }

    public void ErrorRecover() {
        try {
            while (currentToken.getTokenClass() != Token.NL) {
                currentToken = lex.nextToken();
            }
            currentToken = lex.nextToken();
            if (currentToken != null) {
                statement(tree);
            }
        } catch (Exception e) {

        }
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
