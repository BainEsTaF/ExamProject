import javax.swing.*; // GUI components
import java.awt.*; // layouts, colors, fonts
import java.awt.event.*; // Event handling for button clicks
import java.io.*; // to capture console output from external classes 

public class DecryptionTool extends JFrame {
    private JTextArea inputTextArea, outputTextArea, analysisTextArea;
    private JTextField keyField;
    private JComboBox<String> algorithmComboBox;
    private JRadioButton englishRadio, russianRadio;
    private JCheckBox bruteforceCheckBox;
    private JButton decryptButton, clearButton;
    private JLabel inputLabel, outputLabel, analysisLabel, keyLabel;


    // Window Set Up
    public DecryptionTool() {
        setTitle("Decryption Tool");
        setSize(650, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // автоматический остановливает программу, когда закрываем уиндоу
        setLayout(new BorderLayout(15, 15)); 
        getContentPane().setBackground(new Color(240, 248, 255));

    // Control Section
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10)); // aligns components to the left, with 10-pixel horizontal and vertical gaps between them
        topPanel.setBackground(new Color(230, 240, 250)); 
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // a 10-pixel padding around the panel's edges

        
        JLabel langLabel = new JLabel("Select Language:");
        langLabel.setFont(new Font("Arial", Font.BOLD, 12));
        englishRadio = new JRadioButton("English", true);
        russianRadio = new JRadioButton("Russian");
        ButtonGroup langGroup = new ButtonGroup(); // groups the languages (только одну можно выбрать)
        langGroup.add(englishRadio);
        langGroup.add(russianRadio);

        
        JLabel algoLabel = new JLabel("Algorithm:");
        algoLabel.setFont(new Font("Arial", Font.BOLD, 12));
        String[] algorithms = {"Caesar", "Vigenère"};
        algorithmComboBox = new JComboBox<>(algorithms);   // creates a dropdown
        algorithmComboBox.setFont(new Font("Arial", Font.PLAIN, 12));
        algorithmComboBox.setBackground(Color.WHITE); 

        bruteforceCheckBox = new JCheckBox("Enable Bruteforce");
        bruteforceCheckBox.setFont(new Font("Arial", Font.PLAIN, 12));
        bruteforceCheckBox.setBackground(new Color(230, 240, 250));

        decryptButton = new JButton("Decrypt"); 
        decryptButton.setFont(new Font("Arial", Font.BOLD, 12));
        decryptButton.setBackground(new Color(46, 139, 87));
        decryptButton.setForeground(Color.WHITE);  // "Decrypt"
        decryptButton.setFocusPainted(false);

        clearButton = new JButton("Clear");
        clearButton.setFont(new Font("Arial", Font.BOLD, 12));
        clearButton.setBackground(new Color(220, 20, 60));
        clearButton.setForeground(Color.WHITE);
        clearButton.setFocusPainted(false);  //removes the focus border


        
        keyLabel = new JLabel("Detected Key:");
        keyLabel.setFont(new Font("Arial", Font.BOLD, 12));
        keyField = new JTextField(12);  // 12 characters width
        keyField.setFont(new Font("Arial", Font.PLAIN, 12)); 
        keyField.setBackground(Color.WHITE);
        keyField.setEditable(false);  // readonly 

        // Organize the contol elements in the top 
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

