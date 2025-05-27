import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class DecryptionTool extends JFrame {
    private JTextArea inputTextArea, outputTextArea;
    private JTextField keyField;
    private JComboBox<String> algorithmComboBox;
    private JRadioButton englishRadio, russianRadio;
    private JButton decryptButton, clearButton;
    private JLabel inputLabel, outputLabel;

    public DecryptionTool() {
        setTitle("Decryption Tool");
        setSize(650, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(new Color(240, 248, 255));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBackground(new Color(230, 240, 250));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel langLabel = new JLabel("Select Language:");
        langLabel.setFont(new Font("Arial", Font.BOLD, 12));
        englishRadio = new JRadioButton("English", true);
        russianRadio = new JRadioButton("Russian");
        ButtonGroup langGroup = new ButtonGroup();
        langGroup.add(englishRadio);
        langGroup.add(russianRadio);

        JLabel algoLabel = new JLabel("Algorithm:");
        algoLabel.setFont(new Font("Arial", Font.BOLD, 12));
        String[] algorithms = {"Caesar", "Vigenère"};
        algorithmComboBox = new JComboBox<>(algorithms);
        algorithmComboBox.setFont(new Font("Arial", Font.PLAIN, 12));
        algorithmComboBox.setBackground(Color.WHITE);

        JLabel keyLabel = new JLabel("Suggested Key (optional):");
        keyLabel.setFont(new Font("Arial", Font.BOLD, 12));
        keyField = new JTextField(12);
        keyField.setFont(new Font("Arial", Font.PLAIN, 12));
        keyField.setBackground(Color.WHITE);

        decryptButton = new JButton("Decrypt");
        decryptButton.setFont(new Font("Arial", Font.BOLD, 12));
        decryptButton.setBackground(new Color(46, 139, 87));
        decryptButton.setForeground(Color.WHITE);
        decryptButton.setFocusPainted(false);

        clearButton = new JButton("Clear");
        clearButton.setFont(new Font("Arial", Font.BOLD, 12));
        clearButton.setBackground(new Color(220, 20, 60));
        clearButton.setForeground(Color.WHITE);
        clearButton.setFocusPainted(false);

        topPanel.add(langLabel);
        topPanel.add(englishRadio);
        topPanel.add(russianRadio);
        topPanel.add(algoLabel);
        topPanel.add(algorithmComboBox);
        topPanel.add(keyLabel);
        topPanel.add(keyField);
        topPanel.add(decryptButton);
        topPanel.add(clearButton);

        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        centerPanel.setBackground(new Color(245, 245, 245));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputLabel = new JLabel("Encrypted Text:");
        inputLabel.setFont(new Font("Arial", Font.BOLD, 12));
        inputTextArea = new JTextArea(5, 40);
        inputTextArea.setLineWrap(true);
        inputTextArea.setFont(new Font("Arial", Font.PLAIN, 12));
        inputTextArea.setBackground(Color.WHITE);

        outputLabel = new JLabel("Decrypted Text:");
        outputLabel.setFont(new Font("Arial", Font.BOLD, 12));
        outputTextArea = new JTextArea(5, 40);
        outputTextArea.setLineWrap(true);
        outputTextArea.setEditable(false);
        outputTextArea.setFont(new Font("Arial", Font.PLAIN, 12));
        outputTextArea.setBackground(Color.WHITE);

        centerPanel.add(inputLabel);
        centerPanel.add(new JScrollPane(inputTextArea));
        centerPanel.add(outputLabel);
        centerPanel.add(new JScrollPane(outputTextArea));

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        decryptButton.addActionListener(e -> decryptText());
        clearButton.addActionListener(e -> clearFields());
        englishRadio.addActionListener(e -> updateLabels());
        russianRadio.addActionListener(e -> updateLabels());
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    }

    private void decryptText() {
        String input = inputTextArea.getText().trim();
        String suggestedKey = keyField.getText().trim();
        String algorithm = (String) algorithmComboBox.getSelectedItem();
        String language = englishRadio.isSelected() ? "English" : "Russian";
        String langCode = englishRadio.isSelected() ? "en" : "ru";

        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter encrypted text.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String result = "";
        if (algorithm.equals("Caesar")) {
            if (suggestedKey.isEmpty()) {
                StringBuilder analysisResult = new StringBuilder();
                java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
                System.setOut(new java.io.PrintStream(outContent));
                CaesarDecrypt.analyzeCaesarDecryption(input, langCode);
                analysisResult.append(outContent.toString());
                System.setOut(new java.io.PrintStream(new java.io.FileOutputStream(java.io.FileDescriptor.out)));
                result = analysisResult.toString();
            } else {
                try {
                    int shift = Integer.parseInt(suggestedKey);
                    String alphabet = langCode.equals("ru") ? "абвгдеёжзийклмнопрстуфхцчшщъыьэюя" : "abcdefghijklmnopqrstuvwxyz";
                    int n = alphabet.length();
                    StringBuilder decrypted = new StringBuilder();
                    for (char ch : input.toCharArray()) {
                        boolean isUpper = Character.isUpperCase(ch);
                        char lowerChar = Character.toLowerCase(ch);
                        int index = alphabet.indexOf(lowerChar);
                        if (index != -1) {
                            int newIndex = (index - shift + n) % n;
                            char newChar = alphabet.charAt(newIndex);
                            decrypted.append(isUpper ? Character.toUpperCase(newChar) : newChar);
                        } else {
                            decrypted.append(ch);
                        }
                    }
                    result = decrypted.toString();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "For Caesar cipher, key must be an integer.",
                            "Key Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        } else {
            String key = suggestedKey.isEmpty() ?
                    VigenereCracker.autoDetectKeyLength(input,
                            language.equals("English") ? VigenereCracker.ENG_ALPHABET : VigenereCracker.RUS_ALPHABET,
                            language.equals("English") ? VigenereCracker.ENG_FREQ : VigenereCracker.RUS_FREQ, 20).key : suggestedKey;
            keyField.setText(key);
            result = VigenereCracker.decryptVigenere(input, key,
                    language.equals("English") ? VigenereCracker.ENG_ALPHABET : VigenereCracker.RUS_ALPHABET);
        }
        outputTextArea.setText(result);
    }

    private void clearFields() {
        inputTextArea.setText("");
        outputTextArea.setText("");
        keyField.setText("");
    }

    private void updateLabels() {
        if (englishRadio.isSelected()) {
            inputLabel.setText("Encrypted Text:");
            outputLabel.setText("Decrypted Text:");
        } else {
            inputLabel.setText("Зашифрованный текст:");
            outputLabel.setText("Расшифрованный текст:");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DecryptionTool tool = new DecryptionTool();
            tool.setVisible(true);
        });
    }
}