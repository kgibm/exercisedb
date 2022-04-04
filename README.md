# exercisedb

## Development

Package and test the app:

```
mvn clean install
```

### Start PostgreSQL locally

```
podman run -it --rm --ulimit memlock=-1:-1 \
           --name postgres \
           -e POSTGRES_USER=dbuser1 \
           -e POSTGRES_PASSWORD=letmein \
           -e POSTGRES_DB=database1 \
           -p 5432:5432 \
           postgres
```

### Run in the foreground 

```
mvn clean liberty:run
```

(stop with Ctrl+C)

Browse to [http://localhost:9080/exercisedb/](http://localhost:9080/exercisedb/)

### Run in the background

```
mvn clean liberty:start
```

(stop with `mvn liberty:stop` from same directory)

Browse to [http://localhost:9080/exercisedb/](http://localhost:9080/exercisedb/)
