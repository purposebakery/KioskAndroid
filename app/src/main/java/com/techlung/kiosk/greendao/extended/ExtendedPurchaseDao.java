package com.techlung.kiosk.greendao.extended;


import com.techlung.kiosk.greendao.generated.Purchase;
import com.techlung.kiosk.greendao.generated.PurchaseDao;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class ExtendedPurchaseDao {
    public PurchaseDao dao;

    public ExtendedPurchaseDao(PurchaseDao PurchaseDao) {
        this.dao = PurchaseDao;
    }

    public List<Purchase> getAllPurchases() {
        QueryBuilder<Purchase> queryBuilder = dao.queryBuilder();
        return queryBuilder.list();
    }

    public long getCount() {
        QueryBuilder<Purchase> queryBuilder = dao.queryBuilder();
        return queryBuilder.count();
    }

    public void insertOrReplace(Purchase Purchase) {
        dao.insertOrReplace(Purchase);
    }

    public void update(Purchase Purchase) {
        dao.update(Purchase);
    }

    public void delete(Purchase Purchase) {
        dao.delete(Purchase);
    }

    public void deleteAll() {
        dao.deleteAll();
    }
}
