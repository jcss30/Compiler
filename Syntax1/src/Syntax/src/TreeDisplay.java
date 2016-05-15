import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;

public class TreeDisplay extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//Tree Components
	JTree myTree = null;
	JScrollPane scrollPane = null;
	
	TreeDisplay(JTree theTree){
		myTree = theTree;
		scrollPane = new JScrollPane(myTree);
		
		this.setTitle("Parse Tree");
		this.setSize(400,400);
		//this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.add(scrollPane);
	}
}