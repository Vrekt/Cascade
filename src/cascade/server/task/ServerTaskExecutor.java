package cascade.server.task;

import cascade.Cascade;
import cascade.server.backend.ServerBackend;
import cascade.server.task.tasks.ConnectionTask;
import cascade.server.task.tasks.ValidateTask;

public class ServerTaskExecutor {

    private final ServerBackend backend;

    public ServerTaskExecutor(ServerBackend backend) {
        this.backend = backend;
    }

    public void start() {

        final ConnectionTask connectionTask = new ConnectionTask();

        Thread connectionThread = new Thread(() -> {
            while (Cascade.getServer().isRunning()) {
                // execute our work.
                connectionTask.executeTask(backend);
            }
        });

        final ValidateTask validateTask = new ValidateTask();

        Thread validateThread = new Thread(() -> {
            while (Cascade.getServer().isRunning()) {
                // execute our work.
                validateTask.executeTask(backend);
            }
        });

        // start the threads.
        connectionThread.start();
        validateThread.start();
    }

}
