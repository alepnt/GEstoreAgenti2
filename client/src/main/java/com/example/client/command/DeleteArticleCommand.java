package com.example.client.command;

import com.example.client.service.BackendGateway;

/**
 * Elimina un articolo dal catalogo.
 */
public class DeleteArticleCommand implements ClientCommand<Void> {

    private final Long id;

    public DeleteArticleCommand(Long id) {
        this.id = id;
    }

    @Override
    public CommandResult<Void> execute(BackendGateway gateway) {
        gateway.deleteArticle(id);
        return CommandResult.withoutHistory(null);
    }

    @Override
    public String description() {
        return "Eliminazione articolo #" + id;
    }
}
