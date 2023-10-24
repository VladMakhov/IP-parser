package api;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

/**
 * Class to get domain from single IP
 * */
public class CertificateService {

    /**
     * Single IP -> domain name
     * */
    public String getDomain(String ipAddress) {
        return extractDomainFromCertificate(extractCertificateFromIp(ipAddress));
    }

    private Certificate[] extractCertificateFromIp(String ipAddress) {
        try {
            SSLContext sslContext = SSLContext.getDefault();
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(ipAddress, 443), 1000);
                SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(socket, ipAddress, 443, true);
                sslSocket.startHandshake();
                return sslSocket.getSession().getPeerCertificates();
            }
        } catch (Exception ignored) {
        }
        return new Certificate[0];
    }

    private String extractDomainFromCertificate(Certificate[] certificates) {
        if (certificates.length > 0 && certificates[0] instanceof X509Certificate) {
            X509Certificate x509Certificate = (X509Certificate) certificates[0];
            return x509Certificate.getSubjectX500Principal().getName();
        }
        return "";
    }

}
