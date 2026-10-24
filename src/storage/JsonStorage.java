package storage;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class JsonStorage {

    public static void shkruaj(String file, List<String> items) {
        try {
            Files.createDirectories(Paths.get("data"));
            StringBuilder sb = new StringBuilder("[\n");
            for (int i = 0; i < items.size(); i++) {
                sb.append("  ").append(items.get(i));
                if (i < items.size() - 1) sb.append(",");
                sb.append("\n");
            }
            sb.append("]");
            Files.writeString(Paths.get("data/" + file), sb.toString());
        } catch (IOException e) {
            System.out.println("Gabim gjatë shkrimit: " + e.getMessage());
        }
    }

    public static List<String> lexo(String file) {
        List<String> items = new ArrayList<>();
        try {
            Path path = Paths.get("data/" + file);
            if (!Files.exists(path)) return items;
            String content = Files.readString(path).trim();
            if (content.length() < 3) return items;
            content = content.substring(1, content.length() - 1);
            String[] parts = content.split("\\},\\s*\\{");
            for (int i = 0; i < parts.length; i++) {
                String p = parts[i].trim();
                if (!p.startsWith("{")) p = "{" + p;
                if (!p.endsWith("}")) p = p + "}";
                items.add(p);
            }
        } catch (IOException e) {
            System.out.println("Gabim gjatë leximit: " + e.getMessage());
        }
        return items;
    }
}
