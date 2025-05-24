import java.util.*;

public class VigenereCracker {
    // ENG ALPHABET and FREQUENCY
    private static final String ENG_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final double[] ENG_FREQ = {
            0.08167, 0.01492, 0.02782, 0.04253, 0.12702, 0.02228, 0.02015, 0.06094,
            0.06966, 0.00153, 0.00772, 0.04025, 0.02406, 0.06749, 0.07507, 0.01929,
            0.00095, 0.05987, 0.06327, 0.09056, 0.02758, 0.00978, 0.02360, 0.00150,
            0.01974, 0.00074
    };
    // RUS ALPHABET and FREQUENCY
    private static final String RUS_ALPHABET = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";
    private static final double[] RUS_FREQ = {
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

        String alphabet = (choice == 1) ? ENG_ALPHABET : RUS_ALPHABET;
        double[] freq = (choice == 1) ? ENG_FREQ : RUS_FREQ;

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
    }
    static class KeyResult {
        String key;
        double score;

        KeyResult(String key, double score) {
            this.key = key;
            this.score = score;
        }
    }
    
    private static KeyResult autoDetectKeyLength(String text, String alphabet, double[] langFreq, int maxKeyLen) {
        double bestScore = Double.MAX_VALUE;
        String bestKey = "";
        for (int keyLen = 1; keyLen <= maxKeyLen; keyLen++) {
            String key = findKeyByFrequency(text, keyLen, alphabet, langFreq);
            double score = scoreDecryption(text, key, alphabet, langFreq);
            if (score < bestScore) {
                bestScore = score;
                bestKey = key;
            }
        }
        return new KeyResult(bestKey, bestScore);
    }
    private static String findKeyByFrequency(String text, int keyLen, String alphabet, double[] langFreq) {
        int N = alphabet.length();
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < keyLen; i++) {
            int[] count = new int[N];
            int total = 0;
            for (int pos = i; pos < text.length(); pos += keyLen) {
                char c = text.charAt(pos);
                int idx = alphabet.indexOf(c);
                if (idx != -1) {
                    count[idx]++;
                    total++;
                }
            }
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
            System.out.println("Segment " + (i + 1) + " (shift=" + bestShift +
                    ", key letter='" + alphabet.charAt(bestShift) + "'):");
            List<Map.Entry<Character, Double>> freqList = new ArrayList<>();
            for (int j = 0; j < N; j++) {
                if (count[j] > 0) {
                    freqList.add(new AbstractMap.SimpleEntry<>(alphabet.charAt(j), count[j] * 100.0 / total));
                }
            }
            freqList.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
            System.out.println("  Top 3 letters:");
            for (int j = 0; j < Math.min(3, freqList.size()); j++) {
                Map.Entry<Character, Double> entry = freqList.get(j);
                System.out.printf("    %c: %.2f%%\n", entry.getKey(), entry.getValue());
            }
        }
        return key.toString();
    }
    private static double scoreDecryption(String text, String key, String alphabet, double[] langFreq) {
        int N = alphabet.length();
        int keyLen = key.length();
        double totalChi = 0;
        for (int i = 0; i < keyLen; i++) {
            int[] count = new int[N];
            int total = 0;
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
            for (int j = 0; j < N; j++) {
                int shiftedIdx = (j + kidx) % N;
                double observed = count[shiftedIdx];
                double expected = total * langFreq[j];
                if (expected != 0) {
                    chi += Math.pow(observed - expected, 2) / expected;
                }
            }
            totalChi += chi;
        }
        return totalChi / keyLen;
    }
}