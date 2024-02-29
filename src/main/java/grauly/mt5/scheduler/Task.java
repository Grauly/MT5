package grauly.mt5.scheduler;

abstract public class Task {
    private int ticksTillRun;
    private int period;
    private int timeSinceLastRan;
    private boolean canceled;

    public void tick() {
        if(this.canceled) return;
        ticksTillRun = Math.max(0,ticksTillRun - 1);
        if(ticksTillRun > 0) return;
        if(timeSinceLastRan == 0) {
            run();
        }
        timeSinceLastRan = timeSinceLastRan +1 % period;
    }

    public abstract void run();

    public void startTask(TaskScheduler scheduler, int delay, int period) {
        this.ticksTillRun = delay;
        this.period = period;
        scheduler.scheduleTask(this);
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }
}
