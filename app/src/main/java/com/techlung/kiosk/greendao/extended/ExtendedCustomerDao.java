package com.techlung.kiosk.greendao.extended;


import com.techlung.kiosk.greendao.generated.Customer;
import com.techlung.kiosk.greendao.generated.CustomerDao;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class ExtendedCustomerDao {
    public CustomerDao dao;

    public ExtendedCustomerDao(CustomerDao CustomerDao) {
        this.dao = CustomerDao;
    }

    public List<Customer> getAllCustomers() {
        QueryBuilder<Customer> queryBuilder = dao.queryBuilder();
        return queryBuilder.list();
    }

    public Customer getCustomerById(Long id) {
        QueryBuilder<Customer> queryBuilder = dao.queryBuilder();
        queryBuilder.where(CustomerDao.Properties.Id.eq(id));
        return queryBuilder.unique();
    }

    public long getCount() {
        QueryBuilder<Customer> queryBuilder = dao.queryBuilder();
        return queryBuilder.count();
    }

    public void insertOrReplace(Customer Customer) {
        dao.insertOrReplace(Customer);
    }

    public void update(Customer Customer) {
        dao.update(Customer);
    }

    public void delete(Customer Customer) {
        dao.delete(Customer);
    }

    public void deleteAll() {
        dao.deleteAll();
    }
}