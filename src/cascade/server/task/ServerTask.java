package cascade.server.task;

import cascade.server.backend.ServerBackend;

public interface ServerTask {

    /**
     * Execute this task.
     *
     * @param backend the backend.
     */
    void executeTask(ServerBackend backend);

}
