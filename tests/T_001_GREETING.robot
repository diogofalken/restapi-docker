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

Form data
    [Documentation]  Creates a dictionary with the ${info} passed as argument
    [Arguments]  ${info}
    &{data}=  Create Dictionary  message=${info}
    [Return]  ${data}

Form headers
    [Documentation]  Creates a dictionary with the headers needed for the requests
    &{headers}=  Create Dictionary  Content-Type=application/x-www-form-urlencoded
    [Return]  ${headers}

Create greeting 
    [Documentation]  Used to create a fake entry in the service in order to test the DELETE and PUT endpoints
    Post Request  api  greeting/${testID}

Delete greeting
    [Documentation]  Used to delete the fake entry in the service
    Delete Request  api  greeting/${testID}

*** Test Cases ***
TC_001_GET_ALL_GREETING
    [Documentation]  Testing the endpoint GET /greeting that returns all greetings inside the service
    [Tags]    GET

    ${response}=  Get Request  api  greeting
    Should Be Equal As Strings  ${response.status_code}  200


TC_002_GET_GREETING
    [Documentation]  Testing the endpoint GET /greeting/:id that returns the greeting with id=:id
    [Tags]    GET

    ${response}=  Get Request  api  greeting/${ID}
    Should Be Equal As Strings  ${response.status_code}  200

TC_003_POST_NEW_GREETING
    [Documentation]  Testing the endpoint POST /greeting that adds a new message to the service
    [Tags]    POST    

    &{data}=  Form data  teste
    &{headers}=  Form headers
    ${response}=  Post Request  api  greeting  data=${data}  headers=${headers}
    Should Be Equal As Strings  ${response.status_code}  200
    ${response_data}=  To Json  ${response.content}
    Dictionary Should Contain Value  ${response_data}  teste

TC_004_PUT_GREETING
    [Documentation]  Testing the endpoint PUT /greeting/:id that changes the message (sent as Form data) from the key with id=:id
    [Tags]    PUT
    [Setup]  Create greeting

    &{data}=  Form data  newMessage
    &{headers}=  Form headers
    ${response}=  Put Request  api  greeting/${testID}  data=${data}  headers=${headers}
    Should Be Equal As Strings  ${response.status_code}  200
    ${response_data}=  To Json  ${response.content}
    Dictionary Should Contain Value  ${response_data}  newMessage
    [Teardown]  Delete greeting

TC_005_DELETE_GREETING
    [Documentation]  Testing the endpoint DELETE /greeting/:id that deletes the message the key :id
    [Tags]    DELETE${info}
    [Setup]  Create greeting

    ${response}=  Delete Request  api  greeting/${testID}
    Should Be Equal As Strings  ${response.status_code}  200

TC_006_INVALID_ROUTE
    [Documentation]  Testing the a invalid endpoint
    [Tags]    GET
    [Setup]  Create greeting

    ${response}=  Get Request  api  xpto
    Should Be Equal As Strings  ${response.status_code}  400
    ${response_data}=  To Json  ${response.content}
    Dictionary Should Contain Value  ${response_data}  Invalid endpoint

TC_007_POST_INVALID_MESSAGE
    [Documentation]  Testing the a invalid message with POST should return a 400 status code
    [Tags]    POST
    [Setup]  Create greeting

    &{data}=  Form data  Olá
    &{headers}=  Form headers
    ${response}=  Post Request  api  greeting  data=${data}  headers=${headers}
    Should Be Equal As Strings  ${response.status_code}  400
    ${response_data}=  To Json  ${response.content}
    Dictionary Should Contain Value  ${response_data}  Message contains invalid chars

TC_008_PUT_INVALID_MESSAGE
    [Documentation]  Testing the a invalid message with PUT should return a 400 status code
    [Tags]    PUT
    [Setup]  Create greeting

    &{data}=  Form data  olá
    &{headers}=  Form headers
    ${response}=  Put Request  api  greeting/${testID}  data=${data}  headers=${headers}
    Should Be Equal As Strings  ${response.status_code}  400
    ${response_data}=  To Json  ${response.content}
    Dictionary Should Contain Value  ${response_data}  Message contains invalid chars