package dev.pawan.systemdesign.printerproblem.strategy;

import dev.pawan.systemdesign.printerproblem.entity.PrintRequest;
import dev.pawan.systemdesign.printerproblem.entity.Printer;
import dev.pawan.systemdesign.printerproblem.registry.PrinterRegistry;

public interface PrinterSearchStrategy {
    Printer searchAndAcquire(PrinterRegistry registry, PrintRequest request);
}