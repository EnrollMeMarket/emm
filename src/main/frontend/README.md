# Enroll Me Market #

Enroll Me Market is an extension to an existing application (Enroll-Me), which serves students to find an optimal 
classes timetable, based on their preferences. 

However, once you get your timetable, you might not be fully satisfied with it. But maybe there is someone you could 
swap your terms with, making both of you happy?

That is where we come in! A students' representative can download the timetables of a group of students as a CSV file 
from Enroll-Me and upload in to the Market. Then everyone can mark which classes they would like to _give away_ and 
which ones they could _get_ in exchange.

As soon as we find a matching pair of students, we perform an echange between them. A summary of successful swaps can 
then be downloaded by the administrator.

### What do I need to run it? ###

* git
* NodeJS: https://nodejs.org/en/
* npm ```npm install -g npm@latest```
* gulp: ```npm install -g gulp-cli```
* bower ```npm install -g bower```

### How to run it locally? ###

* Navigate to the if-enroll-market-front directory
* Type ```npm install``` and ```bower install```
* Launch Spring backend
* Type ```gulp serve```. The application should open in a new tab in your browser automatically. It's supposed to use localhost:3000.

### How to run it in production? ###

Use ```gulp build``` to use ```prod``` environment of ```gulp build:integ``` to use ```integ```. It will generate ```dist``` directory containing static files of the application. You can use any web server to run them (e.g. node or python built-in http.server).

Detailed environments configuration is provided in config.json (described below).

### config.json ###

This file serves as a configuration for environments: dev, integ and prod. You need to provide URLs of the server in this file before compilation.
