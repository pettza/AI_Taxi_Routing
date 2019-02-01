import java.util.*;

//Graph class providing methods to determine adjacency/proximity of nodes
public class Graph
{
    //Data structure maintaining node adjacency lists
    private Map<Node, LinkedList<Node>> nodes;

    public Graph(String filename)
    {
        //Define a CSVReader for our file
        CSVReader reader = new CSVReader(filename);

        //Assisting structure for efficient search
        //The nodes get sorted by X and then by Y
        TreeMap<Double, TreeMap<Double, Node>> searchTree = new TreeMap<>();

        nodes = new Hashtable<>();

        long id = 0; //running id for nodes
        Node prev_node = null;
        LinkedList<Node> prev_neighbors = null;
        long prev_street_id = -1;
        CSVReader.ParsedLine fields;

        //While we haven't reached the end of the file
        while((fields = reader.readAndParseLine()) != null)
        {
            double x = fields.x;
            double y = fields.y;
            long street_id = fields.id;
            String name = fields.name;
            Node node = new Node(x, y, id, name); //Create new Node object from data

            LinkedList<Node> neighbors = new LinkedList<>();

            boolean found = false; //Flag to determine whether the node already exists
            TreeMap<Double, Node> subTree;


            if((subTree = searchTree.get(node.getX())) != null)
            {
                Node key;
                if((key = subTree.get(node.getY())) != null)
                {
                    //If the current node is on the same street as the previous one
                    if (street_id == prev_street_id)
                    {
                        //The two nodes are adjacent
                        neighbors.add(prev_node);
                        prev_neighbors.add(key);
                    }

                    //Add neighbors to the existing instance of the node
                    LinkedList<Node> value = nodes.get(key);
                    value.addAll(neighbors);
                    prev_node = key;
                    prev_neighbors = value;
                    found = true;
                }
                //If the node hasn't been found
                else
                {
                    //Add it to the data structure
                    subTree.put(node.getY(), node);
                }
            }
            //If no node with the same x coordinate has been found
            else
            {
                //Create new entry for the structure
                subTree = new TreeMap<>();
                subTree.put(node.getY(), node);
                searchTree.put(node.getX(), subTree);
            }


            if(!found)
            {
                //If the current node is on the same street as the previous one, the nodes are adjacent
                if (street_id == prev_street_id)
                {
                    neighbors.add(prev_node);
                    prev_neighbors.add(node);
                }

                //if the node hasn't been found, add it to the graph
                nodes.put(node, neighbors);
                prev_neighbors = neighbors;
                prev_node = node;
                id++;
            }

            prev_street_id = street_id;
        }
    }

    public final List<Node> getNeighbors(Node node)
    {
        return nodes.get(node);
    }

    //The method finds the graph node nearest to the input node
    public final Node findNearest(Node node)
    {
        Node best = null;
        double bestDist = Double.POSITIVE_INFINITY;
        for (Node key : nodes.keySet())
        {
            double dist = node.distanceFrom(key);
            if (dist < bestDist)
            {
                best = key;
                bestDist = dist;
            }

        }

        return best;
    }
}
