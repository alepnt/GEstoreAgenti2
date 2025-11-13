package com.example.client.command;

import com.example.client.service.BackendGateway;
import com.example.common.dto.ArticleDTO;

import java.util.List;

/**
 * Carica il catalogo articoli.
 */
public class LoadArticlesCommand implements ClientCommand<List<ArticleDTO>> {

    @Override
    public CommandResult<List<ArticleDTO>> execute(BackendGateway gateway) {
        return CommandResult.withoutHistory(gateway.listArticles());
    }

    @Override
    public String description() {
        return "Caricamento catalogo articoli";
    }
}
