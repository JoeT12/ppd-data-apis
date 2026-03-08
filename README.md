# ppd-data-apis

## Overview
This repository contains a Spring Boot application for performing CRUD operations on UK Price Property Data (PPD) via various REST API endpoints. Created by Joseph Taylor as part of COMP-3011 Coursework
1 at the University of Leeds. This application is currently hosted on Microsoft Azure at the endpoint: https://ppd-data-api-app--0000002.jollywater-c3f94696.italynorth.azurecontainerapps.io.

## Local Setup Instructions:
To get the project running locally, complete the following steps:
1. Install Java 25 and add it to the JAVA_HOME environment variable on your system path.
2. Install gradle.
3. Use the command "./gradlew bootRun" in this directory to run the application, this will start the server on localhost port 8080.
4. Study the API documentation, and then use an API client such as POSTMAN to query the applications endpoints.

NOTE: Due to the size of PPD data sets, we have been unable to upload a database into the repository. An empty database will be created on application start-up by
Spring Boot - however the user must manually upload a yearly PPD data via the admin upload endpoint (/api/v1/write/admin/upload-ppd-year). Such datasets can be found
at the following link: https://www.gov.uk/government/statistical-data-sets/price-paid-data-downloads.

## Admin Credentials
To be able to access the admin endpoints, please use the following credentials to log into the application:
- username: admin@example.com
- Password: admin

## API Documentation:
For the API documentation, please navigate to the API-Documentation/ directory. Within this, there are 3 files:
1. COMP3011-CWK1.postman_collection.json: The exported POSTMAN collection for the application which can be imported into POSTMAN for quick setup.
2. API Documentation.pdf: The ChatGPT generated API documentation, based of my manually created POSTMAN collection.
Alternatively, all API documentation has been published to the following endpoint, for easy access: https://documenter.getpostman.com/view/51986844/2sBXcGEzyf#f608d97f-0cca-4186-9bd5-c31bc407337f.

## AI Acknowledgments:
To enable full visibility as to where AI has been used to create this application, I have employed the following techniques:
1. Adding a comment at the top of all code files where AI has been used.
2. Adding an AI_ACKNOWLEDGMENT.md in the same directory as that code with the chat exports.
Any areas in which these aren't present would mean the code was written by me.

