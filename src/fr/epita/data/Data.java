package fr.epita.data;

import fr.epita.datamodel.Task;

import java.util.*;
/**
 * @author shubham
 * @author manish
 * Class which stores the required information of the system.
 */

public class Data {
    //Note: Users are stored in the database
    public static final Map<String, List<Task>> listTasks = new LinkedHashMap<>(); //stores list and tasks mappings

    /*
   In the given case, List is a unique value and there can't be any duplicates, therefore Set is the best option to store the lists.
   Also the complexity of add, contains, next operations of linkedHashSet is equal to O(1).
    */
    public static final Set<String> lists = new LinkedHashSet<>(); //stores lists created

    /*
    A map does not allow any duplicate key values.
    Also the complexity of get, containsKey, put operations of linkedHashMap is equal to O(1).
    We are storing the name of the task as the key in the following map, and the task object as the value. Thus, preventing any duplicate pairs.
    In the given case, Tasks are unique values and there can't be any duplicates, therefore Set is the best option to store the lists.
    */
    public static final Map<String,Task> tasks = new LinkedHashMap<>(); ///stores tasks created

}
