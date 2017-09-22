package io.oopsie.sdk.cli;

import io.oopsie.sdk.error.CLIParseCommandException;
import com.google.common.base.Charsets;
import io.oopsie.sdk.Application;
import io.oopsie.sdk.CreateStatement;
import io.oopsie.sdk.DataType;
import io.oopsie.sdk.GetStatement;
import io.oopsie.sdk.Resource;
import io.oopsie.sdk.ResultSet;
import io.oopsie.sdk.Row;
import io.oopsie.sdk.Site;
import io.oopsie.sdk.Statement;
import io.oopsie.sdk.User;
import io.oopsie.sdk.error.ModelException;
import io.oopsie.sdk.error.OopsieSiteException;
import io.oopsie.sdk.error.StatementExecutionException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
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
    private boolean truncate;

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
                siteInfo.getProperty("siteId"));
        site.init();
        while(true) {
            System.out.print("oopsh> ");
            try {
                Map<String, List<String>> params = splitCommand(scanTerminalInput().trim());
                if(params != null) {
                    printResult(executeCommand(params), truncate);
                }
            } catch(CLIParseCommandException e) {
                System.out.println(e.getMessage());
            }
        }
    }
    
    private Map<String, List<String>> splitCommand(String command) throws CLIParseCommandException {
              
        String trimmedCommand = command.trim();  
        Map<String, List<String>> commandMap = new HashMap();
        Method method = null;
        try {

            // extract head command
            if(commandStartWith(command, Method.CREATE)) {
                method = Method.CREATE;
            } else if(commandStartWith(command, Method.DELETE)) {
                method = Method.DELETE;
            } else if(commandStartWith(command, Method.EXIT)) {
                exit();
            } else if(commandStartWith(command, Method.HELP)) {
                method = Method.HELP;
            } else if(commandStartWith(command, Method.LOGIN)) {
                login();
                return null;
            } else if(commandStartWith(command, Method.LOGOUT)) {
                logout();
                return null;
            } else if(commandStartWith(command, Method.KEY)) {
                setApiKey(true);
                return null;
            } else if(commandStartWith(command, Method.NOKEY)) {
                setApiKey(false);
                return null;
            } else if(commandStartWith(command, Method.READ)) {
                method = Method.READ;
            } else if(commandStartWith(command, Method.UPDATE)) {
                method = Method.UPDATE;
            } else if(commandStartWith(command, Method.SET)) {
                set(command);
                System.out.println("TRUNCATE RESULT=" + truncate);
                return null;
            } else {
                throw new CLIParseCommandException("Statement is not an oopsh command.");
            }
            trimmedCommand = trimmedCommand.substring(method.name().length() + 1).trim();
            // extract app and resource
            String[] target = extractTarget(trimmedCommand);
            
            commandMap.put("@method", asList(method.name()));
            commandMap.put("@app", asList(target[0]));
            commandMap.put("@res", asList(target[1]));

            trimmedCommand = trimmedCommand.substring(target[0].length() + 1).trim();
            trimmedCommand = trimmedCommand.substring(target[1].length()).trim();
        } catch(StringIndexOutOfBoundsException e) {
            throw new CLIParseCommandException("Can't execute command, insufficient information.");
        }
        commandMap.putAll(splitParams(trimmedCommand));
        return commandMap;
    }
    
    private List<String> asList(String string) {
        List<String> list = new ArrayList();
        list.add(string);
        return list;
    }
    
    private Map<String, List<String>> splitParams(String paramsString) {
        String trimmedParams = paramsString.trim();
        List<String> paramsList = Arrays.asList(trimmedParams.split("@:"));
        Map<String, List<String>> params = new HashMap();
        
        paramsList.forEach(p -> {
            
            if(p.length() > 0 && p.contains("=")) {
                
                 // Can't use split since string after '=' may contain additional '=' in value
                String param = p.trim();
                String paramAttrib = param.substring(0, param.indexOf("="));
                String paramVal = param.substring(param.indexOf("=") + 1);
                List<String> valList = params.get(paramAttrib);
                if(valList == null) {
                    valList = new ArrayList();
                    params.put(paramAttrib, valList);
                }
                valList.add(paramVal);
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
    
    private ResultSet executeCommand(Map<String, List<String>> commandMap) throws CLIParseCommandException {
        
        Method method = Method.valueOf(commandMap.remove("@method").get(0));
        String appS = commandMap.remove("@app").get(0);
        String resS = commandMap.remove("@res").get(0);
        
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
        switch(method) {
            case HELP:
                help();
                break;
            case CREATE:
                statement = createStatement(resource, commandMap);
                break;
            case READ:
                statement = readStatement(resource, commandMap);
                break;
            case UPDATE:
            case DELETE:
                System.out.println("'" + method + "' is not yet supported");
                break;
            case EXIT:
                exit();
                break;
            default:
                System.out.println("'" + method + "' is not a recognized command");
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
    
    private CreateStatement createStatement(Resource resource, Map<String, List<String>> attribMap) throws CLIParseCommandException {
        
        CreateStatement statement = resource.create();
        
        if(!attribMap.isEmpty()) {
            attribMap.forEach((a,v) -> {
                 // use first appearance of attrib ...
                Object o = AttributeUtils.getValueObject(resource, a, v.get(0));
                statement.withParam(a, o);
            });
        }
        
        return statement;
    }
    
    private GetStatement readStatement(Resource resource, Map<String, List<String>> paramMap) throws CLIParseCommandException {
        final GetStatement statement = resource.get();
        try {
            
            if(!paramMap.isEmpty()) {

                // only get first limit
                int limit = paramMap.get(ReadParams.LIMIT.command()) != null ?
                        Integer.valueOf(paramMap.remove(ReadParams.LIMIT.command()).get(0)) : 0;
                if(limit > 0) {
                    statement.limit(limit);
                }
                
                // only get first expand
                boolean expand = paramMap.get(ReadParams.EXPAND.command()) != null ?
                        Boolean.valueOf(paramMap.remove(ReadParams.EXPAND.command()).get(0)) : false;
                if(expand) {
                    statement.expandRelations();
                }
                
                // for now only support one id, .. so get first id
                UUID id = paramMap.get(ReadParams.ID.command()) != null ?
                        UUID.fromString(paramMap.remove(ReadParams.ID.command()).get(0)) : null;
                if(id != null) {
                    statement.withId(id);
                }
                
                // last param supported in read is pk's, ... ignoring rest if any other param types ...
                List<String> pks = paramMap.remove(ReadParams.PK.command());
                
                if(pks != null) {
                    pks.forEach(pk -> {
                            String pkParam = pk.substring(0, pk.indexOf("="));
                            String pkVal = pk.substring(pk.indexOf("=") + 1);
                            statement.withParam(pkParam, pkVal);
                        });
                }
            }
        } catch(Exception e) {
            if(e instanceof CLIParseCommandException) {
                throw e;
            } else if(e instanceof OopsieSiteException) {
                throw  new CLIParseCommandException(e.getMessage());
            } else {
                throw new CLIParseCommandException("Can't execute command.");
            }
        }
        
        return statement;
    }
    
    private void login() {
        try {
            User user = new User(siteInfo.getProperty("username"), siteInfo.getProperty("password"));
            site.login(user);
            System.out.println("Logged in as " + user.getEmail());
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    private void logout() {
        try {
            site.logout();
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    private void setApiKey(boolean use) {
        
        String key = use ? siteInfo.getProperty("apiKey") : null;
        site.setApiKey(key);
        site.init();
        System.out.println("Using api key: " + use);
    }
    
    private void help() {
        System.out.println("Printing help!");
    }
    
    private void exit() {
        site.close(30, TimeUnit.SECONDS);
        System.out.println("Done!");
        System.exit(0);
    }
    
    private void set(String command) throws CLIParseCommandException {
        
        try {
            Deque<String> deque = new ArrayDeque(Arrays.asList(command.split(" ")));
            skipToNext(deque); // << not using since we know first is SET
            String next = skipToNext(deque);
            SetParams param = null;

                param = SetParams.valueOf(next.toUpperCase());

            switch(param) {
                case TR:
                case TRUNCATE:
                    next = skipToNext(deque); // next shoul be a bool string
                    truncate = Boolean. valueOf(next.toLowerCase());
                    break;
                case APP:
                case APPLICATION:
                    break;
                case RES:
                case RESOURCE:
                    break;
            }
        } catch(Exception e) {
            throw new CLIParseCommandException("Can't execute command.");
        }
    }
    
    private String skipToNext(Deque<String> deque) {
        
        String next = null;
        while(true) {
            String s = deque.poll();
            if(!s.isEmpty()) {
                next = s;
                break;
            }
        }
        return next;
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

    private void printResult(ResultSet result, boolean truncate) {
        if(truncate) {
            printResultTruncated(result);
        } else {
            printResultFull(result);
        }
    }
    
    private void printResultFull(ResultSet result) {
        
        Resource res = result.getStatement().getResource();
        int colSpace = 0;
        for(String a : result.getColumnNames()) {
            if(a.length() > colSpace) {
                colSpace = a.length();
            }
        }
        colSpace += 10;
        final int spaces = colSpace;
        
        TreeSet<ResultSet.RowColumnMetaData> sortedCols = new TreeSet(new Comparator<ResultSet.RowColumnMetaData>() {
            @Override
            public int compare(ResultSet.RowColumnMetaData o1, ResultSet.RowColumnMetaData o2) {
                return o1.getColumnName().compareTo(o2.getColumnName());
            }
        });
        sortedCols.addAll(result.getColumnMetaData());
        Iterator<Row> iterator = result.iterator();
        int c = 0;
        System.out.println("--------------------------------------------------");
        while(iterator.hasNext()) {
            Row row = iterator.next();
            
            String pkVals = "";
            for(String pkName : res.getPrimaryKeyNames()) {
                pkVals = String.join("",
                        "\"",
                        pkName,
                        "\"",
                        ":",
                        "\"",
                        row.get(pkName).toString(),
                        "\""
                );
            }
            String pk = String.join("", "{", pkVals,"}");
            System.out.println("| ROW " + ++c + ", PK " + pk);
            System.out.println("|");
            sortedCols.forEach(md -> {
                String a = md.getColumnName();
                System.out.println( padOrTrunc("| " + a + ":", spaces - ("| " + a + ":").length()) + row.get(a));
            });
            System.out.println("--------------------------------------------------");
        }
    }
    
    private void printResultTruncated(ResultSet result) {
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
