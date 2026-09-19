package dev.pawan.systemdesign.printerproblem;

public record PrinterJob (
        int userId,
        int noOfPages,
        PrinterType printerType,
        int originFloor
        // sheet size, color preference, duplex printing, etc. can be added as needed
){
}
