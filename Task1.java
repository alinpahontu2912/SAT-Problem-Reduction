// Copyright 2020
// Author: Matei Simtinică

import java.io.*;
import java.util.*;



/**
 * Task1
 * You have to implement 4 methods:
 * readProblemData         - read the problem input and store it however you see fit
 * formulateOracleQuestion - transform the current problem instance into a SAT instance and write the oracle input
 * decipherOracleAnswer    - transform the SAT answer back to the current problem's answer
 * writeAnswer             - write the current problem's answer
 */
public class Task1 extends Task {
    public Map<Integer, ArrayList<Integer>> families = new LinkedHashMap<>();
    public Map<Integer, LinkedHashSet<Integer>> assignedSpies = new LinkedHashMap<>();
    public Map<Integer, ArrayList<Integer>> spiesFamilies = new LinkedHashMap<>();
    ArrayList<Integer> valuesBool = new ArrayList<>();
    public Integer nodes = 0;
    public Integer lines = 0;
    public Integer spies = 0;
    public boolean ok = true;

    /**
     *
     * @param familyNumber number of all families
     * @param spyNumber number of all spies
     *                  initiates the data structures later used for
     *                  finding the problem's solution
     */
    public void initializeHashmaps(int familyNumber, int spyNumber){
        for (int i = 0; i < familyNumber; i ++){
            families.put(i, new ArrayList<>());
        }
        for (int i = 0; i < spyNumber; i ++){
            assignedSpies.put(i, new LinkedHashSet<>());
            spiesFamilies.put(i, new ArrayList<>());
        }
    }

    /**
     *
     * @param nodes number of families
     * @param arrayList contains the families that already have a spy
     * @param list the data structure containing the relations between families
     * @return
     */
    public int findSpyless(int nodes, ArrayList<Integer> arrayList, List<Map.Entry<Integer, ArrayList<Integer>>> list ){
        for (int i = nodes - 1; i >= 0; i --){
            int key = list.get(i).getKey();
            if (!arrayList.contains(key)){
                return i;
            }
        }
        return -1;
    }

    /**
     *
     * @param family wanted family
     * @param spy the spy number in the spies list
     * @param spies the list containing all the spies
     * @return 1 if the wanted family belongs to the spy
     *        -1 otherwise
     */
    public int checkForSPy(int family, int spy, Map<Integer, ArrayList<Integer>> spies ){
        int x = -1;
        if (spies.get(spy).contains(family)){
            x = 1;
        }
        return x;
    }

    /**
     *
     * @param spiesFamilies the data structure containing the spies and their infiltrated families
     * @param spy the spy we are checking for
     * @return 1 if the spy's families are not connected, -1 otherwise
     */
    public int checkSharedSpies(Map<Integer, ArrayList<Integer>> spiesFamilies, int spy){
        ArrayList<Integer> arrayList = spiesFamilies.get(spy);
        int x = 1;
        for (int i = 0; i < arrayList.size(); i ++){
            ArrayList<Integer> fam1 = families.get(arrayList.get(i));
            for (int j = 0; j < arrayList.size(); j ++){
                if (fam1.contains(arrayList.get(j))){
                    x = -1;
                }
            }
        }
        return x;
    }

