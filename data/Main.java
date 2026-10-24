import javax.swing.SwingUtilities;
import ui.ClinicSwingUI;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClinicSwingUI());
    }
}