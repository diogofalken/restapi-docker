*** Settings ***
Library  RequestsLibrary
Library  Collections
Library  log.py
Library  DatabaseLibrary

Suite Setup  Start Test   
Suite Teardown  End Test
Test Teardown  logTestCase  ${TEST NAME}  ${TEST STATUS}

*** Variables ***
${BASE_URL}=  http://localhost:5000/
${DBHost}=  localhost
${DBName}=  restapi
${DBUser}=  root
${DBPass}=  root
${DBPort}=  3306
${testID}=  1000

*** Keywords ***
Start Test 
    Create Session  api  ${BASE_URL}
    startLog
    Connect To Database    pymysql    ${DBName}    ${DBUser}    ${DBPass}    ${DBHost}    ${DBPort}

End Test
    stopLog
    Delete users DB  TC002_Nome1
    Delete users DB  TC002_Nome2
    Disconnect From Database

Create user
    [Documentation]  Used to create a fake entry in the db in order to leave the server clean
    Post Request  api  user/${testID}

Delete users DB
    [Documentation]  Delete the users created in this test
    [Arguments]  ${name}
    Execute Sql String    DELETE FROM users WHERE name="${name}";

*** Test Cases ***
TC_001_POST_USER
    [Documentation]  Testing the endpoint POST /user that sends a JSONArray 
    ...              with users info to be added to mysql db
    [Tags]    POST
    
    @{users}    Create List
    &{headers}=  Create Dictionary  Content-Type=application/json
    &{data}=  Create Dictionary  name=TC002_Nome1    birthDate=24-09-1999    city=Coimbra
    Append to List    ${users}    ${data}
    &{data}=  Create Dictionary  name=TC002_Nome2    birthDate=1999-09-24    city=Viseu
    Append to List    ${users}    ${data}
    ${response}=  Post Request  api  user  data=${users}  headers=${headers}
    Should Be Equal As Strings  ${response.status_code}  200
    ${response_data}=  To Json  ${response.content}
    Dictionary Should Contain Value  ${response_data}  Were added 2 users.
    # Verify in mysql DB
    Check If Exists In Database    SELECT id FROM users WHERE name="TC002_Nome1";
    Check If Exists In Database    SELECT id FROM users WHERE name="TC002_Nome2";


TC_002_POST_USER_TEST_BIRTHDATE
    [Documentation]  Testing the endpoint POST /user that sends a JSONArray 
    ...              with users info to be added to mysql db
    ...              Testing if it returns a 400 in case date is not in the correct format
    ...              YYYY-MM-DD or DD-MM-YYYY
    [Tags]    POST
    
    @{users}    Create List
    &{headers}=  Create Dictionary  Content-Type=application/json
    &{data}=  Create Dictionary  name=testeBirthdate    birthDate=24/1999-1999    city=Coimbra
    Append to List    ${users}    ${data}
    ${response}=  Post Request  api  user  data=${users}  headers=${headers}
    Should Be Equal As Strings  ${response.status_code}  400
    ${response_data}=  To Json  ${response.content}
    Dictionary Should Contain Value  ${response_data}  Params don't follow the rules
    # Verify on MySql DB
    Check If Not Exists In Database    SELECT id FROM users WHERE name="testeBirthdate";

TC_003_POST_USER_EMPTY_ARRAY
    [Documentation]  Testing the endpoint POST /user that sends a JSONArray 
    ...              with users info to be added to mysql db
    ...              Testing if it returns a 400 in case date the JSONArray is empty
    ...              []
    [Tags]    POST
    
    @{users}    Create List
    &{headers}=  Create Dictionary  Content-Type=application/json
    ${response}=  Post Request  api  user  data=${users}  headers=${headers}
    Should Be Equal As Strings  ${response.status_code}  400
    ${response_data}=  To Json  ${response.content}
    Dictionary Should Contain Value  ${response_data}  User array is empty

TC_004_POST_USER_WRONG_CONTENT-Type
    [Documentation]  Testing the endpoint POST /user that sends a JSONArray 
    ...              with users info to be added to mysql db
    ...              Testing if it returns a 400 in case Content type != application/json
    [Tags]    POST
    
    @{users}    Create List
    &{headers}=  Create Dictionary  Content-Type=application/x-www-form-urlencoded
    ${response}=  Post Request  api  user  data=${users}  headers=${headers}
    Should Be Equal As Strings  ${response.status_code}  400
    ${response_data}=  To Json  ${response.content}
    Dictionary Should Contain Value  ${response_data}  Only accepts content of application/json type

