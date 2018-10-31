package com.firepitmedia.earnit.model.editTask;

public class EditTaskResponse {
    private String id;

    private String allowance;

    private String status;

    private String description;

    private String name;

    private String shouldLockAppsIfTaskOverdue;

    private String isDeleted;

    private String createDate;

    private String dueDate;

    private Goal goal;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getAllowance ()
    {
        return allowance;
    }

    public void setAllowance (String allowance)
    {
        this.allowance = allowance;
    }

    public String getStatus ()
    {
        return status;
    }

    public void setStatus (String status)
    {
        this.status = status;
    }

    public String getDescription ()
    {
        return description;
    }

    public void setDescription (String description)
    {
        this.description = description;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getShouldLockAppsIfTaskOverdue ()
    {
        return shouldLockAppsIfTaskOverdue;
    }

    public void setShouldLockAppsIfTaskOverdue (String shouldLockAppsIfTaskOverdue)
    {
        this.shouldLockAppsIfTaskOverdue = shouldLockAppsIfTaskOverdue;
    }

    public String getIsDeleted ()
    {
        return isDeleted;
    }

    public void setIsDeleted (String isDeleted)
    {
        this.isDeleted = isDeleted;
    }

    public String getCreateDate ()
    {
        return createDate;
    }

    public void setCreateDate (String createDate)
    {
        this.createDate = createDate;
    }

    public String getDueDate ()
    {
        return dueDate;
    }

    public void setDueDate (String dueDate)
    {
        this.dueDate = dueDate;
    }

    public Goal getGoal ()
    {
        return goal;
    }

    public void setGoal (Goal goal)
    {
        this.goal = goal;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", allowance = "+allowance+", status = "+status+", description = "+description+", name = "+name+", shouldLockAppsIfTaskOverdue = "+shouldLockAppsIfTaskOverdue+", isDeleted = "+isDeleted+", createDate = "+createDate+", dueDate = "+dueDate+", goal = "+goal+"]";
    }

    public class Goal{

        private int id;

        public Goal(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return "Goal{" +
                    "id=" + id +
                    '}';
        }
    }
}
