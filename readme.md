#Enroll Me Market

### Requirements
To run the app you'll need maven version 3 or higher.

### How to run the application

```mvn spring-boot:run```

Or you can make .jar file:
mvn package
And run it by typing:
java -jar target/Market-0.0.2.jar

### How to run server release in Docker (branch releaseWithSecurity)

To enable oAuth login, you need to specify our credentials in cielnt_id.txt and client_secret.txt.

Then run normally: ```mvn spring-boot:run```

Remember to proxy front requests to localhost:8080 properly (more in front repo's readme)