        // Center Panel
        
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 10, 10)); // 2 rows, 1 column, 10 pixel gaps
        centerPanel.setBackground(new Color(245, 245, 245));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // padding

        //Provides Editable Area
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

        // Organizex input n output in two rows
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

        // enables interactivity
        decryptButton.addActionListener(e -> decryptText());
        clearButton.addActionListener(e -> clearFields());
        englishRadio.addActionListener(e -> updateLabels());
        russianRadio.addActionListener(e -> updateLabels());
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    }

    private void decryptText() {
        String input = inputTextArea.getText().trim(); //remove leading and trailing whitespace
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

        // a ByteArrayOutputStream to capture output printed to System.out
        java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        // Redirect System.out to outContent to capture printed output
        System.setOut(new PrintStream(outContent));
        
        CaesarBruteforce.main(new String[]{input, langCode});

        // Restore the original System.out stream
        System.setOut(originalOut);

        // Get captured output as a string, normalize line endings, and trim whitespace
        result = outContent.toString().replaceAll("\\r\\n", "\n").trim();
        analysisResult = "Bruteforce analysis completed for all shifts.";

        // Dummy key value for Caesar since actual key is not detected in bruteforce mode
        detectedKey = "1";

    } else { 

        java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();

        // Redirect System.out to capture analysis output
        System.setOut(new java.io.PrintStream(outContent));

        // Perform Caesar decryption with frequency analysis
        CaesarDecrypt.analyzeCaesarDecryption(input, langCode);

        // Retrieve the captured output as a string
        analysisResult = outContent.toString();

        // Restore the original System.out (this time using FileDescriptor.out)
        System.setOut(new java.io.PrintStream(new java.io.FileOutputStream(java.io.FileDescriptor.out)));

        // Set result to be the same as analysis result in this mode
        result = analysisResult;

        // Dummy key value for Caesar since it's not explicitly extracted here
        detectedKey = "1";
    }

} else { // If algorithm is not Caesar, assume it's Vigenère

    // Select alphabet depending on the selected language
    String vigAlphabet = language.equals("English") ? VigenereCracker.ENG_ALPHABET : VigenereCracker.RUS_ALPHABET;

    // Prepare a builder to store filtered input (only characters from the chosen alphabet)
    StringBuilder filteredBuilder = new StringBuilder();

    // Convert input to uppercase and filter out characters not in the Vigenère alphabet
    for (char c : input.toUpperCase().toCharArray()) {
        if (vigAlphabet.indexOf(c) != -1) {
            filteredBuilder.append(c);
        }
    }

    // Final filtered text to be used for analysis
    String filteredInput = filteredBuilder.toString();

    // Ensure filtered input is long enough for meaningful Vigenère analysis
    if (filteredInput.length() < 10) {
        // Show error message if not enough text
        JOptionPane.showMessageDialog(this, "Text too short for Vigenère analysis.",
                "Input Error", JOptionPane.ERROR_MESSAGE);
        return; // Exit the method
    }

    // Automatically detect key length and extract key based on frequency analysis
    String key = VigenereCracker.autoDetectKeyLength(
            filteredInput,
            language.equals("English") ? VigenereCracker.ENG_ALPHABET : VigenereCracker.RUS_ALPHABET,
            language.equals("English") ? VigenereCracker.ENG_FREQ : VigenereCracker.RUS_FREQ,
            20 // maximum key length to consider
    ).key;

    // Capture frequency analysis output
    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    System.setOut(new java.io.PrintStream(outContent));

    // Perform frequency analysis to find the most likely key
    VigenereCracker.findKeyByFrequency(
            filteredInput,
            key.length(),
            language.equals("English") ? VigenereCracker.ENG_ALPHABET : VigenereCracker.RUS_ALPHABET,
            language.equals("English") ? VigenereCracker.ENG_FREQ : VigenereCracker.RUS_FREQ
    );

    // Save captured analysis output
    analysisResult = outContent.toString();

    // Restore the original System.out
    System.setOut(new java.io.PrintStream(new java.io.FileOutputStream(java.io.FileDescriptor.out)));

    // Decrypt the input text using the found key
    result = VigenereCracker.decryptVigenere(
            input,
            key,
            language.equals("English") ? VigenereCracker.ENG_ALPHABET : VigenereCracker.RUS_ALPHABET
    );

    // Store the detected key
    detectedKey = key;
}

// Set the result in the output text area
outputTextArea.setText(result);

// Show the analysis information (e.g., detected shifts, key frequency, etc.)
analysisTextArea.setText(analysisResult);

// Display the detected or default key
keyField.setText(detectedKey);


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
    // Schedule a job for the event-dispatching thread:
    // Creating and showing the application's GUI should be done on this special thread to avoid thread-safety issues.
    SwingUtilities.invokeLater(() -> {
        
        // Create a new instance of the DecryptionTool (this initializes the GUI window)
        DecryptionTool tool = new DecryptionTool();

        // Make the window visible on the screen
        tool.setVisible(true);
    });
}

}
