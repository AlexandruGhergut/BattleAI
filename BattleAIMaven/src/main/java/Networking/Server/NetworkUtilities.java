package Networking.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class NetworkUtilities {

    public static String getPublicIP() throws MalformedURLException, IOException {
        String service1 = "http://checkip.amazonaws.com/";
        String service2 = "http://bot.whatismyipaddress.com/";

        URL url = new URL(service1);
        String ip = "";
        try (BufferedReader input = new BufferedReader(new InputStreamReader(url.openStream()))) {
            ip = input.readLine();
        } catch (IOException ex) {
            url = new URL(service2);
            try (BufferedReader input2 = new BufferedReader(new InputStreamReader(url.openStream()))) {
                ip = input2.readLine();
            } catch (IOException ex2) {
                throw ex2;
            }
        }

        return ip;
    }
}
