package com.example.blogapp.articles;

import com.example.blogapp.articles.dtos.CreateArticleRequest;
import com.example.blogapp.articles.dtos.UpdateArticleRequest;
import com.example.blogapp.users.UserService;
import com.example.blogapp.users.UsersRepository;
import com.example.blogapp.users.dtos.CreateUserRequest;
import org.springframework.stereotype.Service;

@Service
public class ArticleService {
    private ArticlesRepository articlesRepository;
    private UsersRepository usersRepository;

    public ArticleService(ArticlesRepository articlesRepository, UsersRepository usersRepository) {
        this.articlesRepository = articlesRepository;
        this.usersRepository = usersRepository;
    }

    public Iterable<ArticleEntity> getAllArticles() {
        return articlesRepository.findAll();
    }

    public ArticleEntity getArticlesBySlug(String slug) {
        var article = articlesRepository.findBySlug(slug);
        if(article == null) {
            throw new ArticleNotFoundException(slug);
        }
        return article;
    }

    public ArticleEntity createArticle(CreateArticleRequest a, Long authorId) {
        var author = usersRepository.findById(authorId).orElseThrow(() -> new UserService.UserNotFoundException(authorId));

        return articlesRepository.save(ArticleEntity.builder()
                .title(a.getTitle())
                //TODO: slugification function
                .slug(a.getTitle().toLowerCase().replaceAll("\\s+", "-"))
                .body(a.getBody())
                .subtitle(a.getSubtitle())
                .author(author)
                .build()
        );
    }

    public ArticleEntity updateArticle(Long articleId, UpdateArticleRequest a) {
        var article = articlesRepository.findById(articleId).orElseThrow(() -> new UserService.UserNotFoundException(articleId));

        if(a.getTitle() != null) {
            article.setTitle(a.getTitle());
            article.setSlug(a.getTitle().toLowerCase().replaceAll("\\s+", "-"));
        }
        if(a.getBody() != null) {
            article.setTitle(a.getBody());
        }
        if(a.getSubtitle() != null) {
            article.setTitle(a.getSubtitle());
        }
        return articlesRepository.save(article);
    }

    static class ArticleNotFoundException extends IllegalArgumentException {
        public ArticleNotFoundException(String slug) {
            super("Article " + slug + " not found");
        }
        public ArticleNotFoundException(Long articleId) {
            super("Article with id " + articleId + " not found");
        }
    }

}
