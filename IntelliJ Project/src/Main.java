import java.awt.*;
import java.io.*;
import java.lang.management.BufferPoolMXBean;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Main
{
    public static void main(String[] args)
    {
        List<Node> taxis = readTaxis("Data\\taxis.csv");
        Graph graph = new Graph("Data\\nodes.csv");

        CSVReader reader = new CSVReader("Data\\client.csv");

        CSVReader.ParsedLine fields = reader.readAndParseLine();
        Node target = new Node(fields.x, fields.y, 0);
        reader.closeFile();

        AStarifier solver = new AStarifier();

        AStarNode n = solver.AStarify(graph, taxis, target);
        writeKML(n, "result-KML.kml");
    }


    private static void writeKML(AStarNode node, String filename)
    {
        BufferedWriter writer;
        try {
             writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename)));

            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<kml xmlns=\"http://earth.google.com/kml/2.1\">\n" +
                    "    <Document>\n" +
                    "        <name>Taxi Routes</name>\n");

            printPaths(node, "", writer);

            writer.write("    </Document>\n" +
                    "</kml>\n");

            writer.close();
        }catch(FileNotFoundException e) {
            e.printStackTrace();
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    //Prints all possible path to node in KML format
    private static void printPaths(AStarNode node, String str, BufferedWriter writer)
    {
        String newStr = node + "\n" + str;

        //If there is no predecessor, print the path
        if(node.getPreviousNodes().isEmpty())
        {
            try {
                Random rnd = new Random();
                String color = String.format("ff%02x%02x%02x", rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

                writer.write("<Style id=\"" + color + "\">\n" +
                        "            <LineStyle>\n" +
                        "                <color>" + color + "</color>\n" +
                        "                <width>4</width>\n" +
                        "            </LineStyle>\n" +
                        "        </Style>\n" +
                        "        <Placemark>\n" +
                        "            <name>Taxi " + node.getId() + "</name>\n" +
                        "            <styleUrl>#" + color + "</styleUrl>\n" +
                        "            <LineString>\n" +
                        "                <altitudeMode>relative</altitudeMode>\n" +
                        "                <coordinates>\n");
                writer.write(newStr);
                writer.write("                </coordinates>\n" +
                        "            </LineString>\n" +
                        "        </Placemark>\n");
            }   catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
        {
            //Add node to the path string and recursively call printPaths for each predecessor
            for ( AStarNode prev : node.getPreviousNodes())
            {
                printPaths(prev, newStr, writer);
            }
        }
    }

    private static List<Node> readTaxis(String filename)
    {
        CSVReader reader = new CSVReader(filename);
        CSVReader.ParsedLine fields;
        LinkedList<Node> taxis = new LinkedList<>();
        while((fields = reader.readAndParseLine()) != null)
        {
            Node node = new Node(fields.x, fields.y, fields.id);
            taxis.add(node);
        }

        reader.closeFile();

        return taxis;
    }


}
