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

    public class ParsedLine
    {
        public double x, y;
        public long id;

        public ParsedLine(String[] fields)
        {
            try {
                x = Double.parseDouble(fields[0]);
                y = Double.parseDouble(fields[1]);
                id = Long.parseLong(fields[2]);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
    }

    public ParsedLine readAndParseLine()
    {
        String[] fields = readLine();
        if (fields == null)
        {
            return  null;
        }

        return new ParsedLine(fields);
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
