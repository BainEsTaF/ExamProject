import javax.swing.*;
import java.awt.*;

public class DecryptionTool extends JFrame {
    private JTextArea inputTextArea, outputTextArea;
    private JTextField keyField;
    private JComboBox<String> algorithmComboBox;
    private JRadioButton englishRadio, russianRadio;
    private JButton decryptButton, clearButton;
    private JProgressBar progressBar;
    private JLabel infoLabel;

    public DecryptionTool() {

        setTitle("Decryption Tool");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel langLabel = new JLabel("Select language:");
        englishRadio = new JRadioButton("English", true);
        russianRadio = new JRadioButton("Russian");
        ButtonGroup langGroup = new ButtonGroup();
        langGroup.add(englishRadio);
        langGroup.add(russianRadio);
        JLabel algoLabel = new JLabel("Algorithm:");
        String[] algorithms = {"Caesar", "Vigenère"};
        algorithmComboBox = new JComboBox<>(algorithms);
        JLabel keyLabel = new JLabel("Key/Password:");
        keyField = new JTextField(10);
        decryptButton = new JButton("Decrypt");
        clearButton = new JButton("Clear");
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
        inputTextArea = new JTextArea(5, 40);
        inputTextArea.setLineWrap(true);
        outputTextArea = new JTextArea(5, 40);
        outputTextArea.setLineWrap(true);
        outputTextArea.setEditable(false);
        centerPanel.add(new JLabel("Encrypted Text:"));
        centerPanel.add(new JScrollPane(inputTextArea));
        centerPanel.add(new JLabel("Decrypted Text:"));
        centerPanel.add(new JScrollPane(outputTextArea));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        progressBar = new JProgressBar();
        infoLabel = new JLabel("Select language and algorithm, enter key, and click Decrypt.");
        bottomPanel.add(progressBar, BorderLayout.NORTH);
        bottomPanel.add(infoLabel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Action listeners
        decryptButton.addActionListener(e -> decryptText());
        clearButton.addActionListener(e -> clearFields());
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    private void decryptText() {
        String input = inputTextArea.getText().trim();
        String key = keyField.getText().trim();
        String algorithm = (String) algorithmComboBox.getSelectedItem();
        String language = englishRadio.isSelected() ? "English" : "Russian";

        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter encrypted text.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String result = "";
        if (algorithm.equals("Caesar")) {
            try {
                int shift = Integer.parseInt(key);
                result = decryptCaesar(input, shift, language);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "For Caesar cipher, key must be an integer.",
                        "Key Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            result = VigenereCracker.decryptVigenere(input, key.isEmpty() ?
                            VigenereCracker.autoDetectKeyLength(input,
                                    language.equals("English") ? VigenereCracker.ENG_ALPHABET : VigenereCracker.RUS_ALPHABET,
                                    language.equals("English") ? VigenereCracker.ENG_FREQ : VigenereCracker.RUS_FREQ, 20).key : key,
                    language.equals("English") ? VigenereCracker.ENG_ALPHABET : VigenereCracker.RUS_ALPHABET);
        }
        outputTextArea.setText(result);
    }

    private void clearFields() {
        inputTextArea.setText("");
        outputTextArea.setText("");
        keyField.setText("");
    }

    private String decryptCaesar(String text, int shift, String language) {
        return "Caesar decryption placeholder: " + text;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DecryptionTool tool = new DecryptionTool();
            tool.setVisible(true);
        });
    }
}