package org.example.dao;
import com.google.gson.reflect.TypeToken;
import org.example.utils.FileUtil;

import java.io.File;
import java.util.List;
import java.util.function.Predicate;

public abstract class BaseDao<T> {
    protected abstract String getFilePath();
    protected abstract TypeToken<List<T>> getTypeToken();

    public List<T> readAll(){
        return FileUtil.readFromJson(getFilePath(),getTypeToken());
    }

    public  void saveAll(List<T> list){
        FileUtil.saveToJson(getFilePath(),list);
    }

    public void add(T item){
        List<T>  list = readAll();
        list.add(item);
        saveAll(list);
    }

    public void del(Predicate<T> predicate){
        List<T> list = readAll();
        list.removeIf(predicate);
        saveAll(list);
    }

    public List<T> findLits(Predicate<T> predicate){
        return readAll().stream().filter(predicate).toList();
    }

    public void update(Predicate<T> predicate,T item){
        List<T> list = readAll();
        boolean f = false;
        for(int i = 0;i < list.size();i++){
            if(predicate.test(list.get(i))){
                list.set(i,item);
                f = true;
            }
        }
        if(f){
            saveAll(list);
        }
    }

}
/*

 */