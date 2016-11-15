package com.techlung.kiosk.greendao.generated;

import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 

/**
 * Entity mapped to table "PURCHASE".
 */
public class Purchase {

    private Long id;
    private Integer amount;
    private Long articleId;
    private Long customerId;

    /**
     * Used to resolve relations
     */
    private transient DaoSession daoSession;

    /**
     * Used for active entity operations.
     */
    private transient PurchaseDao myDao;

    private Article article;
    private Long article__resolvedKey;


    public Purchase() {
    }

    public Purchase(Long id) {
        this.id = id;
    }

    public Purchase(Long id, Integer amount, Long articleId, Long customerId) {
        this.id = id;
        this.amount = amount;
        this.articleId = articleId;
        this.customerId = customerId;
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getPurchaseDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    /**
     * To-one relationship, resolved on first access.
     */
    public Article getArticle() {
        Long __key = this.articleId;
        if (article__resolvedKey == null || !article__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ArticleDao targetDao = daoSession.getArticleDao();
            Article articleNew = targetDao.load(__key);
            synchronized (this) {
                article = articleNew;
                article__resolvedKey = __key;
            }
        }
        return article;
    }

    public void setArticle(Article article) {
        synchronized (this) {
            this.article = article;
            articleId = article == null ? null : article.getId();
            article__resolvedKey = articleId;
        }
    }

    /**
     * Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context.
     */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context.
     */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /**
     * Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context.
     */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

}
