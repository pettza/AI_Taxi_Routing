import java.util.List;
import java.util.Map;
import java.util.Hashtable;
import java.util.LinkedList;

public class Graph
{
    private Map<Node, LinkedList<Node>> nodes;

    public Graph(String filename)
    {
        CSV_Reader reader = new CSV_Reader(filename);

        nodes = new Hashtable<>();
        long id = 0;
        Node prev_node = null;
        LinkedList<Node> prev_neighbors = null;
        long prev_street_id = -1;
        String[] fields;
        while((fields = reader.readLine()) != null)
        {
            try {
                double x = Double.parseDouble(fields[0]);
                double y = Double.parseDouble(fields[1]);
                long street_id = Long.parseLong(fields[2]);
                Node node = new Node(x, y, id);

                LinkedList<Node> neighbors = new LinkedList<>();

                if (street_id == prev_street_id)
                {
                    neighbors.add(prev_node);
                    prev_neighbors.add(node);
                }

                boolean found = false;
                for (Node key : nodes.keySet())
                {
                    if(key.GetX() == node.GetX() && key.GetY() == node.GetY())
                    {
                        LinkedList<Node> value = nodes.get(key);
                        value.addAll(neighbors);
                        prev_node = key;
                        prev_neighbors = value;
                        found = true;
                        break;
                    }
                }

                if(!found)
                {
                    nodes.put(node, neighbors);
                    prev_neighbors = neighbors;
                    prev_node = node;
                    id++;
                }

                prev_street_id = street_id;

            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
    }

    public final List<Node> getNeighbors(Node node)
    {
        return nodes.get(node);
    }

    public final Node findNearest(Node node)
    {
        Node best = null;
        double bestDist = Double.POSITIVE_INFINITY;
        for (Node key : nodes.keySet())
        {
            double dist = node.distance_from(key);
            if (dist < bestDist)
            {
                best = key;
                bestDist = dist;
            }

        }

        return best;
    }
}
