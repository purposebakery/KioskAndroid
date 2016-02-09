package com.techlung.kiosk;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.techlung.kiosk.greendao.extended.ExtendedArticleDao;
import com.techlung.kiosk.greendao.extended.ExtendedCustomerDao;
import com.techlung.kiosk.greendao.extended.ExtendedPurchaseDao;
import com.techlung.kiosk.greendao.extended.KioskDaoFactory;
import com.techlung.kiosk.greendao.generated.Article;
import com.techlung.kiosk.greendao.generated.Customer;
import com.techlung.kiosk.greendao.generated.Purchase;

public final class Utils {

    public static int convertDpToPixel(int dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int px = (int) Math.floor(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                resources.getDisplayMetrics()));
        return px;
    }

    public static int convertPixelsToDp(int px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int dp = (int) Math.floor(px / (metrics.densityDpi / 160f));
        return dp;
    }

    public static void doPayment(Activity activity) {
        KioskDaoFactory factory = KioskDaoFactory.getInstance(activity);
        ExtendedCustomerDao extendedCustomerDao = factory.getExtendedCustomerDao();
        ExtendedArticleDao extendedArticleDao = factory.getExtendedArticleDao();
        ExtendedPurchaseDao extendedPurchaseDao = factory.getExtendedPurchaseDao();

        StringBuffer shortSummary = new StringBuffer();
        StringBuffer entireSummary = new StringBuffer();
        List<String> customerMails = new ArrayList<String>();

        try {
            for (Customer customer : extendedCustomerDao.getAllCustomers()) {
                customerMails.add(customer.getEmail());

                entireSummary.append("----------------------------------" + "\n" + customer.getName() + "\n" + "----------------------------------");

                List<Purchase> purchaseList = customer.getPurchaseList();
                float sum = 0f;
                for (Purchase purchase : purchaseList) {
                    Article article = purchase.getArticle();
                    sum += purchase.getAmount() * article.getPrice();

                    entireSummary.append(article.getName() + "("+ article.getPrice() +" EUR)" + " x " + purchase.getAmount() + " = " + purchase.getAmount() * article.getPrice() + " EUR\n");
                }
                entireSummary.append("----------------------------------" + "\n" + "                            " + sum + " EUR" + "\n\n\n");

                shortSummary.append(customer.getName() + ": " + sum + " EUR\n");

            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // create mail
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, customerMails.toArray(new String[customerMails.size()]));
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, activity.getString(R.string.action_send_subject));

        String emailBody = activity.getString(R.string.action_send_message)+"\n\n" + shortSummary + "\n\n" + "----------------------------------" + "\n\n" + entireSummary + activity.getString(R.string.action_send_bye);
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, emailBody);

        activity.startActivity(Intent.createChooser(emailIntent, activity.getString(R.string.action_send)));
    }

    public static void initData(Context context) {
        KioskDaoFactory factory = KioskDaoFactory.getInstance(context);
        ExtendedCustomerDao extendedCustomerDao = factory.getExtendedCustomerDao();
        ExtendedArticleDao extendedArticleDao = factory.getExtendedArticleDao();

        if (extendedCustomerDao.getCount() == 0) {
            extendedCustomerDao.insertOrReplace(createCustomer("Oliver", "oliver.metz@bertelsmann.de"));
            extendedCustomerDao.insertOrReplace(createCustomer("Thomas", "thomas.hanning@bertelsmann.de"));
            extendedCustomerDao.insertOrReplace(createCustomer("Robert", "robert.strickmann@bertelsmann.de"));
            extendedCustomerDao.insertOrReplace(createCustomer("Willem", "willem.terhoerst@bertelsmann.de"));
            extendedCustomerDao.insertOrReplace(createCustomer("Ilja", "ilja.wolik@bertelsmann.de"));
            extendedCustomerDao.insertOrReplace(createCustomer("Johannes", "johannes.kleeschulte@bertelsmann.de"));
        }

        if (extendedArticleDao.getCount() == 0) {
            extendedArticleDao.insertOrReplace(createArticle("Snickers",0.35f));
            extendedArticleDao.insertOrReplace(createArticle("Kinderriegel",0.30f));
            extendedArticleDao.insertOrReplace(createArticle("Müsliriegel",0.30f));
            extendedArticleDao.insertOrReplace(createArticle("Effect Energy",1.00f));
            extendedArticleDao.insertOrReplace(createArticle("Erdnüsse",0.60f));
        }
    }

    private static Customer createCustomer(String name, String email) {
        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setName(name);
        return customer;
    }

    private static Article createArticle(String name, Float price) {
        Article article = new Article();
        article.setName(name);
        article.setPrice(price);
        return article;
    }
}
