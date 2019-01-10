class Transmission {
    int startTime, finishTime;
    Task fromTask, toTask;
    int fromProc, toProc;

    Transmission(int startTime, int finishTime, Task fromTask, Task toTask, int fromProc, int toProc) {
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.fromTask = fromTask;
        this.toTask = toTask;
        this.fromProc = fromProc;
        this.toProc = toProc;
    }
}
