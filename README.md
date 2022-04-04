# exercisedb

## Development

Package and test the app:

```
mvn clean install
```

### Start MySQL locally

`podman run --ulimit memlock=-1:-1 -it --rm=true --name mysql -e MYSQL_DATABASE=database1 -e MYSQL_USER=mysqluser1 -e MYSQL_PASSWORD=letmein -e MYSQL_ROOT_PASSWORD=letmein -p 3306:3306 mysql:8`

### Run in the foreground 

```
mvn liberty:run
```

(stop with Ctrl+C)

Browse to [http://localhost:9080/exercisedb/](http://localhost:9080/exercisedb/)

### Run in the background

```
mvn liberty:start
```

(stop with `mvn liberty:stop` from same directory)

Browse to [http://localhost:9080/exercisedb/](http://localhost:9080/exercisedb/)
