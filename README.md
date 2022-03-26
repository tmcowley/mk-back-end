# mirrored-keyboard
Third-Year Project: The predictive mirrored keyboard

## About the Project

This project aims to design and implement a typing system suitable for one-handed use on a standard QWERTY keyboard. 

Our technique bisects the keyboard into an active and an inactive section: these can be the left and right keyboard halves. Each alphabetic character from the inactive half will be symmetrically mapped to the active half, according to hand symmetry. 

## About this component

This back-end component serves as the processing core of our web application, serving REST APIs that facilitate single-handed typing and communicate typing-related metrics. The key API is our `submit()` GET-API, which takes a sentence of left, right, or full-form, converts this to left-form, and then returns the predicted full-board sentence. We split APIs into GET (for state-independent operations), and POST (for state-dependent operations, such as sign-up and sign-in). 

The core logic underpinning our sentence processing can be found in `./src/main/kotlin/tmcowley/appserver/Logic.kt`. 

## Launching the back-end component

To test: <br />
```
./mvnw clean install test
```

To run: <br />
```
./mvnw clean install package exec:exec
```

## Technical Configuration 

### Back-end
- [Spring Boot](https://spring.io/projects/spring-boot) 2.5.6
- [Kotlin 1.6.10](https://kotlinlang.org/)
- JDK: [java 17.0.1](https://openjdk.java.net/projects/jdk/17/) 2021-10-19 LTS

### Front-end Configuration
- [React JS](https://reactjs.org/)

## Notices

Licensed under the [GNU General Public License v3.0](https://www.gnu.org/licenses/gpl-3.0.en.html). 
<br />
This allows commerical and personal use while preventing the distribution of closed source versions.