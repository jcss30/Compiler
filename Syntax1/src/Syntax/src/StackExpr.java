import static java.lang.Math.pow;
import javax.swing.JOptionPane;


public class StackExpr {


    public static void main(String args[])
    {
        int t=(Integer.parseInt(JOptionPane.showInputDialog("input value for t")));
        if (t==1)
        {
            String s= JOptionPane.showInputDialog("INPUT POSTFIX EXPRESSION");
            System.out.println(s);
            System.out.println(postfixToInfix(s));

        }
        else if(t==2)
        {
            String s= JOptionPane.showInputDialog("INPUT POSTFIX EXPRESSION");
            System.out.println(s);
            System.out.println(postfixEvaluation(s));
        }
        else if (t==3)
        {

        }
        else if (t==4)
        {
            String s= JOptionPane.showInputDialog("INPUT INFIX EXPRESSION");
            System.out.println("infix: "+s);
            System.out.println("postfix: "+infixToPostfix(s));
            System.out.println("eval: "+postfixEvaluation(infixToPostfix(s)));


        }
        else
        {
            String s= JOptionPane.showInputDialog("INPUT INFIX EXPRESSION");
            System.out.println(s);


            System.out.println(infixToPostfix(prefixToInfix(s)));
        }



    }

    public static String infixToPostfix(String w)
    {
        Object tokenz[]=w.split(" ");
        Stack s = new Stack();
        String u="";
        for(int r=0; r<tokenz.length; r++)
        {
            if (!(tokenz[r].equals("+")||
                    tokenz[r].equals("-")||
                    tokenz[r].equals("/")||
                    tokenz[r].equals("*")||
                    tokenz[r].equals("^")||
                    tokenz[r].equals("(")||
                    tokenz[r].equals(")")))
            {
                u=u+" "+ tokenz[r];
            }
            else  if(!s.isEmpty() &&tokenz[r].toString().equals(")"))
            {
                while(!s.peek().toString().equals("("))
                {u=u+" "+s.pop().toString();}
                s.pop();
            }
            else  if(!s.isEmpty() &&tokenz[r].toString().equals("("))
            {
                s.push(tokenz[r].toString());
            }
            else
            {
                while(!s.isEmpty() &&
                        incomingPrecedence(tokenz[r].toString())<inStackPrecedence(s.peek().toString()))
                {

                    u=u+" "+s.pop().toString();

                }
                s.push(tokenz[r]);
            }
        }
        while(!s.isEmpty())
        {

            u=u+" "+s.pop().toString();

        }

        return u;
    }

    public static String postfixToInfix(String w)
    {
        Object tokenz[]=w.split(" ");
        Stack s = new Stack();
        for(int r=0; r<tokenz.length; r++)
        {
            if ((tokenz[r].equals("+")||
                    tokenz[r].equals("-")||
                    tokenz[r].equals("/")||
                    tokenz[r].equals("*")||
                    tokenz[r].equals("^")))
            {
                String B=s.pop().toString();
                String A=s.pop().toString();
                s.push(("( "+ A+" "+tokenz[r]+" "+ B+ " )"));
            }
            else
            {
                s.push(tokenz[r]);
            }
        }
        return s.pop().toString();
    }


    public static String postfixEvaluation(String w)
    {
        Object tokenz[]=w.split(" ");
        Stack s = new Stack();
        for(int r=0; r<tokenz.length; r++)
        {
            if (tokenz[r].equals("+")||
                    tokenz[r].equals("-")||
                    tokenz[r].equals("/")||
                    tokenz[r].equals("*")||
                    tokenz[r].equals("^"))
            {
                double B=Double.parseDouble(s.pop().toString());
                double A=Double.parseDouble(s.pop().toString());
                s.push(evaluate(A,tokenz[r].toString(),B));

            }
            else
            {
                s.push(tokenz[r]);
            }
        }
        return  s.pop().toString();
    }

    //TODO
    public static double evaluate(double A, String c,double B)
    {
        double sagot=0;
        switch(c)
        {
            case "+": sagot = A+B; break;
            case "-": sagot=A-B; break;
            case "*":sagot=A*B;break;
            case "/":sagot=A/B;break;
            case "^":sagot=pow(A,B);   break;
        }
        return sagot;
    }


    public static String prefixToInfix(String s)
    {
        String tokenz[]=s.split(" ");
        Stack s1 = new Stack();
        for(int r=(tokenz.length-1); r>=0; r--)
        {
            if (!(tokenz[r].equals("+")||
                    tokenz[r].equals("-")||
                    tokenz[r].equals("/")||
                    tokenz[r].equals("*")||
                    tokenz[r].equals("^")))
            {
                s1.push(tokenz[r]);
            }
            else
            {
                String A=s1.pop().toString();
                String B=s1.pop().toString();

                s1.push("( "+A+" "+tokenz[r]+" "+B+" )");

            }

        }
        return s1.pop().toString();
    }


    public static String infixToPrefix(String e)
    {
        Stack optr = new Stack();
        Stack rev = new Stack();
        String tokenz[]= e.split(" ");
        String sagot="";
        for(int r=(tokenz.length-1); r>=0; r--)
        {
            if (!(tokenz[r].equals("+")||
                    tokenz[r].equals("-")||
                    tokenz[r].equals("/")||
                    tokenz[r].equals("*")||
                    tokenz[r].equals("^")||
                    tokenz[r].equals(")")||
                    tokenz[r].equals("(")))
            {
                rev.push(tokenz[r]);
            }
            else
            {
                while(!optr.isEmpty()&&incomingPrecedence(tokenz[r])<incomingPrecedence(optr.peek().toString()))
                {
                    rev.push(optr.pop());
                }
                optr.push(tokenz[r]);
            }
        }
        while(!optr.isEmpty())
        {
            rev.push(optr.pop());
        }
        while(!rev.isEmpty())
        {
            if(rev.peek().equals(")")||rev.peek().equals("("))
            {
                rev.pop();
            }
            else
            {
                sagot = sagot+" "+rev.pop().toString();
            }
        }
        return sagot;
    }
    public static int incomingPrecedence(String x)
    {
        int prec=0;
        switch(x)
        {
            case "+":;
            case "-": prec=1;break;
            case "*":;
            case "/":prec=3;break;
            case "(":prec =0;break;
            case ")":prec =9; break;
            case "^":prec=6;break;
            default: prec=0;

        }
        return prec;
    }
    public static int inStackPrecedence(String x)
    {
        int prec=0;
        switch(x)
        {
            case "+":;
            case "-": prec=2;break;
            case "*":;
            case "/":prec=4;break;
            case "(":prec =0;break;
            case ")":prec =0; break;
            case "^":prec=5;break;
            default: prec=0;

        }
        return prec;
    }


}