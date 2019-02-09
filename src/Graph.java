import java.util.*;
import java.io.IOException;

import com.ugos.jiprolog.engine.*;

//Graph class providing methods to determine adjacency/proximity of nodes
public class Graph
{
    //Prolog engine to store all knowledge information about the world
    private JIPEngine engine;

    //Class to be returned by the getLine method
    private class LineInfo
    {
        public long id;
        public int oneway; //1: Default order, -1: Reverse order, 0: Both
        public boolean lit;
        public double maxspeed;
        public boolean toll;

        LineInfo(long id_v, int oneway_v, boolean lit_v, double maxspeed_v, boolean toll_v)
        {
            id = id_v;
            oneway = oneway_v;
            lit = lit_v;
            maxspeed = maxspeed_v;
            toll = toll_v;
        }
    }

    public Graph(String lineInfoFile, String nodeInfoFile, String trafficInfoFile) throws IOException, JIPEvaluationException
    {
        //initialize engine and consult a Line and Node related rules prolog file
        engine = new JIPEngine();
        engine.consultFile("prolog\\LinesNodesRules.pl");

        //read all line & traffic info
        readLineInfo(lineInfoFile);
        readTrafficInfo(trafficInfoFile);

        //Define a CSVReader and a Term Parser for our node file
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

            //read a whole line and add it to prolog database
            while((fields = reader.readLine()) != null)
            {
                node = readNode(fields);
                line_id = Long.parseLong(fields[2]);
                if(prev_line_id == line_id) line.add(node);
                else break; //if we changed lines, break inner loop
            }

            addLine(prev_line_id, line);

            prev_node = node;
            prev_line_id = line_id;
        }
    }

    //Constructs a node object from a line of the nodes.csv file
    private Node readNode(String[] fields)
    {
        double x = Double.parseDouble(fields[0]);
        double y = Double.parseDouble(fields[1]);
        long node_id = Long.parseLong(fields[3]);

        return new Node(x, y, node_id); //Create new Node object from data
    }

    //Tries to add a line to the database if it is acceptable (check LinesNodesRules.pl)
    private void addLine(long line_id, List<Node> l) throws JIPEvaluationException
    {
        LineInfo lineInfo = getLineInfo(line_id);
        if (lineInfo == null) return; //not acceptable

        Node prevNode = l.get(0);
        addNode(prevNode);

        JIPTermParser parser = engine.getTermParser();
        JIPQuery query;
        for(int i=1; i<l.size(); i++)
        {
            Node curNode = l.get(i);
            //Add node to database
            addNode(curNode);

            //Fact next(id_1,id_2,line_id) means node "id_1" connects to node "id_2" via line "line_id".
            //If the line bidirectional or is given in the correct order
            if(lineInfo.oneway > -1)
            {
                query = engine.openSynchronousQuery(parser.parseTerm("assert(next(" + prevNode.getId() + "," + curNode.getId() + "," + lineInfo.id + "))."));
                if(query.nextSolution() == null) throw new JIPEvaluationException("addLine: Assertion Failed!");
            }
            //If the line bidirectional or is given in the reverse order
            if(lineInfo.oneway < 1)
            {
                query = engine.openSynchronousQuery(parser.parseTerm("assert(next(" + curNode.getId() + "," + prevNode.getId() + "," + lineInfo.id + "))."));
                if(query.nextSolution() == null) throw new JIPEvaluationException("addLine: Assertion Failed!");
            }

            prevNode = curNode;
        }
    }

    //Adds node to the prolog database
    private void addNode(Node node) throws JIPEvaluationException
    {
        JIPTermParser parser = engine.getTermParser();
        JIPQuery query;

        query = engine.openSynchronousQuery(parser.parseTerm("assert(node(" + node.getId() + "," + node.getX() + "," + node.getY() + "))."));
        if(query.nextSolution() == null) throw new JIPEvaluationException("addNode: Assertion Failed!");
    }

    //Queries the database for the information of a specific line
    private LineInfo getLineInfo(long line_id)
    {
        JIPTermParser parser = engine.getTermParser();
        JIPQuery query = engine.openSynchronousQuery(parser.parseTerm("line(" + line_id + ", Oneway, Lit, Maxspeed, Toll)."));
        JIPTerm term = query.nextSolution();
        if (term == null) return null;

        int oneway = Integer.parseInt(term.getVariablesTable().get("Oneway").toString());
        boolean lit = Boolean.parseBoolean(term.getVariablesTable().get("Lit").toString());
        double maxspeed = Double.parseDouble(term.getVariablesTable().get("Maxspeed").toString());
        boolean toll = Boolean.parseBoolean(term.getVariablesTable().get("Toll").toString());

        return new LineInfo(line_id, oneway, lit, maxspeed, toll);
    }

    //Queries the database for the information of a specific node
    private String getTrafficInfo(long lineId, String time)
    {
        int hour = Integer.parseInt(time.substring(0, 2));

        int temp = hour - hour % 2;
        String timeWindow = String.format("%02d:00-%02d:00", (temp-1)%24, (temp+1)%24);

        JIPTermParser parser = engine.getTermParser();
        JIPQuery query = engine.openSynchronousQuery(parser.parseTerm("traffic(" + lineId + "," + timeWindow + ",Level)."));
        JIPTerm term;
        if ((term = query.nextSolution()) == null) throw new JIPEvaluationException("getTrafficInfo: Something should match query");
        return term.getVariablesTable().get("Level").toString();
    }

    //Queries the database for the information of a specific node
    private Node getNodeInfo(long node_id) throws JIPEvaluationException
    {
        JIPTermParser parser = engine.getTermParser();
        JIPQuery query = engine.openSynchronousQuery(parser.parseTerm("node(" + node_id + ", X, Y)."));
        JIPTerm term;
        if((term = query.nextSolution()) == null) throw new JIPEvaluationException("getNodeInfo: failed to find node " + node_id);
        double x = Double.parseDouble(term.getVariablesTable().get("X").toString());
        double y = Double.parseDouble(term.getVariablesTable().get("Y").toString());

        return new Node(x, y, node_id);
    }

    //Reads line information from file lines.csv and adds it to the database
    private void readLineInfo(String lineInfoFile) throws IOException, JIPEvaluationException
    {
        CSVReader reader = new CSVReader(lineInfoFile);
        String[] fields;
        JIPTermParser parser = engine.getTermParser();
        while((fields = reader.readLine()) != null)
        {
            for(int i = 0; i < fields.length; i++)
            {
                if (fields[i].isEmpty()) fields[i] = "empty";
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
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            } catch (JIPSyntaxErrorException e)
            {}

        }
        reader.closeFile();
    }

    //Reads traffic information from file traffic.csv and adds it to the database
    void readTrafficInfo(String trafficInfoFile) throws IOException, JIPEvaluationException
    {
        CSVReader reader = new CSVReader(trafficInfoFile);
        String[] fields;
        JIPTermParser parser = engine.getTermParser();
        while((fields = reader.readLine()) != null)
        {
            if(fields.length != 4) continue;
            if(fields[2].isEmpty()) continue;
            try {
                String[] times = fields[2].split("[|=]");
                StringBuilder strb = new StringBuilder("assert(trafficInfo(" + fields[0] + ", [");
                for(int i = 0; i<times.length; i+=2)
                {
                    strb.append("(" + times[i] + ", " + times[i+1] + "),");
                }
                strb.setCharAt(strb.length()-1, ']');
                strb.append(")).");

                JIPQuery query = engine.openSynchronousQuery(parser.parseTerm(strb.toString()));
                if (query.nextSolution() == null) throw new JIPEvaluationException("TrafficInfo: failed to assert fact");
            } catch (ArrayIndexOutOfBoundsException | JIPSyntaxErrorException e) {
                e.printStackTrace();
            }
        }
        reader.closeFile();
    }

    //Get neighbors of a specific node
    public final List<Node> getNeighbors(Node node)
    {
        JIPTermParser parser = engine.getTermParser();
        JIPQuery query = engine.openSynchronousQuery(parser.parseTerm("next(" + node.getId() + ", NeighborId, _LineId)."));
        JIPTerm term;
        ArrayList<Node> neighbors = new ArrayList<>();
        while ((term = query.nextSolution()) != null)
        {
            long neighbor_id = (long) Double.parseDouble(term.getVariablesTable().get("NeighborId").toString());
            Node neighbor = getNodeInfo(neighbor_id);
            neighbors.add(neighbor);
        }

        return neighbors;
    }

    //Expects a string with the format hh:mm and tells us if it's day or not
    private boolean isDay(String time)
    {
        if (time.compareTo("20:00") < 0 && time.compareTo("06:30") > 0) return false;
        else return true;
    }

    //Time required to travel the Euclidean distance between two points if taxi is moving at the maximum speed possible
    public double heuristic(Node n1, Node n2)
    {
        double dx = n1.getX() - n2.getX();
        double dy = n1.getY() - n2.getY();
        return Math.sqrt(dx*dx + dy*dy) / 120.0;
    }

    //Estimated distance to travel from adjacent nodes "n1" to "n2" at time "time"
    public double distance(Node n1, Node n2, String time)
    {
        JIPTermParser parser = engine.getTermParser();

        //Find id of the line connecting the 2 nodes
        JIPQuery query = engine.openSynchronousQuery(parser.parseTerm("next(" + n1.getId() + "," + n2.getId() + ",LineId)."));
        JIPTerm term;
        if((term = query.nextSolution()) == null) throw new JIPEvaluationException("distance: failed to find connection " + n1.getId() + n2.getId());
        long line_id = Long.parseLong(term.getVariablesTable().get("LineId").toString());
        LineInfo lineInfo = getLineInfo(line_id);
        double speed = lineInfo.maxspeed;
        String traffic = getTrafficInfo(line_id, time);

        //Use different percentage of the maximum speed based on traffic levels
        if (traffic.equals("low")) speed = lineInfo.maxspeed;
        else if (traffic.equals("unknown")) speed = 0.8 * lineInfo.maxspeed;
        else if (traffic.equals("medium")) speed = 0.5 * lineInfo.maxspeed;
        else if (traffic.equals("high")) speed = 0.2 * lineInfo.maxspeed;


        double dx = n1.getX() - n2.getX();
        double dy = n1.getY() - n2.getY();
        double dist = Math.sqrt(dx*dx + dy*dy) / speed;

        //Add penalty for unlit road during nighttime
        if (!isDay(time) && !lineInfo.lit) dist *= 1.1;
        //Add penalty for tolls
        if (lineInfo.toll) dist *= 1.1;

        return dist;
    }

    //The method finds the graph node nearest to the input node
    public final Node findNearest(Node node)
    {
        Node best = null;
        double bestDist = Double.POSITIVE_INFINITY;

        JIPTermParser parser = engine.getTermParser();
        JIPQuery query = engine.openSynchronousQuery(parser.parseTerm("node(NodeId, X, Y)."));
        JIPTerm term;

        while ((term = query.nextSolution()) != null)
        {
            try {
                long node_id = (long) Double.parseDouble(term.getVariablesTable().get("NodeId").toString());
                double x = Double.parseDouble(term.getVariablesTable().get("X").toString());
                double y = Double.parseDouble(term.getVariablesTable().get("Y").toString());

                Node other = new Node(x, y, node_id);

                double dist = heuristic(node, other);

                if (dist < bestDist) {
                    best = other;
                    bestDist = dist;
                }
            } catch (NumberFormatException e)
            {
                e.printStackTrace();
            }
        }

        return best;
    }
}