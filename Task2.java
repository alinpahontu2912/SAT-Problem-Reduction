// Copyright 2020
// Author: Matei Simtinică

import javax.swing.plaf.IconUIResource;
import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Task2
 * You have to implement 4 methods:
 * readProblemData         - read the problem input and store it however you see fit
 * formulateOracleQuestion - transform the current problem instance into a SAT instance and write the oracle input
 * decipherOracleAnswer    - transform the SAT answer back to the current problem's answer
 * writeAnswer             - write the current problem's answer
 */
public class Task2 extends Task {
    int nodes;
    int lines;
    int extended;
    public Map<Integer, ArrayList<Integer>> families = new LinkedHashMap<>();
    LinkedHashSet<Integer> clique = new LinkedHashSet<>();
    LinkedHashSet<Integer> maxClique = new LinkedHashSet<>();
    ArrayList<Integer> boolValues = new ArrayList<>();

    /**
     *
     * @param famNumb number of families
     * @param families the data structure used to memorize
     *                the relations between families
     */
    public void inititializeMaps(int famNumb, Map<Integer, ArrayList<Integer>> families){
        for( int i = 0; i < famNumb; i++ ){
            families.put(i, new ArrayList<>());
        }
    }

    /**
     *
     * @param arrayList1 first array
     * @param arrayList2 second array
     * @return an arraylist containing the common elements of the the two arrays
     */
    public ArrayList<Integer> getCommon(ArrayList<Integer> arrayList1 ,ArrayList<Integer> arrayList2 ){
        ArrayList<Integer> common = new ArrayList<>();
        // using hashSet to remove duplicates automatically
        LinkedHashSet<Integer> goodCommons = new LinkedHashSet<>();
        for (Integer integer : arrayList1) {
            if (arrayList2.contains(integer)) {
                common.add(integer);
            }
        }
        if (common.size() != 0){
            for (int i = 0; i < common.size(); i ++){
                boolean ok = true;
                for (int j = 0; j < common.size() && j != i; j ++){
                    if (!families.get(common.get(i)).contains(common.get(j))){
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
     * @param nodeSet a set of nodes to be checked
     * @return 1 if the set is a clique, -1 otherwise
     */
    public int checkClique(LinkedHashSet<Integer> nodeSet){
        int x = 1;
        ArrayList<Integer> arrayList = new ArrayList<>();
        arrayList.addAll(nodeSet);
        for (int i = 0; i < arrayList.size(); i ++){
            ArrayList<Integer> fam1 = families.get(arrayList.get(i));
            for (int j = 0; j < arrayList.size(); j ++){
                if (i != j){
                    if (!fam1.contains(arrayList.get(j))){
                        x = -1;
                    }
                }
            }
        }
        return x;
    }

    /**
     *
     * @param size the clique size
     * @param wantedNumber the size of the found clique
     * @return 1 if the found clique's size is greater or equal to the one we are looking for
     */
    public int checkSize(int size, int wantedNumber){
        if (size >= wantedNumber){
            return 1;
        }
        return -1;
    }

    /**
     *
     * @param nodes number of nodes
     * @param families the data structure containing the relations between families
     * @return A set with the family that make up the biggest clique
     */
    public LinkedHashSet<Integer> findCLique(int nodes,
                                             Map<Integer, ArrayList<Integer>> families){
        clique = new LinkedHashSet<>();
        maxClique = new LinkedHashSet<>();
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
        extended = scanner.nextInt();
        inititializeMaps(nodes,families);
        // reading the initial data and creating the graph
        for (int i = 0; i < lines; i ++){
            int x =  scanner.nextInt();
            int y = scanner.nextInt();
            families.get(x - 1).add(y - 1);
            families.get(y - 1).add(x - 1);
        }

    }

    @Override
    public void formulateOracleQuestion() throws IOException {
        // writing the initial formula
        FileWriter fileWriter1 = new FileWriter(new File(oracleInFilename));
        fileWriter1.write("p cnf ");
        fileWriter1.write(Integer.toString(nodes + 2));
        fileWriter1.write(" ");
        fileWriter1.write(Integer.toString(3));
        fileWriter1.write("\n");
        for (int i = 0; i < nodes; i ++){
            fileWriter1.write((i + 1) + " ");
        }
        fileWriter1.write("0 \n");
        fileWriter1.write((nodes + 1) + " 0\n");
        fileWriter1.write((nodes + 2) + " 0\n");
        fileWriter1.close();

    }

    @Override
    public void decipherOracleAnswer() throws IOException {
        clique = findCLique(nodes,families);
        // assigning each formula variable its value
        ArrayList<Integer> areCLique = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(oracleInFilename));
        FileWriter fileWriter = new FileWriter(oracleOutFilename);
        String line = reader.readLine();
        String[] first = line.split(" ");
        int variables = Integer.parseInt(first[2]);
        line = reader.readLine();
        String[] newLine = line.split(" ");
        for (int i = 0; i < newLine.length - 1; i ++) {
            if (clique.contains(Integer.parseInt(newLine[i]) - 1)) {
                boolValues.add(Integer.parseInt(newLine[i]));
                areCLique.add(Integer.parseInt(newLine[i]));
            } else {
                boolValues.add(Integer.parseInt(newLine[i]) * -1);
            }
        }
        line = reader.readLine();
        newLine = line.split(" ");
        boolValues.add(Integer.parseInt(newLine[0]) * checkSize(clique.size(), extended));
        line = reader.readLine();
        newLine = line.split(" ");
        boolValues.add(Integer.parseInt(newLine[0]) * checkClique(clique));
        if (areCLique.size() >= extended && boolValues.get(boolValues.size() - 1) > 0
        && boolValues.get(boolValues.size() - 2) > 0) {
            fileWriter.write("True\n");
            fileWriter.write(variables + "\n");
            for (int i = 0; i < variables; i ++){
                fileWriter.write(boolValues.get(i) + " ");
            }
        } else {
            fileWriter.write("False");
        }
        fileWriter.close();
    }

    @Override
    public void writeAnswer() throws IOException {
        // After finding the values that satisfy the problem,
        // I am writing the response
        FileWriter fileWriter1 = new FileWriter(outFilename);
        BufferedReader reader = new BufferedReader(new FileReader(oracleOutFilename));
        String line = reader.readLine();
        int count = 0;
        if (line.contains("False")){
            fileWriter1.write("False");
        }
        else {
            fileWriter1.write("True\n");
            line = reader.readLine();
            int variables = Integer.parseInt(line);
            line = reader.readLine();
            String[] newLine = line.split(" ");
            for (int i = 0; i < variables - 2 && count != extended; i ++){
                if (Integer.parseInt(newLine[i]) > 0){
                   fileWriter1.write(Integer.parseInt(newLine[i]) + " ");
                   count ++;
                }
            }
        }
        fileWriter1.close();
    }
}
