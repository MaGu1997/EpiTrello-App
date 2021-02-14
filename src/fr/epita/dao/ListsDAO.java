package fr.epita.dao;

import fr.epita.data.Data;
import fr.epita.datamodel.Lists;
import fr.epita.datamodel.Task;
import fr.epita.dataservice.EpitrelloDataService;

import java.util.*;

/**
 * @author shubham
 * @author manish
 */

public class ListsDAO {
    /**
     * Function to add a new List to the system
     * @param newList List to be created
     * @return String"Success" if the list is created
     * and "List already not exist!" if the list doesn't exist in the system.
     */
    public static String addList(Lists newList) {
        Lists exist = searchList(newList.getListName());
        if (exist == null) {
            Data.lists.add(newList.getListName());
            return "Success";
        } else {
            return "List already exists!";
        }
    }

    /**
     * Function to search an existing List in the system
     * @param listName The name of List to be searched in the system.
     * @return An object of the found list and null if
     * the list doesn't exist in the system.
     */
    public static Lists searchList(String listName) {
        if (Data.lists.contains(listName)) {
            return new Lists(listName);
        }
        return null;
    }

    /**
     * Function to search an existing List in the system
     * @param listName The name of the List to be deleted from the system.
     * @return String "Success" if the task is created,
     * and "List does not exist!" if the list doesn't exist in the system.
     * Note: This method will also delete the associated tasks of the list.
     */
    public static String deleteList(String listName) {
        List<Task> tempTaskList = new ArrayList<>();
        if (searchList(listName) != null) {
            Data.lists.remove(listName);
            for (Map.Entry<String, List<Task>> entry : Data.listTasks.entrySet()) {
                if (entry.getKey().equals(listName)) {
                    tempTaskList = entry.getValue();
                }
            }
            for (Task t : tempTaskList) {
                if (t.getUser()!=null && t.getList().getListName().equals(listName)) {
                    for (Map.Entry<String, LinkedHashSet<String>> assignedEntry : EpitrelloDataService.assignedTasks.entrySet()) {
                        LinkedHashSet<String> temp = assignedEntry.getValue();
                        temp.remove(t.getTaskName());
                        EpitrelloDataService.assignedTasks.put(t.getUser().getName(), temp);
                    }
                    Data.tasks.remove(t.getTaskName());
                }
            }
            Data.listTasks.remove(listName);
            return "Success";
        }
        return "List does not exist!";
    }


    public static Set<String> readAll() {
        return Data.lists;
    }
}
