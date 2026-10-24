package service;

import model.User;
import storage.JsonStorage;
import java.util.*;

public class AuthService {
    private List<User> users = new ArrayList<>();
    private User currentUser;

    public AuthService() {
        for (String j : JsonStorage.lexo("users.json")) {
            users.add(User.fromJson(j));
        }

        if (users.isEmpty()) {
            users.add(new User("admin", "admin", "Administratori", true));
            ruaj();
        }
    }

    public boolean regjistro(String username, String password, String emri) {
        return regjistro(username, password, emri, "");
    }

    public boolean regjistro(String username, String password, String emri, String telefoni) {
        if (username.isEmpty() || password.length() < 4 || emri.isEmpty()) {
            System.out.println("⚠ Të dhëna të pavlefshme! Password min. 4 karaktere.");
            return false;
        }
        for (User u : users) {
            if (u.getUsername().equals(username)) {
                System.out.println("⚠ Username ekziston!");
                return false;
            }
        }
        users.add(new User(username, password, emri, telefoni, "Perdorues", false));
        ruaj();
        return true;
    }

    public User gjejPerdoruesin(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) return user;
        }
        return null;
    }

    public boolean perditesoTelefonin(String username, String telefoni) {
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            if (user.getUsername().equals(username)) {
                users.set(i, new User(user.getUsername(), user.getPassword(), user.getEmri(), telefoni,
                        user.getRole(), user.isAdmin()));
                if (currentUser != null && currentUser.getUsername().equals(username)) {
                    currentUser = users.get(i);
                }
                ruaj();
                return true;
            }
        }
        return false;
    }

    public boolean login(String username, String password) {
        for (User u : users) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                currentUser = new User(u.getUsername(), u.getPassword(), u.getEmri(), u.getTelefoni(), u.getRole(), u.isAdmin());
                return true;
            }
        }
        return false;
    }

    public void logout() { currentUser = null; }
    public User getCurrentUser() { return currentUser; }
    public List<User> getAllUsers() { return users; }

    private void ruaj() {
        List<String> list = new ArrayList<>();
        for (User u : users) list.add(u.toJson());
        JsonStorage.shkruaj("users.json", list);
    }
}