package fr.epita.datamodel;
/**
 * @author shubham
 * @author manish
 */
public class Task {

    private Lists list;
    private String taskName;
    private int estimatedTime;
    private int priority;
    private String  description;
    private User user;

    public Task() {
    }
    public Task(Lists list, String taskName, int estimatedTime, int priority, String description) {
        this.list = list;
        this.taskName = taskName;
        this.estimatedTime = estimatedTime;
        this.priority = priority;
        this.description = description;
        this.user= null;
    }
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }



    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public int getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(int estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Lists getList() {
        return list;
    }

    public void setList(Lists list) {
        this.list = list;
    }
}
