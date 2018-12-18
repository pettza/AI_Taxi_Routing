import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class CSVWriter
{
    private BufferedWriter bw;

    public CSVWriter(String filename)
    {
        try {
            bw = new BufferedWriter(new FileWriter(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeln(double x, double y)
    {
        try {
            bw.write(x + "," + y + ",0\n" );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeFile()
    {
        try {
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
