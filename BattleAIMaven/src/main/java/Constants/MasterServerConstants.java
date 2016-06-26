package Constants;

/**
 *
 * @author Dragos-Alexandru
 */
public interface MasterServerConstants {

    final String MASTER_USERNAME = "BattleAIMS";
    final String IP = getIP();
    int PORT = 60010;
    long PACKET_DELAY = 3000; // expressed in milliseconds
    final boolean TCP_NO_DELAY = true;
    
    public static String getIP() {
        if (System.getenv().get("TRAVIS") != null || System.getProperty("TEST") != null) {
            return "localhost";
        } else {
            //When creating a match via LAN it looks like this causes a deadlock
            //Please investigate.
            /*String ip = JOptionPane.showInputDialog(null, "Master Server IP: ",
                    "MS - IP", JOptionPane.INFORMATION_MESSAGE);
            while(ip == null){
                ip = JOptionPane.showInputDialog(null, "Master Server IP: ",
                    "MS - IP", JOptionPane.INFORMATION_MESSAGE);
            }*/
            return "84.232.219.164";
        }
    }

}
