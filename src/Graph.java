import java.util.*;
import java.io.IOException;

import com.ugos.jiprolog.engine.*;

//Graph class providing methods to determine adjacency/proximity of nodes
public class Graph
{
    private JIPEngine engine;

    private class LineInfo
    {
        public long id;
        public int oneway;
        public boolean lit;
        public int maxspeed;
        public boolean toll;

        LineInfo(long id_v, int oneway_v, boolean lit_v, int maxspeed_v, boolean toll_v)
        {
            id = id_v;
            oneway = oneway_v;
            lit = lit_v;
            maxspeed = maxspeed_v;
            toll = toll_v;
        }
    }

    public Graph(String lineInfoFile, String nodeInfoFile) throws IOException, JIPEvaluationException
    {
        engine = new JIPEngine();
        engine.consultFile("prolog\\LinesNodesRules.pl");
        readLineInfo(lineInfoFile);

        //Define a CSVReader for our node file
        CSVReader reader = new CSVReader(nodeInfoFile);

        JIPTermParser parser = engine.getTermParser();
        String[] fields;

        Node prev_node = null;
        long prev_line_id = -1;
        if((fields = reader.readLine()) != null)
        {
            prev_node = readNode(fields);
            prev_line_id = Long.parseLong(fields[3]);
        }

        //While we haven't reached the end of the file
        while(fields != null)
        {
            List<Node> line = new ArrayList<>();
            line.add(prev_node);

            Node node = null;
            long line_id = -1;

            while((fields = reader.readLine()) != null)
            {
                node = readNode(fields);
                line_id = Long.parseLong(fields[2]);
                if(prev_line_id == line_id) line.add(node);
                else break;
            }

            addLine(prev_line_id, line);


            prev_node = node;
            prev_line_id = line_id;
        }
    }

    private Node readNode(String[] fields)
    {
        double x = Double.parseDouble(fields[0]);
        double y = Double.parseDouble(fields[1]);
        long node_id = Long.parseLong(fields[3]);
        String name = fields[4];

        return new Node(x, y, node_id, name); //Create new Node object from data
    }

    private void addLine(long line_id, List<Node> l) throws JIPEvaluationException
    {
        LineInfo lineInfo = getLineInfo(line_id);
        if (lineInfo == null) return;

        Node prevNode = l.get(0);
        addNode(prevNode);

        JIPTermParser parser = engine.getTermParser();
        JIPQuery query;
        for(int i=1; i<l.size(); i++)
        {
            Node curNode = l.get(i);
            addNode(curNode);

            if(lineInfo.oneway > -1)
            {
                query = engine.openSynchronousQuery(parser.parseTerm("assert(next(" + prevNode.getId() + "," + curNode.getId() + "," + lineInfo.id + "))."));
                if(query.nextSolution() == null) throw new JIPEvaluationException("addLine: Assertion Failed!");
            }
            if(lineInfo.oneway < 1)
            {
                query = engine.openSynchronousQuery(parser.parseTerm("assert(next(" + curNode.getId() + "," + prevNode.getId() + "," + lineInfo.id + "))."));
                if(query.nextSolution() == null) throw new JIPEvaluationException("addLine: Assertion Failed!");
            }

            prevNode = curNode;
        }
    }

    private void addNode(Node node) throws JIPEvaluationException
    {
        JIPTermParser parser = engine.getTermParser();
        JIPQuery query;

        query = engine.openSynchronousQuery(parser.parseTerm("assert(node(" + node.getId() + "," + node.getX() + "," + node.getY() + ", \"" + node.getName() + "\"))."));
        if(query.nextSolution() == null) throw new JIPEvaluationException("addNode: Assertion Failed!");
    }

    private LineInfo getLineInfo(long line_id)
    {
        JIPTermParser parser = engine.getTermParser();
        JIPQuery query = engine.openSynchronousQuery(parser.parseTerm("line(" + line_id + ", Oneway, Lit, Maxspeed, Toll)."));
        JIPTerm term = query.nextSolution();
        if (term == null) return null;

        int oneway = Integer.parseInt(term.getVariablesTable().get("Oneway").toString());
        boolean lit = Boolean.parseBoolean(term.getVariablesTable().get("Lit").toString());
        int maxspeed = Integer.parseInt(term.getVariablesTable().get("Maxspeed").toString());
        boolean toll = Boolean.parseBoolean(term.getVariablesTable().get("Toll").toString());

        return new LineInfo(line_id, oneway, lit, maxspeed, toll);
    }

