package cascade.server.task.tasks;

import cascade.server.backend.ServerBackend;
import cascade.server.task.ServerTask;

public class ConnectionTask implements ServerTask {

    /**
     * Accept new clients.
     *
     * @param backend the backend.
     */
    @Override
    public void executeTask(ServerBackend backend) {
        backend.acceptClient();
    }
}
