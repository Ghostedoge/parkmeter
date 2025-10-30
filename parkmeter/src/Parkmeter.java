import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Parkmeter extends JFrame {

    private JComboBox<String> metodaPlatnosciBox;
    private JTextField godzinyField;
    private JTextArea wynikArea;
    private static final double CENA_GODZINY = 2.0;

    public Parkmeter() {
        setTitle("💰 Symulator Parkometru");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(240, 248, 255));

        JLabel tytul = new JLabel("SYMULATOR PARKOMETRU", SwingConstants.CENTER);
        tytul.setFont(new Font("Segoe UI", Font.BOLD, 22));
        tytul.setForeground(new Color(25, 25, 112));
        panel.add(tytul, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.setBackground(new Color(240, 248, 255));

        inputPanel.add(new JLabel("Metoda płatności:", SwingConstants.RIGHT));
        metodaPlatnosciBox = new JComboBox<>(new String[]{"Monety", "Banknoty", "Karta"});
        inputPanel.add(metodaPlatnosciBox);

        inputPanel.add(new JLabel("Czas parkowania (h):", SwingConstants.RIGHT));
        godzinyField = new JTextField();
        inputPanel.add(godzinyField);

        JButton zatwierdzBtn = new JButton("💳 Zatwierdź płatność");
        zatwierdzBtn.setBackground(new Color(100, 149, 237));
        zatwierdzBtn.setForeground(Color.WHITE);
        zatwierdzBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        zatwierdzBtn.setFocusPainted(false);
        inputPanel.add(zatwierdzBtn);

        panel.add(inputPanel, BorderLayout.CENTER);

        wynikArea = new JTextArea();
        wynikArea.setEditable(false);
        wynikArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        wynikArea.setBackground(new Color(255, 255, 240));
        wynikArea.setBorder(BorderFactory.createTitledBorder("Wydruk biletu"));
        panel.add(new JScrollPane(wynikArea), BorderLayout.SOUTH);

        zatwierdzBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                obsluzPlatnosc();
            }
        });

        add(panel);
    }

    private void obsluzPlatnosc() {
        try {
            int godziny = Integer.parseInt(godzinyField.getText());
            if (godziny <= 0) {
                JOptionPane.showMessageDialog(this, "Podaj poprawny czas parkowania!", "Błąd", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String metoda = (String) metodaPlatnosciBox.getSelectedItem();
            double naleznosc = godziny * CENA_GODZINY;

            String komunikat = "";
            double zaplacono = naleznosc;
            double reszta = 0;

            switch (metoda) {
                case "Monety":
                    komunikat = "Wrzuciłeś monety o łącznej wartości: " + naleznosc + " zł";
                    break;
                case "Banknoty":
                    komunikat = "Wpłacono banknoty o łącznej wartości: " + naleznosc + " zł";
                    break;
                case "Karta":
                    komunikat = "Pobrano z karty: " + naleznosc + " zł";
                    break;
            }

            drukujBilet(godziny, naleznosc, zaplacono, reszta, metoda, komunikat);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Podaj liczbę godzin!", "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void drukujBilet(int godziny, double naleznosc, double zaplacono, double reszta, String metoda, String komunikat) {
        LocalDateTime teraz = LocalDateTime.now();
        LocalDateTime koniec = teraz.plusHours(godziny);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        StringBuilder bilet = new StringBuilder();
        bilet.append("=== BILET PARKINGOWY ===\n");
        bilet.append("Czas rozpoczęcia: ").append(teraz.format(fmt)).append("\n");
        bilet.append("Czas zakończenia:  ").append(koniec.format(fmt)).append("\n");
        bilet.append(String.format("Opłata: %.2f zł\n", naleznosc));
        bilet.append(String.format("Metoda płatności: %s\n", metoda));
        bilet.append(komunikat).append("\n");
        bilet.append("========================\n");
        bilet.append("Dziękujemy i szerokiej drogi!\n");

        wynikArea.setText(bilet.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Parkmeter().setVisible(true);
        });
    }
}
