package com.techlung.kiosk;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.techlung.kiosk.greendao.extended.KioskDaoFactory;
import com.techlung.kiosk.greendao.generated.Article;

import java.util.ArrayList;
import java.util.List;

public class ArticleActivity extends AppCompatActivity {

    private List<Article> articles = new ArrayList<Article>();
    private ArrayAdapter<Article> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton addCustomer = (FloatingActionButton) findViewById(R.id.addArticle);
        addCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editArticle(new Article(), true);
            }
        });

        GridView articleGrid = (GridView) findViewById(R.id.articleGrid);
        adapter = new ArticleAdapter(this, android.R.layout.simple_list_item_1, articles);
        articleGrid.setAdapter(adapter);

        articleGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                editArticle(articles.get(position), false);
                return true;
            }
        });

        updateUi();
    }

    private void updateUi() {

        articles.clear();
        articles.addAll(KioskDaoFactory.getInstance(this).getExtendedArticleDao().getAllArticles());

        adapter.notifyDataSetChanged();
    }

    private void editArticle(final Article article, boolean createNew) {

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
        name.setText(article.getName());
        name.setHint(getString(R.string.article_modify_name));

        final EditText price = new EditText(this);
        price.setInputType(EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(price);
        price.setHint(getString(R.string.article_modify_price));
        price.setText(Float.toString(article.getPrice()));

        builder.setView(layout);

        builder.setPositiveButton(R.string.alert_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                article.setName(name.getText().toString());
                article.setPrice(Float.parseFloat(price.getText().toString()));

                KioskDaoFactory.getInstance(ArticleActivity.this).getExtendedArticleDao().insertOrReplace(article);

                updateUi();
            }
        });

        if (!createNew) {
            builder.setNegativeButton(R.string.alert_delete, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    KioskDaoFactory.getInstance(ArticleActivity.this).getExtendedArticleDao().delete(articles.get(which));

                    updateUi();
                }
            });
        }

        builder.show();

    }

    private static class ArticleAdapter extends ArrayAdapter<Article> {

        public ArticleAdapter(Context context, int resource, List<Article> articles) {
            super(context, resource, articles);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            return super.getView(position, convertView, parent);
        }
    }

}