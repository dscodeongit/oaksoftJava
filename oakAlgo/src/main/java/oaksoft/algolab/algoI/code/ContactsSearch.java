package oaksoft.algolab.algoI.code;

import edu.princeton.cs.algs4.StdOut;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * We're going to make our own Contacts application! The application must perform two types of operations:
 *
 * add name, where  is a string denoting a contact name. This must store  as a new contact in the application.
 * find partial, where  is a string denoting a partial name to search the application for. It must count the number of contacts starting with  and print the count on a new line.
 * Given  sequential add and find operations, perform each operation in order.
 *
 * Input Format
 *
 * The first line contains a single integer, , denoting the number of operations to perform.
 * Each line  of the  subsequent lines contains an operation in one of the two forms defined above.
 *
 * Constraints
 *
 * It is guaranteed that  and  contain lowercase English letters only.
 * The input doesn't have any duplicate  for the  operation.
 * Output Format
 *
 * For each find partial operation, print the number of contact names starting with  on a new line.
 *
 * Sample Input
 *
 * 4
 * add hack
 * add hackerrank
 * find hac
 * find hak
 * Sample Output
 *
 * 2
 * 0
 * Explanation
 *
 * We perform the following sequence of operations:
 *
 * Add a contact named hack.
 * Add a contact named hackerrank.
 * Find and print the number of contact names beginning with hac. There are currently two contact names in the application and both of them start with hac, so we print  on a new line.
 * Find and print the number of contact names beginning with hak. There are currently two contact names in the application but neither of them start with hak, so we print  on a new line.
 */
public class ContactsSearch {
    public static int[] contacts(String[][] queries) {
        Map<String, Integer> subStrOccMap = new HashMap<>();
        List<Integer> ans = new ArrayList<>();
        for(String[] q : queries) {
            if(q[0].equals("add")){
                String name = q[1];
                for(int i = 0; i< name.length(); i++) {
                    String sub = name.substring(0, i+1);
                    subStrOccMap.put(sub, subStrOccMap.getOrDefault(sub, 0) + 1);
                }
            }else if(q[0].equals("find")){
               int count = subStrOccMap.getOrDefault(q[1], 0);
               ans.add(count);
            }
        }
        int[] rs = new int[ans.size()];
        int i = 0;
        for(int c : ans){
            rs[i++]=c;
        }
        return rs;
    }

    public static void main(String[] args) throws Exception {
        File input = new File("data/contactSearch.txt");
        BufferedReader br = new BufferedReader(new FileReader(input));
        String fl = br.readLine();
        int opCount = Integer.parseInt(fl);
        String[][] ops = new String[opCount][2];
        final AtomicInteger i = new AtomicInteger(0);
        br.lines().forEach(line -> {
            if(line.contains(" ")){
                String[] op = line.split(" ");
                ops[i.get()][0] = op[0];
                ops[i.getAndIncrement()][1] = op[1];
            }
        });

        int[] rs = contacts(ops);

        StdOut.println(Arrays.toString(rs));
    }
}
