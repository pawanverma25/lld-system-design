package dev.pawan.systemdesign.printerproblem.entity;

import dev.pawan.systemdesign.printerproblem.enums.PrinterStatus;
import dev.pawan.systemdesign.printerproblem.enums.PrinterType;

import java.util.concurrent.atomic.AtomicReference;

public class Printer {
    private final String id;
    private final int floorNumber;
    private final PrinterType type;
    private final int speedPpm;
    private final AtomicReference<PrinterStatus> status;

    public Printer(String id, int floorNumber, PrinterType type, int speedPpm) {
        this.id = id;
        this.floorNumber = floorNumber;
        this.type = type;
        this.speedPpm = speedPpm;
        this.status = new AtomicReference<>(PrinterStatus.AVAILABLE);
    }

    public boolean tryAcquire() {
        return status.compareAndSet(PrinterStatus.AVAILABLE, PrinterStatus.BUSY);
    }

    public void release() {
        status.compareAndSet(PrinterStatus.BUSY, PrinterStatus.AVAILABLE);
    }

    public void markOffline() { status.set(PrinterStatus.OFFLINE); }

    // Getters
    public String getId() { return id; }
    public int getFloorNumber() { return floorNumber; }
    public PrinterType getType() { return type; }
    public int getSpeedPpm() { return speedPpm; }
    public PrinterStatus getStatus() { return status.get(); }
}