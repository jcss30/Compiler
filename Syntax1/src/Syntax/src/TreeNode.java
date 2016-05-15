import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;

public class TreeNode extends Object {

    public DefaultMutableTreeNode node;
    protected String key;
    protected Token token;
    protected TreeNode left, right;
    protected JTree jtree = new JTree(node);
    protected ArrayList<TreeNode> children =  new ArrayList<>();
    protected int level;

    public TreeNode(Token token) {
        this.token = token;
        node = new DefaultMutableTreeNode(token.getLexeme());
    }
    public TreeNode(String key){
        this.key = key;
        node = new DefaultMutableTreeNode(key);
    }

    public TreeNode(Token token, Token[] tokens) {
        this.token = token;
        for(Token tempToken: tokens){
            children.add(new TreeNode(tempToken));
        }
    }

    public  static void main(String args[]){

        TreeNode a= new TreeNode("A");
        TreeNode b= new TreeNode(new Token("LOL","B",1,1));
        TreeNode c= new TreeNode(new Token("LOL","C",1,1));
        TreeNode d= new TreeNode(new Token("LOL","D",1,1));
        TreeNode x= new TreeNode("PLAY");
        TreeNode gq = new TreeNode(new Token("PLAY","PLAY",1,1));
        gq.addChildren(new TreeNode[]{a,b});
        a.addChild(c);
        b.addChild(d);
        b.addChild(x);

        gq.createTree();

        System.out.println(gq.toString());
        Tree treeviewer = new Tree(gq.jtree);
        treeviewer.setVisible(true);



        //this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


    }

    public ArrayList<TreeNode> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<TreeNode> children) {
        this.children = children;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void addChild(TreeNode treeNode){
        children.add(treeNode);
    }

    public void addChild(String string){
        children.add(new TreeNode(string));
    }

    public void addChild(Token token){
        children.add(new TreeNode(token));
    }

    public void addChildren(TreeNode[] treeNodes){
        for(TreeNode treeNode: treeNodes){
            children.add(treeNode);
        }
    }

    public Token getToken() {
        return token;
    }


    //kuhain ang string ng createTree
    public String toString() {
        String info="[";
        if(token!=null)

            info+=token.getLexeme();
        else
            info+=key;

        if(!children.isEmpty()){
            for (TreeNode treeNode : children) {
                node.add(treeNode.node);
                info+= treeNode.toString();
            }
        } else {
            //details += "]";
        }
        info += "]";
        return info;
    }

    public void createTree() {
        //sa paggawa ng createTree
        if(!children.isEmpty()){
            for (TreeNode treeNode : children) {
                node.add(treeNode.node);

            }
        }

    }

    public JTree getJTree(){
        return new JTree(node);
    }
    
    /*
    public boolean delete(String pinapabura, TreeNode parent)
    {
            if (pinapabura.compareTo(token)<0) {
                  if (left != null)
                        return left.delete(pinapabura, this);
                  else
                        return false;
            } 
            else if (pinapabura.compareTo(token)>0)
            {
                  if (right != null)
                        return right.delete(pinapabura, this);
                  else
                        return false;
            } 
            else 
            {
                  if (left != null && right != null) 
                  {
                        token = left.maxValue();
                        left.delete(token, this);
                  } 
                  else if (parent.left == this) 
                  {
                        parent.left = (left != null) ? left : right;
                  } else if (parent.right == this) {
                        parent.right = (left != null) ? left : right;
                  }
                  return true;
            }
      }
     public String maxValue() {
            if (right == null)
                  return token;
            else
                  return right.maxValue();
      }
    */
}