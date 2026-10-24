package model;

public class Rezervimi {
    private String numriRezervimit;
    private String username;
    private String emriKlientit;
    private String telefoni;
    private int sherbimiId;
    private String data;
    private int sasia;
    private double cmimiNjesi;

    public Rezervimi(String username, int sherbimiId, String data) {
        this("", username, sherbimiId, data, 1, -1);
    }

    public Rezervimi(String username, int sherbimiId, String data, int sasia, double cmimiNjesi) {
        this("", username, sherbimiId, data, sasia, cmimiNjesi);
    }

    public Rezervimi(String numriRezervimit, String username, int sherbimiId, String data, int sasia, double cmimiNjesi) {
        this(numriRezervimit, username, "", "", sherbimiId, data, sasia, cmimiNjesi);
    }

    public Rezervimi(String numriRezervimit, String username, String emriKlientit, String telefoni,
            int sherbimiId, String data, int sasia, double cmimiNjesi) {
        this.numriRezervimit = numriRezervimit == null ? "" : numriRezervimit;
        this.username = username;
        this.emriKlientit = emriKlientit == null ? "" : emriKlientit;
        this.telefoni = telefoni == null ? "" : telefoni;
        this.sherbimiId = sherbimiId;
        this.data = data;
        this.sasia = sasia;
        this.cmimiNjesi = cmimiNjesi;
    }

    public String getNumriRezervimit() { return numriRezervimit; }
    public void setNumriRezervimit(String numriRezervimit) { this.numriRezervimit = numriRezervimit; }
    public String getUsername() { return username; }
    public String getEmriKlientit() { return emriKlientit; }
    public String getTelefoni() { return telefoni; }
    public int getSherbimiId() { return sherbimiId; }
    public String getData() { return data; }
    public int getSasia() { return sasia; }
    public double getCmimiNjesi() { return cmimiNjesi; }

    public String toJson() {
        return String.format(
            "{\"rezervimiId\":\"%s\",\"username\":\"%s\",\"emriKlientit\":\"%s\",\"telefoni\":\"%s\",\"sherbimiId\":%d,\"data\":\"%s\",\"sasia\":%d,\"cmimiNjesi\":%.2f}",
            escapeJson(numriRezervimit), escapeJson(username), escapeJson(emriKlientit), escapeJson(telefoni),
            sherbimiId, escapeJson(data), sasia, cmimiNjesi);
    }

    public static Rezervimi fromJson(String json) {
        String u = extract(json, "username");
        int s = Integer.parseInt(extract(json, "sherbimiId"));
        String d = extract(json, "data");
        String quantity = extract(json, "sasia");
        String unitPrice = extract(json, "cmimiNjesi");
        return new Rezervimi(extract(json, "rezervimiId"), u, extract(json, "emriKlientit"),
            extract(json, "telefoni"), s, d,
            quantity.isEmpty() ? 1 : Integer.parseInt(quantity),
            unitPrice.isEmpty() ? -1 : Double.parseDouble(unitPrice));
    }

    private static String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }

    private static String extract(String json, String key) {
        String quotedKey = "\"" + key + "\"";
        int searchFrom = 0;
        int keyIndex;
        int colonIndex = -1;
        while ((keyIndex = json.indexOf(quotedKey, searchFrom)) != -1) {
            colonIndex = keyIndex + quotedKey.length();
            while (colonIndex < json.length() && Character.isWhitespace(json.charAt(colonIndex))) colonIndex++;
            if (colonIndex < json.length() && json.charAt(colonIndex) == ':') break;
            searchFrom = keyIndex + quotedKey.length();
        }
        if (keyIndex == -1) return "";

        int start = colonIndex + 1;
        while (start < json.length() && Character.isWhitespace(json.charAt(start))) {
            start++;
        }

        if (start < json.length() && json.charAt(start) == '"') {
            StringBuilder value = new StringBuilder();
            for (int i = start + 1; i < json.length(); i++) {
                char current = json.charAt(i);
                if (current == '"') return value.toString();
                if (current == '\\' && i + 1 < json.length()) {
                    char escaped = json.charAt(++i);
                    if (escaped == 'n') value.append('\n');
                    else if (escaped == 'r') value.append('\r');
                    else if (escaped == 't') value.append('\t');
                    else value.append(escaped);
                } else {
                    value.append(current);
                }
            }
            return value.toString();
        }

        int end = start;
        while (end < json.length() && json.charAt(end) != ',' && json.charAt(end) != '}') {
            end++;
        }
        return json.substring(start, end).trim();
    }
}