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
}