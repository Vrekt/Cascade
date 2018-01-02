package cascade.server.task.tasks;

import cascade.server.backend.ServerBackend;
import cascade.server.task.ServerTask;

public class ValidateTask implements ServerTask {
    /**
     * Validate all connected clients.
     *
     * @param backend the backend.
     */
    @Override
    public void executeTask(ServerBackend backend) {
        backend.validate();
    }
}
