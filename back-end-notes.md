# locally running the back-end

```
mvn clean package; # compiles, tests, makes jar;
mvn exec:exec; # runs: java -jar target/app-server-1.0.0.jar;
```

## Compile and run:

```
mvn clean install package exec:exec;
```

# running the containerised back-end

```
docker build -t full-app .;
docker run --publish 8080:8080 full-app; # publishes exposed container ports locally (host:container)
```

# executing commands within the container

```
docker container ls; # get CONTAINER ID
docker exec -it $container_id bash;
```

# launching post requests

```
lsof -i -P -n | grep LISTEN; # list open ports
```

```
curl -d "test" -X POST http://localhost:8080/test
```

## launching post requests with Postman

```
POST: http://localhost:8080/api/test
Body text/JSON: "{}"
```
