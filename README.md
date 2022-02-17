## Why another automation tool?
With currently available open source automation tools, we have to setup our computer and learn a few things before we are ready to write a test. 

Let's take example of Java based selenium test writing. We need to install (have admin rights?) Java (which Java version?), maven on our computer. Next, we have to know a unit test or BDD framework well enough. Once we are ready with tests, we have to know a version control (ever faced git conflict resolution?) to push the tests. We also have to organize tests, get them in sync with manual tests in various files (Java or BDD). These tests are visible and executable only to people who know coding creating barrier to automation adoption.

These aspects can be taken care by a handful automation engineers, and the rest of the team should write automated test without going through above steps. Read on to learn more...

## Orrish Automation Principles
In order to be successful in automation, it is important that product experts (QA, Business users) are able to write, manage and execute automated tests themselves without spending time setting up their machines for automation.
###### QA
QA is the product expert spending more time in
* Understanding the product features
* Clarifying, documenting, writing and executing automated tests, and analyzing the failures.
* Exploratory test and manual execution of not automated tests.
* Test data and test environment management with help from various other teams.
###### Automation Engineer
SDET (Software development engineer in test) or automation engineers spend a lot of time coding, scripting and DevOps skills. They perform below tasks 
* Implement good coding principles and design patterns, R&D on the evolving automation tech stack.
* Setup automation server centrally.
* Train QA members to write/execute their own automated tests, train in analyzing test failure root cause.
* Extend automation if required - good coding knowledge needed.
* Maintain automation infrastructure and CI integration - good DevOps skill needed.
* Create tools for task automation, process automation, data generation, data cleanup, mock servers etc.

## Features of this tool
* Can be installed via docker or manually on a central server.
* No need to setup individual machine and no prior coding knowledge is needed to write/execute tests.
* Supports API, Relational database, MongoDB, Web and Mobile steps.
* Supports keyword driven and data driven tests. See examples at [data driven](http://fitnesse.org/FitNesse.UserGuide.WritingAcceptanceTests.SliM.DecisionTable) and [keyword driven](http://fitnesse.org/FitNesse.UserGuide.WritingAcceptanceTests.SliM.ScriptTable) in FitNesse website.
* Mix and match steps to create desired executable tests.
* Because it is a wiki server, use this tool as documentation for all.
* By default, it generates [Extent Report](https://github.com/extent-framework/extentreports-java). It can also be configured to publish report to [Report Portal](https://reportportal.io/).
* It can be run from Jenkins via Jenkins FitNesse plugin.

## Architecture
* It uses [FitNesse](http://fitnesse.org) to write, manage and execute test cases. Because FitNesse runs as a wiki server, there is no need for QA/BA to install anything on their machines. 
* It is a Java framework using open source libraries like rest-assured (API), JDBC for relational database, Mongo driver for MongoDB, Appium, Selenium, Playwright etc.
* The code that FitNesse uses to run automated tests is in this repository. This code is fed into FitNesse as a fat jar file.

## Setting up the framework on a central server
* The easiest way to set up is via docker with below command. Access the automation server on the port you specified in below command.

  ```docker run -p <your_desired_port>:80 suratdas/orrish-core:1.0.1```
* It is also possible to build components individually. If you plan to modify code and build on your own, follow below process
  - Clone/download this repo.
  - Download [FitNesse Jar](http://fitnesse.org) and place it in the location where you cloned this repo.
  - Run command ```java -jar fitnesse-standalone.jar -p <desired_port>``` and access the server on the port you specified in this command. This will create a FitNesseRoot folder.
  - Create a fat jar from this repo with the command ```mvn compile assembly:single```. Rename the created jar to orrish-core.jar and move it under FitNesseRoot/target folder in above step.

## A typical workflow with this tool
#### QA/BA
* Learn FitNesse and be aware of the methods available. Using those methods, create executable test cases in a folder like structure.
* If you use web tests, you may want to know how to find DOM locator to be included in the test case.
* Execute tests, analyze report.
* Use this tool as documentation and collaboration.
#### Automation engineer
* Maintain automation server set up centrally.
* All tests written by QA members will be stored as plain text file in folder like structure. If you use docker to run automation server, you may want to use ```docker cp <container_id>:/app/FitNesseRoot .``` command to transfer tests from the container to your host machine and commit them periodically to your version control system.
* If existing methods are not sufficient, feel free to raise a pull request. You can also modify code to be fed to automation server. You create a jar file with command ```mvn compile assembly:single``` and place it in the container using command ```docker cp target/*.jar <container_id>:/app/target```
* Integrate tests in continuous integration environment (There is Jenkins plugin for FitNesse).
* You may want to set up multiple servers based on needs. 

## Screenshots
![Test Case Execution](https://github.com/Orrish-Automation/orrish-core/blob/main/TestCase.png?raw=true)
![Test Report](https://github.com/Orrish-Automation/orrish-core/blob/main/TestReport.png?raw=true)
