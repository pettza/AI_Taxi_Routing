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

        CSV_Reader reader = new CSV_Reader("Data\\client.csv");

        CSV_Reader.ParsedLine fields = reader.readAndParseLine();
        Node target = new Node(fields.x, fields.y, 0);
        reader.closeFile();

        AStarifier solver = new AStarifier();

        AStarNode n = solver.AStarify(graph, taxis, target);
        writeKML(n, "test.txt");
    }

    private static void writeKML(AStarNode node, String filename)
    {
        CSV_Writer writer = new CSV_Writer(filename);
        do {
            writer.writeln(node.GetX(), node.GetY());
            node = node.getPreviousNodes().get(0);
        } while (!node.getPreviousNodes().isEmpty());

        writer.closeFile();
    }


    private static List<Node> readTaxis(String filename)
    {
        CSV_Reader reader = new CSV_Reader(filename);
        CSV_Reader.ParsedLine fields;
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
