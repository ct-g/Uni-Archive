// Conor Tumbers
// c3190729@uon.edu.au
// SENG2200, Assignment 3
// Last modified 25/06/20

import java.util.LinkedList;
import java.util.PriorityQueue;

public class ProductionLine {
    private LinkedList<Stage> stageList;
    private LinkedList<InterStorage> interStorageList;

    private LinkedList<Stage> startStages;
    private double timeLimit;

    private Warehouse warehouse;

    // The constructor sets up the production line topology
    public ProductionLine(int M, int N, int Qmax, double timeLimit) {
        this.timeLimit = timeLimit;

        // Create storage
        InterStorage q01 = new InterStorage("q01", Qmax);
        InterStorage q12 = new InterStorage("q12", Qmax);
        InterStorage q23 = new InterStorage("q23", Qmax);
        InterStorage q34 = new InterStorage("q34", Qmax);
        InterStorage q45 = new InterStorage("q45", Qmax);
        InterStorage q56 = new InterStorage("q56", Qmax);

        // Add storage to a list for output purposes
        interStorageList = new LinkedList<>();
        interStorageList.add(q01);
        interStorageList.add(q12);
        interStorageList.add(q23);
        interStorageList.add(q34);
        interStorageList.add(q45);
        interStorageList.add(q56);

        // Warehouse is a storage of infinite size at end of production line
        warehouse = new Warehouse();

        // Create stages
        LinkedList<Stage> stages = new LinkedList<>();

        // Generate initial stages that can create items
        StartStage s0a = new StartStage(2 * M, 2* N, q01, "S0a", timeLimit);
        StartStage s0b = new StartStage(M, N, q01, "S0b", timeLimit);
        startStages = new LinkedList<>();
        startStages.add(s0a);
        startStages.add(s0b);

        // Generate all subsequent stages
        // The stages list is used to pass all prior stages that directly connect to the new stage
        stages.add(s0a);
        stages.add(s0b);
        Stage s1 = new Stage(M, N, q01, q12, "S1", timeLimit, stages);

        stages.clear();
        stages.add(s1);
        Stage s2 = new Stage(M, N, q12, q23, "S2", timeLimit, stages);

        stages.clear();
        stages.add(s2);
        Stage s3a = new Stage(2 * M, 2 * N, q23, q34, "S3a", timeLimit, stages);
        Stage s3b = new Stage(2 * M, 2 * N, q23, q34, "S3b", timeLimit, stages);

        stages.clear();
        stages.add(s3a);
        stages.add(s3b);
        Stage s4 = new Stage(M, N, q34, q45, "S4", timeLimit, stages);

        stages.clear();
        stages.add(s4);
        Stage s5a = new Stage(2 * M, 2 * N, q45, q56, "S5a", timeLimit, stages);
        Stage s5b = new Stage(2 * M, 2 * N, q45, q56, "S5b", timeLimit, stages);

        stages.clear();
        stages.add(s5a);
        stages.add(s5b);
        Stage s6 = new Stage(M, N, q56, warehouse, "S6", timeLimit, stages);

        // Add stages to a list for the purposes of command line output
        stageList = new LinkedList<>();
        stageList.add(s0a);
        stageList.add(s0b);
        stageList.add(s1);
        stageList.add(s2);
        stageList.add(s3a);
        stageList.add(s3b);
        stageList.add(s4);
        stageList.add(s5a);
        stageList.add(s5b);
        stageList.add(s6);
    }

    // Simulate the production of items and output statistics
    public void produce () {
        double currentTime = 0.0;

        // Generate first event(s) by iterating through the list of start stages
        PriorityQueue<Event> eventQueue = new PriorityQueue<>();

        for (Stage stage : startStages) {
            eventQueue.addAll(stage.update(currentTime));
        }

        // Simulate production
        while(!eventQueue.isEmpty() && currentTime <= timeLimit) {
            Event currentEvent = eventQueue.poll();
            currentTime = currentEvent.getTime();

            // Add all generated events to the priority queue
            eventQueue.addAll(currentEvent.update(currentTime));
        }

        // Output final results of production
        System.out.println("Production Stages:");
        System.out.println("--------------------------------------------");
        System.out.println("Stage   Work[%]      Starve[t]      Block[t]");
        for (Stage stage : stageList) {
            System.out.println(stage);
        }
        System.out.println("");

        System.out.println("Storage Queues:");
        System.out.println("------------------------------");
        System.out.println("Store   AvgTime[t]    AvgItems");
        for (InterStorage interStorage : interStorageList) {
            System.out.println(interStorage);
        }
        System.out.println("");

        System.out.println("Production Paths:");
        System.out.println("------------------");
        warehouse.productionPaths();
        System.out.println("");

        System.out.println("Production Items:");
        System.out.println("------------------");
        warehouse.productionItems();
    }
}
