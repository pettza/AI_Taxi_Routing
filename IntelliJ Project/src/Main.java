import java.util.LinkedList;
import java.util.List;

public class Main
{
    public static void main(String[] args)
    {
        AStarNode n1 = new AStarNode(new Node(0.0,0.0,0), 0.0, 0.0, new LinkedList<>());
        AStarNode n2 = new AStarNode(new Node(1.0,1.0,1), 0.0, 0.0, n1);
        writeKML(n2, "test.txt");

        List<Node> taxis = readTaxis("Data\\taxis.csv");
        Graph graph = new Graph("Data\\nodes.csv");

        CSVReader reader = new CSVReader("Data\\client.csv");

        CSVReader.ParsedLine fields = reader.readAndParseLine();
        Node target = new Node(fields.x, fields.y, 0);
        reader.closeFile();

        AStarifier solver = new AStarifier();

        AStarNode n = solver.AStarify(graph, taxis, target);
        writeKML(n, "test.txt");
    }

    private static void writeKML(AStarNode node, String filename)
    {
        CSVWriter writer = new CSVWriter(filename);
        do {
            writer.writeln(node.getX(), node.getY());
            node = node.getPreviousNodes().get(0);
        } while (!node.getPreviousNodes().isEmpty());

        writer.closeFile();
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
