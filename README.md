# WF Exam

> A Spring based web application that searches github public projects and does basic analytics activity based on the contributors 100 commits.

## Requirements.

- Maven 3+
- Java 1.8+
- Github client

## Setup

> Clone the application. `git clone https://github.com/mmagsino/wf-github-api.git`

## Run as development.

- Run a maven command `mvn clean spring-boot:run`
- Open browser and type `http:localhost:8080`

## Run as a jar file.

- Build the package `mvn clean package`.
- Build the package by skipping the integration test. ``
- Run on console `java -jar target/wf-github-api-0.0.1-SNAPSHOT.jar`.
- Open browser and type `http:localhost:8080`

## Features

[x] Search a public project from Github and return results.
[x] Auto complete search feature.
[x] Bookmarkable result pages.
[x] Exposed Rest API and [documentation](http://localhost:8080/swagger-ui.html).
[x] List of contributors for a given project.
[x] Basic analytics 
   - Impact of each user on a given project.
   - Projection of commits on a timeline.

<ul>
    <li>
       Contact: <sup>michael03.25magsino@gmail.com, mike2_magsino@yahoo.com</sup>
    </li>
</ul>