package dci.exp.one.controllers;

import dci.exp.one.services.RunnerService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.scheduling.TaskExecutors;

@Controller("/runner")
public class RunnerController {

    private final RunnerService runnerService;

    public RunnerController(RunnerService runnerService) {
        this.runnerService = runnerService;
    }

    // Importante: esto puede tardar y bloquear, así que lo mandamos al pool BLOCKING/IO
    @ExecuteOn(TaskExecutors.BLOCKING)
    @Get("/run-all")
    public HttpResponse<String> runAll() {
        runnerService.runAllMains();
        return HttpResponse.ok("Se ejecutaron todas las clases con main() en el paquete dci.exp.one.abacus.v1.rare");
    }

    @ExecuteOn(TaskExecutors.BLOCKING)
    @Get("/hello")
    public HttpResponse<String> runHello(String n) {
        runnerService.runAllMains();
        return HttpResponse.ok("Se ejecutaron todas las clases con main() en el paquete dci.exp.one.abacus.v1.rare");
    }
}
