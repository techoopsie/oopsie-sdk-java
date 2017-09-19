package io.oopsie.sdk;

import io.oopsie.sdk.error.CLIParseCommandException;
import com.google.common.base.Charsets;
import io.oopsie.sdk.error.ModelException;
import io.oopsie.sdk.error.StatementExecutionException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.TreeSet;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CLI {
    
    private Scanner terminalInput;
    private Properties siteInfo;
    private Site site;
    private String last;

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
                printResult(executeCommand(splitCommand(scanTerminalInput().trim())));
            } catch(CLIParseCommandException e) {
                System.out.println(e.getMessage());
            }
        }
    }
    
    private Map<String, String> splitCommand(String command) throws CLIParseCommandException {
              
        String trimmedCommand = command.trim();  
        Map<String, String> commandMap = new HashMap();
        Method method = null;
        try {

            // extract head command
            if(commandStartWith(command, Method.CREATE)) {
                method = Method.CREATE;
            } else if(commandStartWith(command, Method.DELETE)) {
                method = Method.DELETE;
            } else if(commandStartWith(command, Method.EXIT)) {
                method = Method.EXIT;
            } else if(commandStartWith(command, Method.HELP)) {
                method = Method.HELP;
            } else if(commandStartWith(command, Method.READ)) {
                method = Method.READ;
            } else if(commandStartWith(command, Method.UPDATE)) {
                method = Method.UPDATE;
            } else {
                throw new CLIParseCommandException("Statement is not an oopsh command.");
            }
            trimmedCommand = trimmedCommand.substring(method.name().length() + 1).trim();
            // extract app and resource
            String[] target = extractTarget(trimmedCommand);
            commandMap.put("method", method.name());
            commandMap.put("app", target[0]);
            commandMap.put("res", target[1]);

            trimmedCommand = trimmedCommand.substring(target[0].length() + 1).trim();
            trimmedCommand = trimmedCommand.substring(target[1].length() + 1).trim();
        } catch(StringIndexOutOfBoundsException e) {
            throw new CLIParseCommandException("Can't execute command, insufficient information.");
        }
        
        switch(method) {
            case CREATE:
                commandMap.putAll(splitCreateParams(trimmedCommand));
                break;
        }
        return commandMap;
    }
    
    private Map<String, String> splitCreateParams(String paramsString) {
        String trimmedParams = paramsString.trim();
        List<String> paramsList = Arrays.asList(trimmedParams.split("@:"));
        Map<String, String> params = new HashMap();
        paramsList.forEach(p -> {
            if(p.length() > 0 && p.contains("=")) {
                String param = p.trim();
                String paramAttrib = param.substring(0, param.indexOf("="));
                String paramVal = param.substring(param.indexOf("=") + 1);
                params.put(paramAttrib, paramVal);
            }
        });
        return params;
    }
    
    private boolean commandStartWith(String commandString, Method command) {
        return commandString.trim().toUpperCase().startsWith(command.name());
    }
    
    private String[] extractTarget(String targetString) {
        String[] target = new String[2];
        target[0] = targetString.trim().split(" ")[0];
        target[1] = targetString.trim().substring(target[0].length()+1).trim().split(" ")[0];
        return target;
    } 
    
    private ResultSet executeCommand(Map<String, String> commandMap) throws CLIParseCommandException {
        
        Method head = Method.valueOf(commandMap.remove("method"));
        String appS = commandMap.remove("app");
        String resS = commandMap.remove("res");
        
        Application app;
        try {
            app = site.getApplication(appS);
        } catch(ModelException e) {
            throw new CLIParseCommandException("Application '" + appS + "' could not be found.");
        }
        
        Resource resource;
        try {
            resource = app.getResource(resS);
        } catch(ModelException e) {
            throw new CLIParseCommandException("Resource '" + resS + "' could not be found.");
        }
        
        Statement statement = null;
        switch(head) {
            case HELP:
                help();
                break;
            case CREATE:
                statement = createStatement(resource, commandMap);
                break;
            case READ:
            case UPDATE:
            case DELETE:
                System.out.println("'" + commandMap.get("method") + "' is not yet supported");
                break;
            case EXIT:
                exit();
                break;
            default:
                System.out.println("'" + commandMap.get("method") + "' is not a recognized command");
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
    
    private CreateStatement createStatement(Resource resource, Map<String, String> attribMap) throws CLIParseCommandException {
        
        CreateStatement statement = null;
        if(attribMap.isEmpty()) {
            statement = resource.create();
        } else {
            // the rest should be attribs mapped to their values
            Map<String, Object> attribVals = new HashMap();
            
            
            for(String k : attribMap.keySet()) {
                Object objectVal;
                try {
                    objectVal = AttributeUtils.getValueObject(resource, k, attribMap.get(k));
                } catch(IllegalArgumentException | ModelException e) {
                    throw new CLIParseCommandException(e.getMessage());
                }
                attribVals.put(k, objectVal);
            }
            statement = resource.create().withParams(attribVals);
        }
        
        return statement;
    }
    
    private GetStatement parseRead(String command, Deque<String> commandTokens) throws CLIParseCommandException {
        
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
        
        GetStatement statement = null;
        if(commandTokens.isEmpty()) {
            statement = resource.get();
        } else {
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
            statement = resource.get().withParams(attribVals);
        }
        
        return statement;
    }
    
    /**
     * Polling commandTokens until next is not space.
     * @param commandTokens 
     * @return last poll that is not space
     */
    private String ignoreSpace(Deque<String> commandTokens) {
        
        String next = null;
        while(true) {
            next = commandTokens.poll();
            if(!next.equals(" ")) {
                break;
            }
        }
        return next;
     }
    
    private void help() {
        System.out.println("Printing help!");
    }
    
    private void exit() {
        System.out.println("Goodbye!");
        System.exit(0);
    }
    
    private String scanTerminalInput() {
        String command = terminalInput.nextLine();
        if(command.equals("prev")) {
            if(last != null) {
                command = last;
                System.out.println("oopsh> RUN " + command);
            } else {
                System.out.print("oopsh> ");
                command = terminalInput.nextLine();
            }
        } else {
            last = command;
        }
        return command;
    }

    private void printResult(ResultSet result) {
        int colLenght = 20;
        TreeSet<String> treeSet = new TreeSet(result.getStatement().getResource().getAllSettableAttributeNames());
        
        System.out.print("| " + padOrTrunc("eid", 36 - "eid".length()));
        
        treeSet.forEach(a -> {
                int pad = colLenght - a.length();
                System.out.print("| " +padOrTrunc(a, pad));
        });
        System.out.print("\n");
        System.out.print("|-------------------------------------");
        for(int i = 0; i < treeSet.size(); i++) {
            System.out.print("|");
            for(int j = 0;j < colLenght+1; j++) {
                System.out.print("-");
            }
        }
        System.out.print("\n");
        
        result.all().forEach(row -> {
            
            String eid = row.get("eid").toString();
            System.out.print("| " +padOrTrunc(eid, 36 - eid.length()));
            treeSet.forEach(a -> {
                Object obj = row.get(a);
                String val = obj != null ? obj.toString() : "null";
                int pad = colLenght - val.length();
                System.out.print("| " + padOrTrunc(val, pad));
            });
            System.out.println("");
        });
    }
    
    private String padOrTrunc(String string, int amount) {
        if(amount > -1) {
            for(int i = 0; i < amount; i++) {
                string += " ";
            }
        } else {
            string = string.substring(0, string.length() + amount - 3)  + "...";
        }
        return string;
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
