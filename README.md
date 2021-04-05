![README.md](dacdoc-resources/circle-grey-12px.png "checked on 2021-04-05T16&#058;45&#058;42.898")

[![build_status](https://travis-ci.org/flussig/dacdoc.svg?branch=master)](https://travis-ci.org/flussig/dacdoc) [![Maven Central](https://img.shields.io/maven-central/v/com.github.flussig/dacdoc-maven-plugin.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.flussig%22%20AND%20a:%22dacdoc-maven-plugin%22)

# What is DacDoc
TL;DR 

DacDoc is the approach to keep technical documentation resilient to future changes by treating it as a code: keeping it in git repo and providing tests to some parts that can change in future.

## Problems with documentation
Every technical project of certain complexity starts having problems with technical documentation. 
With an evolution of a project some of the elements of documentation are changing, documentation is getting outdated, and a future reader has no clue if certain statements stay true.

Worse, next person to update the documentation can feel uncertain what is outdated and can create another page that will largely copy already existing information.

If this situation sounds familiar, then you can give DacDoc a try.

## Improving documentation with DacDoc
DacDoc is a set of libraries and a maven plugin that allows placing tests for certain statements in the documentation. 
Its use cases are not limited by technical documentation, but are mainly aimed to technical community (developers, devops, admins) because it requires some technical knowledge.

Such, applying dacdoc maven plugin requires having **Java** (8 and higher) installed on your machine along with **maven** and **git**. 
For simplest use - cases such as checking correctness of links in your documentation - no specific code is required. For more specific use cases DacDoc allows for creation of user-defined tests in Java.

With this approach documentation exists in pre-compiled and compiled version. Pre-compiled version specifies fragments that need to be tested and tests that need to be run; compiled version has all tests applied and all test fail/pass indicators inserted. 