TC_005_GET_ALL_USER
    [Documentation]  Testing the endpoint GET /user that returns all users inside the db
    [Tags]    GET

    ${response}=  Get Request  api  user
    Should Be Equal As Strings  ${response.status_code}  200

TC_006_DELETE_USER
    [Documentation]  Testing the endpoint DELETE /user/:id that deletes the user with :id
    [Tags]    DELETE
    [Setup]   Create user

    ${response}=  Delete Request  api  user/${testID}
    Should Be Equal As Strings  ${response.status_code}  200
    ${response_data}=  To Json  ${response.content}
    Dictionary Should Contain Value  ${response_data}  User with id ${testID} deleted with success from DB.
    # Verify on MySql DB
    Check If Not Exists In Database    SELECT id FROM users WHERE id="${testID}";
    
TC_007_DELETE_USER_WRONG_ID
    [Documentation]  Testing the endpoint DELETE /user/:id that deletes the user with :id
    ...              but using a wrong ID
    [Tags]    DELETE

    ${response}=  Delete Request  api  user/50000
    Should Be Equal As Strings  ${response.status_code}  400
    ${response_data}=  To Json  ${response.content}
    Dictionary Should Contain Value  ${response_data}  ID doesn't exist on DB.
    Check If Not Exists In Database    SELECT id FROM users WHERE id="50000";

TC_008_GET_USER
    [Documentation]  Testing the endpoint GET /user/:id that returns the user with id=:id
    [Tags]    GET
    [Setup]    Create user

    ${response}=  Get Request  api  user/${testID}
    Should Be Equal As Strings  ${response.status_code}  200
    # Verify in MySql DB
    Check If Exists In Database    SELECT id FROM users WHERE id="${testID}";
    # Delete testID
    Delete Request  api  user/${testID}

TC_009_GET_USER_WRONG_ID
    [Documentation]  Testing the endpoint GET /user/:id with id that doesn't exist on db
    [Tags]    GET

    ${response}=  Get Request  api  user/50000
    Should Be Equal As Strings  ${response.status_code}  400
    ${response_data}=  To Json  ${response.content}
    Dictionary Should Contain Value  ${response_data}  ID doesn't exist on DB.
    # Verify in MySql DB
    Check If Not Exists In Database    SELECT id FROM users WHERE id="50000";

TC_010_PUT_USER
    [Documentation]  Testing the endpoint PUT /user/:id
    [Tags]    PUT
    [Setup]  Create user

    @{users}    Create List
    &{headers}=  Create Dictionary  Content-Type=application/json
    &{data}=  Create Dictionary  name=testePUT    birthDate=24-09-1999    city=Coimbra
    Append to List    ${users}    ${data}
    ${response}=  Put Request  api  user/${testID}  data=${users}  headers=${headers}
    Should Be Equal As Strings  ${response.status_code}  200
    ${response_data}=  To Json  ${response.content}
    Dictionary Should Contain Value  ${response_data}  User with id ${testID} was updated with success.
    # Verify on MySql DB
    Check If Exists In Database    SELECT id FROM users WHERE name="testePUT";
    # Delete testID
    Delete Request  api  user/${testID}

TC_011_PUT_USER_WRONG_ID
    [Documentation]  Testing the endpoint PUT /user/:id with a wrong ID
    [Tags]    PUT
    [Setup]  Create user

    @{users}    Create List
    &{headers}=  Create Dictionary  Content-Type=application/json
    &{data}=  Create Dictionary  name=testePUT    birthDate=24-09-1999    city=Coimbra
    Append to List    ${users}    ${data}
    ${response}=  Put Request  api  user/50000  data=${users}  headers=${headers}
    Should Be Equal As Strings  ${response.status_code}  400
    ${response_data}=  To Json  ${response.content}
    Dictionary Should Contain Value  ${response_data}  ID doesn't exist on DB.
    # Verify on MySql DB
    Check If Not Exists In Database    SELECT id FROM users WHERE id=50000;