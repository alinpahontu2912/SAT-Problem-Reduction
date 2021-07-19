// Copyright 2020
// Author: Matei Simtinică

import java.io.*;
import java.util.*;

/**
 * Task3
 * This being an optimization problem, the solve method's logic has to work differently.
 * You have to search for the minimum number of arrests by successively querying the oracle.
 * Hint: it might be easier to reduce the current task to a previously solved task
 */
public class Task3 extends Task {
    String task2InFilename;
    String task2OutFilename;
    public Map<Integer, ArrayList<Integer>> families = new LinkedHashMap<>();
    public Map<Integer, ArrayList<Integer>> complementary;
    public ArrayList<Integer> maxClique = new ArrayList<>();
    public int extended;
    public int nodes;
    public int lines;
    public boolean ok = false;

    /**
     *
     * @param famNumb number of families
     * @param families the data structure to be osed for stocking the families' relations
     */
    public void inititializeMaps(int famNumb, Map<Integer, ArrayList<Integer>> families){
        for( int i = 0; i < famNumb; i++ ){
            families.put(i, new ArrayList<>());
        }
    }

    /**
     *
     * @param families initial graph
     * @return complementary graph
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
     * @param map a graph
     * @return number of edges
     */
    public int relationsNumber( Map<Integer, ArrayList<Integer>> map ){
        int cnt = 0;
        for (int i = 0; i < map.size(); i ++){
            cnt += map.get(i).size();
        }
        cnt = cnt / 2;
        return cnt;
    }

    @Override
    public void solve() throws IOException, InterruptedException {
        task2InFilename = inFilename + "_t2";
        task2OutFilename = outFilename + "_t2";
        Task2 task2Solver = new Task2();
        task2Solver.addFiles(task2InFilename, oracleInFilename, oracleOutFilename, task2OutFilename);
        readProblemData();
        complementary = makeComplementary(families);
        // the size of the max clique in a grraph can be at most equal to the number of nodes
        extended = nodes;
        // searching for the largest clique
        while (!ok) {
            reduceToTask2();
            task2Solver.solve();
            extractAnswerFromTask2();
            extended --;
        }
        writeAnswer();
    }

    @Override
    public void readProblemData() throws IOException {
        Scanner scanner = new Scanner(new File(inFilename));
        nodes = scanner.nextInt();
        lines = scanner.nextInt();
        inititializeMaps(nodes,families);
        // reading the initial data and creating the graph
        for (int i = 0; i < lines; i ++){
            int x =  scanner.nextInt();
            int y = scanner.nextInt();
            families.get(x - 1).add(y - 1);
            families.get(y - 1).add(x - 1);
        }
    }

    public void reduceToTask2() throws IOException {
        // I reduce the problem to task2 by writing the input file containing
        // the complementary graph and the size of the clique I am looking for
        File file = new File(task2InFilename);
        FileWriter fileWriter = new FileWriter(file);
        int relations = relationsNumber(complementary);
        fileWriter.write(nodes + " " + relations + " " + extended + "\n");
        Map<Integer, ArrayList<Integer>> families2 = new LinkedHashMap<>();
        inititializeMaps(nodes, families2);
        for (int i = 0; i < nodes; i ++){
            for (int j = 0; j < complementary.get(i).size(); j ++) {
                if (families2.get(i).size() != complementary.get(i).size() &&
                        !families2.get(i).contains(complementary.get(i).get(j))) {
                    int x = i + 1;
                    int y = complementary.get(i).get(j) + 1;
                    fileWriter.write(x + " " + y + "\n");
                    families2.get(i).add(complementary.get(i).get(j));
                    families2.get(complementary.get(i).get(j)).add(i);
                }
            }
        }
        fileWriter.close();
    }


    public void extractAnswerFromTask2() throws IOException {
        // fetting the clique from the task2 result
        File file = new File(task2OutFilename);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = reader.readLine();
        if (line.contains("True")){
            ok = true;
            line = reader.readLine();
            String[] numbers = line.split(" ");
            for (int i =0; i < numbers.length; i ++){
                maxClique.add(Integer.parseInt(numbers[i]) - 1);
            }
        }

    }

    @Override
    public void writeAnswer() throws IOException {
        FileWriter fileWriter = new FileWriter(outFilename);
        for (int i = 0; i < nodes; i ++){
            if (!maxClique.contains(i)){
                fileWriter.write((i + 1) + " ");
            }
        }
        fileWriter.close();
    }
}
