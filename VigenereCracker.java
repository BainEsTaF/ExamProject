import java.util.*;

public class VigenereCracker {
    // ENG ALPHABET and FREQUENCY
    public static final String ENG_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final double[] ENG_FREQ = {
            0.08167, 0.01492, 0.02782, 0.04253, 0.12702, 0.02228, 0.02015, 0.06094,
            0.06966, 0.00153, 0.00772, 0.04025, 0.02406, 0.06749, 0.07507, 0.01929,
            0.00095, 0.05987, 0.06327, 0.09056, 0.02758, 0.00978, 0.02360, 0.00150,
            0.01974, 0.00074
    };
    // RUS ALPHABET and FREQUENCY
    public static final String RUS_ALPHABET = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";
    public static final double[] RUS_FREQ = {
            0.0764, 0.0201, 0.0438, 0.0172, 0.0309, 0.0875, 0.0020, 0.0101,
            0.0148, 0.0709, 0.0121, 0.0330, 0.0496, 0.0317, 0.0678, 0.1118,
            0.0247, 0.0423, 0.0497, 0.0609, 0.0222, 0.0021, 0.0095, 0.0039,
            0.0140, 0.0072, 0.0030, 0.0002, 0.0236, 0.0184, 0.0036, 0.0047, 0.0196
    };
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in, "UTF-8");
        System.out.println("Input encrypted text:");
        String text = scanner.nextLine();
        System.out.println("Select cipher language (1 – English, 2 – Russian):");
        int choice = scanner.nextInt();
        scanner.nextLine();

        String alphabet;
        double[] freq;
        if (choice == 1) {
            alphabet = ENG_ALPHABET;
            freq = ENG_FREQ;
        } else {
            alphabet = RUS_ALPHABET;
            freq = RUS_FREQ;
        }
        // FILTERING FOR ALPHABET
        StringBuilder filteredBuilder = new StringBuilder();
        for (char c : text.toUpperCase().toCharArray()) {
            if (alphabet.indexOf(c) != -1) {
                filteredBuilder.append(c);
            }
        }
        String filtered = filteredBuilder.toString();

        if (filtered.length() < 10) {
            System.out.println("Text too short for analysis.");
            return;
        }
        int maxKeyLen = 20;
        // AUTO FIND LENGTH KEY AND KEY
        KeyResult result = autoDetectKeyLength(filtered, alphabet, freq, maxKeyLen);
        System.out.println("Auto detected key length: " + result.key.length());
        System.out.println("Detected key: " + result.key);
        String decrypted = decryptVigenere(text, result.key, alphabet);
        System.out.println("Decrypted text:");
        System.out.println(decrypted);
    }
    // SAVE RESULT IN OBJECT
    public static class KeyResult {
        String key;
        double score;

        KeyResult(String key, double score) {
            this.key = key;
            this.score = score;
        }
    }
    // AUTO FIND LENGTH FOR CHI-SQUARE
    public static KeyResult autoDetectKeyLength(String text, String alphabet, double[] langFreq, int maxKeyLen) {
        // Заносим в стринг текст? убрав все лишнее из него
        String filtered = text.toUpperCase().replaceAll("[^" + alphabet + "]", "");
        if (filtered.length() < 10) {
            throw new IllegalArgumentException("Text is too short or does not contain valid characters from the alphabet.");
        }
        // Создаем массив мап для хранения повторяющихся элементов
        Map<String, List<Integer>> repeats = new HashMap<>();
        // Находим повторяющиеся элементы по 3 символа
        int minLen = 3;
        for (int i = 0; i < filtered.length() - minLen; i++) {
            String fragment = filtered.substring(i, i + minLen);
            repeats.computeIfAbsent(fragment, k -> new ArrayList<>()).add(i);
        }
        // Вычесляем длину между повторяющимися элементами
        List<Integer> distances = new ArrayList<>();
        for (List<Integer> positions : repeats.values()) {
            if (positions.size() > 1) {
                for (int i = 1; i < positions.size(); i++) {
                    distances.add(positions.get(i) - positions.get(i - 1));
                }
            }
        }
        // Вывод оишбки если нет таких элементов
        if (distances.isEmpty()) {
            System.out.println("No repeating fragments found, defaulting to key length 3.");
            return new KeyResult(findKeyByFrequency(filtered, 3, alphabet, langFreq), -1);
        }
        // Подсчет каждого сегмента и запись его длины для хранения в мап
        Map<Integer, Integer> factorCount = new HashMap<>();
        for (int distance : distances) {
            for (int factor = 2; factor <= maxKeyLen; factor++) {
                if (distance % factor == 0) {
                    factorCount.put(factor, factorCount.getOrDefault(factor, 0) + 1);
                }
            }
        }
        // Берем поток данных(Stream) из мап, находим самый вероятный, заносим его, если не находит то заносит 3
        int bestKeyLen = factorCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(3);
        String key = findKeyByFrequency(filtered, bestKeyLen, alphabet, langFreq);
        double score = scoreDecryption(filtered, key, alphabet, langFreq);
        return new KeyResult(key, score);
    }
    // CHI-SQUARE FOR CALCULATE THE AVERAGE SCORE
    public static double scoreDecryption(String text, String key, String alphabet, double[] langFreq) {
        int N = alphabet.length();
        int keyLen = key.length();
        double totalChi = 0;
        // Фор для на нахождения хи-квадрат(это показатель связи между сегментами и паролем)
        for (int i = 0; i < keyLen; i++) {
            int[] count = new int[N];
            int total = 0;
            // Фор для вычесления позиции
            for (int pos = i; pos < text.length(); pos += keyLen) {
                char c = text.charAt(pos);
                int idx = alphabet.indexOf(c);
                if (idx != -1) {
                    count[idx]++;
                    total++;
                }
            }
            if (total == 0) continue;
            int kidx = alphabet.indexOf(key.charAt(i));
            double chi = 0;
            // Фор для вычесления всех хи-квадратов, формула: (o-e)^2/e
            for (int j = 0; j < N; j++) {
                int shiftedIdx = (j + kidx) % N;
                double observed = count[shiftedIdx];
                double expected = total * langFreq[j];
                if (expected != 0) chi += Math.pow(observed - expected, 2) / expected;
            }
            totalChi += chi;
        }
        //Вычесление среднего хи
        return totalChi / keyLen;
    }
    // FREQ ANALYSIS and FIND KEY
    public static String findKeyByFrequency(String text, int keyLen, String alphabet, double[] langFreq) {
        int N = alphabet.length();
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < keyLen; i++) {
            int[] count = new int[N];
            int total = 0;
            // Фор для вычесления позиции
            for (int pos = i; pos < text.length(); pos += keyLen) {
                int idx = alphabet.indexOf(text.charAt(pos));
                if (idx != -1) {
                    count[idx]++;
                    total++;
                }
            }
            // Нахождение корреляции(зависимость между сдвигом и сегментами), для подбора нилучшей
            double bestCorr = -1;
            int bestShift = 0;
            for (int shift = 0; shift < N; shift++) {
                double corr = 0;
                for (int j = 0; j < N; j++) {
                    corr += langFreq[j] * (count[(j + shift) % N] / (double) total);
                }
                if (corr > bestCorr) {
                    bestCorr = corr;
                    bestShift = shift;
                }
            }
            key.append(alphabet.charAt(bestShift));

            // OUTPUT FREQ ANALYSIS
            System.out.println("Segment " + (i + 1) + " (shift=" + bestShift +
                    ", key letter='" + alphabet.charAt(bestShift) + "'):");
            // Лист для хранения мапов с символами и их вероятностью
            List<Map.Entry<Character, Double>> freqList = new ArrayList<>();
            for (int j = 0; j < N; j++) {
                if (count[j] > 0) {
                    char letter = alphabet.charAt(j);
                    double perc = count[j] * 100.0 / total;
                    freqList.add(new AbstractMap.SimpleEntry<>(letter, perc));
                }
            }
            // SORT IN DESCENDING ORDER
            freqList.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
            // OUTPUT TOP 3
            System.out.println("  Top 3 letters:");
            for (int j = 0; j < Math.min(3, freqList.size()); j++) {
                Map.Entry<Character, Double> entry = freqList.get(j);
                System.out.printf("    %c: %.2f%%\n", entry.getKey(), entry.getValue());
            }
        }
        return key.toString();
    }
    // DECRYPT VIGENERE WITH KEY
    public static String decryptVigenere(String cipher, String key, String alphabet) {
        StringBuilder result = new StringBuilder();
        int N = alphabet.length();
        int keyLen = key.length();
        int ki = 0;
        //Фор который проводит все символы через ключ по формуле: (ci - ki + N) mod N
        for (char c : cipher.toCharArray()) {
            int idx = alphabet.indexOf(Character.toUpperCase(c));
            if (idx != -1) {
                char kchar = key.charAt(ki % keyLen);
                int kidx = alphabet.indexOf(kchar);
                int pidx = (idx - kidx + N) % N;
                char pchar = alphabet.charAt(pidx);
                if (Character.isLowerCase(c)) {
                    result.append(Character.toLowerCase(pchar));
                } else {
                    result.append(pchar);
                }
                ki++;
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}