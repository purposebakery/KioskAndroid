package com.techlung.kiosk.greendao.extended;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.techlung.kiosk.FileHandler;
import com.techlung.kiosk.greendao.generated.ArticleDao;
import com.techlung.kiosk.greendao.generated.CustomerDao;
import com.techlung.kiosk.greendao.generated.DaoMaster;
import com.techlung.kiosk.greendao.generated.PurchaseDao;

import java.io.File;

/**
 * Created by metz037 on 02.01.16.
 */
public class KioskDaoFactory {

    private static KioskDaoFactory instance;
    protected DaoMaster daoMaster;
    protected SQLiteDatabase db;
    private Context context;
    private ExtendedArticleDao extendedArticleDao;
    private ExtendedCustomerDao extendedCustomerDao;
    private ExtendedPurchaseDao extendedPurchaseDao;

    // Bewegungsdaten
    public static KioskDaoFactory getInstance(Context context) {
        if (instance == null) {
            instance = new KioskDaoFactory();
            instance.context = context;
            instance.reinitialiseDb();
        }
        return instance;
    }


    public void reinitialiseDb() {
        if (db == null) {
            makeSureDbIsInitialised();
        } else {
            closeDb();
            makeSureDbIsInitialised();
        }
    }

    public void makeSureDbIsInitialised() {
        if (db == null) {

            String dbFilePath = getDbFilePath();
            boolean dbFileExisted = (new File(dbFilePath)).exists();

            /*
            // TODO hook migration
            if (dbFileExisted) {
                migrateIfNecessary();
            }*/

            db = context.openOrCreateDatabase(dbFilePath, SQLiteDatabase.CREATE_IF_NECESSARY, null);
            daoMaster = new DaoMaster(db);

            if (!dbFileExisted) {
                recreateDb();
            }
        }
    }

    /*
    // TODO hook migration
    private void migrateIfNecessary() {
        IsoXmlOpenHelper openHelper = new IsoXmlOpenHelper(context, getDbFilePath(), null);
        SQLiteDatabase databaseTemp = openHelper.getWritableDatabase(); // trigger upgrade
        databaseTemp.close();
    }*/

    private String getDbFilePath() {
        return FileHandler.getDBFile(context).getAbsolutePath();
    }

    public void closeDb() {
        if (db != null) {
            db.close();
            db = null;
            daoMaster = null;

            extendedArticleDao = null;
            extendedCustomerDao = null;
            extendedPurchaseDao = null;
        }
    }

    public void clearDb() {
        makeSureDbIsInitialised();

        db.beginTransaction();

        try {
            getExtendedArticleDao().deleteAll();
            getExtendedCustomerDao().deleteAll();
            getExtendedPurchaseDao().deleteAll();

            db.setTransactionSuccessful();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            db.endTransaction();
        }

        Log.d(KioskDaoFactory.class.getName(), "Cleared all Tables of Database");
    }

    public void recreateDb() {
        DaoMaster.dropAllTables(db, true);
        DaoMaster.createAllTables(db, true);
        db.setVersion(DaoMaster.SCHEMA_VERSION);
    }

    public ExtendedArticleDao getExtendedArticleDao() {
        if (extendedArticleDao == null) {
            ArticleDao moodScopeDao = daoMaster.newSession().getArticleDao();
            extendedArticleDao = new ExtendedArticleDao(moodScopeDao);
        }
        return extendedArticleDao;
    }

    public ExtendedCustomerDao getExtendedCustomerDao() {
        if (extendedCustomerDao == null) {
            CustomerDao moodRatingDao = daoMaster.newSession().getCustomerDao();
            extendedCustomerDao = new ExtendedCustomerDao(moodRatingDao);
        }
        return extendedCustomerDao;
    }

    public ExtendedPurchaseDao getExtendedPurchaseDao() {
        if (extendedPurchaseDao == null) {
            PurchaseDao logEntryDao = daoMaster.newSession().getPurchaseDao();
            extendedPurchaseDao = new ExtendedPurchaseDao(logEntryDao);
        }
        return extendedPurchaseDao;
    }

}