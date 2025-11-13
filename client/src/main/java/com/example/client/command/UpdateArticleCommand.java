package com.example.client.command;

import com.example.client.service.BackendGateway;
import com.example.common.dto.ArticleDTO;

/**
 * Aggiorna un articolo esistente.
 */
public class UpdateArticleCommand implements ClientCommand<ArticleDTO> {

    private final Long id;
    private final ArticleDTO article;

    public UpdateArticleCommand(Long id, ArticleDTO article) {
        this.id = id;
        this.article = article;
    }

    @Override
    public CommandResult<ArticleDTO> execute(BackendGateway gateway) {
        ArticleDTO updated = gateway.updateArticle(id, article);
        return CommandResult.withoutHistory(updated);
    }

    @Override
    public String description() {
        return "Aggiornamento articolo #" + id;
    }
}
