package dev.pawan.systemdesign.printerproblem.strategy;

import dev.pawan.systemdesign.printerproblem.entity.PrintRequest;
import dev.pawan.systemdesign.printerproblem.entity.Printer;
import dev.pawan.systemdesign.printerproblem.enums.PrinterType;
import dev.pawan.systemdesign.printerproblem.registry.PrinterRegistry;

public class NearestFloorSearchStrategy implements PrinterSearchStrategy {

    @Override
    public Printer searchAndAcquire(PrinterRegistry registry, PrintRequest request) {
        int reqFloor = request.getSourceFloor();
        int maxOffset = Math.max(
                Math.abs(registry.getMaxFloor() - reqFloor),
                Math.abs(reqFloor - registry.getMinFloor())
        );

        for (int offset = 0; offset <= maxOffset; offset++) {
            // Check upward
            Printer p = checkFloor(registry, reqFloor + offset, request.getRequiredType());
            if (p != null) return p;

            // Check downward (skip 0 to avoid double checking current floor)
            if (offset > 0) {
                p = checkFloor(registry, reqFloor - offset, request.getRequiredType());
                if (p != null) return p;
            }
        }
        return null;
    }

    private Printer checkFloor(PrinterRegistry registry, int floorNum, PrinterType type) {
        for (Printer p : registry.getPrintersOnFloor(floorNum)) {
            if ((type == null || p.getType() == type) && p.tryAcquire()) {
                return p;
            }
        }
        return null;
    }
}