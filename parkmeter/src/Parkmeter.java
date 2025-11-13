import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Parkmeter extends JFrame {

    private JButton moneta1Btn, moneta2Btn, moneta5Btn;
    private JButton zaplacKartaBtn, zatwierdzBtn, drukujBtn, resetBtn;

    private JTextField rejestracjaField, godzinyField;
    private JTextArea biletArea;

    private JLabel sumaLabel, naleznoscLabel;

    private static final double CENA_GODZINY = 2.0;
    private double naleznosc = 0;
    private double suma = 0;
    private int godziny = 0;

    private boolean daneZatwierdzone = false;
    private boolean uzytoMonet = false;

    private String metodaPlatnosci = "Nie zapłacono";

    public Parkmeter() {
        setTitle("Symulator Parkometru");
        setSize(650, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        add(ekranPlatnosci());
    }

    private JPanel ekranPlatnosci() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(60, 60, 60));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel gorny = new JPanel(new GridLayout(3, 2, 10, 10));
        gorny.setOpaque(false);

        JLabel l1 = new JLabel("Numer rejestracyjny:", SwingConstants.RIGHT);
        JLabel l2 = new JLabel("Czas parkowania (h):", SwingConstants.RIGHT);
        l1.setForeground(Color.WHITE);
        l2.setForeground(Color.WHITE);

        rejestracjaField = new JTextField();
        godzinyField = new JTextField();

        naleznoscLabel = new JLabel("Należność: 0.00 zł", SwingConstants.CENTER);
        naleznoscLabel.setForeground(Color.WHITE);
        naleznoscLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        sumaLabel = new JLabel("Wrzucono: 0.00 zł", SwingConstants.CENTER);
        sumaLabel.setForeground(Color.WHITE);
        sumaLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        gorny.add(l1);
        gorny.add(rejestracjaField);
        gorny.add(l2);
        gorny.add(godzinyField);
        gorny.add(naleznoscLabel);
        gorny.add(sumaLabel);

        panel.add(gorny, BorderLayout.NORTH);

        JPanel srodek = new JPanel();
        srodek.setOpaque(false);

        zatwierdzBtn = new JButton("ZATWIERDŹ DANE");
        styl(zatwierdzBtn, new Color(80, 160, 80));
        zatwierdzBtn.setPreferredSize(new Dimension(200, 40));

        srodek.add(zatwierdzBtn);

        panel.add(srodek, BorderLayout.CENTER);


        JPanel przyciski = new JPanel(new FlowLayout());
        przyciski.setOpaque(false);

        moneta1Btn = new JButton("1 zł");
        moneta2Btn = new JButton("2 zł");
        moneta5Btn = new JButton("5 zł");
        zaplacKartaBtn = new JButton("Zapłać kartą");
        drukujBtn = new JButton("Drukuj bilet");
        resetBtn = new JButton("Resetuj");

        styl(moneta1Btn, new Color(100, 100, 100));
        styl(moneta2Btn, new Color(100, 100, 100));
        styl(moneta5Btn, new Color(100, 100, 100));
        styl(zaplacKartaBtn, new Color(60, 130, 60));
        styl(drukujBtn, new Color(60, 60, 130));
        styl(resetBtn, new Color(130, 60, 60));

        moneta1Btn.setEnabled(false);
        moneta2Btn.setEnabled(false);
        moneta5Btn.setEnabled(false);
        zaplacKartaBtn.setEnabled(false);
        drukujBtn.setEnabled(false);

        przyciski.add(moneta1Btn);
        przyciski.add(moneta2Btn);
        przyciski.add(moneta5Btn);
        przyciski.add(zaplacKartaBtn);
        przyciski.add(drukujBtn);
        przyciski.add(resetBtn);

        biletArea = new JTextArea(12, 45);
        biletArea.setEditable(false);
        biletArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        biletArea.setBackground(new Color(230, 230, 230));

        JScrollPane scroll = new JScrollPane(biletArea);
        scroll.setPreferredSize(new Dimension(550, 300));

        JPanel dolny = new JPanel(new BorderLayout());
        dolny.setOpaque(false);

        dolny.add(przyciski, BorderLayout.NORTH);
        dolny.add(scroll, BorderLayout.CENTER);

        panel.add(dolny, BorderLayout.SOUTH);

        zatwierdzBtn.addActionListener(e -> zatwierdzDane());
        moneta1Btn.addActionListener(e -> wrzucMonete(1));
        moneta2Btn.addActionListener(e -> wrzucMonete(2));
        moneta5Btn.addActionListener(e -> wrzucMonete(5));
        zaplacKartaBtn.addActionListener(e -> zaplacKarta());
        drukujBtn.addActionListener(e -> drukujBilet());
        resetBtn.addActionListener(e -> resetuj());

        return panel;
    }

    private void styl(JButton btn, Color kolor) {
        btn.setBackground(kolor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
    }

    private void wrzucMonete(double kwota) {
        if (!daneZatwierdzone) return;

        uzytoMonet = true;
        zaplacKartaBtn.setEnabled(false);

        suma += kwota;
        metodaPlatnosci = "Gotówka";

        sumaLabel.setText(String.format("Wrzucono: %.2f zł", suma));

        if (suma >= naleznosc) {
            drukujBtn.setEnabled(true);
        }
    }

    private void zaplacKarta() {
        if (!daneZatwierdzone || uzytoMonet) return;

        metodaPlatnosci = "Karta";
        suma = naleznosc;

        sumaLabel.setText(String.format("Zapłacono kartą: %.2f zł", suma));

        drukujBtn.setEnabled(true);
    }

    private boolean wyliczNaleznosc() {
        String rej = rejestracjaField.getText().trim();

        if (rej.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Podaj numer rejestracyjny!", "Błąd", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            godziny = Integer.parseInt(godzinyField.getText());
            if (godziny <= 0) throw new NumberFormatException();

            naleznosc = godziny * CENA_GODZINY;
            naleznoscLabel.setText(String.format("Należność: %.2f zł", naleznosc));

            return true;

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Podaj poprawną liczbę godzin!", "Błąd", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void zatwierdzDane() {
        if (!wyliczNaleznosc()) return;

        daneZatwierdzone = true;

        moneta1Btn.setEnabled(true);
        moneta2Btn.setEnabled(true);
        moneta5Btn.setEnabled(true);
        zaplacKartaBtn.setEnabled(true);
        zatwierdzBtn.setEnabled(false);

        JOptionPane.showMessageDialog(this, "Dane zatwierdzone.\nMożesz dokonać płatności.");
    }

    private void drukujBilet() {

        double reszta = suma - naleznosc;
        if (reszta < 0) reszta = 0;

        LocalDateTime teraz = LocalDateTime.now();
        LocalDateTime koniec = teraz.plusHours(godziny);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        biletArea.setText(
                "--- BILET PARKINGOWY --- \n" +
                        "Numer rejestracyjny: " + rejestracjaField.getText().trim() + "\n" +
                        "Start: " + teraz.format(fmt) + "\n" +
                        "Koniec: " + koniec.format(fmt) + "\n" +
                        String.format("Opłata: %.2f zł\n", naleznosc) +
                        String.format("Wpłacono: %.2f zł\n", suma) +
                        String.format("Reszta: %.2f zł\n", reszta) +
                        "Metoda płatności: " + metodaPlatnosci + "\n"
        );
    }

    private void resetuj() {
        suma = 0;
        naleznosc = 0;
        godziny = 0;
        uzytoMonet = false;
        daneZatwierdzone = false;
        metodaPlatnosci = "Nie zapłacono";

        rejestracjaField.setText("");
        godzinyField.setText("");

        sumaLabel.setText("Wrzucono: 0.00 zł");
        naleznoscLabel.setText("Należność: 0.00 zł");

        biletArea.setText("");

        moneta1Btn.setEnabled(false);
        moneta2Btn.setEnabled(false);
        moneta5Btn.setEnabled(false);
        zaplacKartaBtn.setEnabled(false);
        drukujBtn.setEnabled(false);
        zatwierdzBtn.setEnabled(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Parkmeter().setVisible(true));
    }
}
