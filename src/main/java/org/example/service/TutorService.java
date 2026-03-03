package org.example.service;

import org.example.dao.RequirementDao;
import org.example.model.TutorRequirement;
import java.util.List;
import java.util.function.Predicate;

public class TutorService {
    private RequirementDao reqDao=new RequirementDao();
    //发表家教申请
    public void publish(TutorRequirement req) {
        req.setParentUsername(UserService.currentUser.getUsername());
        reqDao.add(req);
    }
    //删除已通过的申请
    public void update() {
        List<TutorRequirement>list=reqDao.readAll();
        for(int i=0;i<list.size();i++) {
            if(list.get(i).isClosed()==true){
                int finalI = i;
                reqDao.del(r-> r.getReqID().equals(list.get(finalI).getReqID()));
            }
        }
    }

    public List<TutorRequirement> getAllRequirements() {
        return reqDao.showAll();
    }
}