package tests.daoTest;

import fr.epita.dao.ListsDAO;
import fr.epita.datamodel.Lists;

import java.util.LinkedHashSet;
import java.util.Set;

public class ListsDAOTest {
    public static void main(String[] args){
            testCreate();
            testSearch();
            testDelete();
            testReadAll();
    }

    public static void testCreate(){
        System.out.println("Testing list create....");
        Lists newList1 = new Lists("Code");
        System.out.println(ListsDAO.addList(newList1));

        Lists newList2 = new Lists("Code");
        System.out.println(ListsDAO.addList(newList2));

        Lists newList3 = new Lists("Test");
        System.out.println(ListsDAO.addList(newList3));

        Lists newList4 = new Lists("Deployment");
        System.out.println(ListsDAO.addList(newList4));;

    }

    public static void testSearch(){
        System.out.println("\n\nTesting list search....");
        Lists foundList = ListsDAO.searchList("Code");
        if(foundList!= null){
            System.out.println(foundList.getListName());
        }else
            System.out.println("List does not exist");

        foundList = ListsDAO.searchList("XYZ");
        if(foundList!= null){
            System.out.println(foundList.getListName());
        }else
            System.out.println("List does not exist");

        foundList = ListsDAO.searchList("Deployment");
        if(foundList!= null){
            System.out.println(foundList.getListName());
        }else
            System.out.println("List does not exist");


    }

    public static void testDelete(){
        System.out.println("\n\nTesting list delete....");
        System.out.println(ListsDAO.deleteList("Code"));
        System.out.println(ListsDAO.deleteList("Code"));
        System.out.println(ListsDAO.deleteList("XYZ"));
        System.out.println(ListsDAO.deleteList("Deployment"));
    }

    private static void testReadAll() {
        System.out.println("\n\nTesting list readAll....");
        Set<String> allLists = new LinkedHashSet<>(ListsDAO.readAll());
        for(String listName : allLists){
            System.out.println(listName);
        }

    }
}
