<h1 align="center">Orrish Automation</h1>
<p align="center">
    <a href="https://github.com/Orrish-Automation/orrish-core/actions/workflows/UnitTest.yml">
      <img alt="Build Status" src="https://github.com/Orrish-Automation/orrish-core/actions/workflows/UnitTest.yml/badge.svg?branch=main" />
    </a>
    <a href="https://codecov.io/gh/Orrish-Automation/orrish-core">
      <img alt="Unit Test Coverage" src="https://codecov.io/gh/Orrish-Automation/orrish-core/branch/main/graph/badge.svg" />
    </a>
    :zap:
    <a href="https://github.com/Orrish-Automation/orrish-core/actions/workflows/EndToEndTest.yml">
      <img alt="End To End Tests" src="https://github.com/Orrish-Automation/orrish-core/actions/workflows/EndToEndTest.yml/badge.svg?branch=integration" />
    </a>
    <a href="https://codecov.io/gh/Orrish-Automation/orrish-core">
      <img alt="End-to-end Test Coverage" src="https://codecov.io/gh/Orrish-Automation/orrish-core/branch/integration/graph/badge.svg" />
    </a>
</p>
<h2 align="center">Web based end-to-end automation and collaboration for the whole team.</h2>

For most open source automation tools, we have to set up our local machine before we can write/execute a test. We need to know build tools/coding. Let's consider challenges of unit test based automation framework. 

| Install & build framework | Automation adoption | Version Control | Organize test | Documentation / collaboration |
| --- | --- |  --- | --- | --- |
| Version, access, dependency, compile, build issues. | Creates silos. Build/coding for automation is barrier to entry. | Well-versed including Git conflict resolution? | Organizing tests in class/BDD/xml files not effective. Review takes time. | Issues with sharing information, progress tracking, reviewing with non-coders. |

##### :trophy: Solution : Write and execute tests similar to Unit test/BDD but on a web page (FitNesse) accessible to all without any local setup.
Below is the illustration. More examples are available when you install it and launch the FitNesse wiki.

