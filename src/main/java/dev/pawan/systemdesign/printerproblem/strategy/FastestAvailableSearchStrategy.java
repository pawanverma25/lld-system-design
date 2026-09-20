package dev.pawan.systemdesign.printerproblem.strategy;
import dev.pawan.systemdesign.printerproblem.entity.PrintRequest;
import dev.pawan.systemdesign.printerproblem.entity.Printer;
import dev.pawan.systemdesign.printerproblem.registry.PrinterRegistry;

import java.util.Comparator;
import java.util.List;

public class FastestAvailableSearchStrategy implements PrinterSearchStrategy {

    @Override
    public Printer searchAndAcquire(PrinterRegistry registry, PrintRequest request) {
        // Collect all printers matching the type
        List<Printer> matchingPrinters = registry.getAllFloors().values().stream()
                .flatMap(List::stream)
                .filter(p -> p.getType() == request.getRequiredType() || request.getRequiredType() == null)
                .sorted(Comparator.comparingInt(Printer::getSpeedPpm).reversed())
                .toList();

        // Try to acquire the fastest one; if it fails (another thread grabbed it), try the next.
        for (Printer p : matchingPrinters) {
            if (p.tryAcquire()) {
                return p;
            }
        }
        return null;
    }
}