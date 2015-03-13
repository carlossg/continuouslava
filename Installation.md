# Requirements #

You really must read the Pragmatic Automation article [Bubble, Bubble, Build's In Trouble](http://www.pragmaticautomation.com/cgi-bin/pragauto.cgi/Monitor/Devices/BubbleBubbleBuildsInTrouble.rdoc)

You will need also the [Pragmatic Automation X10 software](http://www.pragmaticautomation.com/code/pragautox10-1_0.zip) referenced in the article. Note that it's distributed under a [restricted license](PragmaticAutomationX10License.md).
Download and extract the zip file and copy its `src` folder into `src/main/java` and delete `com/pragauto/X10Publisher.java`


# Building #

Run Maven

`mvn clean package`

to build the application.


# Setup #

1) Copy `javax.comm:comm` and `org.rxtx:rxtxcomm` jars from the Maven repo to

`JRE_PATH\lib\ext` or `JDK_PATH\jre\lib\ext`


2) Copy `src/main/resources/javax.comm.properties` to

`JRE_PATH\lib` or `JDK_PATH\jre\lib`


3) Get `rxtxSerial.dll` (or equivalent for Linux or OSX) from [RXTX](http://rxtx.qbang.org/wiki/index.php/Download). Tested with rxtx-2.0-7pre1-i386-pc-mingw32. Copy it to

`JRE_PATH\bin` or `JDK_PATH\jre\bin`

# Running with Continuum #

```
java eu.carlossanchez.continuouslava.continuum.ContinuumMonitor Continuum_URL [COMPort] [SuccessDevice] [FailureDevice] [startingAtHour] [endingAtHour]
  Continuum_URL : url of the Continuum server, usually http://host:port/continuum/xmlrpc
  COMPort: COM1 by default
  SuccessDevice: X10 Code of device to turn on on success, by default A1
  FailureDevice: X10 Code of device to turn on on failure, by default A2
  startingAtHour: hour to start turning on the lamps, by default 8
  endingAtHour: hour to turn off both the lamps, by default 20
```


# Running with CruiseControl #

```
java eu.carlossanchez.continuouslava.cruisecontrol.CruiseControlMonitor CCTray_feed_url [COMPort] [SuccessDevice] [FailureDevice] [startingAtHour] [endingAtHour]
  CCTray_feed_url : url of the CCTray feed, usually http://cruisecontrol_machine/dashboard/cctray.xml
  COMPort: COM1 by default
  SuccessDevice: X10 Code of device to turn on on success, by default A1
  FailureDevice: X10 Code of device to turn on on failure, by default A2
  startingAtHour: hour to start turning on the lamps, by default 8
  endingAtHour: hour to turn off both the lamps, by default 20
```