    /**
     *
     * @throws IOException
     *    this function assigns true or false to each variable
     */
    public void getAnswear() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(oracleInFilename));
        String line = reader.readLine();
        String[] first = line.split(" ");
        for (int j = 0; j < nodes; j ++) {
            line = reader.readLine();
            String[] newLine = line.split(" ");
            for (int i = 0; i < spies; i++) {
                valuesBool.add(Integer.parseInt(newLine[i]) * checkForSPy(j, i, spiesFamilies));
            }
        }

        for (int i = 0; i < spies; i ++){
            line = reader.readLine();
            String[] newLine = line.split(" ");
            if (Integer.parseInt(newLine[0]) < 0){
                ok = false;
            }
        }
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
            nodes = scanner.nextInt();          // number of families
            lines = scanner.nextInt();          // number of family relations
            spies = scanner.nextInt();          // number of spies
        initializeHashmaps(nodes,spies);
        for (int i = 0; i < lines; i ++){
            int x =  scanner.nextInt();
            int y = scanner.nextInt();
            // creating the graph containing the family relations
            families.get(x - 1).add(y - 1);
            families.get(y - 1).add(x - 1);
        }

    }

    @Override
    public void formulateOracleQuestion() throws IOException {
        FileWriter fileWriter1 = new FileWriter(oracleInFilename);
        fileWriter1.write("p cnf ");
        fileWriter1.write(Integer.toString(nodes * spies));
        fileWriter1.write(" ");
        fileWriter1.write(Integer.toString(nodes));
        fileWriter1.write("\n");
        // wrting the information about the formula that solves the problem
        for (int i = 1; i <= nodes * spies; i += spies){
            for (int j = i; j < i + spies; j ++ ) {
                fileWriter1.write(j + " ");
            }
            fileWriter1.write(0 + "\n");
        }
        for (int i = nodes * spies + 1; i < (nodes+1) * spies + 1; i ++ ){
            fileWriter1.write(i + " 0\n");
        }
        fileWriter1.close();
    }

    @Override
    public void decipherOracleAnswer() throws IOException {
        FileWriter fileWriter = new FileWriter(oracleOutFilename);
        // sorting the families based on the number of their friendly relations
        List<Map.Entry<Integer, ArrayList<Integer>>> sorted = new ArrayList<>(families.entrySet());
        sorted.sort(Comparator.comparingInt(t0 -> t0.getValue().size()));
        ArrayList<Integer> hasSpy = new ArrayList<>();
        int spyNumber = 0;
        // trying to assign each family a spy
        while ( spyNumber < spies ) {
            int spyless = findSpyless(nodes, hasSpy, sorted);
            if (spyless != -1){
                assignedSpies.get(spyNumber).addAll(sorted.get(spyless).getValue());
                assignedSpies.get(spyNumber).add(sorted.get(spyless).getKey());
                hasSpy.add((sorted.get(spyless).getKey()));
                spiesFamilies.get(spyNumber).add(sorted.get(spyless).getKey());
            }
            for (int i = nodes - 1; i >= 0 && hasSpy.size() != nodes; i --){
                if (!hasSpy.contains(sorted.get(i).getKey()) &&
                        !assignedSpies.get(spyNumber).contains(sorted.get(i).getKey())){
                    assignedSpies.get(spyNumber).addAll(sorted.get(i).getValue());
                    assignedSpies.get(spyNumber).add(sorted.get(i).getKey());
                    hasSpy.add((sorted.get(i).getKey()));
                    spiesFamilies.get(spyNumber).add(sorted.get(i).getKey());
                }
            }
            spyNumber++;
        }
        // obtaining the variables' values
        getAnswear();
        int count = 0;
        for (int i = 0; i < nodes * spies; i ++){
            if (valuesBool.get(i) > 0){
                count++;
            }
        }
        if (count != nodes && ok){
            fileWriter.write("False");
        }
        else {
            fileWriter.write("True\n");
            fileWriter.write(nodes * spies + "\n");
            for (int i = 0; i < nodes * spies; i ++){
                fileWriter.write(valuesBool.get(i) + " ");
            }
            for (int i = 0; i < spies; i ++){
                fileWriter.write( (nodes * spies + i + 1) * checkSharedSpies(spiesFamilies,i) + " ");
            }
        }
        fileWriter.close();
    }

    @Override
    public void writeAnswer() throws IOException {
        FileWriter fileWriter1 = new FileWriter(outFilename);
        BufferedReader reader = new BufferedReader(new FileReader(oracleOutFilename));
        String line = reader.readLine();
        if (line.contains("False")){
            fileWriter1.write("False");
        }
        else {
            fileWriter1.write("True\n");
            line = reader.readLine();
            int number = nodes * spies;
            line = reader.readLine();
            String[] allVar = line.split(" ");
            for (int i = 0 ; i < number; i ++){
                    if (Integer.parseInt(allVar[i]) > 0){
                        fileWriter1.write(i%spies + 1 + " ");
                    }
                }
            }
        fileWriter1.close();
        }
    }

