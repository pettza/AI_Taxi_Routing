import com.ugos.jiprolog.engine.*;

import java.io.*;
import java.util.*;

public class Main
{
    //Prolog engine to store all knowledge information about the taxis
    static JIPEngine engine;

    public static void main(String[] args)
    {
        engine = new JIPEngine();

        try{
            //Consult rules about taxis
            engine.consultFile("prolog\\TaxiRules.pl");

            String directory = "Our_Example-Low_Traffic";

            readTaxis(directory + "\\taxis.csv");

            Graph graph = new Graph(directory + "\\lines.csv", directory + "\\nodes.csv", directory + "\\traffic.csv");

            CSVReader reader = new CSVReader(directory + "\\client.csv");
            String[] fields = reader.readLine();
            reader.closeFile();

            double clientX = Double.parseDouble(fields[0]);
            double clientY = Double.parseDouble(fields[1]);
            Node client = new Node(clientX, clientY, 0);

            double targetX = Double.parseDouble(fields[2]);
            double targetY = Double.parseDouble(fields[3]);
            Node target = new Node(targetX, targetY, 0);

            List<TaxiNode> taxis = getCompatibleTaxis(fields);

            AStarifier solver = new AStarifier();

            SortedMap<AStarNode, TaxiNode> pathScored = new TreeMap<>();
            for(TaxiNode taxi : taxis)
            {
                AStarNode n = solver.AStarify(graph, taxi, client, fields[4]);
                if (n != null) pathScored.put(n, taxi);
            }

            int k = 4;
            int i = 0;
            taxis = new ArrayList<>();
            List<AStarNode> bestPaths = new ArrayList<>();
            System.out.println("Taxis sorted by distance:");
            for(AStarNode node : pathScored.keySet())
            {
                if(i >= k) break;
                i++;
                taxis.add(pathScored.get(node));
                bestPaths.add(node);
                System.out.println("Taxi " + pathScored.get(node).getId());
            }

            System.out.println();

            taxis.sort(TaxiNode::compareTo);

            System.out.println("Taxis sorted by other criteria:");
            for(TaxiNode taxi : taxis)
            {
                System.out.println("Taxi " + taxi.getId() + ": " + taxi.score());
            }

            AStarNode n = solver.AStarify(graph, client, target, fields[4]);
            if (n != null) bestPaths.add(n);


            writeKML(bestPaths, "result-KML.kml");
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    private static void writeKML(Collection<AStarNode> nodes, String filename) throws IOException
    {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename)));

            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<kml xmlns=\"http://earth.google.com/kml/2.1\">\n" +
                    "    <Document>\n" +
                    "        <name>Taxi Routes</name>\n");

            for(AStarNode node : nodes) printPaths(node, "", writer);

            writer.write("    </Document>\n" +
                    "</kml>\n");
        }catch(FileNotFoundException e) {
            e.printStackTrace();
        }catch(IOException e) {
            e.printStackTrace();
        }finally {
            writer.close();
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
            for ( AStarNode prev : node.getPreviousNodes()) {
                printPaths(prev, newStr, writer);
            }
        }
    }

    //Reads taxi information from file taxis.csv and adds it to the database
    private static void readTaxis(String taxisInfoFile) throws IOException
    {
        CSVReader reader = new CSVReader(taxisInfoFile);
        String[] fields;
        JIPTermParser parser = engine.getTermParser();
        while((fields = reader.readLine()) != null)
        {
            try {
                String[] languages = fields[5].split("[|]");
                StringBuilder strb = new StringBuilder("[");
                for(String str : languages)
                {
                    strb.append(str + ",");
                }
                strb.setCharAt(strb.length() - 1, ']');

                JIPQuery query = engine.openSynchronousQuery(parser.parseTerm("assert(taxi(" +
                        fields[0] + ',' + //X
                        fields[1] + ',' + //Y
                        fields[2] + ',' + //id
                        fields[3] + ',' + //availablle
                        fields[4] + ',' + //capacity
                        strb.toString() + ',' + //languages
                        fields[6] + ',' +//rating
                        fields[7] + ',' +//long distance
                        fields[8].split("\t")[0] + //type
                        "))."));
                if (query.nextSolution() == null) throw new JIPEvaluationException("TrafficInfo: failed to assert fact");
            } catch (ArrayIndexOutOfBoundsException | JIPSyntaxErrorException e) {
                e.printStackTrace();
            }
        }
        reader.closeFile();
    }

    //Querries prolog database for compatible taxis using the "compatibleTaxi" predicate
    static List<TaxiNode> getCompatibleTaxis(String[] clientInfo)
    {
        JIPTermParser parser = engine.getTermParser();
        JIPQuery query = engine.openSynchronousQuery(parser.parseTerm("compatibleTaxi(client(" +
                clientInfo[0] + ',' + //X
                clientInfo[1] + ',' + //Y
                clientInfo[2] + ',' + //destX
                clientInfo[3] + ',' + //destY
                clientInfo[4] + ',' + //time
                clientInfo[5] + ',' + //Persons
                clientInfo[6] + ',' +//language
                clientInfo[7] + ")"//luggage
                + ", TaxiID)."));

        List<TaxiNode> ret = new ArrayList<>();
        JIPTerm term;
        while((term = query.nextSolution()) != null)
        {
            long id = Long.parseLong(term.getVariablesTable().get("TaxiID").toString());
            ret.add(getTaxi(id));
        }
        return ret;
    }


    //Queries the database for the information of a specific taxi
    static TaxiNode getTaxi(long id)
    {
        JIPTermParser parser = engine.getTermParser();
        JIPQuery query = engine.openSynchronousQuery(parser.parseTerm("taxi(X,Y," + id + ",_,_,_,Rating,_,Type)."));
        JIPTerm term;


        if((term = query.nextSolution()) == null) throw new JIPEvaluationException("getNode: failed to find node " + id);
        double x = Double.parseDouble(term.getVariablesTable().get("X").toString());
        double y = Double.parseDouble(term.getVariablesTable().get("Y").toString());
        double rating = Double.parseDouble(term.getVariablesTable().get("Rating").toString());
        String type = term.getVariablesTable().get("Type").toString();

        return new TaxiNode(x,y,id,rating,type);
    }
}


