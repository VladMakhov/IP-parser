package api;

import java.util.ArrayList;
import java.util.List;


public class IpService {
    /**
     * Create a List of IPs from a mask (default is 0.0.0.0/24 representing 0.0.0.0 - 0.0.0.255)
     * */
    public List<String> generateListFromMask(String ip) {
        if (validateMask(ip)) {
            List<String> result = new ArrayList<>();
            String lastValue;

            int p = ip.length() - 1;

            while (true) {
                if (ip.charAt(p) != '.') {
                    p--;
                } else {
                    lastValue = ip.substring(p + 1);
                    break;
                }
            }

            int start = Integer.parseInt(lastValue);
            for (int i = start; i <= 255; i++) {
                String newIp = ip.substring(0, p + 1) + i;
                result.add(newIp);
            }
            return result;
        } else {
            throw new IllegalArgumentException("Incorrect IP mask");
        }
    }

    /**
    * Private validation of IP method
    * */
    private static boolean validateMask(String ip) {
        if (ip.length() >= 7 && ip.length() <= 15 && !ip.contains(" ")) {
            byte c = 0;
            for (int i = 0; i < ip.length(); i++) {
                if (ip.charAt(i) == '.') c++;
            }
            return c == 3;
        } else {
            return false;
        }
    }

}
