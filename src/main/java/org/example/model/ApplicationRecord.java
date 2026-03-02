package org.example.model;

import java.io.Serializable;
import java.util.UUID;
public class ApplicationRecord implements Serializable {
    private String recordid;
    private String reqid;
    private String Studentname;
    private boolean flag;
    public ApplicationRecord() {
        this.recordid = UUID.randomUUID().toString().substring(0, 8);
        this.flag=false;
    }

    public ApplicationRecord( String reqid, String studentname) {
        this.recordid = UUID.randomUUID().toString().substring(0, 8);
        this.reqid = reqid;
        this.Studentname = studentname;
        this.flag = false;
    }

}
