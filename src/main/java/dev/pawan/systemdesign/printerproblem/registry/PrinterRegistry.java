package dev.pawan.systemdesign.printerproblem.registry;

import dev.pawan.systemdesign.printerproblem.entity.Printer;

import java.util.List;
import java.util.Map;

public interface PrinterRegistry {
    void addPrinter(Printer printer);
    List<Printer> getPrintersOnFloor(int floorNumber);
    Map<Integer, List<Printer>> getAllFloors();
    int getMinFloor();
    int getMaxFloor();
}