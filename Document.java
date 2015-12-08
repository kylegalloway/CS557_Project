import java.util.*;

public class Document {
    private ArrayList<KVpair> doc_array = new ArrayList<KVpair>();
    private ArrayList<String> keys = new ArrayList<String>();

    public void addPair(KVpair kvpair) {
        this.doc_array.add(kvpair);
        this.keys.add(kvpair.getKey());
    }
    public KVpair get(int i) { return this.doc_array.get(i); }
    public String get(String key) {
        String result = null;
        for (int i = 0; i < this.doc_array.size(); i++) {
            if(this.doc_array.get(i).getKey().equals(key)) {
                result = this.doc_array.get(i).getValue();
            }
        }
        return result;
    }
    public int size() { return this.doc_array.size(); }
    public ArrayList<String> getKeys() { return this.keys; }
    public boolean contains(String s) { return this.keys.contains(s); }
    @Override
    public String toString() {
        String result = "";
        for (KVpair pair: this.doc_array) {
            result += " "+pair.getKey()+": "+pair.getValue();
        }
        return result.substring(1,result.length());
    }
}

class KVpair {
    private String key;
    private String value;

    // Constructor for Product Class
    public KVpair(String key, String value) {
        this.key = key.toLowerCase();
        this.value = value.toLowerCase();
    } // End constructor

    public String getKey() { return key; }
    public String getValue() { return value; }

    public int compareTo(KVpair otherPair) {
        if(this.key.equals(otherPair.getKey().toLowerCase())) {
            return 1;
        }
        return 0;
    }
}