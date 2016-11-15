package com.techlung.kiosk.greendao.generated;

import java.util.List;

import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END

/**
 * Entity mapped to table "CUSTOMER".
 */
public class Customer {

    private Long id;
    private String name;
    private String email;

    /**
     * Used to resolve relations
     */
    private transient DaoSession daoSession;

    /**
     * Used for active entity operations.
     */
    private transient CustomerDao myDao;

    private List<Purchase> purchaseList;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public Customer() {
    }

    public Customer(Long id) {
        this.id = id;
    }

    public Customer(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getCustomerDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    /**
     * To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity.
     */
    public List<Purchase> getPurchaseList() {
        if (purchaseList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            PurchaseDao targetDao = daoSession.getPurchaseDao();
            List<Purchase> purchaseListNew = targetDao._queryCustomer_PurchaseList(id);
            synchronized (this) {
                if (purchaseList == null) {
                    purchaseList = purchaseListNew;
                }
            }
        }
        return purchaseList;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    public synchronized void resetPurchaseList() {
        purchaseList = null;
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

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}
