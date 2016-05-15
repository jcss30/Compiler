import java.io.*;
import javax.swing.JOptionPane;

public class IOReader {

    public static String IOReader() throws FileNotFoundException, IOException {
        String fileName = JOptionPane.showInputDialog ( "Enter the path of the file" ); 
        String str = "";
       
       
       String [] fileType= fileName.split("\\.");
       if(fileType[1].equals("play"))
       {
           File inFile = new File(fileName);
       
        BufferedReader bf = new BufferedReader(new FileReader(inFile));
        String buffer;
        str = "";

        while ((buffer = bf.readLine()) != null) {
            str += buffer + "\n";
        }

        bf.close();
       }
       else
           System.out.println("invalid file type");

        return str;
    }
     

    public static void IOWriter(Token[] t) throws IOException {

        File outFile = new File("C:/Users/Jerard/Documents/NetBeansProjects/Analyzer/Children.wp");

        BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
        int index = 0;
        
        while (t[index] != null) {
            writer.write(t[index].toString());
            writer.newLine();
            index++;
        }

        writer.close();

    }

}
