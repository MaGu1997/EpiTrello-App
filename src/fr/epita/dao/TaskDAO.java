package fr.epita.dao;

import fr.epita.data.Data;
import fr.epita.datamodel.Lists;
import fr.epita.datamodel.Task;
import fr.epita.dataservice.EpitrelloDataService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Handles all the CRUD Operations for Tasks
 * @author shubham
 * @author manish
 */

public class TaskDAO {
    /**
     * Function to add a new Task to the system
     * @param newTask Task to be created
     * @return String "Success" if the task is created, "Task already exists!" if the task is already
     * existing in the system and "List does not exist!" if the list doesn't exist in the system.
     */
    public static String addTask(Task newTask) {

        if (ListsDAO.searchList(newTask.getList().getListName()) != null) {
            if(Data.tasks.containsKey(newTask.getTaskName())){
                return "Task already exists!";
            }else {
                Data.tasks.put(newTask.getTaskName(), newTask);
                if(Data.listTasks.containsKey(newTask.getList().getListName())) {
                    List<Task> taskListTemp = Data.listTasks.get(newTask.getList().getListName());
                    taskListTemp.add(newTask);
                    Data.listTasks.put(newTask.getList().getListName(), taskListTemp);
                }else{
                    List<Task> taskListTemp = new ArrayList<>();
                    taskListTemp.add(newTask);
                    Data.listTasks.put(newTask.getList().getListName(), taskListTemp);
                }
                return "Success";
            }
        }
        return "List does not exists!";
    }

    /**
     * Function to update an existing Task from the system
     * @param editedTask Task to be updated
     * @return String "Success" if the task is updated
     * and "Task does not exist!" if the task doesn't exist in the system.
     */
    public static String updateTask(Task editedTask){
        Task foundTask = search(editedTask.getTaskName());
        if(foundTask != null){
            editedTask.setList(new Lists(foundTask.getList().getListName()));
            Data.tasks.put(editedTask.getTaskName(),editedTask);
            List<Task> temp = Data.listTasks.get(editedTask.getList().getListName());
            ArrayList<Task> newListTemp = new ArrayList<>();
            for(Task t : temp){
                if(t.getTaskName().equals(editedTask.getTaskName())){
                    newListTemp.add(temp.indexOf(t), editedTask);
                    temp.clear();
                    temp.addAll(newListTemp);
                }
            }
            Data.listTasks.put(editedTask.getList().getListName(), temp);
            return "Success";
        }
        return "Task does not exist!";

    }
    /**
     * Function to delete an existing Task from the system
     * @param taskName Task to be deleted
     * @return String "Success" if the task is deleted
     * and "Task does not exist!" if the task doesn't exist in the system.
     */
    public static String deleteTask(String taskName) {
        Task foundTask = search(taskName);
        if(foundTask != null){
            Data.tasks.remove(taskName);
            for(Map.Entry<String, List<Task>> entry : Data.listTasks.entrySet()){
                if(entry.getKey().equals(foundTask.getList().getListName())){
                    List<Task> temp = entry.getValue();
                    temp.removeIf(t -> t.getTaskName().equals(taskName));
                    Data.listTasks.put(entry.getKey(), temp);
                }
            }
            if(foundTask.getUser() != null){
                EpitrelloDataService.assignedTasks.remove(foundTask.getUser().getName());
            }
            return "Success";

        }
        return "Task does not exist!";
    }

    /**
     * Function to read all the existing Tasks from the system
     * @return Map (taskName, taskObject)
     */
    public static Map<String, Task> readAll(){
        return Data.tasks;
    }

    /**
     * Function to search all the existing Tasks from the system
     * @param taskName The name of the task
     * @return Task If the task exists in the system
     * null If the task doesn't exists in the system
     */
    public static Task search(String taskName){
        if(Data.tasks.containsKey(taskName)){
            return Data.tasks.get(taskName);
        }
        return null;
    }
}
