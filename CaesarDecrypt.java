import java.util.*;

public class CaesarDecrypt {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);


        System.out.print("Введите зашифрованный текст: ");
        String cipherText = scanner.nextLine();

        System.out.print("Выберите язык (en/ru): ");
        String language = scanner.nextLine().trim().toLowerCase();

        if (!language.equals("en") && !language.equals("ru")) {
            System.out.println("Неверный язык. Используйте 'en' или 'ru'.");
            return;
        }

        analyzeCaesarDecryption(cipherText, language);
    }

    public static void analyzeCaesarDecryption(String cipherText, String language) {
        String alphabet = language.equals("ru") ? "абвгдеёжзийклмнопрстуфхцчшщъыьэюя" : "abcdefghijklmnopqrstuvwxyz";
        char mostFrequentExpected = language.equals("ru") ? 'о' : 'e';
        int alphabetLength = alphabet.length();

        // Counting letter frequencies
        Map<Character, Integer> frequency = new HashMap<>();
        for (char ch : cipherText.toLowerCase().toCharArray()) {
            if (alphabet.indexOf(ch) != -1) {
                frequency.put(ch, frequency.getOrDefault(ch, 0) + 1);
            }
        }

        if (frequency.isEmpty()) {
            System.out.println(" Текст не содержит букв выбранного алфавита.");
            return;
        }

        // Sort the frequencies in descending order
        List<Map.Entry<Character, Integer>> sortedFreq = new ArrayList<>(frequency.entrySet());
        sortedFreq.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        System.out.println("\n Частотный анализ:");
        for (Map.Entry<Character, Integer> entry : sortedFreq) {
            System.out.printf("   %c : %d\n", entry.getKey(), entry.getValue());
        }

        // TOP-3 deciphers of the most frequent letters
        System.out.println("\n TOP-3 вероятные расшифровки:");
        int top = Math.min(3, sortedFreq.size());
        for (int i = 0; i < top; i++) {
            char frequentChar = sortedFreq.get(i).getKey();
            int shift = (alphabet.indexOf(frequentChar) - alphabet.indexOf(mostFrequentExpected) + alphabetLength) % alphabetLength;

            StringBuilder result = new StringBuilder();
            for (char ch : cipherText.toCharArray()) {
                boolean isUpper = Character.isUpperCase(ch);
                char lowerChar = Character.toLowerCase(ch);
                int index = alphabet.indexOf(lowerChar);

                if (index != -1) {
                    int newIndex = (index - shift + alphabetLength) % alphabetLength;
                    char newChar = alphabet.charAt(newIndex);
                    result.append(isUpper ? Character.toUpperCase(newChar) : newChar);
                } else {
                    result.append(ch);
                }
            }

            System.out.printf("   #%d [сдвиг = %d, частая буква = '%c']:\n     %s\n\n",
                    i + 1, shift, frequentChar, result.toString());
        }
    }
}
