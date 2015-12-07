import java.io.File;
import java.io.FileNotFoundException;
import java.lang.IllegalStateException;
import java.lang.Integer;
import java.util.NoSuchElementException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class KyleDB {
    private static Map<Integer, Map<String,String>> storage = new HashMap<Integer, Map<String,String>>();
    public static void main(String[] args) {
        if (args.length == 1) {
            try {
                Scanner input = new Scanner(new File(args[0]));
                Integer id = 1;
                while (input.hasNext()) {
                    Map<String,String> tempMap = new HashMap<String,String>();
                    Scanner line = new Scanner(input.nextLine()).useDelimiter("\\W+");
                    while (line.hasNext()) {
                        String next = line.next();
                        next = next.substring(0, next.length());
                        tempMap.put(next,line.next());
                    }
                    storage.put(id, tempMap);
                    id++;
                }
                // for(Integer key: storage.keySet()) {
                //     for(String s: storage.get(key).keySet()) {
                //         System.out.printf("%d\t%s: %s\n", key, s, storage.get(key).get(s));
                //     }
                // }

                if (input != null) { input.close(); }
            }
            catch (NoSuchElementException e) { readfile_error(); }
            catch (IllegalStateException ex) { readfile_error(); }
            catch (FileNotFoundException exc) { readfile_error(); }

            runDB();
        }
    }

    public static void runDB() {
        System.out.printf("Welcome to KyleDB.\nFor help type 'help' then hit enter.\nTo quit at any time type 'quit' then hit enter.\n\n\n\n>> ");
        Scanner listener = new Scanner(System.in);
        boolean endflag = false;
        while(!(endflag)) {
            String user_input = listener.next();
            if (user_input.equals("help")) {
                System.out.printf("\nHelp is on it's way!\n\nTo query the database please use the query command:\n\ndb.CS557.query(condition, field)\n\ncondition -- field_name op value\n    zero or more select conditions. If there is more than one condition they will be separated by an 'and' ('or' will not be included). Zero conditions are denoted with just a comma and a space before the field, and it means include all documents e.g. (, field).\n\nop can be =, <, >\n\nfield -- field_name(s)\n    zero or more field names separated by '+' signs. This operation specifies the list of fields to be displayed.  If there are zero fields, the closing parenthesis is used and it means include all fields for each document that satisfies the condition, including the ID field.  Unlike Mongo DB, the ID field is only included if specified.  If a document does not have a field in the field_name list, but it has other fields appearing in the list, then it should be included in the result.\n\nquery() returns the entire collection, all fields for all documents\n\nExample queries and their results:\n    db.CS557.query(Manager = 555, SNum+Dept)\n    Dept: 10  SNum: 777\n    SNum: 222\n    db.CS557.query(Age > 15 and Manager = 555)\n    ID: 2 Dept: 10  Manager:  555 SNum: 777 Age: 20\n    db.CS557.query(, ID+SNum)\n    ID: 1 SNum: 555\n    ID: 2 SNum: 777\n    ID: 3 SNum: 888\n    ID: 4 SNum: 222\n\nTo use an aggregate query (sum, max, avg):\n    aggregate(field)\n\nExample aggregate queries and their results:\n    db.CS557.sum(Age)\n    48\n    db.CS557.max(SNum)\n    888\n    db.CS557.avg(Age)\n    16\n\nTo get the Cartesian product of two fields:\n    cartprod(f1, f2)\n\nExample of Cartesian product query and its result:\n    db.CS557.cartprod(Dept, Age)\n    DEPT: 5 Age: 20\n    DEPT: 5 Age: 18\n    DEPT: 5 Age: 10\n    DEPT: 10 Age: 20\n    DEPT: 10 Age: 18\n    DEPT: 10 Age: 10\n\n>> ");
            } else if(user_input.equals("quit")) {
                endflag = true;
            } else if(user_input.startsWith("db.CS557.query")) {
                System.out.printf("Query\n\n>> ");
            } else if(user_input.startsWith("db.CS557.max")) {
                int max = Integer.MIN_VALUE;
                String field = user_input.substring(13, user_input.length() - 1);
                for(Integer key: storage.keySet()) {
                    if(storage.get(key).containsKey(field)) {
                        int value = Integer.valueOf(storage.get(key).get(field));
                        if(value > max) {
                            max = value;
                        }

                    }
                }
                if(max > Integer.MIN_VALUE) {System.out.printf("%d\n\n>> ",max);}
                else {System.out.printf("\n>> ");}
            } else if(user_input.startsWith("db.CS557.avg")) {
                int sum = 0;
                int count = 0;
                String field = user_input.substring(13, user_input.length() - 1);
                for(Integer key: storage.keySet()) {
                    if(storage.get(key).containsKey(field)) {
                        sum += Integer.valueOf(storage.get(key).get(field));
                        count += 1;
                    }
                }
                System.out.printf("%d\n\n>> ",sum/count);
            } else if(user_input.startsWith("db.CS557.sum")) {
                int sum = 0;
                String field = user_input.substring(13, user_input.length() - 1);
                for(Integer key: storage.keySet()) {
                    if(storage.get(key).containsKey(field)) {
                        sum += Integer.valueOf(storage.get(key).get(field));
                    }
                }
                System.out.printf("%d\n\n>> ",sum);
            } else if(user_input.startsWith("db.CS557.cartprod")) {
                System.out.printf("Cartesian Product\n\n>> ");
            } else if(user_input.startsWith("db.CS557.sort")) {
                System.out.printf("Sort\n\n>> ");
            } else if(user_input.startsWith("db.CS557.delete")) {
                System.out.printf("Delete\n\n>> ");
            } else {
                System.out.printf("I'm sorry but the command you've entered is not valid. Please try again.\n\n>> ");
            }
        }
    }

    public static void readfile_error() {
        System.err.println("File improperly formed. Terminating.");
        System.exit(1);
    }
}