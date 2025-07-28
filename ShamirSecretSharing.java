import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.FileReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements Shamir's Secret Sharing algorithm to reconstruct a secret
 * from distributed shares using polynomial interpolation.
 */
public class ShamirSecretSharing {

    /**
     * Represents a point (x,y) on the polynomial curve
     */
    static class PolynomialPoint {
        final BigInteger x;  // The share's identifier
        final BigInteger y;  // The share's value

        PolynomialPoint(BigInteger x, BigInteger y) {
            this.x = x;
            this.y = y;
        }
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            printUsage();
            System.exit(1);
        }

        try {
            processTestCases(args[0], args[1]);
        } catch (Exception e) {
            handleError(e);
        }
    }

    private static void printUsage() {
        System.err.println("Usage: java -cp \".;gson-2.10.1.jar\" ShamirSecretSharing test1.json test2.json");
        System.err.println("Please provide two JSON files containing the secret shares");
    }

    private static void processTestCases(String file1, String file2) throws Exception {
        System.out.println("Processing Test Case 1:");
        BigInteger secret1 = processSecretFile(file1);
        System.out.println("Recovered Secret: " + secret1);

        System.out.println("\nProcessing Test Case 2:");
        BigInteger secret2 = processSecretFile(file2);
        System.out.println("Recovered Secret: " + secret2);
    }

    private static void handleError(Exception e) {
        System.err.println("Error during secret reconstruction: " + e.getMessage());
        System.err.println("Please check your input files and try again.");
        e.printStackTrace();
        System.exit(1);
    }

    /**
     * Processes a JSON file containing secret shares and reconstructs the original secret
     */
    private static BigInteger processSecretFile(String filename) throws Exception {
        // 1. Read and parse the JSON file
        JsonObject jsonData = readJsonFile(filename);
        
        // 2. Extract the threshold parameters
        JsonObject config = jsonData.getAsJsonObject("keys");
        int totalShares = config.get("n").getAsInt();
        int requiredShares = config.get("k").getAsInt();
        
        // 3. Decode all share points
        List<PolynomialPoint> shares = extractShares(jsonData);
        
        // 4. Validate we have enough shares
        if (shares.size() < requiredShares) {
            throw new IllegalArgumentException(
                String.format("Need %d shares but only found %d", requiredShares, shares.size())
            );
        }

        // 5. Use the first k shares to reconstruct the secret
        List<PolynomialPoint> selectedShares = shares.subList(0, requiredShares);
        return reconstructSecret(selectedShares);
    }

    private static JsonObject readJsonFile(String filename) throws Exception {
        FileReader reader = new FileReader(filename);
        return JsonParser.parseReader(reader).getAsJsonObject();
    }

    private static List<PolynomialPoint> extractShares(JsonObject jsonData) {
        List<PolynomialPoint> shares = new ArrayList<>();
        
        for (String key : jsonData.keySet()) {
            if (key.equals("keys")) continue;  // Skip configuration
            
            JsonObject shareData = jsonData.getAsJsonObject(key);
            BigInteger x = new BigInteger(key);  // The share's ID
            
            // Decode the share value from its specified base
            int numericBase = shareData.get("base").getAsInt();
            String encodedValue = shareData.get("value").getAsString();
            BigInteger y = new BigInteger(encodedValue, numericBase);
            
            shares.add(new PolynomialPoint(x, y));
        }
        
        return shares;
    }

    /**
     * Reconstructs the secret using Lagrange polynomial interpolation
     */
    private static BigInteger reconstructSecret(List<PolynomialPoint> points) {
        BigInteger secret = BigInteger.ZERO;
        int numberOfPoints = points.size();

        for (int i = 0; i < numberOfPoints; i++) {
            PolynomialPoint currentPoint = points.get(i);
            
            // Calculate the Lagrange basis polynomial
            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;
            
            for (int j = 0; j < numberOfPoints; j++) {
                if (i == j) continue;
                
                PolynomialPoint otherPoint = points.get(j);
                numerator = numerator.multiply(otherPoint.x.negate());
                denominator = denominator.multiply(
                    currentPoint.x.subtract(otherPoint.x)
                );
            }
            
            // Add the current term's contribution to the secret
            BigInteger term = currentPoint.y
                .multiply(numerator)
                .divide(denominator);
                
            secret = secret.add(term);
        }

        return secret;
    }
}