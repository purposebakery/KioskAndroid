package com.techlung.kiosk.greendao.extended;


import com.techlung.kiosk.greendao.generated.Article;
import com.techlung.kiosk.greendao.generated.ArticleDao;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class ExtendedArticleDao {
    public ArticleDao dao;

    public ExtendedArticleDao(ArticleDao ArticleDao) {
        this.dao = ArticleDao;
    }

    public List<Article> getAllArticles() {
        QueryBuilder<Article> queryBuilder = dao.queryBuilder();
        queryBuilder.orderAsc(ArticleDao.Properties.Price);
        return queryBuilder.list();
    }

    public long getCount() {
        QueryBuilder<Article> queryBuilder = dao.queryBuilder();
        return queryBuilder.count();
    }

    public void insertOrReplace(Article Article) {
        dao.insertOrReplace(Article);
    }

    public void update(Article Article) {
        dao.update(Article);
    }

    public void delete(Article Article) {
        dao.delete(Article);
    }

    public void deleteAll() {
        dao.deleteAll();
    }
}