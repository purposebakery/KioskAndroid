package com.techlung.kiosk.model;

import com.techlung.kiosk.utils.Utils;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.Sort;
import io.realm.annotations.PrimaryKey;

public class Customer extends RealmObject {

    @PrimaryKey
    long id;
    String name;
    String email;

    float purchaseValueSum;
    int rank;

    public static void updateRanking() {
        Realm.getDefaultInstance().beginTransaction();
        List<Customer> customers = Realm.getDefaultInstance().where(Customer.class).findAll();

        for (Customer customer : customers) {
            float sum = 0;
            List<Purchase> purchases = Realm.getDefaultInstance().where(Purchase.class).equalTo("customerId", customer.getId()).findAll();
            ;
            for (Purchase purchase : purchases) {
                if (purchase.getArticle() == null || purchase.getArticle().getName().contains(Utils.SPENDE)) {
                    continue;
                }
                sum += (float) purchase.getAmount() * purchase.getArticle().getPrice();
            }
            customer.setPurchaseValueSum(sum);
        }
        Realm.getDefaultInstance().commitTransaction();

        Realm.getDefaultInstance().beginTransaction();
        List<Customer> customersAgain = Realm.getDefaultInstance().where(Customer.class).findAllSorted("purchaseValueSum", Sort.DESCENDING);
        int rank = 0;
        for (Customer customer : customersAgain) {
            customer.setRank(rank++);
        }
        Realm.getDefaultInstance().commitTransaction();
    }

    public static long createId() {
        List<Customer> customers = Realm.getDefaultInstance().where(Customer.class).findAllSorted("id", Sort.DESCENDING);

        if (customers.isEmpty()) {
            return 0;
        } else {
            return customers.get(0).getId() + 1;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public float getPurchaseValueSum() {
        return purchaseValueSum;
    }

    public void setPurchaseValueSum(float purchaseValueSum) {
        this.purchaseValueSum = purchaseValueSum;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}

