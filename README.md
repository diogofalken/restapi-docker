# restapi-docker

The idea behind this project was to write a API with java using Maven and make it run inside a docker container and with the help of JDBC make connection a mysql container.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

In order to run this app its needed

- Docker:

```bash
Ubuntu installation:

$ sudo apt-get update
$ sudo apt-get install docker-ce docker-ce-cli containerd.io
```

- Java (11+):

```bash
Ubuntu installation:

$ sudo apt-get update
$ sudo apt install openjdk-11-jdk
```

### Installing

Clone repository:

```bash
$ git clone https://github.com/diogofalken/restapi-docker.git
$ cd restapi-docker/
```

Start services:

```bash
$ sudo docker-compose up --build -d
```

_Note 1: MySql will listen on port 3306._  
_Note 2: restapi will listen on port 5000._

## API

### Status Codes

restapi-docker returns the following status codes in its API:

| Status Code | Description             |
| :---------- | :---------------------- |
| 200         | `OK`                    |
| 400         | `BAD REQUEST`           |
| 404         | `NOT FOUND`             |
| 500         | `INTERNAL SERVER ERROR` |

### **All greetings**

**Endpoint path:** `GET /greeting`  
**Returns (success):**

```json
[
  {
    "1": "Test Greeting"
  },
  {
    "2": "teste"
  },
  {
    "1000": "Greeting message for Robot Testing"
  }
]
```

### **One greeting**

**Endpoint path:** `GET /greeting/:id`  
**Returns (success):**

```json
{
  "1": "Test Greeting"
}
```

**Returns (error):**

```json
{
  "message": "No entry with the id x"
}
```

### **New greeting**

**Endpoint path:** `POST /greeting`  
**Body:**

- "message": string

**Returns (success):**

```json
{
  "message": "Test message"
}
```

**Returns (error):**

```json
{
  "message": "Message contains invalid chars"
}
```

### **Edit greeting**

**Endpoint path:** `PUT /greeting/:id`  
**Body:**

- "message": string

**Returns (success):**

```json
{
  "2": "New message"
}
```

**Returns (error):**

```json
{
  "message": "Message contains invalid chars"
}
```

### **Delete greeting**

**Endpoint path:** `DELETE /greeting/:id`  
**Returns (success):**

```json
{
  "message": "Deleted the message with id x"
}
```

**Returns (error):**

```json
{
  "message": "No entry with the id x"
}
```

### **All users**

**Endpoint path:** `GET /user`  
**Returns (success):**

```json
[
  {
    "city": "Coimbra",
    "name": "Diogo",
    "id": "1",
    "birthDate": "1999-09-24"
  },
  {
    "city": "Viseu",
    "name": "Breno",
    "id": "2",
    "birthDate": "1999-09-24"
  }
]
```

### **One user**

**Endpoint path:** `GET /user/:id`  
**Returns (success):**

```json
[
  {
    "city": "Coimbra",
    "name": "Diogo",
    "id": "1",
    "birthDate": "1999-09-24"
  }
]
```

**Returns (error):**

```json
{
  "message": "ID doesn't exist on DB."
}
```

### **New user**

**Endpoint path:** `POST /user`  
**Body:**

```json
[
  {
    "name": "Diogo",
    "birthDate": "24-09-1999",
    "city": "Coimbra"
  },
  {
    "name": "John",
    "birthDate": "24-09-2001",
    "city": "Viseu"
  }
]
```

**Returns (success):**

```json
{
  "message": "Were added 2 users."
}
```

**Returns (error):**

```json
{
  "message": "Error message"
}
```

### **Delete user**

**Endpoint path:** `DELETE /user/:id`  
**Returns (success):**

```json
{
  "message": "User with id x deleted with success from DB."
}
```

**Returns (error):**

```json
{
  "message": "ID doesn't exist on DB."
}
```

## Tests

For tests I use RobotFramework, and can be accessed in /tests directory and can be runned with:

```bash
$ robot T_001_GREETING
$ robot T_002_USER
```

```bash
==============================================================================
T 001 GREETING & T 002 USER
==============================================================================
T 001 GREETING & T 002 USER.T 001 GREETING
==============================================================================
TC_001_GET_ALL_GREETING :: Testing the endpoint GET /greeting that... | PASS |
------------------------------------------------------------------------------
TC_002_GET_GREETING :: Testing the endpoint GET /greeting/:id that... | PASS |
------------------------------------------------------------------------------
TC_003_POST_NEW_GREETING :: Testing the endpoint POST /greeting th... | PASS |
------------------------------------------------------------------------------
TC_004_PUT_GREETING :: Testing the endpoint PUT /greeting/:id that... | PASS |
------------------------------------------------------------------------------
TC_005_DELETE_GREETING :: Testing the endpoint DELETE /greeting/:i... | PASS |
------------------------------------------------------------------------------
TC_006_INVALID_ROUTE :: Testing the a invalid endpoint                | PASS |
------------------------------------------------------------------------------
TC_007_POST_INVALID_MESSAGE :: Testing the a invalid message with ... | PASS |
------------------------------------------------------------------------------
TC_008_PUT_INVALID_MESSAGE :: Testing the a invalid message with P... | PASS |
------------------------------------------------------------------------------
T 001 GREETING & T 002 USER.T 001 GREETING                            | PASS |
8 critical tests, 8 passed, 0 failed
8 tests total, 8 passed, 0 failed
==============================================================================
T 001 GREETING & T 002 USER.T 002 USER
==============================================================================
TC_001_POST_USER :: Testing the endpoint POST /user that sends a J... | PASS |
------------------------------------------------------------------------------
TC_002_POST_USER_TEST_BIRTHDATE :: Testing the endpoint POST /user... | PASS |
------------------------------------------------------------------------------
TC_003_POST_USER_EMPTY_ARRAY :: Testing the endpoint POST /user th... | PASS |
------------------------------------------------------------------------------
TC_004_POST_USER_WRONG_CONTENT-Type :: Testing the endpoint POST /... | PASS |
------------------------------------------------------------------------------
TC_005_GET_ALL_USER :: Testing the endpoint GET /user that returns... | PASS |
------------------------------------------------------------------------------
TC_006_DELETE_USER :: Testing the endpoint DELETE /user/:id that d... | PASS |
------------------------------------------------------------------------------
TC_007_DELETE_USER_WRONG_ID :: Testing the endpoint DELETE /user/:... | PASS |
------------------------------------------------------------------------------
TC_008_GET_USER :: Testing the endpoint GET /user/:id that returns... | PASS |
------------------------------------------------------------------------------
TC_009_GET_USER_WRONG_ID :: Testing the endpoint GET /user/:id wit... | PASS |
------------------------------------------------------------------------------
T 001 GREETING & T 002 USER.T 002 USER                                | PASS |
9 critical tests, 9 passed, 0 failed
9 tests total, 9 passed, 0 failed
==============================================================================
T 001 GREETING & T 002 USER                                           | PASS |
17 critical tests, 17 passed, 0 failed
17 tests total, 17 passed, 0 failed
==============================================================================
Output:  /home/diogo/Documents/docker/restapi-java-docker/tests/output.xml
Log:     /home/diogo/Documents/docker/restapi-java-docker/tests/log.html
Report:  /home/diogo/Documents/docker/restapi-java-docker/tests/report.html
```

## Built With

- [Spark](https://spark.apache.org/) - Used for api
- [Maven](https://maven.apache.org/) - Dependency Management
- [RobotFramework](https://robotframework.org/) - Framework used for tests
- [MySQL](https://www.mysql.com/) - Database management system

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

MIT © [Diogo Costa](http://diogomarques.tk/portfolio/)
