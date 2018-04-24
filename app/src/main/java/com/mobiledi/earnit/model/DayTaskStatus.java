package com.mobiledi.earnit.model;

/**
 * Created by adox on 24.01.2018..
 */

public class DayTaskStatus {
        public int id ;
        public String createdDateTime ;
        public String status ;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(String createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
