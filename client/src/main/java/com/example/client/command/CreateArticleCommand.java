package com.example.client.command;

import com.example.client.service.BackendGateway;
import com.example.common.dto.ArticleDTO;

/**
 * Crea un nuovo articolo.
 */
public class CreateArticleCommand implements ClientCommand<ArticleDTO> {

    private final ArticleDTO article;

    public CreateArticleCommand(ArticleDTO article) {
        this.article = article;
    }

    @Override
    public CommandResult<ArticleDTO> execute(BackendGateway gateway) {
        ArticleDTO created = gateway.createArticle(article);
        return CommandResult.withoutHistory(created);
    }

    @Override
    public String description() {
        return "Creazione articolo " + article.getName();
    }
}
