package service;

import model.Sherbimi;
import storage.JsonStorage;
import java.util.*;

public class SherbimService {
    private List<Sherbimi> sherbimet = new ArrayList<>();
    private int nextId = 1;

    public SherbimService() {
        for (String j : JsonStorage.lexo("sherbimet.json")) {
            Sherbimi s = Sherbimi.fromJson(j);
            if (s.getShtuarNga() == null || s.getShtuarNga().isEmpty() || s.getShtuarNga().equalsIgnoreCase("null")) {
                s = new Sherbimi(s.getId(), s.getEmri(), s.getPershkrimi(), s.getKohezgjatjaMin(), s.getCmimi(), "admin");
            }
            sherbimet.add(s);
            if (s.getId() >= nextId) nextId = s.getId() + 1;
        }
        if (sherbimet.isEmpty()) {
            shto("Kontroll", "Kontroll i përgjithshëm dentar", 20, 15.0);
            shto("Pastrim", "Pastrim profesional i dhëmbëve", 30, 25.0);
            shto("Mbushje", "Mbushje e dhëmbit të prishur", 45, 40.0);
            ruaj();
        }
    }

    public boolean shto(String emri, String pershkrimi, int kohezgjatja, double cmimi) {
        return shto(emri, pershkrimi, kohezgjatja, cmimi, "admin");
    }

    public boolean shto(String emri, String pershkrimi, int kohezgjatja, double cmimi, String shtuarNga) {
        if (emri.isEmpty() || kohezgjatja <= 0 || cmimi <= 0) {
            System.out.println("⚠ Të dhëna të pavlefshme!");
            return false;
        }
        sherbimet.add(new Sherbimi(nextId++, emri, pershkrimi, kohezgjatja, cmimi, shtuarNga));
        ruaj();
        return true;
    }

    public boolean fshij(int id) {
        boolean removed = sherbimet.removeIf(s -> s.getId() == id);
        if (removed) ruaj();
        return removed;
    }

    public boolean edito(int id, String emri, String pershkrimi, int kohezgjatja, double cmimi) {
        if (emri == null || emri.trim().isEmpty() || kohezgjatja <= 0 || cmimi <= 0) return false;
        for (int i = 0; i < sherbimet.size(); i++) {
            Sherbimi current = sherbimet.get(i);
            if (current.getId() == id) {
                sherbimet.set(i, new Sherbimi(id, emri.trim(), pershkrimi == null ? "" : pershkrimi.trim(),
                        kohezgjatja, cmimi, current.getShtuarNga()));
                ruaj();
                return true;
            }
        }
        return false;
    }

    public List<Sherbimi> getAll() { return sherbimet; }

    public Sherbimi gjej(int id) {
        for (Sherbimi s : sherbimet)
            if (s.getId() == id) return s;
        return null;
    }

    private void ruaj() {
        List<String> list = new ArrayList<>();
        for (Sherbimi s : sherbimet) list.add(s.toJson());
        JsonStorage.shkruaj("sherbimet.json", list);
    }
}