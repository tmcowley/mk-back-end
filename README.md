# mirrored-keyboard

[![Java CI with Maven](https://github.com/tmcowley/mk-back-end/actions/workflows/maven.yml/badge.svg?branch=main)](https://github.com/tmcowley/mk-back-end/actions/workflows/maven.yml)

Third-Year Project: The predictive mirrored keyboard

## About the Project

This project aims to design and implement a typing system suitable for one-handed use on a standard QWERTY keyboard.

Our technique bisects the keyboard into an active and an inactive section: these can be the left and right keyboard
halves. Each alphabetic character from the inactive half will be symmetrically mapped to the active half, according to
hand symmetry.

## About this component

This back-end component serves as the processing core of our web application, hosting our business logic and serving REST APIs that facilitate
single-handed typing and communicate typing-related metrics. The key API is our `submit()` GET-API, which takes a
sentence of left, right, or full-form, converts this to left-form, and then returns an ordered list of viable intended sentences.
We split APIs into GET, and POST, with the latter being used for authenticated requests (such as sign-up and sign-in).

The core logic underpinning our sentence processing can be found in `./src/main/kotlin/tmcowley/appserver/Logic.kt`.

## Launching the back-end component

To test: <br />

```
mvn clean test
```

To run: <br />

```
mvn clean package exec:exec
```

## Technical Configuration

### Back-end

- [Spring Boot](https://spring.io/projects/spring-boot) 2.5.6
- [Kotlin](https://kotlinlang.org/) 1.6.10
- JDK: [java 17.0.1](https://openjdk.java.net/projects/jdk/17/) 2021-10-19 LTS

### Front-end

- [TypeScript](https://www.typescriptlang.org/)
- [React JS](https://reactjs.org/)

## Notices

Licensed under the [GNU General Public License v3.0](https://www.gnu.org/licenses/gpl-3.0.en.html).
<br />
This allows commercial and personal use while preventing the distribution of closed source versions.
