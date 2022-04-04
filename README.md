# exercisedb

## Development

Package and test the app:

```
mvn clean install
```

### Run in the foreground 

```
cd exercisedb-ear; mvn liberty:run
```

(stop with Ctrl+C)

Browse to [http://localhost:9080/exercisedb/](http://localhost:9080/exercisedb/)

### Run in the background

```
cd exercisedb-ear; mvn liberty:run
```

(stop with `mvn liberty:stop` from same directory)

Browse to [http://localhost:9080/exercisedb/](http://localhost:9080/exercisedb/)
