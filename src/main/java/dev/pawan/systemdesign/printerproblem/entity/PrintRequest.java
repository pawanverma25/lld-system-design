package dev.pawan.systemdesign.printerproblem.entity;

import dev.pawan.systemdesign.printerproblem.enums.PrinterType;

public class PrintRequest {
    private final int sourceFloor;
    private final PrinterType requiredType;

    public PrintRequest(int sourceFloor, PrinterType requiredType) {
        this.sourceFloor = sourceFloor;
        this.requiredType = requiredType;
    }
    public int getSourceFloor() { return sourceFloor; }
    public PrinterType getRequiredType() { return requiredType; }
}
