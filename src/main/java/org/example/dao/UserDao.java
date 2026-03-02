package org.example.dao;
import com.google.gson.reflect.TypeToken;
import org.example.model.TutorRequirement;
import org.example.model.User;
import java.util.List;

public class UserDao extends BaseDao<User> {
    protected String getFilePath(){
        return "data/users.json";
    }
    protected TypeToken<List<User>> getTypeToken(){
        return new TypeToken<List<User>>(){};
    }
}
