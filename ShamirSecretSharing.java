import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.FileReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class ShamirSecretSharing {

    static class Point {
        final BigInteger x;
        final BigInteger y;

        Point(BigInteger x, BigInteger y) {
            this.x = x;
            this.y = y;
        }
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java -cp \".;gson-2.10.1.jar\" ShamirSecretSharing test1.json test2.json");
            System.exit(1);
        }

        try {
            System.out.println("Test Case 1:");
            processTestCase(args[0]);

            System.out.println("\nTest Case 2:");
            processTestCase(args[1]);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void processTestCase(String filename) throws Exception {
        JsonObject json = JsonParser.parseReader(new FileReader(filename)).getAsJsonObject();
        
        JsonObject keys = json.getAsJsonObject("keys");
        int n = keys.get("n").getAsInt();
        int k = keys.get("k").getAsInt();
        
        List<Point> points = new ArrayList<>();
        for (String key : json.keySet()) {
            if (key.equals("keys")) continue;
            
            JsonObject pointObj = json.getAsJsonObject(key);
            BigInteger x = new BigInteger(key);
            int base = pointObj.get("base").getAsInt();
            BigInteger y = new BigInteger(pointObj.get("value").getAsString(), base);
            
            points.add(new Point(x, y));
        }

        if (points.size() < k) {
            throw new IllegalArgumentException("Not enough points");
        }

        List<Point> selectedPoints = points.subList(0, k);
        BigInteger secret = lagrangeInterpolation(selectedPoints);

        System.out.println("Secret (constant term c): " + secret);
    }

    private static BigInteger lagrangeInterpolation(List<Point> points) {
        BigInteger secret = BigInteger.ZERO;
        int k = points.size();

        for (int i = 0; i < k; i++) {
            BigInteger xi = points.get(i).x;
            BigInteger yi = points.get(i).y;

            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;

            for (int j = 0; j < k; j++) {
                if (i == j) continue;

                BigInteger xj = points.get(j).x;
                numerator = numerator.multiply(xj.negate());
                denominator = denominator.multiply(xi.subtract(xj));
            }

            BigInteger term = yi.multiply(numerator).divide(denominator);
            secret = secret.add(term);
        }

        return secret;
    }
}