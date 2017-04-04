package com.techlung.kiosk.model;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.Sort;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class Article extends RealmObject {

    @PrimaryKey
    private long id;
    private String name;
    private float price;

    @Ignore
    private int purchaseCount;

    public static long createId() {
        List<Article> articles = Realm.getDefaultInstance().where(Article.class).findAllSorted("id", Sort.DESCENDING);

        if (articles.isEmpty()) {
            return 0;
        } else {
            return articles.get(0).getId() + 1;
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getPurchaseCount() {
        return purchaseCount;
    }

    public void setPurchaseCount(int purchaseCount) {
        this.purchaseCount = purchaseCount;
    }
}
