// Copyright 2020
// Author: Matei Simtinică

import java.io.*;
import java.util.*;

/**
 * Bonus Task
 * You have to implement 4 methods:
 * readProblemData         - read the problem input and store it however you see fit
 * formulateOracleQuestion - transform the current problem instance into a SAT instance and write the oracle input
 * decipherOracleAnswer    - transform the SAT answer back to the current problem's answer
 * writeAnswer             - write the current problem's answer
 */
public class BonusTask extends Task {
    int nodes;
    int lines;
    public Map<Integer, ArrayList<Integer>> families = new LinkedHashMap<>();
    public Map<Integer, ArrayList<Integer>> families2 = new LinkedHashMap<>();
    Map<Integer, ArrayList<Integer>> complGraph = new LinkedHashMap<>();
    public LinkedHashSet<Integer> max = new LinkedHashSet<>();
    public ArrayList<Integer> arrests = new ArrayList<>();

    /**
     *
     * @param famNumb number of families
     * @param families data structure to be used for stocking the relations between the families
     */
    public void inititializeMaps(int famNumb, Map<Integer, ArrayList<Integer>> families){
        for( int i = 0; i < famNumb; i++ ){
            families.put(i, new ArrayList<>());
        }
    }

    /**
     *
     * @param node the number of a family
     * @return 1 if the node belongs to he found clique
     *         -1 otherwise
     */
    public int belongsClique(int node){
        if ( arrests.contains(node) ){
            return 1;
        }
        else return -1;
    }

    /**
     *
     * @param arrayList list containing the nodes that make up the clique
     * @return a set containing all the nodes that are connected to the nodes
     *        from the arrayList
     */
    public LinkedHashSet findNodes(ArrayList<Integer> arrayList){
        LinkedHashSet<Integer> allNodes = new LinkedHashSet<>();
        for (Integer integer : arrayList) {
            allNodes.addAll(families.get(integer));
        }
        allNodes.addAll(arrayList);
        // adding th nodes that have no edges, if there are any
        for (int i = 0; i < nodes; i ++){
            if (families.get(i).size() == 0){
                allNodes.add(i);
            }
        }
        return allNodes;
    }

    /**
     *
     * @param families the initial graph
     * @return the complementary graph
     */
    public Map<Integer, ArrayList<Integer>> makeComplementary( Map<Integer, ArrayList<Integer>> families ){
        Map<Integer, ArrayList<Integer>> newGraph = new LinkedHashMap<>();
        inititializeMaps(nodes, newGraph);
        for (int i = 0; i < nodes; i ++){
            for (int k = 0; k < nodes; k ++){
                if (!families.get(i).contains(k) && k != i){
                    newGraph.get(i).add(k);
                }
            }
        }
        return newGraph;
    }

    /**
     *
     * @param arrayList1 list containing the nodes connected to a node
     * @param arrayList2 list containing the nodes connected to another node
     * @return list containing the common elements of the two lists
     */
    public ArrayList<Integer> getCommon(ArrayList<Integer> arrayList1 ,ArrayList<Integer> arrayList2 ){
        ArrayList<Integer> common = new ArrayList<>();
        LinkedHashSet<Integer> goodCommons = new LinkedHashSet<>();
        for (Integer integer : arrayList1) {
            if (arrayList2.contains(integer)) {
                common.add(integer);
            }
        }
        if (common.size() != 1 && common.size() != 0){
            for (int i = 0; i < common.size(); i ++){
                boolean ok = true;
                for (int j = 0; j < common.size() && j != i; j ++){
                    if (!complGraph.get(common.get(i)).contains(common.get(j))){
                        ok = false;
                    }
                }
                if (ok){
                    goodCommons.add(common.get(i));
                }
            }
            common.clear();
            common.addAll(goodCommons);
        }
        return common;
    }

    /**
     *
     * @param nodes number of families
     * @param families the graph containing the relations between the families
     * @return the maxClique found in the graph
     */
    public LinkedHashSet<Integer> findCLique(int nodes,
                                             Map<Integer, ArrayList<Integer>> families){
        LinkedHashSet<Integer> clique = new LinkedHashSet<>();
        LinkedHashSet<Integer> maxClique = new LinkedHashSet<>();
        for (int k = 0; k < nodes ; k ++){
            for (int i = 0; i < nodes && i != k; i ++){
                if (families.get(i).contains(k) &&
                        families.get(k).contains(i)){
                    clique.add(i);
                    clique.add(k);
                    clique.addAll(getCommon(families.get(i), families.get(k)));
                    if (maxClique.size() < clique.size()){
                        maxClique = clique;
                    }
                    clique = new LinkedHashSet<>();
                }
            }
        }
        return maxClique;
    }

    @Override
    public void solve() throws IOException, InterruptedException {
        readProblemData();
        formulateOracleQuestion();
        askOracle();
        decipherOracleAnswer();
        writeAnswer();
    }

    @Override
    public void readProblemData() throws IOException {
        Scanner scanner = new Scanner(new File(inFilename));
        nodes = scanner.nextInt();
        lines = scanner.nextInt();
        inititializeMaps(nodes,families);
        // creating the initial graph
        for (int i = 0; i < lines; i ++){
            int x =  scanner.nextInt();
            int y = scanner.nextInt();
            families.get(x - 1).add(y - 1);
            families.get(y - 1).add(x - 1);
        }
    }

    @Override
    public void formulateOracleQuestion() throws IOException {
        FileWriter fileWriter1 = new FileWriter(oracleInFilename);
        fileWriter1.write("p wcnf ");
        // writing the formula
        fileWriter1.write(Integer.toString(nodes + 1));
        fileWriter1.write(" " + (nodes + 1) + " ");
        fileWriter1.write(nodes * 3 + "\n");
        fileWriter1.write((nodes * 3) + " 1 0\n");
        for (int i = 1; i <= nodes + 1; i ++){
            fileWriter1.write("1 " + (i + 1) + " 0\n");
        }
        fileWriter1.close();

    }

    @Override
    public void decipherOracleAnswer() throws IOException {
        FileWriter fileWriter = new FileWriter(oracleOutFilename);
        complGraph = makeComplementary(families);
        max = findCLique(nodes,complGraph);
        inititializeMaps(nodes, families2);
        // finding the nodes to be removed
        for (int i = 0; i < nodes; i ++){
            if (!max.contains(i)){
                arrests.add(i);
            }
        }
        ArrayList<Integer> newNodes = new ArrayList<>();
        newNodes.addAll(findNodes(arrests));
        // checking if the new graph is equivalent to the initial one
        if (newNodes.size() == nodes) {
            fileWriter.write("True\n");
            fileWriter.write(nodes + 1 + "\n");
            fileWriter.write("1 ");
            for (int i = 1; i <= nodes + 1; i++) {
                fileWriter.write((i + 1) * belongsClique(i - 1) + " ");
            }
        }
        fileWriter.close();
    }

    @Override
    public void writeAnswer() throws IOException {
        FileWriter fileWriter = new FileWriter(outFilename);
        BufferedReader reader = new BufferedReader(new FileReader(oracleOutFilename));
        String line = reader.readLine();
        // getting the solution from the oracle's answer
        if (line.contains("True")){
            line = reader.readLine();
            int length = Integer.parseInt(line);
            line = reader.readLine();
            String[] newLine = line.split(" ");
            for (int i = 1; i < nodes + 1; i ++ ){
                if (Integer.parseInt(newLine[i]) > 0){
                    int node = Integer.parseInt(newLine[i]) - 1;
                    fileWriter.write(node + " ");
                }
            }
        }
        fileWriter.close();
    }
}
