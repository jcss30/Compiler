import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import java.io.*;

public class Play {
    public static void main(String[] args) {
        String inFile = "Children.play";
        String outFile = "Children.wp";

       // try {
        //  inFile = getFile();


        parse(inFile);


        //scanner(inFile);

/*
        } catch (FileNotFoundException fnfe) {
            System.out.println("FILE NOT FOUND");
        } catch (InvalidFileException ife) {
            System.out.println("WRONG FILE EXTENSION");
        }*/



    }

    public static void parse(String filename) {
        try {
            Syntax jcss = new Syntax(filename);
            jcss.program();
            jcss.tree.createTree();

            //gagawa na ng tree
            viewTree(jcss.tree.node);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    //para sa pagview ng nagawang createTree
    public static void viewTree(DefaultMutableTreeNode node){
        Tree treeDisplay= new Tree(new JTree(node));
        treeDisplay.setVisible(true);
    }

    public static void scanner(String filename) {

        String outputfile = filename.replace(".play", ".wp");
        LexicalAnalyzer lexer = new LexicalAnalyzer(filename);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputfile));

            Token t;

            while ((t = lexer.nextToken()) != null) {
                writer.write(t.toString());
                writer.newLine();
            }

            writer.close();

            System.out.println("Done tokenizing file: " + filename);
            System.out.println("Output written in file: " + outputfile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getFile() throws FileNotFoundException, InvalidFileException {
        JFileChooser chooser = new JFileChooser();

        String inFile = "";

        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "ALL PLAY FILES", "play");
        chooser.setFileFilter(filter);
        //.

        JFrame jude = new JFrame();
        int returnVal = chooser.showOpenDialog(jude);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            inFile = chooser.getSelectedFile().getAbsolutePath();
        }
        if (!inFile.endsWith(".play")) throw new InvalidFileException();

        return inFile;
    }

}

class InvalidFileException extends Exception {

    InvalidFileException(String message) {
        super(message);
    }

    InvalidFileException() {
        super();
    }

}