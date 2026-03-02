package org.example.dao;

import com.google.gson.reflect.TypeToken;
import org.example.model.Announcement;
import org.example.model.TutorRequirement;

import java.util.List;

public class AnnouncementDao extends BaseDao<Announcement> {
    protected String getFilePath(){
        return "data/announcements.json";
    }
    protected TypeToken<List<Announcement>> getTypeToken(){
        return new TypeToken<List<Announcement>>(){};
    }
}
