<img src="http://static1.squarespace.com/static/57ecb47344024301f57bc8fa/t/598852628419c22ddf382d9d/1502513980501/?format=1500w" alt="Oopsie" style="width: 200px;"/>

## OOPSIE Java SDK

Create backends with no coding using [OOPSIE Cloud](https://oopsie.io) suitable for all types of applications. What makes oopsie stand out is that it makes your applications Big Data enabled from the beginning. This means that your app ambitions can be very high and you don't have to bother about complex coding for distributed server insfrustructures, handle tens of thousands of api request and manage terabyte of data, oopsie scales with your business strategy.

#### "No idea is too small for a big world!"

With the SDK your java oopsie clients (or java backends for that matter) connects to the [OOPSIE Cloud](https://oopsie.io) and makes it a breeze to work on your java projects without bothering about all the hassles that comes with backend implemantations and maintenance.

## Install

### Maven
	<dependency>
		<groupId>io.oopsie</groupId>
  		<artifactId>oopsie-sdk-java</artifactId>
  		<version>1.0-RC5</version>
	</dependency>
	
## Example

With an [oopsie](https://oopsie.io) site, created and deployed with no coding at all using the [dashboard](https://dashboard.oopsie.io), and just a few lines of SDK code in your client you will have your Big Data enabled app up and running in matter of minutes.

### Prerequisites
* [Register](https://oopsie.io/create-account) yourself and your company at [oopsie](https://oopsie.io)
* [Login](https://dashboard.oopsie.io) to the dashboard and deploy an oopsie site.
* Follow the [Developer Docs](https://docs.techoopsie.com) to get you started with the oopsie cloud tools.

### Initialize site

```
    Site librarySite = new Site(apiUrl, customerId, siteId, apiKey);
    librarySite.init();
    
    ...
    
```

### Choose app and resource

```
    Application bookApp = librarySite.getApplication("BookApp");
    Resource bookRes = bookApp.getResource("Book");
    
    ...
    
```

### Create entity

```
	Statement stmnt = bookRes.create()
		.withParam("Title", "The Master and Margarita")
    		.withParam("Author", "bulgakov, mikhail");
    ResultSet result = librarySite.execute(stmnt);
    Row row = result.one();
    UUID bookId = row.getUUID("eid");
    
    ...
    
```

### Get entity
```
	stmnt = bookRes.get().withParam("eid", bookId);
	ResultSet result = librarySite.execute(stmnt);
	String author = result.one().getString("Author");
	
	...
	
```

### Save entity
```
	Map<String, Object> params = new HashMap();
	params.put("eid", bookId);
	params.put("Title", "The Master and Margarita");
	params.put("Author", "Bulgakov, Mikhail");
	
	stmnt = bookRes.save().withParams(params);
	ResultSet result = librarySite.execute(stmnt);
	
	...
	
```

