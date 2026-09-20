package dev.pawan.systemdesign.printerproblem.registry;

import dev.pawan.systemdesign.printerproblem.entity.Printer;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class BuildingPrinterRegistry implements PrinterRegistry {
    private final Map<Integer, List<Printer>> floorMap = new ConcurrentHashMap<>();
    private int minFloor = Integer.MAX_VALUE;
    private int maxFloor = Integer.MIN_VALUE;

    @Override
    public void addPrinter(Printer p) {
        floorMap.computeIfAbsent(p.getFloorNumber(), k -> new CopyOnWriteArrayList<>()).add(p);
        synchronized (this) {
            minFloor = Math.min(minFloor, p.getFloorNumber());
            maxFloor = Math.max(maxFloor, p.getFloorNumber());
        }
    }

    @Override
    public List<Printer> getPrintersOnFloor(int floorNumber) {
        return floorMap.getOrDefault(floorNumber, List.of());
    }

    @Override
    public Map<Integer, List<Printer>> getAllFloors() { return floorMap; }

    @Override
    public int getMinFloor() { return minFloor; }

    @Override
    public int getMaxFloor() { return maxFloor; }
}