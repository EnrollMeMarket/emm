# Front-End #

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
