## Requirements

- Docker
- Docker Compose
- Internet Connection!!!
- Linux ( Better to have ) `Tested only on Ubuntu 18.04`

## Run the project with docker

- Go to `docker` folder
- Then run
```
docker-compose up -d
```
- Then we need to run the application by IDE or Gradle, for gradle run `./gradlew bootRun` and for IDE run `CreditCardApplication` class
- After that you can navigate to http://localhost:8080 to see the application (You should see not found error in json format)
- We have two functional users

```
1-
 username: admin
 password: admin
2- 
 username: user
 password: user
```

## Endpoints

##### Login (You need to copy the token from this endpoint and paste it to endpoints below)
```
curl -X POST \
  http://localhost:8080/api/account/authenticate \
  -H 'Content-Type: application/json' \
  -d '{
	"username": "admin",
	"password": "admin",
	"remember": true
      }'
```

##### Banks List
```
curl -X GET \
  http://localhost:8080/api/banks \
  -H 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImlkIjoidXNlci0xIiwiZXhwIjoxNTY0NjE3MzA3fQ.4zlkN2Imcaq6eE3-XKEghk-qU7bDNk5Ar4A34EzUs-JUI60een3PQndnc0UF2hctw2RW-1X8Ef_SDFBEVXGfmQ'
```

##### Upload CSV (Blocking)
```
curl -X POST \
  http://localhost:8080/api/cards/csv \
  -H 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImlkIjoidXNlci0xIiwiZXhwIjoxNTY0NjE3MzA3fQ.4zlkN2Imcaq6eE3-XKEghk-qU7bDNk5Ar4A34EzUs-JUI60een3PQndnc0UF2hctw2RW-1X8Ef_SDFBEVXGfmQ' \
  -H 'content-type: multipart/form-data' \
  -F file=@/opt/project/src/test/resources/static/sample-test.csv
```

##### Upload CSV (Streaming)
```
curl -X POST \
  http://localhost:8080/api/cards/csv \
  -H 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImlkIjoidXNlci0xIiwiZXhwIjoxNTY0NjE3MzA3fQ.4zlkN2Imcaq6eE3-XKEghk-qU7bDNk5Ar4A34EzUs-JUI60een3PQndnc0UF2hctw2RW-1X8Ef_SDFBEVXGfmQ' \
  -H 'content-type: multipart/form-data' \
  -H 'Accept: application/stream+json' \
  -F file=@/opt/project/src/test/resources/static/sample-test.csv
```

or

```
curl -X POST \
  http://localhost:8080/api/cards/csv \
  -H 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImlkIjoidXNlci0xIiwiZXhwIjoxNTY0NjE3MzA3fQ.4zlkN2Imcaq6eE3-XKEghk-qU7bDNk5Ar4A34EzUs-JUI60een3PQndnc0UF2hctw2RW-1X8Ef_SDFBEVXGfmQ' \
  -H 'content-type: multipart/form-data' \
  -H 'Accept: text/event-stream' \
  -F file=@/opt/project/src/test/resources/static/sample-test.csv
```

##### Cards List
```
curl -X GET \
  http://localhost:8080/api/cards \
  -H 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImlkIjoidXNlci0xIiwiZXhwIjoxNTY0NjE3MzA3fQ.4zlkN2Imcaq6eE3-XKEghk-qU7bDNk5Ar4A34EzUs-JUI60een3PQndnc0UF2hctw2RW-1X8Ef_SDFBEVXGfmQ'
```

##### For more endpoints you can check out the web.rest package (It follows all RestAPI methods such as: DELETE, GET, POST, PUT, ...)

## Description
You can do all CRUD operations on bank entity and card entity

- Saving cards has some limitations which I listed below:
```
If you upload CSV with exist data, it will update them and create those are not exist
```
- And saving/deleting bank entity can operate only by admin

- Admin can see all cards for all users included itself

## CSV Upload
To upload csv I created a sample file to know about the structure, it is located in following path `/app/src/main/resources/static/sample.csv`
