# exercisedb

## Development

Package and test the app:

```
mvn clean install
```

### Run in the foreground 

```
cd exercisedb-war; mvn liberty:run
```

(stop with Ctrl+C)

Browse to [http://localhost:9080/exercisedb-war-1.0-SNAPSHOT/](http://localhost:9080/exercisedb-war-1.0-SNAPSHOT/)

### Run in the background

```
cd exercisedb-war; mvn liberty:run
```

(stop with `mvn liberty:stop` from same directory)

Browse to [http://localhost:9080/exercisedb-war-1.0-SNAPSHOT/](http://localhost:9080/exercisedb-war-1.0-SNAPSHOT/)
