## Requirements

- Docker
- Docker Compose
- Internet Connection!!!
- Linux ( Better to have ) `Tested only on Ubuntu 18.04`

## Run the project with docker

- Go back to the root folder and go to `docker/app` folder
- Then run
```
docker-compose up -d && docker-compose logs -f --tail=30 app | grep -Ev "(metrics|mongo)"
```

Please wait till the server is showing startup message
- After that you can navigate to http://localhost:8080 to see the application
- We have two users

```
1-
 username: admin
 password: admin
2- 
 username: user
 password: user
```

- Before we finish this section, we have sonar for analyzing Java code quality, to use it you need to go `docker/sonar` folder then run the command below:
- The sonar username/password is `admin/admin`
```
docker-compose up -d && docker-compose logs -f --tail=30 project-validation
```
If you saw it says `BUILD SUCCESS` so it is the time to go `http://localhost:9000` to see the code quality ( AAA )


## Description
You can do all CRUD operations on bank entity and card entity

- Saving cards has some limitations which I listed below:
```
- If you upload CSV with exist data, it will update them and creat those are not exist
```
- And saving/deleting bank entity can operate only by admin

- Admin can see all cards for all users included itself

## CSV Upload
To upload csv I created a sample file to know about the structure, it is located in following path `/app/src/main/resources/static/sample.csv`


## Debugging
To debug the code you need to go to commons.env and uncomment the line starts with #JVM_ARGUMENTS then you can connect to application with port 5005
