package io.oopsie.sdk;

import io.oopsie.sdk.error.CLIParseCommandException;
import com.google.common.base.Charsets;
import io.oopsie.sdk.error.ModelException;
import io.oopsie.sdk.error.StatementExecutionException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CLI {
    
    private Scanner terminalInput;
    private Properties siteInfo;
    private Site site;

    public CLI() {
        this.terminalInput = new Scanner(System.in, Charsets.UTF_8.toString());
    }
    public CLI(File siteInfoFile) throws IOException {
        this();
        siteInfo = new Properties();
        FileInputStream fis = new FileInputStream(siteInfoFile);
        siteInfo.load(fis);
        fis.close();
    }
    
    void start() {
        site = new Site( 
                siteInfo.getProperty("siteUrl"),
                siteInfo.getProperty("customerId"), 
                siteInfo.getProperty("siteId"),
                siteInfo.getProperty("apiKey"));
        site.init();
        while(true) {
            System.out.print("oopsh> ");
            try {
                printResult(executeCommand(scanTerminalInput()));
            } catch(CLIParseCommandException e) {
                System.out.println(e.getMessage());
            }
        }
    }
    
    private ResultSet executeCommand(String command) throws CLIParseCommandException {
        
        Deque<String> commandDeque = new ArrayDeque(Arrays.asList(command.split(" ")));
        String first = commandDeque.poll();
        Command head = Command.ILLEGAL;
        try {
            head = Command.valueOf(first.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CLIParseCommandException("Statement '" + first + "' is not an oopsh statement command.");
        }
        
        Statement statement = null;
        switch(head) {
            case HELP:
                help();
                break;
            case CREATE:
                statement = parseCreate(first, commandDeque);
                break;
            case READ:
                System.out.println("Executing '" + head + "'");
                break;
            case UPDATE:
            case DELETE:
                System.out.println("'" + first + "' is not yet supported");
                break;
            case EXIT:
                exit();
                break;
            case ILLEGAL:
                System.out.println("'" + first + "' is not a recognized command");
                break;
        }
        ResultSet result = null;
        try {
            result = site.execute(statement);
        } catch(StatementExecutionException e) {
            throw new CLIParseCommandException(e.getMessage());
        }
        return result;
    }
    
    private CreateStatement parseCreate(String command, Deque<String> commandTokens) throws CLIParseCommandException {
        
        // next poll must application name
        String next = commandTokens.poll();
        Application app;
        try {
            app = site.getApplication(next);
        } catch(ModelException e) {
            throw new CLIParseCommandException("Application '" + next + "' could not be found.");
        }
        
        // next poll must resource name
        next = commandTokens.poll();
        Resource resource;
        try {
            resource = app.getResource(next);
        } catch(ModelException e) {
            throw new CLIParseCommandException("Resource '" + next + "' could not be found.");
        }
        
        CreateStatement statement = null;
        if(commandTokens.isEmpty()) {
            statement = resource.create();
        } else {
            // the rest should be attribs mapped to their values
            Map<String, Object> attribVals = new HashMap();
            while(!commandTokens.isEmpty()) {
                String[] attrib = commandTokens.poll().split("=");
                if(attrib.length < 2) {
                    throw new CLIParseCommandException("Attribute parameter '" +  attrib[0] + "' is not valid, must conform to format '<name>=<value>'");
                }
                Object objectVal;
                try {
                    objectVal = AttributeUtils.getValueObject(resource, attrib[0], attrib[1]);
                } catch(IllegalArgumentException | ModelException e) {
                    throw new CLIParseCommandException(e.getMessage());
                }
                attribVals.put(attrib[0], objectVal);
            }
            statement = resource.create().withParams(attribVals);
        }
        
        return statement;
    }
    
    private void help() {
        System.out.println("Printing help!");
    }
    
    private void exit() {
        System.out.println("Goodbye!");
        System.exit(0);
    }
    
    private String scanTerminalInput() {
        return terminalInput.nextLine();
    }

    private void printResult(ResultSet result) {
        result.all().forEach(row -> {
            System.out.println(row.toString());
        });
    }
    
    public static void main(String[] args) {
        SpringApplication.run(CLI.class, args);
    }
    
    @Bean
    public static CommandLineRunner run() throws Exception {
        return args -> {
            
            String siteInfoPath;
            if(args.length == 1) {
               siteInfoPath = args[0];
            } else {
                siteInfoPath = String.join("",
                        System.getProperty("user.home"),
                        File.separator,
                        ".oopsie",
                        File.separator,
                        "oopsieSite");
            }
            
            CLI cli;
            File file = new File(siteInfoPath);
            if(file.isFile()) {
                cli = new CLI(file);
            } else {
                cli = new CLI();
            }
            cli.start();
        };
    }
}
