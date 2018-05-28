package com.mobiledi.earnit.model;

import com.mobiledi.earnit.utils.AppConstant;

import java.io.Serializable;

/**
 * Created by adox on 24.01.2018..
 */

public class DayTaskStatus implements Serializable {
        public int id ;
        public String createdDateTime ;
        public String status ;

    private boolean isDayCompleted = false;

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
        isDayCompleted = status.equals(AppConstant.COMPLETED);
        this.status = status;
    }

    public boolean isDayCompleted() {
        return status.equals(AppConstant.COMPLETED);
    }

    @Override
    public String toString() {
        return "DayTaskStatus{" +
                "id=" + id +
                ", createdDateTime='" + createdDateTime + '\'' +
                ", status='" + status + '\'' +
                ", isDayCompleted=" + isDayCompleted +
                '}';
    }
}
