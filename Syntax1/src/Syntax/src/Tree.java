import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;

public class Tree extends JFrame{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    //Tree Components
    JTree angTree = null;


    JScrollPane scrollPane = null;

    Tree(JTree thePuno){
        angTree = thePuno;
        scrollPane = new JScrollPane(angTree);

        this.setTitle("Parse Tree");
        this.setSize(400,400);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.add(scrollPane);
    }



}