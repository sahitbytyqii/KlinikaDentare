package service;

import model.Rezervimi;
import storage.JsonStorage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.*;

public class RezervimService {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);
    private List<Rezervimi> rezervimet = new ArrayList<>();
    private int rezervimiTjeter = 1;

    public RezervimService() {
        boolean needsSave = false;
        for (String j : JsonStorage.lexo("rezervimet.json")) {
            Rezervimi reservation = Rezervimi.fromJson(j);
            rezervimet.add(reservation);
            int number = numberFromId(reservation.getNumriRezervimit());
            if (number >= rezervimiTjeter) rezervimiTjeter = number + 1;
        }
        for (Rezervimi reservation : rezervimet) {
            if (reservation.getNumriRezervimit().isEmpty()) {
                reservation.setNumriRezervimit(formatReservationId(rezervimiTjeter++));
                needsSave = true;
            }
        }
        if (needsSave) ruaj();
    }

    public boolean rezervo(String username, int sherbimiId, String data) {
        if (!eshteDateRezervimiLejuar(data)) return false;
        rezervimet.add(new Rezervimi(formatReservationId(rezervimiTjeter++), username, sherbimiId, data, 1, -1));
        ruaj();
        return true;
    }

    public boolean rezervo(String username, int sherbimiId, String data, int sasia, double cmimiNjesi) {
        return rezervo(username, "", "", sherbimiId, data, sasia, cmimiNjesi);
    }

    public boolean rezervo(String username, String emriKlientit, String telefoni,
            int sherbimiId, String data, int sasia, double cmimiNjesi) {
        Rezervimi line = new Rezervimi("", username, emriKlientit, telefoni, sherbimiId, data, sasia, cmimiNjesi);
        return rezervo(username, emriKlientit, telefoni, data, Collections.singletonList(line));
    }

    public boolean rezervo(String username, String emriKlientit, String telefoni,
            String data, List<Rezervimi> rreshtat) {
        if (!eshteDateRezervimiLejuar(data) || rreshtat == null || rreshtat.isEmpty()) return false;
        for (Rezervimi line : rreshtat) {
            if (line == null || line.getSasia() <= 0 || line.getCmimiNjesi() <= 0) return false;
        }

        String invoiceId = formatReservationId(rezervimiTjeter++);
        for (Rezervimi line : rreshtat) {
            rezervimet.add(new Rezervimi(invoiceId, username, emriKlientit, telefoni,
                    line.getSherbimiId(), data, line.getSasia(), line.getCmimiNjesi()));
        }
        ruaj();
        return true;
    }

    public static boolean eshteDateRezervimiLejuar(String data) {
        try {
            return !LocalDate.parse(data, DATE_FORMAT).isBefore(LocalDate.now());
        } catch (DateTimeParseException | NullPointerException ex) {
            return false;
        }
    }

    public List<Rezervimi> getRezervimetERevit(String username) {
        List<Rezervimi> rez = new ArrayList<>();
        for (Rezervimi r : rezervimet)
            if (r.getUsername().equals(username)) rez.add(r);
        return rez;
    }

    public List<Rezervimi> getAll() { return rezervimet; }

    private static int numberFromId(String id) {
        if (id == null || !id.matches("RE-\\d+")) return 0;
        try {
            return Integer.parseInt(id.substring(3));
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private static String formatReservationId(int number) {
        return String.format(Locale.ROOT, "RE-%03d", number);
    }

    private void ruaj() {
        List<String> list = new ArrayList<>();
        for (Rezervimi r : rezervimet) list.add(r.toJson());
        JsonStorage.shkruaj("rezervimet.json", list);
    }
}