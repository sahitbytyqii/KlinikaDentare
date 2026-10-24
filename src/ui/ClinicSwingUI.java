package ui;

import model.Rezervimi;
import model.Sherbimi;
import model.User;
import service.AuthService;
import service.RezervimService;
import service.SherbimService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ClinicSwingUI extends JFrame {
    private static final Color BG = new Color(236, 244, 255);
    private static final Color PANEL = new Color(255, 255, 255);
    private static final Color ACCENT = new Color(30, 110, 255);
    private static final Color ACCENT_DARK = new Color(13, 76, 188);
    private static final Color ADMIN_ACCENT = new Color(15, 112, 98);
    private static final DateTimeFormatter BOOKING_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/uuuu");
    private static final Color TEXT = new Color(30, 41, 59);

    private final AuthService authService = new AuthService();
    private final SherbimService sherbimService = new SherbimService();
    private final RezervimService rezervimService = new RezervimService();

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);

    private final JTextField usernameField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final JLabel adminWelcomeLabel = new JLabel("Paneli i Administratorit");
    private final JLabel userWelcomeLabel = new JLabel("Paneli i përdoruesit");
    private final JLabel adminReservationsSummary = new JLabel();
    private final JLabel userReservationsSummary = new JLabel();
    private final JLabel adminServiceCount = new JLabel("0");
    private final JLabel adminPatientCount = new JLabel("0");
    private final JLabel adminReservationCount = new JLabel("0");
    private final JLabel userServiceCount = new JLabel("0");
    private final JLabel userReservationCount = new JLabel("0");
    private final JLabel userReservationTotal = new JLabel("0.00 €");
    private final JCheckBox showAccountPasswords = new JCheckBox("Shfaq fjalëkalimet");
    private final JComboBox<User> serviceOwnerSelector = new JComboBox<>();
    private final JLabel servicesByUserStatus = new JLabel("Zgjidh përdoruesin për të parë shërbimet e tij.");
    private final CardLayout adminContentLayout = new CardLayout();
    private final CardLayout userContentLayout = new CardLayout();
    private final JPanel adminContent = new JPanel(adminContentLayout);
    private final JPanel userContent = new JPanel(userContentLayout);
    private final DefaultTableModel adminServicesModel = readOnlyModel(
            new Object[] { "ID", "Shërbimi", "Përshkrimi", "Minuta", "Çmimi (€)", "Shtuar nga" });
    private final DefaultTableModel userServicesModel = readOnlyModel(
            new Object[] { "ID", "Shërbimi", "Përshkrimi", "Minuta", "Çmimi (€)" });
        private final DefaultTableModel adminUsersModel = readOnlyModel(
            new Object[] { "Emri", "Username", "Fjalëkalimi", "Roli" });
    private final DefaultTableModel adminByUserModel = readOnlyModel(
            new Object[] { "Përdoruesi", "Shërbimi", "Përshkrimi", "Minuta", "Çmimi (€)" });
    private final DefaultTableModel adminReservationsModel = readOnlyModel(
            new Object[] { "Nr. rezervimit", "Llogaria", "Data", "Nr. shërbimeve", "Totali (€)" });
    private final DefaultTableModel userReservationsModel = readOnlyModel(
            new Object[] { "Nr. rezervimit", "Data", "Nr. shërbimeve", "Totali (€)" });
    private final JTable adminServicesTable = new JTable(adminServicesModel);
    private final JTable userServicesTable = new JTable(userServicesModel);
    private final JTable adminUsersTable = new JTable(adminUsersModel);
    private final JTable adminByUserTable = new JTable(adminByUserModel);
    private final JTable adminReservationsTable = new JTable(adminReservationsModel);
    private final JTable userReservationsTable = new JTable(userReservationsModel);

    public ClinicSwingUI() {
        setTitle("Klinika Dentare");
        setSize(1000, 660);
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);

        cards.add(createLoginPanel(), "login");
        cards.add(createAdminPanel(), "admin");
        cards.add(createUserPanel(), "user");
        cards.add(createReservationsPanel("admin"), "adminReservations");
        cards.add(createReservationsPanel("user"), "userReservations");

        setContentPane(cards);
        showLogin();
        setVisible(true);
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(218, 228, 238)),
            BorderFactory.createEmptyBorder(30, 32, 30, 32)));
        form.setPreferredSize(new Dimension(390, 410));

        JLabel title = new JLabel("Mirë se u ktheve");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel subtitle = new JLabel("Kyçu në llogarinë tënde");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(new Color(100, 116, 139));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel userLabel = loginLabel("Username");
        JLabel passLabel = loginLabel("Password");
        styleLoginField(usernameField);
        styleLoginField(passwordField);
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        JButton loginButton = new JButton("Kyçu");
        JButton registerButton = new JButton("Regjistrohu");
        styleButton(loginButton, ADMIN_ACCENT);
        registerButton.setBackground(Color.WHITE);
        registerButton.setForeground(ADMIN_ACCENT);
        registerButton.setFocusPainted(false);
        registerButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ADMIN_ACCENT),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)));
        registerButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        loginButton.addActionListener(e -> login());
        registerButton.addActionListener(e -> register());
        usernameField.addActionListener(e -> login());
        passwordField.addActionListener(e -> login());

        JPanel buttons = new JPanel(new GridLayout(1, 2, 12, 0));
        buttons.setBackground(Color.WHITE);
        buttons.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        buttons.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttons.add(loginButton);
        buttons.add(registerButton);

        form.add(title);
        form.add(Box.createVerticalStrut(6));
        form.add(subtitle);
        form.add(Box.createVerticalStrut(28));
        form.add(userLabel);
        form.add(Box.createVerticalStrut(7));
        form.add(usernameField);
        form.add(Box.createVerticalStrut(18));
        form.add(passLabel);
        form.add(Box.createVerticalStrut(7));
        form.add(passwordField);
        form.add(Box.createVerticalStrut(26));
        form.add(buttons);
        getRootPane().setDefaultButton(loginButton);
        panel.add(form);
        return panel;
    }

    private JLabel loginLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private void styleLoginField(JTextField field) {
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
    }

    private JPanel createAdminPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(createDashboardHeader(adminWelcomeLabel, "ADMINISTRATOR", ADMIN_ACCENT), BorderLayout.NORTH);

        styleServiceTable(adminServicesTable, ADMIN_ACCENT);
        styleServiceTable(adminByUserTable, ADMIN_ACCENT, 4);
        styleServiceTable(adminUsersTable, ADMIN_ACCENT);
        adminContent.add(new JScrollPane(adminServicesTable), "services");
        adminContent.add(createAdminServicesByUserPanel(), "servicesByUserWorkspace");
        adminContent.add(createAdminUsersPanel(), "users");
        adminContent.setBackground(Color.WHITE);
        adminContent.setBorder(BorderFactory.createLineBorder(new Color(218, 228, 238)));
        JPanel main = new JPanel(new BorderLayout(0, 12));
        main.setBackground(BG);
        main.add(createMetricStrip(
            createMetricCard("Shërbime", adminServiceCount, ADMIN_ACCENT),
            createMetricCard("Përdorues", adminPatientCount, ADMIN_ACCENT),
            createMetricCard("Rezervime", adminReservationCount, ADMIN_ACCENT)), BorderLayout.NORTH);
        main.add(adminContent, BorderLayout.CENTER);
        panel.add(main, BorderLayout.CENTER);

        JPanel actions = new JPanel(new GridLayout(0, 1, 8, 8));
        actions.setBackground(BG);
        JButton servicesBtn = button("Të gjitha shërbimet", this::listServices);
        JButton usersBtn = button("Përdoruesit dhe fjalëkalimet", this::listUsers);
        JButton byUserBtn = button("Shërbimet sipas përdoruesit", this::listServicesByUser);
        JButton addBtn = button("Shto shërbim", this::addService);
        JButton editBtn = button("Edito shërbim", this::editService);
        JButton deleteBtn = button("Fshij shërbim", this::deleteService);
        JButton reservationsBtn = button("Të gjitha rezervimet", this::showAdminReservations);
        JButton logoutBtn = button("Dil nga llogaria", this::logout);
        for (JButton btn : new JButton[] { servicesBtn, usersBtn, byUserBtn, addBtn, editBtn, deleteBtn, reservationsBtn, logoutBtn }) {
            styleButton(btn, btn == deleteBtn ? new Color(190, 58, 58) : ADMIN_ACCENT);
            btn.setPreferredSize(new Dimension(205, 42));
        }
        actions.add(servicesBtn);
        actions.add(usersBtn);
        actions.add(byUserBtn);
        actions.add(addBtn);
        actions.add(editBtn);
        actions.add(deleteBtn);
        actions.add(reservationsBtn);
        actions.add(logoutBtn);
        panel.add(createActionRail("MENAXHIMI", actions), BorderLayout.EAST);

        return panel;
    }

    private JPanel createUserPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        panel.add(createPatientHeader(), BorderLayout.NORTH);

        styleServiceTable(userServicesTable, ACCENT);
        userContent.add(new JScrollPane(userServicesTable), "services");
        userContent.setBackground(Color.WHITE);
        userContent.setBorder(BorderFactory.createLineBorder(new Color(218, 228, 238)));
        JPanel main = new JPanel(new BorderLayout(0, 12));
        main.setBackground(BG);
        main.add(createMetricStrip(
            createMetricCard("Shërbime në klinikë", userServiceCount, ACCENT),
            createMetricCard("Rezervimet e mia", userReservationCount, ACCENT),
            createMetricCard("Vlera e rezervimeve", userReservationTotal, ACCENT)), BorderLayout.NORTH);
        main.add(userContent, BorderLayout.CENTER);
        panel.add(main, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttons.setBackground(BG);
        JButton servicesBtn = button("Shiko shërbimet", this::listServices);
        JButton addServiceBtn = button("Shto shërbim", this::addService);
        JButton reserveBtn = button("Rezervo shërbim", this::reserveService);
        JButton myResBtn = button("Rezervimet e mia", this::showUserReservations);
        JButton logoutBtn = button("Logout", this::logout);
        styleButton(servicesBtn, ACCENT_DARK);
        styleButton(addServiceBtn, ADMIN_ACCENT);
        styleButton(reserveBtn, new Color(14, 165, 160));
        styleButton(myResBtn, ACCENT);
        styleButton(logoutBtn, new Color(100, 116, 139));
        buttons.add(servicesBtn);
        buttons.add(addServiceBtn);
        buttons.add(reserveBtn);
        buttons.add(myResBtn);
        buttons.add(logoutBtn);
        JPanel toolbar = new JPanel(new BorderLayout(0, 8));
        toolbar.setBackground(BG);
        JLabel toolbarLabel = new JLabel("QASJE E SHPEJTË");
        toolbarLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        toolbarLabel.setForeground(TEXT);
        toolbar.add(toolbarLabel, BorderLayout.NORTH);
        toolbar.add(buttons, BorderLayout.CENTER);
        panel.add(toolbar, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createPatientHeader() {
        JPanel header = new JPanel(new BorderLayout(12, 4));
        header.setBackground(new Color(239, 247, 255));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, ACCENT),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)));
        userWelcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        userWelcomeLabel.setForeground(ACCENT_DARK);
        JPanel titles = new JPanel();
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        titles.setOpaque(false);
        titles.add(userWelcomeLabel);
        JLabel subtitle = new JLabel("Shërbimet e klinikës dhe rezervimet e tua");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(new Color(71, 85, 105));
        titles.add(Box.createVerticalStrut(4));
        titles.add(subtitle);
        JLabel role = new JLabel("PËRDORUES");
        role.setFont(new Font("SansSerif", Font.BOLD, 11));
        role.setForeground(ACCENT_DARK);
        role.setOpaque(true);
        role.setBackground(new Color(214, 232, 252));
        role.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        header.add(titles, BorderLayout.CENTER);
        header.add(role, BorderLayout.EAST);
        return header;
    }

    private JPanel createReservationsPanel(String type) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        boolean admin = "admin".equals(type);
        JLabel title = new JLabel(admin ? "Të gjitha rezervimet" : "Rezervimet e mia");
        JLabel summary = admin ? adminReservationsSummary : userReservationsSummary;
        JButton backButton = button("Kthehu te paneli", admin ? this::showAdminDashboard : this::showUserDashboard);
        styleButton(backButton, admin ? ADMIN_ACCENT : ACCENT);
        JPanel heading = new JPanel(new BorderLayout(12, 4));
        heading.setBackground(BG);
        JPanel labels = new JPanel(new GridLayout(0, 1, 2, 2));
        labels.setBackground(BG);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(admin ? ADMIN_ACCENT : ACCENT_DARK);
        summary.setForeground(TEXT);
        labels.add(title);
        labels.add(summary);
        heading.add(labels, BorderLayout.CENTER);
        heading.add(backButton, BorderLayout.EAST);
        panel.add(heading, BorderLayout.NORTH);

        JTable table = admin ? adminReservationsTable : userReservationsTable;
        styleServiceTable(table, admin ? ADMIN_ACCENT : ACCENT, admin ? new int[] { 4 } : new int[] { 3 });
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                int row = table.rowAtPoint(event.getPoint());
                if (row >= 0) showReservationDetails(table, row, admin);
            }
        });
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAdminServicesByUserPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("Shërbimet e një përdoruesi");
        title.setFont(new Font("SansSerif", Font.BOLD, 17));
        title.setForeground(TEXT);
        serviceOwnerSelector.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof User) {
                    User user = (User) value;
                    setText(user.getEmri() + " (" + user.getUsername() + ")");
                }
                return this;
            }
        });
        serviceOwnerSelector.addActionListener(event -> refreshServicesForSelectedUser());

        JPanel selectorPanel = new JPanel(new BorderLayout(10, 8));
        selectorPanel.setBackground(Color.WHITE);
        selectorPanel.add(title, BorderLayout.NORTH);
        JPanel selectorRow = new JPanel(new BorderLayout(8, 0));
        selectorRow.setBackground(Color.WHITE);
        JLabel prompt = new JLabel("Përdoruesi:");
        prompt.setForeground(TEXT);
        selectorRow.add(prompt, BorderLayout.WEST);
        selectorRow.add(serviceOwnerSelector, BorderLayout.CENTER);
        selectorPanel.add(selectorRow, BorderLayout.CENTER);
        servicesByUserStatus.setForeground(new Color(71, 85, 105));
        selectorPanel.add(servicesByUserStatus, BorderLayout.SOUTH);
        panel.add(selectorPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(adminByUserTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createAdminUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBackground(Color.WHITE);
        JLabel heading = new JLabel("Llogaritë e përdoruesve");
        heading.setFont(new Font("SansSerif", Font.BOLD, 16));
        heading.setForeground(TEXT);
        showAccountPasswords.setOpaque(false);
        showAccountPasswords.setForeground(TEXT);
        showAccountPasswords.addActionListener(event -> refreshAdminUserRows());
        toolbar.add(heading, BorderLayout.WEST);
        toolbar.add(showAccountPasswords, BorderLayout.EAST);
        panel.add(toolbar, BorderLayout.NORTH);
        panel.add(new JScrollPane(adminUsersTable), BorderLayout.CENTER);
        return panel;
    }

    private JButton button(String label, Runnable action) {
        JButton btn = new JButton(label);
        btn.addActionListener(e -> action.run());
        return btn;
    }

    private void styleButton(JButton button) {
        styleButton(button, ACCENT);
    }

    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
    }

    private void showLogin() {
        cardLayout.show(cards, "login");
        usernameField.setText("");
        passwordField.setText("");
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (authService.login(username, password)) {
            if (authService.getCurrentUser().isAdmin()) {
                showAdminDashboard();
            } else {
                showUserDashboard();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Kredenciale të gabuara!", "Gabim", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void register() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String emri = JOptionPane.showInputDialog(this, "Shkruaj emrin e plotë:");

        if (username.isEmpty() || password.isEmpty() || emri == null || emri.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Plotëso të dhënat e sakta.", "Gabim", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String telefoni = JOptionPane.showInputDialog(this, "Numri i telefonit:");
        if (telefoni == null || telefoni.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Numri i telefonit është i nevojshëm për rezervime.", "Të dhëna të paplota", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (authService.regjistro(username, password, emri.trim(), telefoni.trim())) {
            JOptionPane.showMessageDialog(this, "Regjistrimi u krye me sukses!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            showLogin();
        } else {
            JOptionPane.showMessageDialog(this, "Username ekziston ose të dhënat janë të pavlefshme.", "Gabim", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAdminDashboard() {
        cardLayout.show(cards, "admin");
        showAccountPasswords.setSelected(false);
        adminWelcomeLabel.setText("Mirë se erdhe, " + authService.getCurrentUser().getEmri());
        listServices();
    }

    private void showUserDashboard() {
        cardLayout.show(cards, "user");
        userWelcomeLabel.setText("Mirë se erdhe, " + authService.getCurrentUser().getEmri());
        listServices();
    }

    private void logout() {
        authService.logout();
        showLogin();
    }

    private void listUsers() {
        refreshAdminUserRows();
        adminContentLayout.show(adminContent, "users");
    }

    private void refreshAdminUserRows() {
        adminUsersModel.setRowCount(0);
        for (User u : authService.getAllUsers()) {
            boolean administrator = u.isAdmin() || u.getUsername().equalsIgnoreCase("admin");
            String password = administrator ? "E fshehur" : (showAccountPasswords.isSelected() ? u.getPassword() : "••••••••");
            adminUsersModel.addRow(new Object[] { u.getEmri(), u.getUsername(), password,
                    u.getRole() });
        }
    }

    private void listServices() {
        boolean admin = authService.getCurrentUser() != null && authService.getCurrentUser().isAdmin();
        DefaultTableModel model = admin ? adminServicesModel : userServicesModel;
        model.setRowCount(0);
        for (Sherbimi s : sherbimService.getAll()) {
                Object[] row = admin
                    ? new Object[] { s.getId(), s.getEmri(), s.getPershkrimi(), s.getKohezgjatjaMin(), s.getCmimi(), s.getShtuarNga() }
                    : new Object[] { s.getId(), s.getEmri(), s.getPershkrimi(), s.getKohezgjatjaMin(), s.getCmimi() };
            model.addRow(row);
        }
        updateDashboardMetrics();
        if (admin) adminContentLayout.show(adminContent, "services");
        else userContentLayout.show(userContent, "services");
    }

    private void listServicesByUser() {
        serviceOwnerSelector.removeAllItems();
        for (User user : authService.getAllUsers()) serviceOwnerSelector.addItem(user);
        serviceOwnerSelector.setSelectedIndex(-1);
        adminByUserModel.setRowCount(0);
        servicesByUserStatus.setText("Zgjidh një përdorues nga lista.");
        adminContentLayout.show(adminContent, "servicesByUserWorkspace");
    }

    private void refreshServicesForSelectedUser() {
        adminByUserModel.setRowCount(0);
        User selectedUser = (User) serviceOwnerSelector.getSelectedItem();
        if (selectedUser == null) {
            servicesByUserStatus.setText("Zgjidh një përdorues nga lista.");
            return;
        }

        int count = 0;
        for (Sherbimi service : sherbimService.getAll()) {
            if (selectedUser.getUsername().equals(service.getShtuarNga())) {
                adminByUserModel.addRow(new Object[] { selectedUser.getEmri() + " (" + selectedUser.getUsername() + ")",
                        service.getEmri(), service.getPershkrimi(), service.getKohezgjatjaMin(), service.getCmimi() });
                count++;
            }
        }
        servicesByUserStatus.setText(count == 0
                ? "Ky përdorues nuk ka shtuar shërbime."
                : count + (count == 1 ? " shërbim" : " shërbime"));
    }

    private void addService() {
        String emri = JOptionPane.showInputDialog(this, "Emri i shërbimit:");
        if (emri == null || emri.trim().isEmpty()) return;

        String pershkrimi = JOptionPane.showInputDialog(this, "Përshkrimi:");
        if (pershkrimi == null) return;

        String kohezgjatjaText = JOptionPane.showInputDialog(this, "Kohëzgjatja në minuta:");
        if (kohezgjatjaText == null) return;

        String cmimiText = JOptionPane.showInputDialog(this, "Çmimi (€):");
        if (cmimiText == null) return;

        try {
            int kohezgjatja = Integer.parseInt(kohezgjatjaText);
            double cmimi = Double.parseDouble(cmimiText);
            String shtuarNga = authService.getCurrentUser() != null ? authService.getCurrentUser().getUsername() : "admin";
            if (sherbimService.shto(emri.trim(), pershkrimi.trim(), kohezgjatja, cmimi, shtuarNga)) {
                JOptionPane.showMessageDialog(this, "Shërbimi u shtua me sukses!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                listServices();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Vendos numra të sakta.", "Gabim", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editService() {
        String input = JOptionPane.showInputDialog(this, "ID e shërbimit për editim:");
        if (input == null || input.trim().isEmpty()) return;

        try {
            int id = Integer.parseInt(input.trim());
            Sherbimi service = sherbimService.gjej(id);
            if (service == null) {
                JOptionPane.showMessageDialog(this, "Shërbimi nuk u gjet.", "Gabim", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JTextField name = new JTextField(service.getEmri());
            JTextField description = new JTextField(service.getPershkrimi());
            JTextField duration = new JTextField(String.valueOf(service.getKohezgjatjaMin()));
            JTextField price = new JTextField(String.valueOf(service.getCmimi()));
            JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
            form.add(new JLabel("Emri:")); form.add(name);
            form.add(new JLabel("Përshkrimi:")); form.add(description);
            form.add(new JLabel("Kohëzgjatja (min):")); form.add(duration);
            form.add(new JLabel("Çmimi (€):")); form.add(price);
            if (JOptionPane.showConfirmDialog(this, form, "Edito shërbimin " + id,
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;

            int minutes = Integer.parseInt(duration.getText().trim());
            double amount = Double.parseDouble(price.getText().trim().replace(',', '.'));
            if (!sherbimService.edito(id, name.getText().trim(), description.getText().trim(), minutes, amount)) {
                JOptionPane.showMessageDialog(this, "Të dhënat nuk janë të vlefshme.", "Gabim", JOptionPane.ERROR_MESSAGE);
                return;
            }
            listServices();
            JOptionPane.showMessageDialog(this, "Shërbimi u përditësua.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Kohëzgjatja dhe çmimi duhet të jenë numra.", "Gabim", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteService() {
        String input = JOptionPane.showInputDialog(this, "ID e shërbimit për t'u fshirë:");
        if (input == null || input.trim().isEmpty()) return;

        try {
            int id = Integer.parseInt(input);
            if (sherbimService.fshij(id)) {
                JOptionPane.showMessageDialog(this, "Shërbimi u fshi.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                listServices();
            } else {
                JOptionPane.showMessageDialog(this, "Shërbimi nuk u gjet.", "Gabim", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID duhet të jetë numër.", "Gabim", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAdminReservations() {
        adminReservationsModel.setRowCount(0);
        Map<String, List<Rezervimi>> invoices = groupReservations(rezervimService.getAll());
        double total = 0;
        for (Map.Entry<String, List<Rezervimi>> invoice : invoices.entrySet()) {
            Rezervimi first = invoice.getValue().get(0);
            double invoiceTotal = totalForInvoice(invoice.getValue());
            total += invoiceTotal;
            adminReservationsModel.addRow(new Object[] { invoice.getKey(), first.getUsername(), first.getData(),
                    invoice.getValue().size(), invoiceTotal });
        }
        adminReservationsSummary.setText(String.format("%d fatura     Totali: %.2f €", invoices.size(), total));
        updateDashboardMetrics();
        cardLayout.show(cards, "adminReservations");
    }

    private void showUserReservations() {
        List<Rezervimi> reservations = rezervimService.getRezervimetERevit(authService.getCurrentUser().getUsername());
        Map<String, List<Rezervimi>> invoices = groupReservations(reservations);
        userReservationsModel.setRowCount(0);
        double total = 0;
        for (Map.Entry<String, List<Rezervimi>> invoice : invoices.entrySet()) {
            Rezervimi first = invoice.getValue().get(0);
            double invoiceTotal = totalForInvoice(invoice.getValue());
            total += invoiceTotal;
            userReservationsModel.addRow(new Object[] { invoice.getKey(), first.getData(),
                    invoice.getValue().size(), invoiceTotal });
        }
        userReservationsSummary.setText(String.format("%d fatura     Totali: %.2f €", invoices.size(), total));
        updateDashboardMetrics();
        cardLayout.show(cards, "userReservations");
    }

    private void showReservationDetails(JTable table, int viewRow, boolean adminView) {
        int modelRow = table.convertRowIndexToModel(viewRow);
        String reservationId = String.valueOf(table.getModel().getValueAt(modelRow, 0));
        List<Rezervimi> invoiceRows = groupReservations(rezervimService.getAll()).get(reservationId);
        if (invoiceRows == null || invoiceRows.isEmpty()) return;
        Rezervimi reservation = invoiceRows.get(0);

        double total = totalForInvoice(invoiceRows);
        User owner = authService.gjejPerdoruesin(reservation.getUsername());
        String customerName = reservation.getEmriKlientit().trim().isEmpty()
            ? (owner == null ? reservation.getUsername() : owner.getEmri())
            : reservation.getEmriKlientit();
        String phone = reservation.getTelefoni().trim().isEmpty()
            ? (owner == null || owner.getTelefoni().trim().isEmpty() ? "Nuk është dhënë" : owner.getTelefoni())
            : reservation.getTelefoni();

        JDialog dialog = new JDialog(this, "Fatura e rezervimit " + reservation.getNumriRezervimit(), true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        JPanel invoice = new JPanel(new BorderLayout(0, 18));
        invoice.setBackground(Color.WHITE);
        invoice.setBorder(BorderFactory.createEmptyBorder(22, 24, 18, 24));

        JPanel invoiceHeader = new JPanel(new BorderLayout(10, 4));
        invoiceHeader.setBackground(ADMIN_ACCENT);
        invoiceHeader.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        JLabel heading = new JLabel("REZERVIMI " + reservation.getNumriRezervimit());
        heading.setForeground(Color.WHITE);
        heading.setFont(new Font("SansSerif", Font.BOLD, 20));
        JLabel dateLabel = new JLabel(reservation.getData());
        dateLabel.setForeground(Color.WHITE);
        dateLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        invoiceHeader.add(heading, BorderLayout.WEST);
        invoiceHeader.add(dateLabel, BorderLayout.EAST);
        invoice.add(invoiceHeader, BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 16));
        body.setBackground(Color.WHITE);
        JPanel person = new JPanel(new GridLayout(0, 2, 18, 10));
        person.setBackground(Color.WHITE);
        addReservationDetail(person, "Emri dhe mbiemri", customerName);
        addReservationDetail(person, "Data e rezervimit", reservation.getData());
        addReservationDetail(person, "Numri i telefonit", phone);
        if (adminView) addReservationDetail(person, "Llogaria e përdoruesit", reservation.getUsername());
        body.add(person, BorderLayout.NORTH);

        DefaultTableModel serviceModel = readOnlyModel(
            new Object[] { "Shërbimi", "Përshkrimi", "Sasia", "Çmimi/njësi (€)", "Totali (€)" });
        for (Rezervimi line : invoiceRows) {
            Sherbimi service = sherbimService.gjej(line.getSherbimiId());
            double unitPrice = line.getCmimiNjesi() > 0
                ? line.getCmimiNjesi() : (service == null ? 0 : service.getCmimi());
            serviceModel.addRow(new Object[] {
                service == null ? "Shërbim i fshirë" : service.getEmri(),
                service == null ? "" : service.getPershkrimi(),
                line.getSasia(), unitPrice, unitPrice * line.getSasia()
            });
        }
        JTable serviceTable = new JTable(serviceModel);
        styleServiceTable(serviceTable, ADMIN_ACCENT, 3, 4);
        serviceTable.setRowHeight(34);
        body.add(new JScrollPane(serviceTable), BorderLayout.CENTER);

        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setBackground(Color.WHITE);
        JLabel totalTitle = new JLabel("TOTALI");
        totalTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        totalTitle.setForeground(TEXT);
        JLabel totalValue = new JLabel(String.format("%.2f €", total), SwingConstants.RIGHT);
        totalValue.setFont(new Font("SansSerif", Font.BOLD, 22));
        totalValue.setForeground(ADMIN_ACCENT);
        totalPanel.add(totalTitle, BorderLayout.WEST);
        totalPanel.add(totalValue, BorderLayout.EAST);
        body.add(totalPanel, BorderLayout.SOUTH);
        invoice.add(body, BorderLayout.CENTER);

        JButton close = new JButton("Mbyll faturën");
        styleButton(close, ADMIN_ACCENT);
        close.addActionListener(event -> dialog.dispose());
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        footer.setBackground(Color.WHITE);
        footer.add(close);
        invoice.add(footer, BorderLayout.SOUTH);

        dialog.setContentPane(invoice);
        dialog.setSize(760, 470);
        dialog.setMinimumSize(new Dimension(680, 430));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void addReservationDetail(JPanel panel, String label, String value) {
        JLabel name = new JLabel(label);
        name.setFont(new Font("SansSerif", Font.BOLD, 12));
        name.setForeground(TEXT);
        JLabel content = new JLabel(value);
        content.setFont(new Font("SansSerif", Font.PLAIN, 12));
        panel.add(name);
        panel.add(content);
    }

    private void listReservations() {
        showAdminReservations();
    }

    private void reserveService() {
        List<Sherbimi> services = sherbimService.getAll();
        if (services.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nuk ka shërbime për rezervim.", "Pa shërbime", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        List<ReservationEntry> selectedLines = chooseReservationServices(services);
        if (selectedLines == null) return;

        User currentUser = authService.getCurrentUser();
        String phone = currentUser.getTelefoni().trim();
        if (phone.isEmpty()) {
            phone = JOptionPane.showInputDialog(this, "Shkruaj numrin e telefonit për faturën:");
            if (phone == null || phone.trim().isEmpty()) return;
            phone = phone.trim();
            authService.perditesoTelefonin(currentUser.getUsername(), phone);
            currentUser = authService.getCurrentUser();
        }

        LocalDate selectedDate = chooseReservationDate();
        if (selectedDate == null) return;
        String data = BOOKING_DATE_FORMAT.format(selectedDate);
        List<Rezervimi> invoiceLines = new ArrayList<>();
        for (ReservationEntry entry : selectedLines) {
            invoiceLines.add(new Rezervimi("", currentUser.getUsername(), currentUser.getEmri(), phone,
                    entry.service.getId(), data, entry.quantity, entry.service.getCmimi()));
        }

        if (!rezervimService.rezervo(currentUser.getUsername(), currentUser.getEmri(), phone, data, invoiceLines)) {
            JOptionPane.showMessageDialog(this, "Të dhënat e faturës ose data nuk janë të vlefshme.", "Rezervim i pavlefshëm", JOptionPane.WARNING_MESSAGE);
            return;
        }
        updateDashboardMetrics();
        JOptionPane.showMessageDialog(this, "Fatura e rezervimit u krijua me sukses.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
        showUserReservations();
    }

    private List<ReservationEntry> chooseReservationServices(List<Sherbimi> services) {
        JSpinner countSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
        JPanel countPanel = new JPanel(new GridLayout(0, 1, 0, 8));
        countPanel.add(new JLabel("Sa shërbime të ndryshme dëshironi në këtë faturë?"));
        countPanel.add(countSpinner);
        if (JOptionPane.showConfirmDialog(this, countPanel, "Numri i shërbimeve",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return null;

        int count = (Integer) countSpinner.getValue();
        JPanel linesPanel = new JPanel(new GridLayout(count + 1, 3, 8, 8));
        linesPanel.add(new JLabel("Shërbimi"));
        linesPanel.add(new JLabel("Sasia"));
        linesPanel.add(new JLabel("Çmimi/njësi"));
        List<JComboBox<Sherbimi>> selectors = new ArrayList<>();
        List<JSpinner> quantities = new ArrayList<>();
        for (int row = 0; row < count; row++) {
            JComboBox<Sherbimi> selector = new JComboBox<>(services.toArray(new Sherbimi[0]));
            selector.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                        boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof Sherbimi) {
                        Sherbimi service = (Sherbimi) value;
                        setText(service.getEmri() + " | " + String.format("%.2f €", service.getCmimi()));
                    }
                    return this;
                }
            });
            JSpinner quantity = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
            JLabel price = new JLabel();
            Runnable updatePrice = () -> {
                Sherbimi service = (Sherbimi) selector.getSelectedItem();
                price.setText(service == null ? "-" : String.format("%.2f €", service.getCmimi()));
            };
            selector.addActionListener(event -> updatePrice.run());
            updatePrice.run();
            selectors.add(selector);
            quantities.add(quantity);
            linesPanel.add(selector);
            linesPanel.add(quantity);
            linesPanel.add(price);
        }

        if (JOptionPane.showConfirmDialog(this, new JScrollPane(linesPanel), "Shërbimet e faturës",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return null;

        List<ReservationEntry> chosen = new ArrayList<>();
        for (int i = 0; i < selectors.size(); i++) {
            Sherbimi service = (Sherbimi) selectors.get(i).getSelectedItem();
            if (service == null) return null;
            chosen.add(new ReservationEntry(service, (Integer) quantities.get(i).getValue()));
        }
        return chosen;
    }

    private static class ReservationEntry {
        private final Sherbimi service;
        private final int quantity;

        private ReservationEntry(Sherbimi service, int quantity) {
            this.service = service;
            this.quantity = quantity;
        }
    }

    private void myReservations() {
        showUserReservations();
    }

    private LocalDate chooseReservationDate() {
        LocalDate today = LocalDate.now();
        YearMonth[] displayedMonth = { YearMonth.from(today) };
        LocalDate[] selectedDate = { null };
        JDialog dialog = new JDialog(this, "Zgjidh datën e rezervimit", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel calendar = new JPanel(new BorderLayout(8, 10));
        calendar.setBackground(Color.WHITE);
        calendar.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        JLabel monthLabel = new JLabel("", SwingConstants.CENTER);
        monthLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        monthLabel.setForeground(TEXT);
        JPanel weekdays = new JPanel(new GridLayout(1, 7, 3, 3));
        weekdays.setBackground(Color.WHITE);
        for (DayOfWeek day : DayOfWeek.values()) {
            JLabel label = new JLabel(day.getDisplayName(TextStyle.SHORT, Locale.forLanguageTag("sq")), SwingConstants.CENTER);
            label.setFont(new Font("SansSerif", Font.BOLD, 11));
            label.setForeground(new Color(100, 116, 139));
            weekdays.add(label);
        }
        JPanel dayGrid = new JPanel(new GridLayout(0, 7, 4, 4));
        dayGrid.setBackground(Color.WHITE);
        Runnable[] refreshCalendar = new Runnable[1];
        refreshCalendar[0] = () -> {
            monthLabel.setText(displayedMonth[0].getMonth().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("sq"))
                    + " " + displayedMonth[0].getYear());
            dayGrid.removeAll();
            LocalDate firstDay = displayedMonth[0].atDay(1);
            int offset = firstDay.getDayOfWeek().getValue() - 1;
            for (int i = 0; i < offset; i++) dayGrid.add(new JLabel(""));
            for (int day = 1; day <= displayedMonth[0].lengthOfMonth(); day++) {
                LocalDate date = displayedMonth[0].atDay(day);
                JButton dayButton = new JButton(String.valueOf(day));
                dayButton.setFont(new Font("SansSerif", Font.PLAIN, 13));
                dayButton.setFocusPainted(false);
                dayButton.setEnabled(!date.isBefore(today));
                dayButton.setBackground(date.equals(today) ? new Color(224, 242, 254) : Color.WHITE);
                dayButton.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
                dayButton.addActionListener(event -> {
                    selectedDate[0] = date;
                    dialog.dispose();
                });
                dayGrid.add(dayButton);
            }
            dayGrid.revalidate();
            dayGrid.repaint();
        };

        JButton previousMonth = new JButton("‹");
        JButton nextMonth = new JButton("›");
        for (JButton button : new JButton[] { previousMonth, nextMonth }) {
            button.setFont(new Font("SansSerif", Font.BOLD, 18));
            button.setFocusPainted(false);
            button.setBackground(Color.WHITE);
            button.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        }
        previousMonth.addActionListener(event -> {
            displayedMonth[0] = displayedMonth[0].minusMonths(1);
            refreshCalendar[0].run();
        });
        nextMonth.addActionListener(event -> {
            displayedMonth[0] = displayedMonth[0].plusMonths(1);
            refreshCalendar[0].run();
        });
        JPanel monthBar = new JPanel(new BorderLayout());
        monthBar.setBackground(Color.WHITE);
        monthBar.add(previousMonth, BorderLayout.WEST);
        monthBar.add(monthLabel, BorderLayout.CENTER);
        monthBar.add(nextMonth, BorderLayout.EAST);
        JPanel calendarHeader = new JPanel(new BorderLayout(0, 8));
        calendarHeader.setBackground(Color.WHITE);
        calendarHeader.add(monthBar, BorderLayout.NORTH);
        calendarHeader.add(weekdays, BorderLayout.SOUTH);
        calendar.add(calendarHeader, BorderLayout.NORTH);
        calendar.add(dayGrid, BorderLayout.CENTER);
        refreshCalendar[0].run();

        JButton cancel = new JButton("Anulo");
        cancel.addActionListener(event -> dialog.dispose());
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(Color.WHITE);
        footer.add(cancel);
        calendar.add(footer, BorderLayout.SOUTH);

        dialog.setContentPane(calendar);
        dialog.setSize(390, 390);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        return selectedDate[0];
    }

    private static DefaultTableModel readOnlyModel(Object[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void styleServiceTable(JTable table, Color headerColor) {
        styleServiceTable(table, headerColor, new int[0]);
    }

    private void styleServiceTable(JTable table, Color headerColor, int... moneyColumns) {
        table.setRowHeight(30);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
        table.setSelectionBackground(new Color(219, 239, 234));
        table.setSelectionForeground(TEXT);
        table.setGridColor(new Color(226, 232, 240));
        table.setBackground(Color.WHITE);
        table.getTableHeader().setBackground(headerColor);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            protected void setValue(Object value) {
                super.setValue(value);
                setHorizontalAlignment(value instanceof Number ? SwingConstants.RIGHT : SwingConstants.LEFT);
            }
        });
        for (int column : moneyColumns) {
            DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
                @Override
                protected void setValue(Object value) {
                    if (value instanceof Number) setText(String.format("%.2f €", ((Number) value).doubleValue()));
                    else super.setValue(value);
                }
            };
            renderer.setHorizontalAlignment(SwingConstants.RIGHT);
            table.getColumnModel().getColumn(column).setCellRenderer(renderer);
        }
    }

    private JPanel createDashboardHeader(JLabel title, String role, Color color) {
        JPanel header = new JPanel(new BorderLayout(12, 4));
        header.setBackground(PANEL);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(218, 228, 238)),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)));
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(TEXT);
        JLabel roleLabel = new JLabel(role);
        roleLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        roleLabel.setForeground(color);
        roleLabel.setOpaque(true);
        roleLabel.setBackground(new Color(
            Math.min(255, color.getRed() + 220),
            Math.min(255, color.getGreen() + 220),
            Math.min(255, color.getBlue() + 220)));
        roleLabel.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        header.add(title, BorderLayout.WEST);
        header.add(roleLabel, BorderLayout.EAST);
        return header;
    }

    private JPanel createActionRail(String heading, JPanel actions) {
        JPanel rail = new JPanel(new BorderLayout(0, 10));
        rail.setBackground(PANEL);
        rail.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(218, 228, 238)),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        JLabel label = new JLabel(heading);
        label.setFont(new Font("SansSerif", Font.BOLD, 11));
        label.setForeground(TEXT);
        actions.setBackground(PANEL);
        rail.add(label, BorderLayout.NORTH);
        rail.add(actions, BorderLayout.CENTER);
        return rail;
    }

    private JPanel createMetricStrip(JPanel... metrics) {
        JPanel strip = new JPanel(new GridLayout(1, metrics.length, 10, 0));
        strip.setBackground(BG);
        for (JPanel metric : metrics) strip.add(metric);
        return strip;
    }

    private JPanel createMetricCard(String caption, JLabel value, Color accent) {
        JPanel card = new JPanel(new BorderLayout(0, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 228, 238)),
                BorderFactory.createEmptyBorder(9, 12, 9, 12)));
        JLabel captionLabel = new JLabel(caption);
        captionLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        captionLabel.setForeground(TEXT);
        value.setFont(new Font("SansSerif", Font.BOLD, 20));
        value.setForeground(accent);
        card.add(captionLabel, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);
        return card;
    }

    private Map<String, List<Rezervimi>> groupReservations(List<Rezervimi> reservations) {
        Map<String, List<Rezervimi>> invoices = new LinkedHashMap<>();
        for (Rezervimi reservation : reservations) {
            invoices.computeIfAbsent(reservation.getNumriRezervimit(), key -> new ArrayList<>()).add(reservation);
        }
        return invoices;
    }

    private double totalForInvoice(List<Rezervimi> invoiceLines) {
        double total = 0;
        for (Rezervimi line : invoiceLines) {
            Sherbimi service = sherbimService.gjej(line.getSherbimiId());
            double unitPrice = line.getCmimiNjesi() > 0
                    ? line.getCmimiNjesi() : (service == null ? 0 : service.getCmimi());
            total += unitPrice * line.getSasia();
        }
        return total;
    }

    private void updateDashboardMetrics() {
        int patients = 0;
        for (User user : authService.getAllUsers()) {
            if (!user.isAdmin()) patients++;
        }
        adminServiceCount.setText(String.valueOf(sherbimService.getAll().size()));
        adminPatientCount.setText(String.valueOf(patients));
        adminReservationCount.setText(String.valueOf(groupReservations(rezervimService.getAll()).size()));
        userServiceCount.setText(String.valueOf(sherbimService.getAll().size()));

        if (authService.getCurrentUser() != null && !authService.getCurrentUser().isAdmin()) {
            List<Rezervimi> reservations = rezervimService.getRezervimetERevit(authService.getCurrentUser().getUsername());
            Map<String, List<Rezervimi>> invoices = groupReservations(reservations);
            double total = 0;
            for (List<Rezervimi> invoice : invoices.values()) total += totalForInvoice(invoice);
            userReservationCount.setText(String.valueOf(invoices.size()));
            userReservationTotal.setText(String.format("%.2f €", total));
        } else {
            userReservationCount.setText("0");
            userReservationTotal.setText("0.00 €");
        }
    }
}
