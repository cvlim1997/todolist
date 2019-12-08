package student.inti.todolist;

public class TaskModel {
    String assignedby,date,message,priority,time,title,uniqKey;

    public TaskModel(){}

    public TaskModel(String assignedby, String date, String message, String priority, String time, String title, String uniqKey) {
        this.assignedby = assignedby;
        this.date = date;
        this.message = message;
        this.priority = priority;
        this.time = time;
        this.title = title;
        this.uniqKey = uniqKey;
    }

    public String getAssignedby() {
        return assignedby;
    }

    public void setAssignedby(String assignedby) {
        this.assignedby = assignedby;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUniqKey() {
        return uniqKey;
    }

    public void setUniqKey(String uniqKey) {
        this.uniqKey = uniqKey;
    }
}
