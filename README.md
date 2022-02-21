<p align="center">
    <a href="https://github.com/Orrish-Automation/orrish-core/actions/workflows/workflow.yml">
      <img alt="Build Status" src="https://github.com/Orrish-Automation/orrish-core/actions/workflows/workflow.yml/badge.svg" />
    </a>
    <a href="https://codecov.io/gh/Orrish-Automation/orrish-core">
      <img alt="Code Coverage" src="https://codecov.io/gh/Orrish-Automation/orrish-core/branch/main/graph/badge.svg" />
    </a>
  </p>

## Why another automation tool?
With currently available open source automation tools, we have to set up our computer and have some coding knowledge before we are ready to write/execute a test. Consider below challenges of Java based unit test framework. 

| Install & build framework | Automation adoption | Version Control System | Organize test | Documentation / collaboration |
| --- | --- |  --- | --- | --- |
| Version or access issues. Compile errors. | Cannot use automation if you cannot build/code. Barrier to entry creating silos. | Well versed including Git conflict resolution? | Challenge in organizing tests in various files (Java or BDD). | Issues with sharing information, automation progress tracking, reviewing with non-coders. |

##### :gem: Solution : Write and execute tests similar to TestNG/JUnit/BDD but on a web page (FitNesse) accessible to all without any local setup.  
![Unit Test To FitNesse](https://github.com/Orrish-Automation/orrish-core/blob/main/UnitTestToFitNesseTestCase.png?raw=true)

:bulb: If you do not want to transition to using FitNesse, you can use this repo as dependency to Java based frameworks (TestNG/JUnit/BDD) to leverage core automation logic. Please refer to [examples](https://github.com/Orrish-Automation/examples) project in this GitHub organization.

## QA vis-à-vis automation engineer(SDET)

<details>
<summary>Automation is effective when dedicated automation engineers take care of core framework while QA writes and executes automated tests. Expand to learn more</summary>
<br>To implement successful test automation, it is important that product experts (QA, Business users) are able to write, manage and execute automated tests themselves without setting up their machines. An automation engineer will spend more time on framework and other development activities.

| QA / Business users | SDET / Automation Engineer | 
| --- | --- |
| Product expert, understand product features well | Implement good coding principles/design patterns, R&D on evolving automation tech stack. Extend and maintain automation framework code - good coding knowledge needed. |
| Clarify, document, write and execute automated tests, analyze failures | Train team members to write/execute their own automated tests, train in analyzing test failure root cause. |  
| Exploratory test and manual execution of not automated tests |  Setup and maintain automation infrastructure and CI integration - good scripting/DevOps skill needed. |
| Manage test data and test environment with help from various other teams. | Develop tools for task automation, process automation, data generation, data cleanup, mock servers etc. |
</details>

## Architecture
* It uses [FitNesse](http://fitnesse.org) to write, manage and execute test cases. FitNesse runs as a wiki server, so tests are accessed via web and there is no need for QA/BA to install anything on their machines. 
* It is a Java based framework using open source libraries like `rest-assured` for API, `JDBC` for relational database, `mongodb-driver-sync` for MongoDB, `Appium` (mobile), `Selenium`, `Playwright` (web), `extentreports` for reporting etc.
* To run automated tests, FitNesse drives the code in this repo which is fed to it as a fat jar file.

## Features

* Can be installed via docker or manually on a central server.
* Supports API, Relational database, MongoDB, Web and Mobile steps.
* Mix and match steps to create desired executable tests.
* By default, it generates [Extent Report](https://github.com/extent-framework/extentreports-java). It can also be configured to publish report to [Report Portal](https://reportportal.io/).

Below is comparison table of unit test framework vs FitNesse

| Feature | FitNesse | Unit Test | Comment |
|   ---   |  :---:   |   :---:   |   ---   |
| Need to setup individual machine | :tada: | :o: |  Access via FitNesse web eliminates individual setup |
| Possible to write/execute test without coding knowledge | :white_check_mark: | :x: | Basic coding, assertion knowledge is required for unit tests. | 
| Keyword driven and data driven tests | :white_check_mark:  | :dart: | It is much easier to achieve in FitNesse even by non-coders. See FitNesse [data driven](http://fitnesse.org/FitNesse.UserGuide.WritingAcceptanceTests.SliM.DecisionTable) and [keyword driven](http://fitnesse.org/FitNesse.UserGuide.WritingAcceptanceTests.SliM.ScriptTable). |
| Use as documentation and collaboration tool |:white_check_mark: | :x: |
| Reporting |:white_check_mark: | :white_check_mark: |
| CI Integration | :white_check_mark: | :white_check_mark: | FitNesse Has Jenkins plugin. |
| Locate and organize test easily | :white_check_mark: |:dart: | FitNesse tests are organized like folder which is much easier to manage than unit test classes/xml. |
| Easy test readability | :white_check_mark: | :dart: | FitNesse tests will be more readable for all. Unit tests are difficult to read by non-coders. |

## Setting up the framework on a central server

<details>
<summary><b>With docker:</b></summary>

- The easiest way to set up is via docker with below command. Access the automation server on the port you specified in below command.

  `docker run -p <your_desired_port>:80 suratdas/orrish-core:1.0.3`
</details>

<details>
<summary><b>Manual Setup:</b></summary>

If you don't have docker installed or if you want to setup manually, follow below process
  - Clone/download this repo.
  - Run `pushJarAndStartFitnesse.sh` If the port 80 is already in use or if you want to start manually, refer the help section.
</details>
  
## FitNesse specific tasks

<details>
<summary>Expand to see QA and automaton engineer collaboration steps in regards to FitNesse.</summary>

| QA / Business users | SDET / Automation Engineer | 
| --- | --- |
| Be aware of the methods available through FitNesse web UI. Using those methods, write executable test cases in a folder like structure in FitNesse. | Setup automation server for all teams. Set up multiple servers based on needs. Train QA member to write FitNesse tests. |
| For web/mobile tests, learn to find DOM locator to be included in the test case. | Periodic commit of test cases in version control. Check help section for details. |
| Execute tests, analyze report. | Modify code based on team specific requirement. Check help section for details. |
| Use this tool as documentation and collaboration. | Integrate tests in continuous integration environment (There is Jenkins plugin for FitNesse). |
</details>

## Help

<details>
<summary>Expand for help and troubleshoot</summary>

* All tests will be stored as plain text file under FitNesseRoot folder. If you use docker to run automation server, you may want to use `docker cp <container_id>:/app/FitNesseRoot .` command to transfer tests from the container to your host machine. Commit them periodically to your version control system.
* If existing methods are not sufficient, feel free to raise a pull request. You can also modify code, create a jar file with command `mvn compile assembly:single`, rename the jar to `orrish-core.jar` and place it under `target` folder. If using docker container, use command `docker cp target/*.jar <container_id>:/app/target` 
* Update code and start FitNesse manually : 
    - Download [FitNesse Jar](http://fitnesse.org) and place it in the location where you cloned this repo.
    - Create a fat jar from this repo with the command `mvn compile assembly:single`. Rename the created jar to orrish-core.jar and move it under ```target``` folder in the cloned location.
    - Run command `java -jar fitnesse-standalone.jar -p <desired_port>` and access the server on the port you specified in this command.
* Add new area in available steps : Navigate to FitNesseRoot/files/all-steps.json and add the areas here. Remember to update steps described here. 
* Update steps in available steps : In FitNesse homepage, you will find a link to update/add steps. 
</details>

## Screenshots
#### Test Case Execution Along With Documentation
![Test Case Execution](https://github.com/Orrish-Automation/orrish-core/blob/main/TestCase.png?raw=true)
#### Test Report
![Test Report](https://github.com/Orrish-Automation/orrish-core/blob/main/TestReport.png?raw=true)
#### Available Steps
![Available Steps](https://github.com/Orrish-Automation/orrish-core/blob/main/AvailableSteps.png?raw=true)
