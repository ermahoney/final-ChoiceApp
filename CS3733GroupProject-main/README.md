# CS3733GroupProject 
Author: Lauren Wasserman, Emily Mahoney, Jenna Galli, Marie Tessier
# Motivation
This is a webapp that allows users to create a choice for a team.  The team can than give feedback and vote on the alternatives for that choice.  This application is similar to when2meet.com, where team members can access the given choice using an ID and can like/dislike and give feedback.  There is an admin that can access all choices to delete them, either by selecting individually or by how many days old the choices are.
# Code Style
*  Note: API is in progress and this is just the initial API
## Schema
The Schema is used to store information persistently in Amazon RDS in MySQL.
## REST API
The GroovyAPI is the interface to that our system uses as a collection of HTTP requests.This API is a REST API that is defined in Swagger and is a YAML file. REST API is based on the Use Cases in our project.  The API contains GET and POST requests to the same URL. A response generates output, that is specified as a JSON encoded object.

## API
The API is coded with HTML, JavaScript and CSS.
YAML files are used to interact between the API and the Database.

# Notes on Running the Application 
- When initially deleting a choice, liking/disliking feedback, chosing an alternative, or any other actionable item interacting with the database, the user may need to wait a second to see the update. 

- When deleting a Choice in the Admin, only integers are accepted. 
- <b>When deleting a Choice in the Admin, the user may need to wait about five second and than refresh to see the update in the table.</b>

- The description for a Choice can be 300 characters long. The description for an Alternative can be 500 characters long. The name of a Member can be 36 characters long and the password for a Member can be 50 characters long. The content provided in a Feedback can be 100 characters long. 
# Links For Website

### Landing Page for User:
https://cs3733groupproject2020.s3.us-east-2.amazonaws.com/html/InitialOptions.html?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20201209T195214Z&X-Amz-SignedHeaders=host&X-Amz-Expires=604797&X-Amz-Credential=AKIAX4OIXFQA2RUXI5H6%2F20201209%2Fus-east-2%2Fs3%2Faws4_request&X-Amz-Signature=f522d047bd29609ced74948f1ce9e2d1146928ff2e07fcb073c66033f7abeb56


### Landing Page for Admin:

https://cs3733groupproject2020.s3.us-east-2.amazonaws.com/html/AdminPage.html?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20201209T195200Z&X-Amz-SignedHeaders=host&X-Amz-Expires=604796&X-Amz-Credential=AKIAX4OIXFQA2RUXI5H6%2F20201209%2Fus-east-2%2Fs3%2Faws4_request&X-Amz-Signature=7676e33f22d0c9782cd4257bbdb664c73b13d2c5d716c8bc8fd09f3744c5c3e3
