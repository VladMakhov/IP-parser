package api;

import io.javalin.Javalin;

public class Execution {
    public static void main(String[] args) {
        DomainService domainService = new DomainService(new IpService(), new CertificateService());
        Javalin
                .create()
                .get("/parse/{mask}/{threads}", context ->
                        context.result(domainService.doMultithreadedExecution(
                                context.pathParam("mask"), Integer.parseInt(context.pathParam("threads")))))
                .start(1234);

    }
}
