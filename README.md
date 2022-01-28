## Orrish Automation Principles
There is a distinction between the roles of QA and automation engineer. In order to be successful in automation, it is important that product experts (QA, Business users) should be able to write, manage and execute automated tests themselves without spending time setting up their laptops.
###### QA
QA is the product expert spending more time in
* Understanding the product features
* Clarifying and writing test cases, execute them and analyzing the failures.
* Test data and test environment management with help from various other teams.
###### Automation Engineer
Automation engineers spend a lot of time coding, scripting and DevOps skills. They perform below tasks 
* Upskill best coding principles, research and development on the evolving tech stack related automation.
* Setup automation centrally.
* Learn FitNesse features and train each team QA members to write/execute their own tests from central server.
* Extend automation if required - good coding knowledge needed.
* Maintain automation infrastructure - good DevOps skill needed.

## How does this tool help in realizing above principles
* No need to set up own machine.
* Supports API, Relational database, MongoDB, Web and Mobile steps.
* No need to have coding knowledge to write/execute tests.
* Supports keyword driven and data driven tests. See examples at [data driven](http://fitnesse.org/FitNesse.UserGuide.WritingAcceptanceTests.SliM.DecisionTable) and [keyword driven](http://fitnesse.org/FitNesse.UserGuide.WritingAcceptanceTests.SliM.ScriptTable) in FitNesse website.
* Available steps can be accessed by helper tool and can be assembled to create executable tests as we like.

## Architecture
* It uses [FitNesse](http://fitnesse.org) to write, manage and execute test cases. Because FitNesse runs as a wiki server, there is no need for QA/BA to install anything on their machines. The code behind that drives automation is in this repository. This code is fed into FitNesse as jar file. This framework generates [Extent Report](https://github.com/extent-framework/extentreports-java). It can also publish report to [Report Portal](https://reportportal.io/). It can be run from Jenkins via Jenkins FitNesse plugin.
* It is a Java framework using open source libraries like rest-assured (API), JDBC for relational database, Mongo driver for MongoDB, Appium, Selenium, Playwright
* By default, it generates extent report, but can be configured to send results to report portal as well.

## Setting up the framework on a central server
* The easiest way to set up is via docker and access server on the port you specified in below command.
```docker run -p <your_desired_port>:80 suratdas/orrish-core:1.0.0```
* It is also possible to build components individually. This is required if you plan to modify code and build on your own. Follow below process
 - Build from source by giving command ```mvn compile assembly:single```
 - Download [FitNesse Jar](http://fitnesse.org/fitnesse-standalone.jar?responder=releaseDownload&release=20211030)
 - Run commnd ```java -jar fitnesse-standalone.jar -p <desired_port>``` and access the server on the port you specified in this command.

## FAQ
* Where are the test case located?
    * The tests are stored as text based wiki files which can be saved separately. It is recommended to store it in version control.
* Tests are executed on the server, how do we synchronize test run by multiple members?
    * It is possible to setup multiple servers (one per each team). Also, it can be run via Jenkins job with queued job.
* Can it integrate with cloud provider like Sauce Labs, Perfecto etc.?
    * Yes, it is able to connect to those via desired capability definitions.
* I want to use a different framework for reporting, can I integrate it?
    * You may add/customize the code and create your own jar to support framework of your choice. 
