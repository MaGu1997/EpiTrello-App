package tests.servicesTest;

import fr.epita.services.Configuration;

public class TestConfiguration {

    public static void main(String[] args) {

        //given "conf.properties"
        // when
        String outputDataFileName = Configuration.getValueFromKey("output.file");
        // then
        boolean success = outputDataFileName.equals("output.txt");
        System.out.println("Success? " + success);
        if (!success) {
            System.out.println("Configured value : " + outputDataFileName);
        }

    }
}
