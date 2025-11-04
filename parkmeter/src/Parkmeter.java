import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Parkmeter extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    private JButton monetyBtn, kartaBtn;

    private JTextField rejestracjaField, godzinyField;
    private JTextArea biletArea;
    private JLabel sumaLabel, naleznoscLabel;
    private JButton moneta1Btn, moneta2Btn, moneta5Btn, zaplacKartaBtn, powrotBtn;

    private static final double CENA_GODZINY = 2.0;
    private double naleznosc = 0;
    private double suma = 0;
    private String metodaPlatnosci = "";
    private int godziny = 0;

    public Parkmeter() {
        setTitle("💰 Symulator Parkometru");
        setSize(550, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(ekranWyboru(), "wybor");
        mainPanel.add(ekranPlatnosci(), "platnosc");

        add(mainPanel);
        cardLayout.show(mainPanel, "wybor");
    }

    private JPanel ekranWyboru() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(230, 240, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel tytul = new JLabel("Wybierz metodę płatności", SwingConstants.CENTER);
        tytul.setFont(new Font("Segoe UI", Font.BOLD, 22));
        tytul.setForeground(new Color(25, 25, 112));
        panel.add(tytul, BorderLayout.NORTH);

        JPanel przyciski = new JPanel(new GridLayout(1, 2, 20, 0));
        przyciski.setOpaque(false);

        monetyBtn = new JButton("🪙 Monety");
        monetyBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        monetyBtn.setBackground(new Color(100, 149, 237));
        monetyBtn.setForeground(Color.WHITE);

        kartaBtn = new JButton("💳 Karta");
        kartaBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        kartaBtn.setBackground(new Color(60, 179, 113));
        kartaBtn.setForeground(Color.WHITE);

        przyciski.add(monetyBtn);
        przyciski.add(kartaBtn);
        panel.add(przyciski, BorderLayout.CENTER);

        JLabel info = new JLabel("Opłata: 2 zł za godzinę", SwingConstants.CENTER);
        info.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        info.setForeground(Color.DARK_GRAY);
        panel.add(info, BorderLayout.SOUTH);

        monetyBtn.addActionListener(e -> {
            metodaPlatnosci = "Monety";
            cardLayout.show(mainPanel, "platnosc");
            przygotujPlatnosc();
        });

        kartaBtn.addActionListener(e -> {
            metodaPlatnosci = "Karta";
            cardLayout.show(mainPanel, "platnosc");
            przygotujPlatnosc();
        });

        return panel;
    }

    private JPanel ekranPlatnosci() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(245, 250, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel gorny = new JPanel(new GridLayout(3, 2, 10, 10));
        gorny.setOpaque(false);
        gorny.add(new JLabel("Numer rejestracyjny:", SwingConstants.RIGHT));
        rejestracjaField = new JTextField();
        gorny.add(rejestracjaField);

        gorny.add(new JLabel("Czas parkowania (h):", SwingConstants.RIGHT));
        godzinyField = new JTextField();
        gorny.add(godzinyField);

        naleznoscLabel = new JLabel("Należność: 0.00 zł", SwingConstants.CENTER);
        naleznoscLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        gorny.add(naleznoscLabel);

        sumaLabel = new JLabel("Wrzucono: 0.00 zł", SwingConstants.CENTER);
        sumaLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        gorny.add(sumaLabel);

        panel.add(gorny, BorderLayout.NORTH);

        JPanel monetyPanel = new JPanel(new FlowLayout());
        monetyPanel.setOpaque(false);
        moneta1Btn = new JButton("1 zł");
        moneta2Btn = new JButton("2 zł");
        moneta5Btn = new JButton("5 zł");

        JButton zaplacBtn = new JButton("✅ Zatwierdź");
        zaplacKartaBtn = new JButton("💳 Zapłać kartą");
        powrotBtn = new JButton("🔙 Powrót");

        stylPrzycisku(moneta1Btn, new Color(70, 130, 180));
        stylPrzycisku(moneta2Btn, new Color(70, 130, 180));
        stylPrzycisku(moneta5Btn, new Color(70, 130, 180));
        stylPrzycisku(zaplacBtn, new Color(34, 139, 34));
        stylPrzycisku(zaplacKartaBtn, new Color(34, 139, 34));
        stylPrzycisku(powrotBtn, new Color(178, 34, 34));

        monetyPanel.add(moneta1Btn);
        monetyPanel.add(moneta2Btn);
        monetyPanel.add(moneta5Btn);
        monetyPanel.add(zaplacKartaBtn);
        monetyPanel.add(zaplacBtn);
        monetyPanel.add(powrotBtn);
        panel.add(monetyPanel, BorderLayout.CENTER);

        biletArea = new JTextArea();
        biletArea.setEditable(false);
        biletArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        biletArea.setBackground(new Color(255, 255, 240));
        biletArea.setBorder(BorderFactory.createTitledBorder("Bilet parkingowy"));
        panel.add(new JScrollPane(biletArea), BorderLayout.SOUTH);

        moneta1Btn.addActionListener(e -> wrzucMonete(1));
        moneta2Btn.addActionListener(e -> wrzucMonete(2));
        moneta5Btn.addActionListener(e -> wrzucMonete(5));
        zaplacKartaBtn.addActionListener(e -> zaplacKarta());
        zaplacBtn.addActionListener(e -> zatwierdzPlatnosc());
        powrotBtn.addActionListener(e -> powrotDoWyboru());

        return panel;
    }

    private void stylPrzycisku(JButton btn, Color kolor) {
        btn.setBackground(kolor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
    }

    private void przygotujPlatnosc() {
        suma = 0;
        naleznosc = 0;
        sumaLabel.setText("Wrzucono: 0.00 zł");
        naleznoscLabel.setText("Należność: 0.00 zł");
        godzinyField.setText("");
        rejestracjaField.setText("");
        biletArea.setText("");

        boolean monety = metodaPlatnosci.equals("Monety");
        moneta1Btn.setVisible(monety);
        moneta2Btn.setVisible(monety);
        moneta5Btn.setVisible(monety);
        zaplacKartaBtn.setVisible(!monety);
    }

    private void wrzucMonete(double kwota) {
        suma += kwota;
        sumaLabel.setText(String.format("Wrzucono: %.2f zł", suma));
    }

    private void zaplacKarta() {
        if (!pobierzDanePodstawowe()) return;
        suma = naleznosc;
        zatwierdzPlatnosc();
    }

    private boolean pobierzDanePodstawowe() {
        String rejestracja = rejestracjaField.getText().trim();
        if (rejestracja.isEmpty()) {
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

    private void zatwierdzPlatnosc() {
        if (!pobierzDanePodstawowe()) return;

        if (metodaPlatnosci.equals("Monety") && suma < naleznosc) {
            JOptionPane.showMessageDialog(this, "Za mało monet! Wrzuć więcej.", "Uwaga", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double reszta = Math.max(0, suma - naleznosc);
        drukujBilet(rejestracjaField.getText().trim(), godziny, naleznosc, suma, reszta, metodaPlatnosci);
    }

    private void drukujBilet(String rejestracja, int godziny, double naleznosc, double zaplacono, double reszta, String metoda) {
        LocalDateTime teraz = LocalDateTime.now();
        LocalDateTime koniec = teraz.plusHours(godziny);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        StringBuilder bilet = new StringBuilder();
        bilet.append("=== BILET PARKINGOWY ===\n");
        bilet.append("Numer rejestracyjny: ").append(rejestracja).append("\n");
        bilet.append("Czas rozpoczęcia: ").append(teraz.format(fmt)).append("\n");
        bilet.append("Czas zakończenia:  ").append(koniec.format(fmt)).append("\n");
        bilet.append(String.format("Opłata: %.2f zł\n", naleznosc));
        bilet.append(String.format("Zapłacono: %.2f zł\n", zaplacono));
        if (reszta > 0)
            bilet.append(String.format("Reszta: %.2f zł\n", reszta));
        bilet.append("Metoda płatności: ").append(metoda).append("\n");
        bilet.append("========================\n");

        biletArea.setText(bilet.toString());
    }

    private void powrotDoWyboru() {
        cardLayout.show(mainPanel, "wybor");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Parkmeter().setVisible(true));
    }
}