    private Node getNode(long node_id) throws JIPEvaluationException
    {
        JIPTermParser parser = engine.getTermParser();
        JIPQuery query = engine.openSynchronousQuery(parser.parseTerm("node(" + node_id + ", X, Y, Name)."));
        JIPTerm term;
        if((term = query.nextSolution()) == null) throw new JIPEvaluationException("getNode: failed to find node " + node_id);
        double x = Double.parseDouble(term.getVariablesTable().get("X").toString());
        double y = Double.parseDouble(term.getVariablesTable().get("Y").toString());
        String name = term.getVariablesTable().get("Name").toString();

        return new Node(x, y, node_id, name);
    }

    private void readLineInfo(String lineInfoFile) throws IOException, JIPEvaluationException
    {
        CSVReader reader = new CSVReader(lineInfoFile);
        String[] fields;
        JIPTermParser parser = engine.getTermParser();
        while((fields = reader.readLine()) != null)
        {
            for(int i = 0; i < fields.length; i++)
            {
                if (fields[i].length() == 0) fields[i] = "empty";
            }
            try {
                JIPQuery query = engine.openSynchronousQuery(parser.parseTerm("addLine(" +
                        fields[0] + ',' + //id
                        fields[1] + ',' + //highway
                        fields[3] + ',' + //oneway
                        fields[4] + ',' + //lit
                        fields[6] + ',' + //maxspeed
                        fields[9] + ',' + //access
                        fields[17] + //toll
                        ")."));
                if (query.nextSolution() == null) throw new JIPEvaluationException("LineInfo: failed to assert fact");
            } catch (ArrayIndexOutOfBoundsException | JIPSyntaxErrorException e) {
                e.printStackTrace();
            }
        }
    }

    public final List<Node> getNeighbors(Node node)
    {
        JIPTermParser parser = engine.getTermParser();
        JIPQuery query = engine.openSynchronousQuery(parser.parseTerm("next(" + node.getId() + ", NeighborId, _LineId)."));
        JIPTerm term;
        ArrayList<Node> neighbors = new ArrayList<>();
        while ((term = query.nextSolution()) != null)
        {
            long neighbor_id = Long.parseLong(term.getVariablesTable().get("NeighborId").toString());
            Node neighbor = getNode(neighbor_id);
            neighbors.add(neighbor);
        }

        return neighbors;
    }

    public double heuristic(Node n1, Node n2)
    {
        double dx = n1.getX() - n2.getX();
        double dy = n1.getY() - n2.getY();
        return Math.sqrt(dx*dx + dy*dy) / 120.0;
    }

    public double distance(Node n1, Node n2)
    {
        JIPTermParser parser = engine.getTermParser();
        JIPQuery query = engine.openSynchronousQuery(parser.parseTerm("next(" + n1.getId() + n2.getId() + "LineId)."));
        JIPTerm term;
        if((term = query.nextSolution()) == null) throw new JIPEvaluationException("distance: failed to find connection " + n1.getId() + n2.getId());
        long line_id = Long.parseLong(term.getVariablesTable().get("LineId").toString());
        LineInfo lineInfo = getLineInfo(line_id);

        //TODO: implement this shit
        return 0.0;
    }

    //The method finds the graph node nearest to the input node
    public final Node findNearest(Node node)
    {
        Node best = null;
        double bestDist = Double.POSITIVE_INFINITY;

        JIPTermParser parser = engine.getTermParser();
        JIPQuery query = engine.openSynchronousQuery(parser.parseTerm("node(NodeId, X, Y, Name)."));
        JIPTerm term;

        while ((term = query.nextSolution()) != null)
        {   long node_id = Long.parseLong(term.getVariablesTable().get("NodeId").toString());
            double x = Double.parseDouble(term.getVariablesTable().get("X").toString());
            double y = Double.parseDouble(term.getVariablesTable().get("Y").toString());
            String name = term.getVariablesTable().get("Name").toString();

            Node other = new Node(x, y, node_id, name);

            double dist = heuristic(node, other);

            if(dist < bestDist)
            {
                best = other;
            }
        }

        return best;
    }
}