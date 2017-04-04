package com.techlung.kiosk.model;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.Sort;
import io.realm.annotations.PrimaryKey;

public class Purchase extends RealmObject {

    @PrimaryKey
    private long id;
    private int amount;
    private long articleId;
    private long customerId;

    private Article article;
    private Customer customer;

    public static long createId() {
        List<Purchase> purchases = Realm.getDefaultInstance().where(Purchase.class).findAllSorted("id", Sort.DESCENDING);

        if (purchases.isEmpty()) {
            return 0;
        } else {
            return purchases.get(0).getId() + 1;
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public long getArticleId() {
        return articleId;
    }

    public void setArticleId(long articleId) {
        this.articleId = articleId;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
