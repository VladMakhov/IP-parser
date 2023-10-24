package api;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class DomainService {

    private final IpService ipService;
    private final CertificateService certificateService;

    public DomainService(IpService ipService, CertificateService certificateService) {
        this.ipService = ipService;
        this.certificateService = certificateService;
    }

    /**
     * Method receives a string of IPs and return formatted string of IP - domain
     * <p>
     * Example:
     * <p>
     * 0.0.0.1 - domain1
     * <p>
     * 0.0.0.2 - domain2
     * <p> ...
     */
    public String getFormatted(List<String> list) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : list.stream()
                .map(currentIp -> {
                    String domain = certificateService.getDomain(currentIp);
                    if (!domain.isEmpty()) return currentIp + " - " + domain;
                    return null;
                })
                .filter(Objects::nonNull).collect(Collectors.toList())) {
            stringBuilder.append(s).append("\n");
        }
        return stringBuilder.toString();
    }

    /**
     * Creating Executor service with fixed thread pool (given argument is number of threads)
     * <p>
     * In multithreading it gets domain for every given IP and returns formatted result
     */
    public String doMultithreadedExecution(String idAddress, int threadsNumber) {
        ExecutorService executorService = Executors.newFixedThreadPool(threadsNumber);

        StringBuilder result = new StringBuilder();
        List<String> ips = ipService.generateListFromMask(idAddress);
        List<List<String>> list = splitList(ips, threadsNumber);
        for (int i = 0; i < threadsNumber; i++) {
            int finalI = i;
            executorService.execute(() -> {
                String thrRes = getFormatted(list.get(finalI));
                result.append(thrRes);
            });
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return result.toString();
    }

    /**
     * Method splits List of Strings into equal n-size Lists for
     * further use in multithreading environment
     */
    public List<List<String>> splitList(List<String> elements, int n) {
        int size = elements.size();
        int partSize = (int) Math.ceil((double) size / n);

        List<List<String>> result = new ArrayList<>();

        for (int i = 0; i < size; i += partSize) {
            List<String> part = elements.subList(i, Math.min(i + partSize, size));
            result.add(part);
        }
        return result;
    }

}
