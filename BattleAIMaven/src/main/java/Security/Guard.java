package Security;

import Console.ConsoleFrame;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author Dragos-Alexandru
 */
public abstract class Guard {

    public static String scramblePassword(String input) {
        String output;
        String salt = "kzjvhsuryiqh293810d9uhd12983yh";
        String pepper = "8293fhsd8c9y32njksjdhf8923";

        output = salt + pepper + input + pepper + salt;
        try {
            MessageDigest scrambler = MessageDigest.getInstance("SHA-256");
            byte[] byteScrambled = scrambler.digest(output.getBytes());
            output = "";
            for (byte aux : byteScrambled) {
                output += (char) aux;
            }
        } catch (NoSuchAlgorithmException ex) {
            ConsoleFrame.sendMessage(Guard.class.getCanonicalName(), "Failed to hash user password");
            return null;
        }

        ConsoleFrame.sendMessage(Guard.class.getCanonicalName(), "Succesfully hashed user password");

        return output;
    }

}
