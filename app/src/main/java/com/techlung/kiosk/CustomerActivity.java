package com.techlung.kiosk;

import android.app.AlertDialog;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.pixplicity.easyprefs.library.Prefs;
import com.techlung.kiosk.greendao.extended.KioskDaoFactory;
import com.techlung.kiosk.greendao.generated.Customer;

import java.util.ArrayList;
import java.util.List;

public class CustomerActivity extends AppCompatActivity {


    public static final String NEWS = "NEWS";
    private List<Customer> customers = new ArrayList<Customer>();
    private List<String> customersNames = new ArrayList<String>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(this.getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();

        FloatingActionButton addCustomer = (FloatingActionButton) findViewById(R.id.addCustomer);
        addCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editCustomer(new Customer(), true);
            }
        });

        ListView customerListView = (ListView) findViewById(R.id.customerList);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, customersNames);
        customerListView.setAdapter(adapter);

        customerListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                editCustomer(customers.get(position), false);
                return true;
            }
        });

        customerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Customer customer = customers.get(position);
                Intent intent = new Intent(CustomerActivity.this, ArticleActivity.class);
                intent.putExtra(ArticleActivity.CUSTOMER_ID_EXTRA, customer.getId());
                startActivity(intent);
            }
        });

        final TextView news = (TextView) findViewById(R.id.news);
        news.setText(Prefs.getString(NEWS, "Pro Artikel wird 5 Cent an World Vision e.V. gespendet\n\nBester Kunde kriegt am Ende des Monats ein Artikel gratis!"));
        findViewById(R.id.newsContainer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeNews(news);
            }
        });

        Utils.initData(this);

        updateUi();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.customer_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.pay) {
            Utils.getDonationAmountAndDoPayment(this);
            return true;
        } else if (id == R.id.restore) {
            Utils.clearPurchases(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void changeNews(final TextView newsTextView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LinearLayout layout = new LinearLayout(this);
        layout.setPadding(Utils.convertDpToPixel(16, this), 0, Utils.convertDpToPixel(16, this), 0);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText news = new EditText(this);
        layout.addView(news);
        news.setText(newsTextView.getText());

        builder.setView(layout);
        builder.setTitle(R.string.news_title);
        builder.setNegativeButton(R.string.alert_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(R.string.alert_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                newsTextView.setText(news.getText());
                Prefs.putString(NEWS, news.getText().toString());
            }
        });

        builder.show();
    }

    private void updateUi() {
        customers = KioskDaoFactory.getInstance(this).getExtendedCustomerDao().getAllCustomers();

        customersNames.clear();
        for (Customer customer : customers) {
            customersNames.add(customer.getName());
        }

        adapter.notifyDataSetChanged();
    }

    private void editCustomer(final Customer customer, boolean createNew) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (createNew) {
            builder.setTitle(R.string.customer_create);
        } else {
            builder.setTitle(R.string.customer_edit);
        }

        LinearLayout layout = new LinearLayout(this);
        layout.setPadding(Utils.convertDpToPixel(16, this), 0, Utils.convertDpToPixel(16, this), 0);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText name = new EditText(this);
        layout.addView(name);
        name.setText(customer.getName());
        name.setHint(getString(R.string.customer_modify_name));

        final EditText email = new EditText(this);
        email.setInputType(EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        layout.addView(email);
        email.setHint(getString(R.string.customer_modify_mail));
        email.setText(customer.getEmail());

        builder.setView(layout);

        builder.setPositiveButton(R.string.alert_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                customer.setName(name.getText().toString());
                customer.setEmail(email.getText().toString());

                KioskDaoFactory.getInstance(CustomerActivity.this).getExtendedCustomerDao().insertOrReplace(customer);

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
                    KioskDaoFactory.getInstance(CustomerActivity.this).getExtendedCustomerDao().delete(customer);

                    updateUi();
                }
            });
        }

        builder.show();

    }

}
