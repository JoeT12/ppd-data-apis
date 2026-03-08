# ppd-data-apis

## Overview
This repository contains a Spring Boot application for performing CRUD operations on UK Price Property Data (PPD) via various REST API endpoints. Created by Joseph Taylor as part of COMP-3011 Coursework
1 at the University of Leeds.

## Local Setup Instructions:
To get the project running locally, the only requirement is that the user has Java 25 installed and has added this to their JAVA_HOME environment variable in their system path. 
Once completed, the user can then simply use the command "./gradlew bootRun" to run the application. This should start the Spring application on port 8080. The user may then use
POSTMAN or any other API client of their

## Deployed Endpoint
Alternatively, this application has been deployed on Azure at the endpoint: https://ppd-data-api-app--0000002.jollywater-c3f94696.italynorth.azurecontainerapps.io.

## API Documentation:
For the API documentation, please navigate to the API-DOCUMENTATION/ directory. Within this, there are 3 files:
1. COMP3011-CWK1.postman_collection.json: The exported POSTMAN collection for the application which can be imported into POSTMAN for quick setup.
2. API Documentation.pdf: The ChatGPT generated API documentation, based of my manually created POSTMAN collection.
Alternatively, all API documentation has been published to the following endpoint, for easy access: https://documenter.getpostman.com/view/51986844/2sBXcGEzyf#f608d97f-0cca-4186-9bd5-c31bc407337f.

## AI Acknowledgments:
To enable full visibility as to where AI has been used to create this application, I have employed the following techniques:
1. Adding a comment at the top of all code files where AI has been used.
2. Adding an AI_ACKNOWLEDGMENT.md in the same directory as that code with the chat exports.
Any areas in which these aren't present would mean the code was written by me.

