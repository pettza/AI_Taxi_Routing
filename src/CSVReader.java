import java.io.*;

public class CSVReader {

    private BufferedReader br;


    public CSVReader(String filename) throws IOException
    {
        br = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "ISO-8859-7"));
        br.readLine();
    }

    //Reads line and splits it on commas
    public String[] readLine()
    {
        String[] fields = null;
        try {
            String line = br.readLine();
            if (line != null || line.isEmpty()) {
                fields = (line +  ",empty").split(",");
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
