package fr.epita.dataservice;

import fr.epita.dao.ListsDAO;
import fr.epita.dao.TaskDAO;
import fr.epita.dao.UserDAO;
import fr.epita.data.Data;
import fr.epita.datamodel.Lists;
import fr.epita.datamodel.Task;
import fr.epita.datamodel.User;
import fr.epita.services.Configuration;

import java.sql.*;
import java.util.*;
import java.util.function.ToIntFunction;
import java.util.logging.Level;
import java.util.logging.Logger;

import static fr.epita.data.Data.listTasks;

/**
 * @author shubham
 * @author manish
 *
 */

public class EpitrelloDataService {
    public static final Logger logger = Logger.getLogger("Logger.EpiTrelloDataService");
    private static final String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS USER (ID INT PRIMARY KEY AUTO_INCREMENT, NAME VARCHAR(255) UNIQUE)";

    //records of all the assigned tasks in the system
    public static final Map<String, LinkedHashSet<String>> assignedTasks = new LinkedHashMap<>(); //Linked HashMap to preserve the insertion order
    //records of all the tasks which has been completed
    public static final Map<String, LinkedHashSet<String>> completedTasks = new LinkedHashMap<>();


    public static void createTable() {
        try (Connection connection = getConnection();PreparedStatement statement = connection.prepareStatement(CREATE_USER_TABLE, Statement.RETURN_GENERATED_KEYS)) {
            statement.execute();
            System.out.println("Connection to database: Successful");

        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException, ClassNotFoundException {

        Class.forName(Configuration.getValueFromKey("jdbc.driver"));
        String url = Configuration.getValueFromKey("jdbc.url");
        String username = Configuration.getValueFromKey("jdbc.username");
        String password = Configuration.getValueFromKey("jdbc.password");

        return DriverManager.getConnection(url, username, password);
    }

    public String addUser(String userName) {
        return UserDAO.addUser(new User(userName));
    }

    public String addList(String listName) {
        return ListsDAO.addList(new Lists(listName));
    }

    public String addTask(String listName, String name, int estimatedTime, int priority, String description) {
        return TaskDAO.addTask(new Task(new Lists(listName), name, estimatedTime, priority, description));
    }

    public String editTask(String task, int estimatedTime, int priority, String description) {
        Task editedTask = new Task();
        editedTask.setTaskName(task);
        editedTask.setEstimatedTime(estimatedTime);
        editedTask.setPriority(priority);
        editedTask.setDescription(description);
        return TaskDAO.updateTask(editedTask);
    }

    /**
     * Function to assign a task to a user.
     * @param taskName The name of the task and to be assigned.
     * @param userName The name of the user.
     *@return String: In case of user item in the system is not available,
     * the string “User does not exist” returns.
     */
    public String assignTask(String taskName, String userName) {
        Task foundTask = Data.tasks.get(taskName);
        if (UserDAO.search(userName) != null && foundTask != null) {
            foundTask.setUser(new User(userName));
            Data.tasks.put(foundTask.getTaskName(),foundTask);
            if(assignedTasks.containsKey(userName)){
                LinkedHashSet<String> tasksAssignedToUserSet = assignedTasks.get(userName);
                tasksAssignedToUserSet.add(foundTask.getTaskName());
                assignedTasks.put(foundTask.getUser().getName(), tasksAssignedToUserSet);
            }else {
                LinkedHashSet<String> tasksAssignedToUserSet = new LinkedHashSet<>();
                tasksAssignedToUserSet.add(foundTask.getTaskName());
                assignedTasks.put(foundTask.getUser().getName(), tasksAssignedToUserSet);
            }
            return "Success";
        } else if (foundTask == null) {
            return "Task does not exist!";
        } else {
            return "User does not exist!";
        }
    }

    /**
     * This function returns the task details in the specific format.
     * @param taskName The name of the task.
     * @return  The “Task does not exist” if the task is not present in the system.
     */
    public String printTask(String taskName) {
        if(Data.tasks.containsKey(taskName)){
            Task foundTask = Data.tasks.get(taskName);
            String format = foundTask.getTaskName();
            format += "\n" + foundTask.getDescription();
            format += "\nPriority: " + foundTask.getPriority();
            format += "\nEstimated Time: " + foundTask.getEstimatedTime();
            if(foundTask.getUser() != null){
                format += "\nAssigned to " + foundTask.getUser().getName();
            }else{
                format += "\nUnassigned";

            }
            return format;
            }
        return "Task does not exist!";
    }

    /**
     * Function to get a task done.
     * @param taskName The name of the task.
     * @return String: "Success" if the the task exists in the system
     * "Task does not exist!" if the task is not existing in the system.
     */
    public String completeTask(String taskName) {
        if(Data.tasks.containsKey(taskName)) {
            Task foundTask = Data.tasks.get(taskName);
            completedTasks.put(foundTask.getUser().getName(), assignedTasks.get(foundTask.getUser().getName()));
            return "Success";

        }
        return "Task does not exist!";
    }

    /**
     * This function renders users in descending order and if equal, return in any order.
     * The performance of the user is equal to the total time estimate of the tasks performed by that user.
     * @return String: Users
     */
    public String printUsersByPerformance() {
        Map<String, Integer> userPerformance = new LinkedHashMap<>();
        for(Map.Entry<String,LinkedHashSet<String>> entry : assignedTasks.entrySet() ){
            int totalEstimate = 0;
            for(String taskName : entry.getValue()){
                Task foundTask = Data.tasks.get(taskName);
                totalEstimate += foundTask.getEstimatedTime();
            }
            userPerformance.put(entry.getKey(),totalEstimate);
        }
        java.util.List<Map.Entry<String,Integer>> toBeSortedList = new ArrayList<>(userPerformance.entrySet());
        sortList(toBeSortedList);
        Collections.reverse(toBeSortedList);
        String format="";
        for (Map.Entry<String, Integer> entry : toBeSortedList) {
            format += entry.getKey() + "\n";

        }
        return format;
    }

    public static void sortList(java.util.List<Map.Entry<String, Integer>> toBeSorted) {
        Collections.sort(toBeSorted, Comparator.comparingInt((ToIntFunction<Map.Entry<String, Integer>>) Map.Entry::getValue).thenComparingInt(Map.Entry::getValue));
    }

    /**
     * This function returns users in ascending order and if equal, return in any order.
     * The amount of work a user does is the estimated time that all of the tasks assigned to that user.
     * @return String: users
     */
    public String printUsersByWorkload() {
        Map<String, Integer> userPerformance = new LinkedHashMap<>();
        for(Map.Entry<String,LinkedHashSet<String>> entry : assignedTasks.entrySet() ){
            int totalEstimate = 0;
            for(String taskName : entry.getValue()){
                Task foundTask = Data.tasks.get(taskName);
                totalEstimate += foundTask.getEstimatedTime();
            }
            userPerformance.put(entry.getKey(),totalEstimate);
        }
        java.util.List<Map.Entry<String,Integer>> toBeSortedList = new ArrayList<>(userPerformance.entrySet());
        sortList(toBeSortedList);
        String format="";
        for (Map.Entry<String, Integer> entry : toBeSortedList) {
            format += entry.getKey() + "\n";
        }
        return format;
    }
    /**
     * This function Prioritizes all the tasks which are not assigned to the system in the order in which they are equal,
     * @return String : tasks
     */
    public String printUnassignedTasksByPriority() {
        Map<String, Integer> taskByPriority = new LinkedHashMap<>();
        for(Map.Entry<String,Task> entry : Data.tasks.entrySet()){
            if(entry.getValue().getUser() == null){
                taskByPriority.put(entry.getKey(), entry.getValue().getPriority());
            }
        }
        java.util.List<Map.Entry<String,Integer>> toBeSorted = new ArrayList<>(taskByPriority.entrySet());
        sortList(toBeSorted);
        String format="\n";
        for (Map.Entry<String, Integer> entry : toBeSorted) {
            format += printTaskFormat(entry.getKey());
        }
        return format;
    }

    public String printTaskFormat(String taskName){
        Task task = Data.tasks.get(taskName);
        String format = task.getPriority() + " | "  + task.getTaskName() + " | ";
        if(task.getUser() == null){
            format += "Unassigned" + " | " + task.getEstimatedTime() +"h";
        }else {
            format += task.getUser().getName() + " | " + task.getEstimatedTime() + "h";
        }
        return format;
    }

    public String deleteTask(String taskName) {
        return TaskDAO.deleteTask(taskName);
    }


    public String printAllUnfinishedTasksByPriority() {
        Map<String, Integer> allUnfinishedTasks = new LinkedHashMap<>();
        for(Map.Entry<String, Task> entry : TaskDAO.readAll().entrySet()){
            Task temp = entry.getValue();
            for(Map.Entry<String, LinkedHashSet<String>> completedTaskEntry : completedTasks.entrySet()){
                LinkedHashSet<String> completedTask = completedTaskEntry.getValue();
                if(!completedTask.contains(temp.getTaskName())){
                    allUnfinishedTasks.put(temp.getTaskName(), temp.getPriority());
                }
            }
        }
        java.util.List<Map.Entry<String,Integer>> toBeSorted = new ArrayList<>(allUnfinishedTasks.entrySet());
        sortList(toBeSorted);
        String format="\n";
        for (Map.Entry<String, Integer> entry : toBeSorted) {
            format += printTaskFormat(entry.getKey()) + "\n";
        }
        return format;
    }

    /**
     * Using this function we can move a task to another list
     * @param taskName The name of the task to be moved.
     * @param listName The name of the list to which it should be moved.
     * @return String: Success or the “List does not exist” if the destination list is not available in the system.
     */
    public String moveTask(String taskName, String listName) {
        Task foundTask = TaskDAO.search(taskName);
        String from = "";
        if(foundTask != null){
            from = foundTask.getList().getListName();
            List<Task> fromList = listTasks.get(from);
            if(ListsDAO.searchList(listName) != null){
                foundTask.setList(new Lists(listName));
                Data.tasks.put(taskName,foundTask);
                List<Task> toList = listTasks.get(listName);
                toList.add(foundTask);
                listTasks.put(foundTask.getList().getListName(), toList);
                fromList.removeIf(t -> t.getTaskName().equals(taskName));
                return "Success";
            }else {
                return "List does not exists!";
            }
        }else{
            return "Task does not exist!";
        }
    }

    /**
     * Returns all the tasks in a list in order of creation.
     * @param listName The name of the list.
     * @return returns the “List does not exist” string Returns.
     */
    public String printList(String listName) {
        if(ListsDAO.searchList(listName) != null){
            String format ="List "+ listName +"\n";
            for(Map.Entry<String,List<Task>> entry : Data.listTasks.entrySet()){
                if(entry.getKey().equals(listName)){
                    List<Task> allTaskOfListTemp = entry.getValue();
                    for(Task t : allTaskOfListTemp){
                        format += printTaskFormat(t.getTaskName())+"\n";
                    }
                    return format;
                }
            }

        }
        return "List does not exists!";
    }

    /**
     * Make all the tasks of all lists in order of the list and in each list the tasks in order,
     * @return String: The output format of each list is the same as the function of the previous function.
     */
    public String printAllLists() {
        if(!Data.lists.isEmpty()){
            String format = "";
            for(Map.Entry<String,List<Task>> entry : Data.listTasks.entrySet()){
                    format +="\nList "+entry.getKey() +"\n";
                    List<Task> allTaskOfListTemp = entry.getValue();
                    for(Task t : allTaskOfListTemp){
                        format += printTaskFormat(t.getTaskName())+"\n";
                    }
            }
            return format;
        }
        return "List does not exists!";
    }

    /**
     * Returns all user tasks in the order that they were created.
     * @param userName The name of the user.
     * @return String: All user tasks in the order that they were created
     * If the user is not in the System, returns the string “User does not exist”.
     * If there are no task assigned to the user, returns the string "No tasks assigned to the user!".
     */
    public String printUserTasks(String userName) {
        if(UserDAO.search(userName)!=null){
            LinkedHashSet<String> taskTemp = assignedTasks.get(userName);
            String format ="\n";
            if(!taskTemp.isEmpty()) {
                for (String name : taskTemp) {
                    Task foundTask = TaskDAO.search(name);
                    if(foundTask != null){
                        format += printTaskFormat(foundTask.getTaskName()) +"\n";
                    }
                }
                return format;
            } else{
                return "No tasks assigned to the user!";
            }
        }
        return "User does not exists!";
    }

    public String deleteList(String listName) {
        return ListsDAO.deleteList(listName);
    }

    /**
     * This function returns the estimated completion time of all tasks.
     * This value is equal to the maximum time required by users to perform their tasks.
     * @param userName The name of the user
     * @return The time that users need to perform their tasks is equal
     * to the sum of the estimated time of all tasks assigned to that user.
     * Note that this function does not include unassigned tasks.
     */
    public int printTotalEstimatedTime(String userName){
        int totalEstimatedTime=0;
        if(UserDAO.search(userName)!=null) {
            LinkedHashSet<String> assignedTasksTemp = assignedTasks.get(userName);
            for (String name : assignedTasksTemp) {
                Task foundTask = TaskDAO.search(name);
                if (foundTask != null) {
                    totalEstimatedTime += foundTask.getEstimatedTime();
                }
            }
            return totalEstimatedTime;
        }
        return 0;
    }

    /**
     *This function returns the estimated time of completion of all tasks performed.
     *The time that users need to perform their remaining tasks is equal to the sum
     * of the estimated time of all tasks assigned to that user that has not been completed.
     * @return Integer: This value is equal to the maximum the time required by users to perform their remaining tasks.
     * Note that this function does not include unassigned tasks.
     */
    public int printTotalRemainingTime(String userName){
        int totalRemainingTime=0;
        if (UserDAO.search(userName) != null) {
            LinkedHashSet<String> assignedTasksTemp = assignedTasks.get(userName);
            for (String taskName : assignedTasksTemp) {
                Task foundTask = TaskDAO.search(taskName);
                if(foundTask!=null){
                    for(Map.Entry<String, LinkedHashSet<String>> completedTaskEntry : completedTasks.entrySet()){
                        LinkedHashSet<String> completedTask = completedTaskEntry.getValue();
                        if(completedTask.contains(taskName)){
                            totalRemainingTime += foundTask.getEstimatedTime();
                        }
                    }
                }
            }
            return totalRemainingTime;  
        }
        return 0;
    }

    /**
     * This function returns the sum of all user tasks in all lists.
     * @param userName The name of the user.
     * @return Integer: Total number of tasks assigned to the user.
     * If User does not exist in the system, returns 0.
     */
    public int printUserWorkload(String userName){
        int totalWorkload=0;
        if(UserDAO.search(userName)!=null) {
            LinkedHashSet<String> assignedTasksTemp = assignedTasks.get(userName);
            for (String name : assignedTasksTemp) {
                Task foundTask = TaskDAO.search(name);
                if (foundTask != null) {
                    totalWorkload += 1;
                }
            }
            return totalWorkload;
        }
        return 0;
    }
}


