# Jenkins-Diffing-plugin

## 1. Introduction 


### 1.1 Introduction

Jenkins is a common continuous integration server that allows developers to continuously release the latest version of the software to users with ease. Among the greatest problems developers encounter during this process is not being able to view the changes between each build in order to gain better understanding of what’s going, and thus can decide what strategies to take. For our project we aimed to rectify this problem with a plugin. Our plugin, Diff Build Plugin, is a product that enables the comparison of the Jenkins console outputs, POM dependencies, source codes, and Maven phases among different builds.

### 1.2 Features

Our plugin provides the following features:

Line by Line Console Output Diff:
Line by Line Console Output Diff compares the Jenkins console output between two
selected builds, and identifies the lines that have been changed, modified, or
deleted, and displays them side by side.

Console Output Section Diff:
Console Output Section Diff breaks the console output it into 3 sections INFO,
WARNING and ERROR, and compares these sections. It allows developers to view
the changes among sections between 2 specified builds.

Maven Phase Diff:
Maven Phase Diff parses the console output, and extracts the information about
Maven phases, and compares the corresponding Maven phases.

Source Code Diff:
Source Code Diff compares the Java source code between two builds, and identifies
the files added, changed and removed.

POM Dependency Diff:
POM Dependency Diff extracts dependency information from the POM file,
compares the dependency information between two builds. From the differences
users can see added dependencies, removed dependencies and the changed
versions for existing dependencies.

Extend LogParserPlugin to support arbitrary tags:
The origin LogParserPlugin only parses out sections for INFO, ERROR, and
WARNING tags. We extend it to support sections for arbitrary tags, e.g. DEBUG.
As it is difficult to account for all possible tags and sometimes other information
should be parsed out, we added a contribution that allows the parsing of arbitrary
tags.

Extend LogParserPlugin to support basic information section:
We also extend LogParserPlugin to parse out basic information section from Jenkins
console output. Basic information includes JDK version, Maven version, build time,
build completion time and memory usage.

## 2. Architecture 

## 3. Design 

## 4. Usage


