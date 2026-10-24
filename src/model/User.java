package model;

public class User {
    private String username;
    private String password;
    private String emri;
    private String telefoni;
    private String role;
    private boolean admin;

    public User(String username, String password, String emri, boolean admin) {
        this(username, password, emri, "", admin ? "admin" : "pacient", admin);
    }

    public User(String username, String password, String emri, String role) {
        this(username, password, emri, "", role, "admin".equalsIgnoreCase(role));
    }

    public User(String username, String password, String emri, String role, boolean admin) {
        this(username, password, emri, "", role, admin);
    }

    public User(String username, String password, String emri, String telefoni, String role, boolean admin) {
        this.username = username;
        this.password = password;
        this.emri = emri;
        this.telefoni = telefoni == null ? "" : telefoni.trim();
        this.role = role == null || role.trim().isEmpty() ? "pacient" : role.trim();
        this.admin = admin;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmri() { return emri; }
    public String getTelefoni() { return telefoni; }
    public String getRole() { return role; }
    public boolean isAdmin() { return admin; }

    public String toJson() {
        return String.format(
            "{\"username\":\"%s\",\"password\":\"%s\",\"emri\":\"%s\",\"telefoni\":\"%s\",\"role\":\"%s\",\"admin\":%b}",
            escapeJson(username), escapeJson(password), escapeJson(emri), escapeJson(telefoni), escapeJson(role), admin);
    }

    public static User fromJson(String json) {
        String u = extract(json, "username");
        String p = extract(json, "password");
        String e = extract(json, "emri");
        String phone = extract(json, "telefoni");
        String role = extract(json, "role");
        String adminValue = extract(json, "admin");
        boolean admin = adminValue.isEmpty()
            ? "admin".equalsIgnoreCase(role)
            : Boolean.parseBoolean(adminValue);
        if (role.isEmpty()) role = admin ? "admin" : "pacient";
        return new User(u, p, e, phone, role, admin);
    }

        private static String escapeJson(String value) {
        return value.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\b", "\\b")
            .replace("\f", "\\f")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }

    private static String extract(String json, String key) {
        String quotedKey = "\"" + key + "\"";
        int searchFrom = 0;
        int keyIndex;
        int colonIndex = -1;
        while ((keyIndex = json.indexOf(quotedKey, searchFrom)) != -1) {
            colonIndex = keyIndex + quotedKey.length();
            while (colonIndex < json.length() && Character.isWhitespace(json.charAt(colonIndex))) {
                colonIndex++;
            }
            if (colonIndex < json.length() && json.charAt(colonIndex) == ':') break;
            searchFrom = keyIndex + quotedKey.length();
        }
        if (keyIndex == -1) return "";
        colonIndex++;

        int start = colonIndex;
        while (start < json.length() && Character.isWhitespace(json.charAt(start))) {
            start++;
        }

        if (start < json.length() && json.charAt(start) == '"') {
            StringBuilder value = new StringBuilder();
            for (int i = start + 1; i < json.length(); i++) {
                char current = json.charAt(i);
                if (current == '"') return value.toString();
                if (current != '\\') {
                    value.append(current);
                    continue;
                }
                if (++i >= json.length()) break;
                char escaped = json.charAt(i);
                switch (escaped) {
                    case '"': value.append('"'); break;
                    case '\\': value.append('\\'); break;
                    case '/': value.append('/'); break;
                    case 'b': value.append('\b'); break;
                    case 'f': value.append('\f'); break;
                    case 'n': value.append('\n'); break;
                    case 'r': value.append('\r'); break;
                    case 't': value.append('\t'); break;
                    case 'u':
                        if (i + 4 < json.length()) {
                            value.append((char) Integer.parseInt(json.substring(i + 1, i + 5), 16));
                            i += 4;
                        }
                        break;
                    default: value.append(escaped);
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

    @Override
    public String toString() {
        return emri + " (" + username + ") [" + role + "]" + (admin ? " [ADMIN]" : "");
    }
}