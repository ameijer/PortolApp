package com.portol.repository;

import android.util.Log;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.portol.common.model.Category;
import com.portol.common.model.content.CategoryReference;
import com.portol.common.model.content.ContentMetadata;
import com.portol.common.model.content.meta.Rating;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import co.uk.rushorm.core.RushCore;
import co.uk.rushorm.core.RushSearch;

/**
 * Created by alex on 6/8/15.
 */
public class ContentRepository {

    public static final String TAG = "ContentRepository";
    private CategoriesRepository catRepo;


    public ContentRepository(CategoriesRepository catRepo) {
        this.catRepo = catRepo;

    }

    public synchronized List<ContentMetadata> findAllInCategory(Category current) {

        //TODO form a query to do this
        List<ContentMetadata> toCheck = this.getAllContent();
        ArrayList<ContentMetadata> matching = new ArrayList<ContentMetadata>();

        for (ContentMetadata cur : toCheck) {
            if (cur.getMemberOf() == null) continue;


            for (CategoryReference key : cur.getMemberOf()) {
                if (key.getCategoryId() == null) continue;
                if (key.getCategoryId().equalsIgnoreCase(current.getCategoryId())) {
                    Log.w(TAG, "gross nested loop, replace with a nice query");
                    matching.add(cur);
                    break;
                }
            }
        }


        return matching;
    }

    public List<ContentMetadata> getAllContent() {
        ObjectMapper om = new ObjectMapper();
        List<ContentMetadata> allmetas = new RushSearch().find(ContentMetadata.class);


        return allmetas;
    }

    public synchronized List<ContentMetadata> saveContentList(List<ContentMetadata> input) {

        ArrayList<ContentMetadata> results = new ArrayList<ContentMetadata>();
        for (ContentMetadata cur : input) {


            //save content
            Log.i(TAG, "about to save content with ID: " + cur.getParentContentId());
            ContentMetadata saved = null;
            try {
                saved = this.saveSingleContent(cur);
            } catch (Exception e) {
                Log.e(TAG, "error saving content metadata", e);
            }
            results.add(saved);
            Log.i(TAG, "saved content with id: " + saved.getMetadataId());

        }

        return results;
    }


    public synchronized ContentMetadata saveSingleContent(ContentMetadata toSave) {
        ContentMetadata existing = null;
        try {
            existing = new RushSearch().whereEqual("metadataId", toSave.getMetadataId()).findSingle(ContentMetadata.class);
        } catch (Exception e) {
            Log.i(TAG, "exception caught during rushsearch. Rush has probably not been initialized yet", e);
            return null;
        }

        //get managed categories before saving, otherwise this causes duplicate categories!


        if (existing != null) {
            existing.delete();
        }


        toSave.save();
        String id = toSave.getId();
        return new RushSearch().whereId(id).findSingle(ContentMetadata.class);
    }

    private ArrayList<Category> getManagedCategories(List<Category> memberOf) {
        ArrayList<Category> results = new ArrayList<Category>();

        for (Category unmanaged : memberOf) {
            results.add(catRepo.findById(unmanaged.getCategoryId()));
        }


        return results;


    }

    public ContentMetadata findByParentContentId(String contentId) {
        ContentMetadata raw = new RushSearch().whereEqual("parentContentId", contentId).findSingle(ContentMetadata.class);

        if (raw == null) {
            return null;
        }
        return raw;
    }

    public ContentMetadata findByParentContentKey(String videoKey) {
        ContentMetadata raw = new RushSearch().whereEqual("parentContentKey", videoKey).findSingle(ContentMetadata.class);

        if (raw == null) {
            return null;
        }

        return raw;
    }

    public void resetContent() {
        RushCore.getInstance().deleteAll(ContentMetadata.class);
        RushCore.getInstance().deleteAll(CategoryReference.class);
    }
}
