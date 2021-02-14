package fr.epita.launcher;/* Your Package */

/* Your imports */

import fr.epita.dataservice.EpitrelloDataService;
import fr.epita.services.Configuration;

import java.io.File;
import java.io.PrintStream;

/**
 * @author shubham
 * @author manish
 */

public class Main {

    private static final String OUTPUT_FILE = Configuration.getValueFromKey("output.file");
    public static void main(String[] args)  {

        EpitrelloDataService dataserverice = new EpitrelloDataService();

        EpitrelloDataService.createTable();
        writeFile();

        System.out.println( dataserverice.addUser("Thomas") ); // addUser(string username)
        System.out.println( dataserverice.addUser("AmirAli") );
        System.out.println( dataserverice.addUser("Rabih") );


        System.out.println( dataserverice.addList("Code") ); //addList(string name)
        System.out.println( dataserverice.addList("Description") );
        System.out.println( dataserverice.addList("Misc") );


        System.out.println( dataserverice.addTask("Code", "Do Everything", 12, 1, "Write the whole code") );
        /* addTask(string list, string name, unsigned int estimatedTime, unsigned int priority, string description) */
        System.out.println( dataserverice.editTask("Do Everything", 12, 10, "Write the whole code") );
        /* editTask(string task, unsigned int estimatedTime, unsigned int priority, string description) */

        System.out.println( dataserverice.assignTask("Do Everything", "Rabih") ); // assignTask(string task, string user)
        System.out.println( dataserverice.printTask("Do Everything") ); // printTask(string task)

        System.out.println( dataserverice.addTask("Code", "Destroy code formatting", 1, 2, "Rewrite the whole code in a worse format") );
        System.out.println( dataserverice.assignTask("Destroy code formatting", "Thomas") );

        System.out.println( dataserverice.addTask("Description", "Write Description", 3, 1, "Write the damn description") );
        System.out.println( dataserverice.assignTask("Write Description", "AmirAli") );
        System.out.println( dataserverice.addTask("Misc", "Upload Assignment", 1, 1, "Upload it") );

        System.out.println( dataserverice.completeTask("Do Everything") ); // completeTask(string task)
        System.out.println( dataserverice.printUsersByPerformance() );
        System.out.println( dataserverice.printUsersByWorkload() );

        System.out.println( dataserverice.printUnassignedTasksByPriority() );
        System.out.println( dataserverice.deleteTask("Upload Assignment") ); // deleteTask(string task)
        System.out.println( dataserverice.printAllUnfinishedTasksByPriority() );

        System.out.println( dataserverice.addTask("Misc", "Have fun", 10, 2, "Just do it") );
        System.out.println( dataserverice.moveTask("Have fun", "Code") ); // moveTask(string task, string list)
        System.out.println( dataserverice.printTask("Have fun") );


        System.out.println( dataserverice.printList("Code") ); // printList(string list)

        System.out.println( dataserverice.printAllLists() );

        System.out.println( dataserverice.printUserTasks("AmirAli") ); // printUserTasks(string user)

        System.out.println( dataserverice.printUnassignedTasksByPriority() );

        System.out.println( dataserverice.printAllUnfinishedTasksByPriority() );

    }

    /**
     * Function to redirect the console output to the file
     */
    public static void writeFile(){
        try {
            System.out.println("\nOutput is written in the text file!");
            // Creating a File object that represents the disk file.
            PrintStream outputFile = new PrintStream(new File(OUTPUT_FILE));
            // Store current System.out before assigning a new value
            PrintStream console = System.err;
            // Assign output to output stream
            System.setOut(outputFile);
            System.setErr(console);

        } catch (Exception e) {
            e.printStackTrace();
        }




    }
}

