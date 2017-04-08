# Fansite-Analytics

## Usage
To run the program with /log_input using script (The output will be in /log_ouput): 

    sh run.sh


To run the program with java and specific input/output arguments: 

    java -cp ./src/classes/ ProcessLog [Input Path] [Output Path for Host Feature] [Output Path for Resource Feature] [Output Path for Hour Feature] [Output Path for Blocked Feature] 


The java source files are inside the path /src, and the compiled class files are in the /src/classes. The command for compiling:

    javac -d ./src/classes  ./src/*.java



## Solution Introduction

Process the large log file and genrate the specific features.

### Languague
JAVA 1.8

### Features

#### Host
The top 10 most active hosts/IP addresses that have accessed the site.


#### Resource
The top 10 resources on the site that consume the most bandwidth.


#### Hour
The site’s 10 busiest (most frequently visited) 60-minute period.


#### Blocked
Detect failed login attempts over 20 seconds and log all further attempts to reach the site from the same IP address for the next 5 minutes.