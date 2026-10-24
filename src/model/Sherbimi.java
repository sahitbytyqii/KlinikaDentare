package model;

public class Sherbimi {
    private int id;
    private String emri;
    private String pershkrimi;
    private int kohezgjatjaMin;
    private double cmimi;
    private String shtuarNga;

    public Sherbimi(int id, String emri, String pershkrimi, int kohezgjatjaMin, double cmimi) {
        this(id, emri, pershkrimi, kohezgjatjaMin, cmimi, "admin");
    }

    public Sherbimi(int id, String emri, String pershkrimi, int kohezgjatjaMin, double cmimi, String shtuarNga) {
        this.id = id;
        this.emri = emri;
        this.pershkrimi = pershkrimi;
        this.kohezgjatjaMin = kohezgjatjaMin;
        this.cmimi = cmimi;
        this.shtuarNga = (shtuarNga == null || shtuarNga.isEmpty()) ? "admin" : shtuarNga;
    }

    public int getId() { return id; }
    public String getEmri() { return emri; }
    public String getPershkrimi() { return pershkrimi; }
    public int getKohezgjatjaMin() { return kohezgjatjaMin; }
    public double getCmimi() { return cmimi; }
    public String getShtuarNga() { return shtuarNga; }

    public String toJson() {
        return String.format(
            "{\"id\":%d,\"emri\":\"%s\",\"pershkrimi\":\"%s\",\"kohezgjatja\":%d,\"cmimi\":%.2f,\"shtuarNga\":\"%s\"}",
            id, emri, pershkrimi, kohezgjatjaMin, cmimi, shtuarNga);
    }

    public static Sherbimi fromJson(String json) {
        int id = Integer.parseInt(extract(json, "id"));
        String emri = extract(json, "emri");
        String persh = extract(json, "pershkrimi");
        int koh = Integer.parseInt(extract(json, "kohezgjatja"));
        double cm = Double.parseDouble(extract(json, "cmimi"));
        String shtuarNga = extract(json, "shtuarNga");
        if (shtuarNga == null || shtuarNga.isEmpty()) shtuarNga = "admin";
        return new Sherbimi(id, emri, persh, koh, cm, shtuarNga);
    }

    private static String extract(String json, String key) {
        int keyIndex = json.indexOf("\"" + key + "\"");
        if (keyIndex == -1) return "";

        int colonIndex = json.indexOf(':', keyIndex);
        if (colonIndex == -1) return "";

        int start = colonIndex + 1;
        while (start < json.length() && Character.isWhitespace(json.charAt(start))) {
            start++;
        }

        if (start < json.length() && json.charAt(start) == '"') {
            start++;
            int end = json.indexOf('"', start);
            return json.substring(start, end);
        }

        int end = start;
        while (end < json.length() && json.charAt(end) != ',' && json.charAt(end) != '}') {
            end++;
        }
        return json.substring(start, end).trim();
    }

    @Override
    public String toString() {
        return String.format("[%d] %s | %d min | %.2f€ | %s | Shtuar nga: %s",
                id, emri, kohezgjatjaMin, cmimi, pershkrimi, shtuarNga);
    }
}