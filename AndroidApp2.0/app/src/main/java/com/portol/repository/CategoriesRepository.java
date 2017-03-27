package com.portol.repository;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.portol.client.PortolClient;
import com.portol.common.model.Category;

import org.apache.commons.io.Charsets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import co.uk.rushorm.android.RushBitmapFile;
import co.uk.rushorm.core.RushFile;
import co.uk.rushorm.core.RushSearch;


/**
 * Created by alex on 6/8/15.
 */
public class CategoriesRepository {

    public static final String TAG = "CategoriesRepository";
    PortolClient pClient;
    private Context context;

    public CategoriesRepository(Context thisContext) {
        this.context = thisContext;
        pClient = new PortolClient(context);
    }

    public boolean refresh() throws Exception {
        Log.d(TAG, "attempting to retreive all categories for display...");

        List<Category> allCats = null;
        try {
            allCats = pClient.getAllCategories(this);
        } catch (Exception e) {
            Log.e(TAG, "pclient failure in categories repo", e);
            return false;
        }
        Log.d(TAG, "All categories downloaded.");
        this.saveCategoryList(allCats);
        return true;
    }

    public List<Category> getAllValidCategories() {
        //TODO actually filter/rank categories based on expiration
        List<Category> allcats = new RushSearch().find(Category.class);

        for (Category saved : allcats) {
            if (saved.getVersion() != null) {
                String idOfbmp = saved.getVersion();
                RushBitmapFile bitMap = new RushSearch().whereId(idOfbmp).findSingle(RushBitmapFile.class);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                Bitmap bmp2 = null;
                try {
                    bmp2 = bitMap.getImage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                bmp2.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

                saved.setIconEncoded(encoded);
            }
        }

        // List<Category> allcats2 = new RushSearch().find(Category.class);
        return allcats;
    }


    public Category findById(String idofCategory) {
        Category saved = new RushSearch().whereEqual("categoryId", idofCategory).findSingle(Category.class);
        if (saved.getVersion() != null) {
            String idOfbmp = saved.getVersion();
            RushBitmapFile bitMap = new RushSearch().whereId(idOfbmp).findSingle(RushBitmapFile.class);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            Bitmap bmp2 = null;
            try {
                bmp2 = bitMap.getImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bmp2.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

            saved.setIconEncoded(encoded);
        }
        return saved;


    }

    public synchronized void saveCategoryList(List<Category> allCats) {


        for (Category cat : allCats) {
            try {
                this.saveSingleCat(cat);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        return;

    }

    public synchronized Category saveSingleCat(Category toSave) throws Exception {

        List<Category> existing = new RushSearch().whereEqual("categoryId", toSave.getCategoryId()).find(Category.class);
        for (Category exist : existing) {
            List<Category> allcats = new RushSearch().find(Category.class);

            exist.delete();

        }
        if (toSave.getIconEncoded() != null) {
            RushBitmapFile iconHolder = new RushBitmapFile(context.getFilesDir().getAbsolutePath());
            byte[] data = Base64.decode(toSave.getIconEncoded(), Base64.DEFAULT);

            Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
            iconHolder.setImage(bmp);
            iconHolder.save();
            toSave.setIconEncoded("");
            String id = iconHolder.getId();
            toSave.setVersion(id);
        }
        List<Category> allcats3 = new RushSearch().find(Category.class);

        toSave.save();


        String savedId = toSave.getId();
        Category saved = new RushSearch().whereId(savedId).findSingle(Category.class);


        if (saved.getVersion() != null) {
            String idOfbmp = saved.getVersion();
            RushBitmapFile bitMap = new RushSearch().whereId(idOfbmp).findSingle(RushBitmapFile.class);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            Bitmap bmp2 = bitMap.getImage();
            bmp2.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

            saved.setIconEncoded(encoded);
        }
        return saved;

    }


    public Category findByPosition(int position) {
        Category saved = new RushSearch().whereEqual("position", position).findSingle(Category.class);
        if (saved == null) return null;
        if (saved.getVersion() != null) {
            String idOfbmp = saved.getVersion();
            RushBitmapFile bitMap = new RushSearch().whereId(idOfbmp).findSingle(RushBitmapFile.class);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            Bitmap bmp2 = null;
            try {
                bmp2 = bitMap.getImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bmp2.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

            saved.setIconEncoded(encoded);
        }
        return saved;
    }
}
