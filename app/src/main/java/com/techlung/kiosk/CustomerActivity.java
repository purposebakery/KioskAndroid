package com.techlung.kiosk;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.techlung.kiosk.greendao.extended.KioskDaoFactory;
import com.techlung.kiosk.greendao.generated.Customer;

import java.util.ArrayList;
import java.util.List;

public class CustomerActivity extends AppCompatActivity {

    private List<Customer> customers = new ArrayList<Customer>();
    private List<String> customersNames = new ArrayList<String>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                // TODO open other activity
            }
        });

        updateUi();
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

        if (!createNew) {
            builder.setNegativeButton(R.string.alert_delete, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    KioskDaoFactory.getInstance(CustomerActivity.this).getExtendedCustomerDao().delete(customers.get(which));

                    updateUi();
                }
            });
        }

        builder.show();

    }

}
