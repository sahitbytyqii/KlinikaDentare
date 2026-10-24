package ui;

import model.*;
import service.*;
import java.util.*;

public class Menu {
    private Scanner sc = new Scanner(System.in);
    private AuthService auth = new AuthService();
    private SherbimService sherbimService = new SherbimService();
    private RezervimService rezervimService = new RezervimService();

    public void start() {
        while (true) {
            if (auth.getCurrentUser() == null) {
                loginMenu();
            } else if (auth.getCurrentUser().isAdmin()) {
                adminMenu();
            } else {
                userMenu();
            }
        }
    }

    private void loginMenu() {
        System.out.println("\n=== KLINIKA DENTARE ===");
        System.out.println("1. Login");
        System.out.println("2. Regjistrohu");
        System.out.println("0. Dil");
        System.out.print("Zgjedhja: ");
        String z = sc.nextLine();

        switch (z) {
            case "1":
                System.out.print("Username: ");
                String u = sc.nextLine();
                System.out.print("Password: ");
                String p = sc.nextLine();
                if (!auth.login(u, p)) System.out.println("⚠ Kredenciale të gabuara!");
                break;
            case "2":
                System.out.print("Username: ");
                String nu = sc.nextLine();
                System.out.print("Password: ");
                String np = sc.nextLine();
                System.out.print("Emri i plotë: ");
                String ne = sc.nextLine();
                if (auth.regjistro(nu, np, ne)) System.out.println("✓ Regjistrimi me sukses!");
                break;
            case "0":
                System.out.println("Mirupafshim!");
                System.exit(0);
        }
    }

    private void adminMenu() {
        System.out.println("\n=== ADMIN PANEL ===");
        System.out.println("1. Shiko të gjithë përdoruesit");
        System.out.println("2. Shiko të gjitha shërbimet");
        System.out.println("3. Shto shërbim");
        System.out.println("4. Fshij shërbim");
        System.out.println("5. Shiko të gjitha rezervimet");
        System.out.println("0. Logout");
        System.out.print("Zgjedhja: ");
        String z = sc.nextLine();

        switch (z) {
            case "1":
                System.out.println("\n-- Përdoruesit --");
                for (User u : auth.getAllUsers()) System.out.println("  " + u);
                break;
            case "2":
                System.out.println("\n-- Shërbimet --");
                for (Sherbimi s : sherbimService.getAll()) System.out.println("  " + s);
                break;
            case "3":
                System.out.print("Emri: ");
                String e = sc.nextLine();
                System.out.print("Përshkrimi: ");
                String p = sc.nextLine();
                System.out.print("Kohëzgjatja (min): ");
                int k = Integer.parseInt(sc.nextLine());
                System.out.print("Çmimi (€): ");
                double c = Double.parseDouble(sc.nextLine());
                if (sherbimService.shto(e, p, k, c)) System.out.println("✓ U shtua!");
                break;
            case "4":
                System.out.print("ID e shërbimit: ");
                int id = Integer.parseInt(sc.nextLine());
                if (sherbimService.fshij(id)) System.out.println("✓ U fshi!");
                else System.out.println("⚠ Nuk u gjet!");
                break;
            case "5":
                System.out.println("\n-- Rezervimet --");
                for (Rezervimi r : rezervimService.getAll()) {
                    Sherbimi s = sherbimService.gjej(r.getSherbimiId());
                    System.out.println("  " + r.getUsername() + " -> " +
                        (s != null ? s.getEmri() : "?") + " (" + r.getData() + ")");
                }
                break;
            case "0":
                auth.logout();
                break;
        }
    }

    private void userMenu() {
        System.out.println("\n=== PACIENTI: " + auth.getCurrentUser().getEmri() + " ===");
        System.out.println("1. Shiko shërbimet");
        System.out.println("2. Rezervo shërbim");
        System.out.println("3. Rezervimet e mia");
        System.out.println("0. Logout");
        System.out.print("Zgjedhja: ");
        String z = sc.nextLine();

        switch (z) {
            case "1":
                for (Sherbimi s : sherbimService.getAll()) System.out.println("  " + s);
                break;
            case "2":
                for (Sherbimi s : sherbimService.getAll()) System.out.println("  " + s);
                System.out.print("ID e shërbimit: ");
                int id = Integer.parseInt(sc.nextLine());
                if (sherbimService.gjej(id) == null) {
                    System.out.println("⚠ Shërbimi nuk ekziston!");
                    break;
                }
                System.out.print("Data (dd/mm/yyyy): ");
                String d = sc.nextLine();
                if (rezervimService.rezervo(auth.getCurrentUser().getUsername(), id, d)) {
                    System.out.println("✓ Rezervimi u krye!");
                } else {
                    System.out.println("⚠ Data duhet të jetë sot ose në një ditë të ardhshme (dd/mm/yyyy).");
                }
                break;
            case "3":
                System.out.println("\n-- Rezervimet e mia --");
                for (Rezervimi r : rezervimService.getRezervimetERevit(
                        auth.getCurrentUser().getUsername())) {
                    Sherbimi s = sherbimService.gjej(r.getSherbimiId());
                    System.out.println("  " + (s != null ? s.getEmri() : "?") + " - " + r.getData());
                }
                break;
            case "0":
                auth.logout();
                break;
        }
    }
}