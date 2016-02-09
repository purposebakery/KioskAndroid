package com.techlung.kiosk;

import java.io.File;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

public class KioskDaoGenerator {

    public static final String TEMP_NAMESPACE = "com.techlung.kiosk.greendao.generated";
    public static final String TEMP_PATH = "com/techlung/kiosk/greendao/generated/";
    public static final String TEMP_ROOT = "com";
    public static final String TARGET_PATH = "app/src/main/java/com/techlung/kiosk/greendao/generated/";

    public static final int DATABASE_VERSION = 8;

    public static void main(String[] args) throws Exception {
        System.out.println("Working Directory = " + System.getProperty("user.dir"));

        new File(TEMP_PATH).mkdirs();
        new File(TARGET_PATH).mkdirs();
        System.out.println("##################  copy files from src ##################");
        moveFiles(TARGET_PATH, TEMP_PATH);
        File f = new File(TARGET_PATH);
        deleteDir(f);
        new File(TARGET_PATH).mkdirs();
        System.out.println("##################  Generating DAO's ##################");
        generateDaos();
        System.out.println("##################  copy files to src ##################");
        moveFiles(TEMP_PATH, TARGET_PATH);
        System.out.println("##################       clean        ##################");
        deleteDir(new File(TEMP_ROOT));
        System.out.println("##################       done!        ##################");
    }

    private static void generateDaos() throws Exception {
        Schema schema = new Schema(DATABASE_VERSION, TEMP_NAMESPACE);

        Entity customer = createCustomerEntity(schema);

        Entity article = createArticleEntity(schema);

        Entity purchase = createPurchaseEntity(schema, article, customer);


        new DaoGenerator().generateAll(schema, ".");
    }

    private static Entity createCustomerEntity(Schema schema) {
        Entity customer = schema.addEntity("Customer");
        customer.setHasKeepSections(true);

        customer.addLongProperty("id").primaryKey().autoincrement();
        customer.addStringProperty("name");
        customer.addStringProperty("email").unique();

        return customer;
    }

    private static Entity createArticleEntity(Schema schema) {
        Entity article = schema.addEntity("Article");
        article.setHasKeepSections(true);

        article.addLongProperty("id").primaryKey().autoincrement();
        article.addStringProperty("name");
        article.addFloatProperty("price");

        return article;
    }

    private static Entity createPurchaseEntity(Schema schema, Entity article, Entity customer) {
        Entity purchase = schema.addEntity("Purchase");

        purchase.addLongProperty("id").primaryKey().autoincrement();

        purchase.addIntProperty("amount");
        Property articleProp = purchase.addLongProperty("articleId").getProperty();
        Property customerProp = purchase.addLongProperty("customerId").getProperty();

        purchase.addToOne(article, articleProp);
        customer.addToMany(purchase, customerProp);

        return purchase;
    }



    // Utilities
    private static void moveFiles(String fromPath, String toPath) {
        File fromDirectory = new File(fromPath);

        for (File from : fromDirectory.listFiles()) {
            File to = new File(toPath + from.getName());
            System.out.print("Moving: ");
            System.out.print(from.getAbsolutePath());
            System.out.print("  -->  ");
            System.out.println(to.getAbsolutePath());
            from.renameTo(to);
        }
    }

    private static void deleteDir(File path) {
        for (File file : path.listFiles()) {
            if (file.isDirectory()) {
                deleteDir(file);
            }
            System.out.println("Deleting: " + file.getAbsolutePath());
            file.delete();
        }
        System.out.println("Deleting: " + path.getAbsolutePath());
        path.delete();
    }
}
