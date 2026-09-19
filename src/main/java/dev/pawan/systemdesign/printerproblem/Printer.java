package dev.pawan.systemdesign.printerproblem;

import java.util.List;
import java.util.Queue;

public class Printer {

    private int printerId;
    private PrinterType printerType;
    private int floor;
    private Queue<PrinterJob> queue;

    int getQueueSize() {
        return queue.size();
    }

    public PrinterJob submitJob(PrinterJob job) {
        // Add the job to the queue
        queue.add(job);
        return job;
    }
}
