import java.io.*;

public class CSVReader {

    private BufferedReader br;


    public CSVReader(String filename)
    {
        try {

            br = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "ISO-8859-7"));
            br.readLine();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            closeFile();
        }
    }

    //Reads line and splits it on commas
    public String[] readLine()
    {
        String[] fields = null;
        try {
            String line = br.readLine();
            if (line != null || line.isEmpty()) {
                fields = line.split(",");
            }
        } catch (IOException e) {
            br.close();
            e.printStackTrace();
        } finally {
            return fields;
        }
    }

    //Class used in readAndParseLine
    public class ParsedLine
    {
        public double x, y;
        public long id;
        public String name;

        public ParsedLine(String[] fields)
        {
            int i = 0;
            try {
                x = Double.parseDouble(fields[i]);
                i++;
                y = Double.parseDouble(fields[i]);
                i++;
                id = Long.parseLong(fields[i]);
                i++;
                name = fields[i];
            } catch (IndexOutOfBoundsException e) {
                if(i == 2)
                {
                    id = 0;
                }
                name = new String();
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
