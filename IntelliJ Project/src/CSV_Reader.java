import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class CSV_Reader {

    private BufferedReader br;


    public CSV_Reader(String filename)
    {


        try {

            br = new BufferedReader(new FileReader(filename));
            br.readLine();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            closeFile();
        }
    }

    public String[] readLine()
    {
        String[] fields = null;
        try {
            String line = br.readLine();
            if (line != null) {
                fields = line.split(",");
            }
        } catch (IOException e) {
            br.close();
            e.printStackTrace();
        } finally {
            return fields;
        }
    }

    public void closeFile()
    {
        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
