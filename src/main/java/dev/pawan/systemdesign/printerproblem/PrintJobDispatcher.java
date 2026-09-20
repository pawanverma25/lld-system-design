package dev.pawan.systemdesign.printerproblem;

import dev.pawan.systemdesign.printerproblem.entity.PrintRequest;
import dev.pawan.systemdesign.printerproblem.entity.Printer;
import dev.pawan.systemdesign.printerproblem.registry.PrinterRegistry;
import dev.pawan.systemdesign.printerproblem.strategy.PrinterSearchStrategy;

public class PrintJobDispatcher {
    private final PrinterRegistry registry;
    // Default strategy
    private PrinterSearchStrategy defaultStrategy;

    public PrintJobDispatcher(PrinterRegistry registry, PrinterSearchStrategy defaultStrategy) {
        this.registry = registry;
        this.defaultStrategy = defaultStrategy;
    }

    // Allows dynamic changing of default strategy
    public void setDefaultStrategy(PrinterSearchStrategy strategy) {
        this.defaultStrategy = strategy;
    }

    // Process using default strategy
    public Printer dispatch(PrintRequest request) {
        return dispatch(request, this.defaultStrategy);
    }

    // Process using a specific strategy overridden for this single request
    public Printer dispatch(PrintRequest request, PrinterSearchStrategy strategy) {
        Printer assignedPrinter = strategy.searchAndAcquire(registry, request);
        if (assignedPrinter == null) {
            System.out.println("Job Failed: No available printers found for request on floor " + request.getSourceFloor());
        }
        return assignedPrinter;
    }
}