*** Settings ***
Library  RequestsLibrary
Library  Collections
Library  log.py

Suite Setup  Initiate Session   
Suite Teardown  stopLog
Test Teardown  logTestCase  ${TEST NAME}  ${TEST STATUS}

*** Variables ***
${BASE_URL}=  http://localhost:5000/
${ID}=  1
${testID}=  1000

*** Keywords ***
Initiate Session 
    Create Session  api  ${BASE_URL}
    startLog

Create user
    [Documentation]  Used to create a fake entry in the db in order to leave the server clean
    Post Request  api  user/${testID}

*** Test Cases ***
TC_001_POST_USER
    [Documentation]  Testing the endpoint POST /user that sends a JSONArray 
    ...              with users info to be added to mysql db
    [Tags]    POST
    
    @{users}    Create List
    &{headers}=  Create Dictionary  Content-Type=application/json
    &{data}=  Create Dictionary  name=Diogo    birthDate=24-09-1999    city=Coimbra
    Append to List    ${users}    ${data}
    &{data}=  Create Dictionary  name=Breno    birthDate=1999-09-24    city=Viseu
    Append to List    ${users}    ${data}
    ${response}=  Post Request  api  user  data=${users}  headers=${headers}
    Should Be Equal As Strings  ${response.status_code}  200
    ${response_data}=  To Json  ${response.content}
    Dictionary Should Contain Value  ${response_data}  Were added 2 users.

TC_002_POST_USER_TEST_BIRTHDATE
    [Documentation]  Testing the endpoint POST /user that sends a JSONArray 
    ...              with users info to be added to mysql db
    ...              Testing if it returns a 400 in case date is not in the correct format
    ...              YYYY-MM-DD or DD-MM-YYYY
    [Tags]    POST
    
    @{users}    Create List
    &{headers}=  Create Dictionary  Content-Type=application/json
    &{data}=  Create Dictionary  name=Diogo    birthDate=24/1999-1999    city=Coimbra
    Append to List    ${users}    ${data}
    ${response}=  Post Request  api  user  data=${users}  headers=${headers}
    Should Be Equal As Strings  ${response.status_code}  400
    ${response_data}=  To Json  ${response.content}
    Dictionary Should Contain Value  ${response_data}  Params don't follow the rules

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

TC_007_DELETE_USER_WRONG_ID
    [Documentation]  Testing the endpoint DELETE /user/:id that deletes the user with :id
    ...              but using a wrong ID
    [Tags]    DELETE

    ${response}=  Delete Request  api  user/50000
    Should Be Equal As Strings  ${response.status_code}  400
    ${response_data}=  To Json  ${response.content}
    Dictionary Should Contain Value  ${response_data}  ID doesn't exist on DB.

TC_008_GET_USER
    [Documentation]  Testing the endpoint GET /user/:id that returns the user with id=:id
    [Tags]    GET

    ${response}=  Get Request  api  user/${ID}
    Should Be Equal As Strings  ${response.status_code}  200

TC_009_GET_USER_WRONG_ID
    [Documentation]  Testing the endpoint GET /user/:id with id that doesn't exist on db
    [Tags]    GET

    ${response}=  Get Request  api  user/50000
    Should Be Equal As Strings  ${response.status_code}  400
    ${response_data}=  To Json  ${response.content}
    Dictionary Should Contain Value  ${response_data}  ID doesn't exist on DB.