![Unit Test To FitNesse](https://github.com/Orrish-Automation/orrish-core/blob/main/UnitTestToFitNesseTestCase.png?raw=true)

:bulb: If you do not want to transition to using FitNesse, you can use this repo as dependency to Java based frameworks (TestNG/JUnit/BDD) to leverage core automation logic. Please refer to [examples](https://github.com/Orrish-Automation/examples) project in this GitHub organization.

## :handshake: Automation engineer(SDET) can help whole team contribute to automation

<details>
<summary>Automation is effective when dedicated automation engineers take care of core framework while product experts write and execute automated tests. Expand to learn more</summary>
<br>For successful test automation, it is important that product experts (QA, business users, non-coders, customer support etc.) are able to write, manage and execute tests without setting up their machines. This will allow automation engineers to spend time on automation framework and other framework development activities.

| QA / Business users / Non-coder | SDET / Automation Engineer | 
| --- | --- |
| Product expert, understand product features well. | Implement good coding principles/design patterns, R&D on evolving tech stack. Extend/maintain automation framework code - good coding knowledge needed. |
| Clarify, document, write and execute automated tests, analyze failures | Train team members to write/execute their own automated tests, train in analyzing test failure root cause. |  
| Exploratory test and manual execution of not automated tests. |  Setup and maintain automation infrastructure and CI integration - good scripting/DevOps skill needed. |
| Manage test data and test environment with help from various other teams. | Develop tools for task automation, process automation, data generation, data cleanup, mock servers etc. |
</details>

## :building_construction: Architecture
* It uses [FitNesse](http://fitnesse.org) which runs as a wiki server, so tests are accessed/added/executed via web. Hence, no need for QA/BA to set up anything on local machine.
* To run automated tests, FitNesse drives the code in this repo which is fed to it as a fat jar file.
* This is a Java based framework using open source libraries like `rest-assured` for API, `JDBC` for relational database, `mongodb-driver-sync` for MongoDB, `Appium` (mobile), `Selenium`, `Playwright` (web), `extentreports` for reporting etc.
* It also has page showing the available steps (mapped to methods) that can be referred while adding/editing test.

## :briefcase: Features
* Can be installed via docker or manually on a central server.
* Supports API, Relational database, MongoDB, Web and Mobile steps. It is extensible and pull requests with more libraries are welcome.
* Mix and match steps to create desired executable tests.
* By default, it generates [Extent Report](https://github.com/extent-framework/extentreports-java). It can also be configured to publish report to [Report Portal](https://reportportal.io/).

Below is comparison table of unit test framework vs FitNesse

| Feature | FitNesse | Unit Test | Comment |
|   ---   |  :---:   |   :---:   |   ---   |
| No local machine setup | :white_check_mark: | :x: |  Access via FitNesse web eliminates individual setup. |
| Codeless test writing | :white_check_mark: | :x: | |
| Keyword and data driven tests | :white_check_mark:  | :ballot_box_with_check: | FitNesse [data driven](http://fitnesse.org/FitNesse.UserGuide.WritingAcceptanceTests.SliM.DecisionTable) and [keyword driven](http://fitnesse.org/FitNesse.UserGuide.WritingAcceptanceTests.SliM.ScriptTable) are easier. |
| Use as documentation and collaboration tool |:white_check_mark: | :x: | FitNesse is a collaboration wiki + automation tool for non-coders.
| Reporting |:white_check_mark: | :white_check_mark: |
| CI Integration | :white_check_mark: | :white_check_mark: | FitNesse runs from terminal or via Jenkins plugin. |
| Organize test easily | :white_check_mark: |:ballot_box_with_check: | Folder like FitNesse tests are easier to manage than unit test class/xml/feature files. |
| Easy test readability | :white_check_mark: | :ballot_box_with_check: | FitNesse tests are more readable for non-coders. |
| Easy version control | :white_check_mark: | :ballot_box_with_check: | Real time FitNesse tests access via wiki server, no VCS conflict. |

## :hammer_and_wrench: Setting up the framework on a central server

<details>
<summary><b>With docker:</b></summary>

- The easiest way to set up is via docker with below command. Access the automation server on the port you specified in below command.

  `docker run -p <your_desired_port>:80 suratdas/orrish-core:1.0.3`
</details>

<details>
<summary><b>Manual Setup:</b></summary>

If you don't have docker installed or if you want to set up manually, follow below process
  - Clone/download this repo.
  - Run `pushJarAndStartFitnesse.sh` :green_book: If the port 80 is already in use or if you want to start manually, refer the help section.
</details>
  
## :book: FitNesse specific tasks

<details>
<summary>Expand to see suggested collaboration steps in regards to FitNesse.</summary>

| QA / Business users | SDET / Automation Engineer | 
| --- | --- |
| Be aware of the methods available through FitNesse web UI. Using those methods, write executable test cases in a folder like structure in FitNesse. | Setup automation server for all teams. Set up multiple servers based on needs. Train QA member to write FitNesse tests. |
| For web/mobile tests, learn to find DOM locator to be included in the test case. | Periodic commit of test cases in version control. Check help section for details. |
| Execute tests, analyze report. | Modify code based on team specific requirement. Check help section for details. |
| Use this tool as documentation and collaboration. | Integrate tests in continuous integration environment (There is Jenkins plugin for FitNesse). |
</details>

## :information_source: Help

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

## :framed_picture: Screenshots
#### Test execution along with documentation
![Test Case Execution](https://github.com/Orrish-Automation/orrish-core/blob/main/TestCase.png?raw=true)
#### Test report
![Test Report](https://github.com/Orrish-Automation/orrish-core/blob/main/TestReport.png?raw=true)
#### Available steps
![Available Steps](https://github.com/Orrish-Automation/orrish-core/blob/main/AvailableSteps.png?raw=true)
