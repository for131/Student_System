package org.example.dao;
import com.google.gson.reflect.TypeToken;
import org.example.model.TutorRequirement;
import org.example.utils.FileUtil;

import org.example.model.User;
import java.util.List;
import java.util.Optional;
public class RequirementDao extends BaseDao<TutorRequirement>{
    protected String getFilePath(){
        return "data/requirements.json";
    }
    protected TypeToken<List<TutorRequirement>> getTypeToken(){
        return new TypeToken<List<TutorRequirement>>(){};
    }
}