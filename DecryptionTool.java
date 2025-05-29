import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class DecryptionTool extends JFrame {
    private JTextArea inputTextArea, outputTextArea, analysisTextArea;
    private JTextField keyField;
    private JComboBox<String> algorithmComboBox;
    private JRadioButton englishRadio, russianRadio;
    private JCheckBox bruteforceCheckBox;
    private JButton decryptButton, clearButton;
    private JLabel inputLabel, outputLabel, analysisLabel, keyLabel;

    public DecryptionTool() {
        setTitle("Decryption Tool");
        setSize(650, 500);
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

        bruteforceCheckBox = new JCheckBox("Enable Bruteforce");
        bruteforceCheckBox.setFont(new Font("Arial", Font.PLAIN, 12));
        bruteforceCheckBox.setBackground(new Color(230, 240, 250));

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

     
        keyLabel = new JLabel("Detected Key:");
        keyLabel.setFont(new Font("Arial", Font.BOLD, 12));
        keyField = new JTextField(12);
        keyField.setFont(new Font("Arial", Font.PLAIN, 12));
        keyField.setBackground(Color.WHITE);
        keyField.setEditable(false);

        topPanel.add(langLabel);
        topPanel.add(englishRadio);
        topPanel.add(russianRadio);
        topPanel.add(algoLabel);
        topPanel.add(algorithmComboBox);
        topPanel.add(bruteforceCheckBox);
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

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBackground(new Color(245, 245, 245));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        analysisLabel = new JLabel("Analysis Results:");
        analysisLabel.setFont(new Font("Arial", Font.BOLD, 12));
        analysisTextArea = new JTextArea(8, 40);
        analysisTextArea.setLineWrap(true);
        analysisTextArea.setEditable(false);
        analysisTextArea.setFont(new Font("Arial", Font.PLAIN, 12));
        analysisTextArea.setBackground(Color.WHITE);
        bottomPanel.add(analysisLabel, BorderLayout.NORTH);
        bottomPanel.add(new JScrollPane(analysisTextArea), BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        decryptButton.addActionListener(e -> decryptText());
        clearButton.addActionListener(e -> clearFields());
        englishRadio.addActionListener(e -> updateLabels());
        russianRadio.addActionListener(e -> updateLabels());
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    }

    private void decryptText() {
        String input = inputTextArea.getText().trim();
        String algorithm = (String) algorithmComboBox.getSelectedItem();
        String language = englishRadio.isSelected() ? "English" : "Russian";
        String langCode = englishRadio.isSelected() ? "en" : "ru";

        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter encrypted text.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String result = "";
        String analysisResult = "";
        String detectedKey = "";
        if (algorithm.equals("Caesar")) {
            if (bruteforceCheckBox.isSelected()) {
                java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
                PrintStream originalOut = System.out;
                System.setOut(new PrintStream(outContent));
                CaesarBruteforce.runBruteforce(input, langCode);
                System.setOut(originalOut);
                result = outContent.toString().replaceAll("\\r\\n", "\n").trim();
                analysisResult = "Bruteforce analysis completed for all shifts.";
                detectedKey = "1";
            } else {
                java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
                System.setOut(new java.io.PrintStream(outContent));
                CaesarDecrypt.analyzeCaesarDecryption(input, langCode);
                analysisResult = outContent.toString();
                System.setOut(new java.io.PrintStream(new java.io.FileOutputStream(java.io.FileDescriptor.out)));
                result = analysisResult;
                detectedKey = "1";
            }
        } else {
            String vigAlphabet = language.equals("English") ? VigenereCracker.ENG_ALPHABET : VigenereCracker.RUS_ALPHABET;
            StringBuilder filteredBuilder = new StringBuilder();
            for (char c : input.toUpperCase().toCharArray()) {
                if (vigAlphabet.indexOf(c) != -1) {
                    filteredBuilder.append(c);
                }
            }
            String filteredInput = filteredBuilder.toString();
            if (filteredInput.length() < 10) {
                JOptionPane.showMessageDialog(this, "Text too short for Vigenère analysis.",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String key = VigenereCracker.autoDetectKeyLength(filteredInput,
                    language.equals("English") ? VigenereCracker.ENG_ALPHABET : VigenereCracker.RUS_ALPHABET,
                    language.equals("English") ? VigenereCracker.ENG_FREQ : VigenereCracker.RUS_FREQ, 20).key;
            java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
            System.setOut(new java.io.PrintStream(outContent));
            VigenereCracker.findKeyByFrequency(filteredInput, key.length(),
                    language.equals("English") ? VigenereCracker.ENG_ALPHABET : VigenereCracker.RUS_ALPHABET,
                    language.equals("English") ? VigenereCracker.ENG_FREQ : VigenereCracker.RUS_FREQ);
            analysisResult = outContent.toString();
            System.setOut(new java.io.PrintStream(new java.io.FileOutputStream(java.io.FileDescriptor.out)));
            result = VigenereCracker.decryptVigenere(input, key,
                    language.equals("English") ? VigenereCracker.ENG_ALPHABET : VigenereCracker.RUS_ALPHABET);
            detectedKey = key;
        }
        outputTextArea.setText(result);
        analysisTextArea.setText(analysisResult);
        keyField.setText(detectedKey);
    }

    private void clearFields() {
        inputTextArea.setText("");
        outputTextArea.setText("");
        analysisTextArea.setText("");
        keyField.setText("");
    }

    private void updateLabels() {
        if (englishRadio.isSelected()) {
            inputLabel.setText("Encrypted Text:");
            outputLabel.setText("Decrypted Text:");
            analysisLabel.setText("Analysis Results:");
            keyLabel.setText("Detected Key:");
        } else {
            inputLabel.setText("Зашифрованный текст:");
            outputLabel.setText("Расшифрованный текст:");
            analysisLabel.setText("Результаты анализа:");
            keyLabel.setText("Найденный ключ:");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DecryptionTool tool = new DecryptionTool();
            tool.setVisible(true);
        });
    }
}
