package com.techlung.kiosk;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.pixplicity.easyprefs.library.Prefs;
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

    public static void getDonationAmountAndDoPayment(final Activity activity) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);

        LinearLayout layout = new LinearLayout(activity);
        layout.setPadding(Utils.convertDpToPixel(16, activity), 0, Utils.convertDpToPixel(16, activity), 0);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText donation = new EditText(activity);
        layout.addView(donation);
        donation.setText("0,05");

        builder.setView(layout);
        builder.setTitle(R.string.action_send_donation);
        builder.setNegativeButton(R.string.alert_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(R.string.alert_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doPayment(activity, Float.parseFloat(donation.getText().toString().replace(",", ".")));
            }
        });

        builder.show();
    }

    public static void clearPurchases(final Context context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.clear_question);
        builder.setNegativeButton(R.string.alert_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(R.string.alert_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                KioskDaoFactory.getInstance(context).getExtendedPurchaseDao().deleteAll();
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public static void doPayment(Activity activity, float donation) {
        KioskDaoFactory factory = KioskDaoFactory.getInstance(activity);
        ExtendedCustomerDao extendedCustomerDao = factory.getExtendedCustomerDao();
        ExtendedArticleDao extendedArticleDao = factory.getExtendedArticleDao();
        ExtendedPurchaseDao extendedPurchaseDao = factory.getExtendedPurchaseDao();

        StringBuffer shortSummary = new StringBuffer();
        StringBuffer entireSummary = new StringBuffer();
        List<String> customerMails = new ArrayList<String>();

        DecimalFormat format = new DecimalFormat("0.00");

        try {
            int donationCounter = 0;
            for (Customer customer : extendedCustomerDao.getAllCustomers()) {

                if (customer.getName().startsWith("#"))  {
                    continue;
                }

                List<Purchase> purchaseList = customer.getPurchaseList();
                float sum = 0f;
                StringBuffer purchaseSummary = new StringBuffer();
                for (Purchase purchase : purchaseList) {
                    if (purchase.getAmount() > 0) {
                        Article article = purchase.getArticle();
                        if (article == null) {
                            continue;
                        }
                        sum += purchase.getAmount() * article.getPrice();
                        donationCounter += purchase.getAmount();

                        purchaseSummary.append(article.getName() + " ("+ format.format(article.getPrice()) +" "+activity.getString(R.string.sym_euro)+ ")" + " x " + purchase.getAmount() + " = " + format.format(purchase.getAmount() * article.getPrice()) + " "+activity.getString(R.string.sym_euro)+"\n");
                    }
                }

                if (sum != 0) {
                    customerMails.add(customer.getEmail());

                    entireSummary.append("---------------------------------- " + customer.getName() + " ----------------------------------" + "\n");
                    entireSummary.append(purchaseSummary);
                    entireSummary.append("----------------------------------" + "\n" + "SUMME: " + format.format(sum) + " "+ activity.getString(R.string.sym_euro) + "\n\n\n");

                    shortSummary.append(customer.getName() + ": " + format.format(sum) + " "+activity.getString(R.string.sym_euro)+"\n");
                }


            }

            if (donationCounter != 0) {
                shortSummary.append("\nSpende: " + donationCounter + " x " + format.format(donation) + " = " + format.format(donationCounter * donation) + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // create mail
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, customerMails.toArray(new String[customerMails.size()]));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.action_send_subject) + " " + dateFormat.format(new Date()));

        String emailBody = activity.getString(R.string.action_send_message)+"\n\n" + shortSummary + "\n\n" + "################## DETAILS ##################" + "\n\n" + entireSummary + activity.getString(R.string.action_send_bye);
        emailIntent.putExtra(Intent.EXTRA_TEXT, emailBody);

        activity.startActivity(Intent.createChooser(emailIntent, activity.getString(R.string.action_send)));
    }

    public static void initData(Context context) {
        KioskDaoFactory factory = KioskDaoFactory.getInstance(context);
        ExtendedCustomerDao extendedCustomerDao = factory.getExtendedCustomerDao();
        ExtendedArticleDao extendedArticleDao = factory.getExtendedArticleDao();

        if (extendedCustomerDao.getCount() == 0) {
            extendedCustomerDao.insertOrReplace(createCustomer("## BAR ##", "oliver.metz@bertelsmann.de"));
            extendedCustomerDao.insertOrReplace(createCustomer("Thomas", "thomas.hanning@bertelsmann.de"));
            extendedCustomerDao.insertOrReplace(createCustomer("Robert", "robert.strickmann@bertelsmann.de"));
            extendedCustomerDao.insertOrReplace(createCustomer("Willem", "willem.terhoerst@bertelsmann.de"));
            extendedCustomerDao.insertOrReplace(createCustomer("Ilja", "ilja.wolik@bertelsmann.de"));
            extendedCustomerDao.insertOrReplace(createCustomer("Johannes", "johannes.kleeschulte@bertelsmann.de"));
        }

        if (extendedArticleDao.getCount() == 0) {
            extendedArticleDao.insertOrReplace(createArticle("Kinderriegel",0.25f));
            extendedArticleDao.insertOrReplace(createArticle("Müsliriegel",0.25f));
            extendedArticleDao.insertOrReplace(createArticle("Snickers",0.30f));
            extendedArticleDao.insertOrReplace(createArticle("Balisto",0.30f));
            extendedArticleDao.insertOrReplace(createArticle("Früchteriegel",0.30f));
            extendedArticleDao.insertOrReplace(createArticle("Koppers",0.35f));
            extendedArticleDao.insertOrReplace(createArticle("Milky Way",0.35f));
            extendedArticleDao.insertOrReplace(createArticle("Twix",0.40f));
            extendedArticleDao.insertOrReplace(createArticle("Pickup",0.45f));
            extendedArticleDao.insertOrReplace(createArticle("Kitkat",0.45f));
            extendedArticleDao.insertOrReplace(createArticle("Erdnüsse",0.60f));
            extendedArticleDao.insertOrReplace(createArticle("Effect Energy",1.00f));
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
