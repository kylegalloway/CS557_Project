/******************************************************************************
* CS 557 Final Project Due 12/09/15
* KyleDB.java
* Kyle Galloway
******************************************************************************/
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.IllegalStateException;
import java.lang.Integer;
import java.lang.StringIndexOutOfBoundsException;
import java.util.*;

public class KyleDB {
    private static ArrayList<Document> storage = new ArrayList<Document>();

    public static void main(String[] args) {
        if (args.length == 1) {
            try {
                Scanner input = new Scanner(new File(args[0]));
                while (input.hasNext()) {
                    Scanner line = new Scanner(input.nextLine()).useDelimiter("\\W+");
                    Document doc = new Document();
                    while (line.hasNext()) {
                        String next = line.next();
                        next = next.substring(0, next.length());
                        doc.addPair(new KVpair(next,line.next()));
                    }
                    storage.add(doc);
                }

                if (input != null) { input.close(); }
            }
            catch (NoSuchElementException e) { readfile_error(); }
            catch (IllegalStateException ex) { readfile_error(); }
            catch (FileNotFoundException exc) { readfile_error(); }

            runDB();
        }
    }

    public static void runDB() {
        System.out.printf("Welcome to KyleDB.\nFor help type 'help' or 'H' then hit enter.\nTo quit at any time type 'quit' or 'Q' then hit enter.\n\n\n\n>> ");
        Scanner listener = new Scanner(System.in);
        boolean endflag = false;
        while(!(endflag)) {
            try {
                String user_input = listener.nextLine();
                if (user_input.toLowerCase().equals("help".toLowerCase()) || user_input.toLowerCase().equals("h".toLowerCase())) {
                    System.out.printf("\nHelp is on it's way!\n\nTo query the database please use the query command:\n\ndb.CS557.query(condition, field)\n\ncondition -- field_name op value\n    zero or more select conditions. If there is more than one condition they will be separated by an 'and' ('or' will not be included). Zero conditions are denoted with just a comma and a space before the field, and it means include all documents e.g. (, field).\n\nop can be =, <, >\n\nfield -- field_name(s)\n    zero or more field names separated by '+' signs. This operation specifies the list of fields to be displayed.  If there are zero fields, the closing parenthesis is used and it means include all fields for each document that satisfies the condition, including the ID field.  Unlike Mongo DB, the ID field is only included if specified.  If a document does not have a field in the field_name list, but it has other fields appearing in the list, then it should be included in the result.\n\nquery() returns the entire collection, all fields for all documents\n\nExample queries and their results:\n    db.CS557.query(Manager = 555, SNum+Dept)\n    Dept: 10  SNum: 777\n    SNum: 222\n    db.CS557.query(Age > 15 and Manager = 555)\n    ID: 2 Dept: 10  Manager:  555 SNum: 777 Age: 20\n    db.CS557.query(, ID+SNum)\n    ID: 1 SNum: 555\n    ID: 2 SNum: 777\n    ID: 3 SNum: 888\n    ID: 4 SNum: 222\n\nTo use an aggregate query (sum, max, avg):\n    aggregate(field)\n\nExample aggregate queries and their results:\n    db.CS557.sum(Age)\n    48\n    db.CS557.max(SNum)\n    888\n    db.CS557.avg(Age)\n    16\n\nTo get the Cartesian product of two fields:\n    cartprod(f1, f2)\n\nExample of Cartesian product query and its result:\n    db.CS557.cartprod(Dept, Age)\n    DEPT: 5 Age: 20\n    DEPT: 5 Age: 18\n    DEPT: 5 Age: 10\n    DEPT: 10 Age: 20\n    DEPT: 10 Age: 18\n    DEPT: 10 Age: 10\n\n>> ");
                } else if(user_input.toLowerCase().equals("quit".toLowerCase()) || user_input.toLowerCase().equals("q".toLowerCase())) {
                    endflag = true;
                } else if(user_input.toLowerCase().equals("show".toLowerCase())) {
                    for (int i = 0; i < storage.size(); i++) {
                        for (int j = 0; j < storage.get(i).size(); j++) {
                            System.out.printf("%d\t%s: %s\n", i + 1, storage.get(i).get(j).getKey(), storage.get(i).get(j).getValue());
                        }
                    }
                    System.out.printf("\n\n>> ");
                } else if(user_input.toLowerCase().startsWith("db.CS557.query".toLowerCase()) && user_input.endsWith(")")) {
                    String[] fields = user_input.substring(15, user_input.length() - 1).split(",");
                    String[] queries = fields[0].split("and");
                    ArrayList<Integer> result_ids = new ArrayList<Integer>();
                    boolean firstpass = true;

                    for (int i = 0; i < queries.length; i++) {
                        if(queries[i].contains("<")) {
                            String[] query = queries[i].split("\\<");
                            for (int k = 0; k < storage.size(); k++) {
                                String key = query[0].toLowerCase().trim();
                                if(storage.get(k).contains(key) && (Integer.valueOf(storage.get(k).get(key)) < Integer.valueOf(query[1].trim()))) {
                                    if(firstpass) {
                                        result_ids.add(k);
                                    }
                                } else {
                                    result_ids.remove(new Integer(k));
                                }
                            }
                        } else if(queries[i].contains(">")) {
                            String[] query = queries[i].split("\\>");
                            for (int k = 0; k < storage.size(); k++) {
                                String key = query[0].toLowerCase().trim();
                                if(storage.get(k).contains(key) && (Integer.valueOf(storage.get(k).get(key)) > Integer.valueOf(query[1].trim()))) {
                                    if(firstpass) {
                                        result_ids.add(k);
                                    }
                                } else {
                                    result_ids.remove(new Integer(k));
                                }
                            }
                        } else if(queries[i].contains("=")) {
                            String[] query = queries[i].split("\\=");
                            for (int k = 0; k < storage.size(); k++) {
                                String key = query[0].toLowerCase().trim();
                                if(storage.get(k).contains(key) && (Integer.valueOf(storage.get(k).get(key)).equals(Integer.valueOf(query[1].trim())))) {
                                    if(firstpass) {
                                        result_ids.add(k);
                                    }
                                } else {
                                    result_ids.remove(new Integer(k));
                                }
                            }
                        } else {
                            System.out.printf("I'm sorry but the command you've entered is not valid. Please try again.\n\n>> ");
                        }
                        firstpass = false;
                    }


                    if(fields.length == 2) {
                        String[] projections = fields[1].split("\\+");
                        for (int i = 0; i < result_ids.size(); i++) {
                            String result = "";
                            for (String proj: projections) {
                                String val = storage.get(result_ids.get(i)).get(proj.toLowerCase().trim());
                                if(val != null) {
                                    result += " "+proj.toLowerCase().trim()+": "+val;
                                }
                            }
                            System.out.printf("%s\n",result.substring(1,result.length()));
                        }
                    } else {
                        for (int i = 0; i < result_ids.size(); i++) {
                            System.out.printf("%s\n", storage.get(result_ids.get(i)));
                        }
                    }
                    System.out.printf("\n\n>> ");
                } else if(user_input.toLowerCase().startsWith("db.CS557.max".toLowerCase()) && user_input.endsWith(")")) {
                    int max = Integer.MIN_VALUE;
                    String field = user_input.substring(13, user_input.length() - 1);
                    for (int i = 0; i < storage.size(); i++) {
                        for (int j = 0; j < storage.get(i).size(); j++) {
                            if(storage.get(i).get(j).getKey().toLowerCase().equals(field.toLowerCase())) {
                                int value = Integer.valueOf(storage.get(i).get(j).getValue());
                                if(value > max) {
                                    max = value;
                                }
                            }
                        }
                    }
                    if(max > Integer.MIN_VALUE) { System.out.printf("%d\n\n>> ",max); }
                    else { System.out.printf("\n>> "); }
                } else if(user_input.toLowerCase().startsWith("db.CS557.avg".toLowerCase()) && user_input.endsWith(")")) {
                    int sum = 0;
                    int count = 0;
                    String field = user_input.substring(13, user_input.length() - 1);
                    for (int i = 0; i < storage.size(); i++) {
                        for (int j = 0; j < storage.get(i).size(); j++) {
                            if(storage.get(i).get(j).getKey().toLowerCase().equals(field.toLowerCase())) {
                                    sum += Integer.valueOf(storage.get(i).get(j).getValue());
                                    count += 1;
                            }
                        }
                    }
                    System.out.printf("%d\n\n>> ",sum/count);
                } else if(user_input.toLowerCase().startsWith("db.CS557.sum".toLowerCase()) && user_input.endsWith(")")) {
                    int sum = 0;
                    String field = user_input.substring(13, user_input.length() - 1);
                    for (int i = 0; i < storage.size(); i++) {
                        for (int j = 0; j < storage.get(i).size(); j++) {
                            if(storage.get(i).get(j).getKey().toLowerCase().equals(field.toLowerCase())) {
                                sum += Integer.valueOf(storage.get(i).get(j).getValue());
                            }
                        }
                    }
                    System.out.printf("%d\n\n>> ",sum);
                } else if(user_input.toLowerCase().startsWith("db.CS557.cartprod".toLowerCase()) && user_input.endsWith(")")) {
                    String[] fields = user_input.substring(18, user_input.length() - 1).split(",");
                    if (fields.length == 2) {
                        for (int i = 0; i < storage.size(); i++) {
                            for (int j = 0; j < storage.get(i).size(); j++) {
                                if(storage.get(i).get(j).getKey().toLowerCase().equals(fields[0].toLowerCase())) {
                                    for (int k = 0; k < storage.size(); k++) {
                                        for (int l = 0; l < storage.get(i).size(); l++) {
                                            if(storage.get(k).get(l).getKey().toLowerCase().equals(fields[1].toLowerCase())) {
                                                System.out.printf("%s: %s %s: %s\n", storage.get(i).get(j).getKey(), storage.get(i).get(j).getValue(), storage.get(k).get(l).getKey(), storage.get(k).get(l).getValue());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    System.out.printf("\n>> ");
                } else if(user_input.toLowerCase().startsWith("db.CS557.sort".toLowerCase()) && user_input.endsWith(")")) {
                    String field = user_input.substring(14, user_input.length() - 1);
                    System.out.printf("Sort on %s\n\n>> ",field);
                } else if(user_input.toLowerCase().startsWith("db.CS557.delete".toLowerCase()) && user_input.endsWith(")")) {
                    System.out.printf("Delete\n\n>> ");
                } else {
                    System.out.printf("I'm sorry but the command you've entered is not valid. Please try again.\n\n>> ");
                }
            } catch (StringIndexOutOfBoundsException exce) {
                System.out.printf("I'm sorry but the command you've entered is not valid. Please try again.\n\n>> ");
            }
        }
    }

    public static void readfile_error() {
        System.err.println("File improperly formed. Terminating.");
        System.exit(1);
    }
}