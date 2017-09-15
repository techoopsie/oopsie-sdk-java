package io.oopsie.sdk;

import io.oopsie.sdk.model.Application;
import io.oopsie.sdk.model.CreateStatement;
import io.oopsie.sdk.model.GetStatement;
import io.oopsie.sdk.model.Resource;
import io.oopsie.sdk.model.Result;
import io.oopsie.sdk.model.Site;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CLI {
    
    public static void main(String[] args) {
        SpringApplication.run(CLI.class, args);
    }
    
    @Bean
    public CommandLineRunner run() throws Exception {
        
        return args -> {

            if(args == null || args.length < 1 || args[0].equals("help")) {
                printHelp();
            } else {
                execute(args[0], args[1], args[2], args[3]);
            }
        };
    }
    
    private static void printHelp() {
        System.out.println("Printing help!");
    }
    
    private static void execute(String apiUrl, String customerId, String siteId, String command) throws InterruptedException, ExecutionException {
        
        
        // Initialize the OOPSIE site
        String apiKey = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIwNzAwNzdhMi00ZjNiLTQxNzMtYWZlOS0zNGM3NDgzY2E4NGEiLCJkZXNjciI6ImtleTEiLCJzaXRlIjoiNzZlNzlhODktMzk2My00YzNkLWE2NzEtZDdlMmY4MzU2ZjExIiwiaXNzIjoiZmVmYTFkYmYtNWY2ZC00ZDM4LWFhYzEtMTI5OGZhODBkNGNmIiwidHlwZSI6ImFwaUtleSIsImlhdCI6MTUwNTIzNjk0NCwianRpIjoiMDcwMDc3YTItNGYzYi00MTczLWFmZTktMzRjNzQ4M2NhODRhIn0.SIRKCosWuddOAdv3wBK92oNle6N4UAfbeuoMfqCDfRQ";
        Site site = new Site(apiUrl, customerId, siteId, apiKey);
        site.init();
        
        // Handle for the "PersonReg" application
        Application personRegApp = site.getApplication("PersonReg");
        
        
        // Handle for the "persons" reosurce
        Resource persons = personRegApp.getResource("persons");
        
        // Create a "persons" entity
        Map<String, Object> personParams = new HashMap();
        personParams.put("firstName", "Nicolas");
        personParams.put("lastName", "Gullstrand");

        
        CreateStatement createPerson = persons.create()
                .set(personParams)    // <-- We can pass in a map to set several values, or ...
                .set("pk", "A") // <-- ... we can set them one by one ...
                .set("ck", "B");
        site.execute(createPerson);
 
        GetStatement getPersons = persons.get().withParam("pk", "A");
        Result result = site.execute(getPersons);
        
        // Voila! ... Now you have created a new person entity in the OOPSIE Cloud
        // go ahead and examine the result object!
        
        // try to close gracefully with a timeout. Will exit earlier if all executions
        // terminated before timeout otherwise forced to terminate.
        site.close(15, TimeUnit.MINUTES);
    }
}
