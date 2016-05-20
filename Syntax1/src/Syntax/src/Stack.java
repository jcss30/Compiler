public class Stack<T> {
    private int top = 0;
    private final static int stackMax = 100;
    private Object[] stk = new Object[stackMax + 1];

    public Stack() {
    }

// other methods

    public void clear()
    {
        top=0;
    }
    public boolean isEmpty()
    {
        if (top==0)
            return true;
        else return false;
    }
    public T pop()
    {
        if(isEmpty())
        {
            System.out.println("System Underflow Error");
            return null;
        }
        else
        {
            top=top-1;
            return  (T) stk[top+1];
        }
    }
    public T peek()
    {
        if(isEmpty())
        {
            System.out.print("Stack is Empty");
            return null;
        }
        else
            return(T) stk[top].toString();
    }
    public void push(Object el)
    {
        if (top==stackMax)
        {
            System.out.println("Stack push overflow error");
        }
        else
        {
            top=top+1;
            stk[top]=el;
        }
    }

}