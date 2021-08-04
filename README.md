Pahontu Stefan Alin 321CA --------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------
------------------------------------Algorithm Analysis----------------------------------------------
Complexity legend:
'families' = the number of families ( => n )
'spies' = the number of of spies ( => k )
'lines' = the number of relations between families ( => m )
'extended' = the size of the extended family ( => k )

Task 1:
    To find a spy for each family, I will consider from the very beginning that a family can only
have one unique spy. This means that I can translate this problem into just making sure that each
family belongs to a spy and that a spy is not infiltrated into two families who get along. To solve
this problem, I will use a number of ('families' + 1) * 'spies' variables and a number of 'families'
clauses. The first 'families' clauses contain 'families' * 'spies' variables that will tell me
if each family has a spy, and to what spy it belongs. The last 'spies' clauses will check if the
families assigned to each spy are not connected.

Complexities:
initializeHashmaps: O(n) + O(k), but since k is usually smaller than n, we can consider O(n)
findSpyless: O(1)
checkForSPy: O(1)
getAnswear: O(n * k)
checkSharedSpies: O(n1 * n1), where n1 is the number of families assigned to a spy;
                worst case scenario: O(n^2), when the graph has no edges
readProblemData: O(m)
formulateOracleQuestion: O(n * k)
decipherOracleAnswer: O(n * k),  worst-case is O(n^2)
writeAnswer: O(n * k) if answer is true O(1) otherwise

Task 2:
    We know that every graph with at least one edge, contains a clique of size 2. To solve this problem,
I will check all the connections between the families (which I will be viewing as a non-oriented graph)
to find the max-clique from the graph. If there are nodes that can make up a max-clique, if these nodes
are indeed connected, meaning they form a clique and the size of the max-clique's size is greater than,
or equal to the size of the clique we are searching for, it means the problem's answer is true. I will be
using 'nodes' + 2 variables and 3 clauses, the first containing 'nodes' variables and the second and third
only one. I will be checking if the size of the max-clique found is at least equal to the size I am
searching for and that the nodes I picked to make the max-clique are indeed connected. If the max-clique's
size is greater than the one I need, any combination of nodes making up the max-clique(of size equal to
the size of the clique I am searching for), will deliver a correct output. The first clause will contain
the first 'nodes' variables that will tell whether a node belongs to the max-clique and the second and third
will check if the chosen set of nodes is actually a clique and that its size is at least equal to the one
I want.
Complexities:
initializeHashmaps: O(n)
getCommon: O(n1 * n2), where n1 is the neighbours number of the first node
                        and n2 is the neighbours number of the second node
-> worst case scenario is when both nodes are connected to every single node of the graph, resulting in O(n^2)
checkSize: O(1)
findCLique: O(n * (n - 1) * O(getCommon)) ( O(n^4) is the worst case scenario, when the graph is a complete
                                            graph)
checkClique: O(n1^2), where n1 is the number of nodes from the subset considered a clique;
                           worst case scenario: O(n^2), when the subset contains all the nodes from the graph
readProblemData: O(m)
formulateOracleQuestion: O(n)
decipherOracleAnswer: O(n^2) (worst case scenario)
writeAnswer: O(k) if answer is true, O(1) if answer is false

Task 3:
    To find the minimum number of arrests, I have to remove the edges of a minimum number of nodes from
the graph, so that the remaining graph will have no edges. By reducing this problem to the precedent one,
I have deducted that this is efficiently done by eliminating the edges outsides of the nodes that make up the
max-clique in the complementary graph. To do so, I will have to build the complementary graph and run task2
until I get the size of the max-clique and the correct output will be nodes that are not part of it.
Note: I will start by searching for cliques of size 'families', then decrease the size.

Complexities:
initializeHashmaps: O(n)
makeComplementary: O(n^2)
relationsNumber: O(n)
readProblemData: O(m)
reduceToTask2: O(n^2)
extractAnswerFromTask2: O(l), where l is the size of the max-clique found in the complementary graph
writeAnswer: O(n)

BonusTask:
    To solve this task, I am using the same logic from the previous task. However, I will find the max-clique
without having to reduce the problem to task2. This means I will have to check if the found clique covers every
single edge in the graph, and it is of minimum size. To ensure it is a minimum clique, I will find the max-clique
in the complementary graph, and then remove those nodes, so that the remaining ones are the ones I need. I gave
every soft clause a weight equal to 1, each representing whether the given node is part of the clique or not.
To make sure every edge is covered by the chosen clique and to improve performance, I am using the following logic:
The nodes in the clique I need contain all the edges of the graph. This means, I can rebuild the initial graph,
by only using these nodes. To further improve performance, I am not building and comparing a new graph to the
original one every time, I am only checking if the nodes in the clique I found have connections to all the nodes
from the original.(If some nodes from the graph have no edges at all, I will also take them into consideration,
while checking the 'new graph')

Complexities:
initializeHashmaps: O(n)
belongsClique: O(1)
findNodes: O(l), where l is the size of the found clique
makeComplementary: O(n^2)
getCommon: O(n1 * n2), where n1 is the neighbours number of the first node
                        and n2 is the neighbours number of the second node
-> worst case scenario is when both nodes are connected to every single node from the graph, resulting in O(n^2)
findCLique: O(n * (n - 1) * O(getCommon)) ( O(n^4) is the worst case scenario, when the graph is a complete
                                            graph)
readProblemData: O(m)
formulateOracleQuestion: O(n)
decipherOracleQuestion: O(n^2)
writeAnswer: O(n)

Note: the complexity of the solve method will always be equal to the greatest complexity that appears in a function
that is used to solve a certain task, exception being task3, where the complexity will also depend on the number of
times it takes to find out the max-clique by using task2.
