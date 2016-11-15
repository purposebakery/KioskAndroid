package com.techlung.kiosk;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.techlung.kiosk.greendao.extended.ExtendedPurchaseDao;
import com.techlung.kiosk.greendao.extended.KioskDaoFactory;
import com.techlung.kiosk.greendao.generated.Article;
import com.techlung.kiosk.greendao.generated.Customer;
import com.techlung.kiosk.greendao.generated.Purchase;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ArticleActivity extends AppCompatActivity {

    public static final String CUSTOMER_ID_EXTRA = "CUSTOMER_ID_EXTRA";

    private List<Article> articles = new ArrayList<Article>();
    private ArrayAdapter<Article> adapter;

    private HashMap<Long, Purchase> purchases = new HashMap<Long, Purchase>();

    private Customer customer;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        FloatingActionButton addCustomer = (FloatingActionButton) findViewById(R.id.addArticle);
        addCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editArticle(new Article(), true);
            }
        });

        ListView articleGrid = (ListView) findViewById(R.id.articleList);
        adapter = new ArticleAdapter(this, R.layout.article_list_item, articles, purchases, this);
        articleGrid.setAdapter(adapter);

        Long customerId = getIntent().getLongExtra(CUSTOMER_ID_EXTRA, -1);
        customer = KioskDaoFactory.getInstance(this).getExtendedCustomerDao().getCustomerById(customerId);
        setTitle(customer.getName());

        updateUi();

        timeoutExit();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            //startActivity(new Intent(getActivity(), SettingsActivity.class));
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void timeoutExit() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ArticleActivity.this.finish();
            }
        }, 60000);
    }

    private void updateUi() {

        articles.clear();
        articles.addAll(KioskDaoFactory.getInstance(this).getExtendedArticleDao().getAllArticles());

        // initalize purchases if necessary

        ExtendedPurchaseDao extendedPurchaseDao = KioskDaoFactory.getInstance(this).getExtendedPurchaseDao();
        purchases.clear();
        for (Article article : articles) {
            Purchase purchase = extendedPurchaseDao.getPurchaseByCustomerAndArticle(customer.getId(), article.getId());
            if (purchase == null) {
                Purchase newPurchase = new Purchase();
                newPurchase.setArticleId(article.getId());
                newPurchase.setCustomerId(customer.getId());
                newPurchase.setAmount(0);
                extendedPurchaseDao.insertOrReplace(newPurchase);

                purchase = extendedPurchaseDao.getPurchaseByCustomerAndArticle(customer.getId(), article.getId());
            }

            purchases.put(article.getId(), purchase);
        }

        adapter.notifyDataSetChanged();
    }

    private void editArticle(final Article article, boolean createNew) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (createNew) {
            builder.setTitle(R.string.article_create);
            article.setPrice(0f);
        } else {
            builder.setTitle(R.string.article_edit);
        }

        LinearLayout layout = new LinearLayout(this);
        layout.setPadding(Utils.convertDpToPixel(16, this), 0, Utils.convertDpToPixel(16, this), 0);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText name = new EditText(this);
        layout.addView(name);
        name.setText(article.getName());
        name.setHint(getString(R.string.article_modify_name));

        final EditText price = new EditText(this);
        price.setInputType(EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(price);
        price.setHint(getString(R.string.article_modify_price));
        DecimalFormat format = new DecimalFormat("0.00");
        price.setText(format.format(article.getPrice()));

        builder.setView(layout);

        builder.setPositiveButton(R.string.alert_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                article.setName(name.getText().toString());
                article.setPrice(Float.parseFloat(price.getText().toString().replace(",", ".")));

                KioskDaoFactory.getInstance(ArticleActivity.this).getExtendedArticleDao().insertOrReplace(article);

                updateUi();
            }
        });

        builder.setNegativeButton(R.string.alert_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        if (!createNew) {
            builder.setNeutralButton(R.string.alert_delete, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    KioskDaoFactory.getInstance(ArticleActivity.this).getExtendedArticleDao().delete(article);

                    updateUi();
                }
            });
        }

        builder.show();

    }

    private class ArticleAdapter extends ArrayAdapter<Article> {

        private HashMap<Long, Purchase> purchases;
        private ArticleActivity activity;

        public ArticleAdapter(Context context, int resource, List<Article> articles, HashMap<Long, Purchase> purchases, ArticleActivity activity) {
            super(context, resource, articles);

            this.purchases = purchases;
            this.activity = activity;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.article_list_item, parent, false);
            }

            final Article article = getItem(position);
            final Purchase purchase = purchases.get(article.getId());

            TextView name = (TextView) convertView.findViewById(R.id.name);
            TextView price = (TextView) convertView.findViewById(R.id.price);

            name.setText(article.getName());

            DecimalFormat format = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.GERMANY));
            price.setText(format.format(article.getPrice()) + " " + getString(R.string.sym_euro));

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    purchase.setAmount(purchase.getAmount() + 1);
                    KioskDaoFactory.getInstance(getContext()).getExtendedPurchaseDao().insertOrReplace(purchase);
                    onBackPressed();
                   notifyUserInput();
                }
            });

            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (Utils.isAdmin) {
                        ArticleActivity.this.editArticle(article, false);
                    }
                    return false;
                }
            });
            return convertView;
        }

        public void notifyUserInput() {
            Vibrator v = (Vibrator) this.getContext().getSystemService(Context.VIBRATOR_SERVICE);
            if (v.hasVibrator()) {
                v.vibrate(100);
            } else {
                try {
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                    r.play();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}