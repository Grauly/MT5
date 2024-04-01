package grauly.mt5.scheduler;

import java.util.function.Supplier;

public class SingleRunLaterLambdaTask extends Task {
    private final Supplier<Integer> func;

    public SingleRunLaterLambdaTask(Supplier<Integer> func) {
        this.func = func;
    }

    @Override
    public void run() {
        func.get();
        this.setCanceled(true);
    }
}
