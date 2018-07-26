# Engine Team Coding Challenge

## 1. Briefing
Please find below a coding problem we'd like you to solve. There is no “right” or “wrong” answer; rather we want to see how you code. So, make
your code talk! You should submit your solution in a way that makes it easy for us to use. So, we expect to see a build and/or a run script. Please
use English as the primary language in your solution to enable us to verify the behaviour. You can code in whichever language you want, but we
would prefer Java, Scala or Python.

### 1.1 What are we looking for?
We want to see you can create production-quality code. This means naming conventions, coding style, sensible design and meaningful
commenting. If you feel you can give your work to a brand new colleague with a minimal of hand-over, you've probably got it right. To verify your
code, we expect to see some tests. We realize that writing even a trivial application, and having it "production ready" is a lot of work - so it's okay
to leave "ToDo" comments in your code. Show us the intent, but don't write too much boilerplate. Show that you're coding something that is
maintainable. You can use any library of public repositories.

### 1.2 What are we not looking for?
We don't expect you to use every design pattern you've ever heard of - only apply patterns when it makes sense to do so. We don't expect you to
build an user interface. We're not expecting you to have optimized the solution for performance or memory size - readability is more important
than performance. We'd like to see what you'd do under normal conditions - therefore, if you run out of time, just say so.

## 2. Task description
Imagine we have an existing system for our employees to submit booking requests for meetings for our single meeting room. However, this
system is not checking whether the meeting room is available at the requested time.
We want you to implement a new system for processing batches of booking requests to create a timetable for the meeting room.
We want you to create a simple micro service which will accept HTTP requests with the text mentioned in section 2.1 Input as request body and it
should return a text like mentioned in 2.2 Output. The endpoint should be reachable under localhost:8080/timetable-creation. You can use any
library you want to build the service.

### 2.1 Input
Your processing service gets the following input in text form (Content-Type: text/plain) from a HTTP request.
The first line of the input text represents the company office hours, in 24 hour clock format
The remainder of the input represents individual booking requests. Each booking request is in the following format (no empty lines):
* <request submission time, in the format YYYY-MM-DD HH:MM:SS> <String:employee id>
* <meeting start time, in the format YYYY-MM-DD HH:MM> <String:meeting duration in hours>

A sample text input your endpoint accepts:

```0900 1730
2018-05-17 10:17:06 EMP001
2018-05-21 09:00 2
2018-05-16 12:34:56 EMP002
2018-05-21 09:00 2
2018-05-16 09:28:23 EMP003
2018-05-22 14:00 2
2018-05-17 11:23:45 EMP004
2018-05-22 16:00 1
2018-05-15 17:29:12 EMP005
2018-05-21 16:00 3
2018-05-30 17:29:12 EMP006
2018-05-21 10:00 3
```

### 2.2 Output
Your system must provide a successful booking calendar as output, with bookings being grouped by day and sorted by starting time . For the
sample input displayed above, your system must provide the following output and status code 200 if succeeded.
```2018-05-21
09:00 11:00 EMP002
2018-05-22
14:00 16:00 EMP003
16:00 17:00 EMP004
```

### 2.3 Constraints
No part of a meeting may fall outside office hours.
Meetings may not overlap (E.g. the request of EMP002 and EMP006 overlap)
The booking submission system only allows one submission at a time, so submission times are guaranteed to be unique.
in case of conflicting meetings priority must be given to the one submitted earlier
The ordering of booking submissions in the supplied input is not guaranteed. (E.g. EMP002 submitted his request before EMP001, but in
the input they are shuffled)

### 2.4 Notes
The current requirements make no provision for alerting users of failed bookings; it is up to the user to confirm that their booking was
successful.
You can assume syntactically correct inputs.

## 3. Submission
Please note that your code should work on an fresh installation of a UNIX-like OS (e.g. ubuntu or macOS) with Java JDK 8, Python 2, Python 3
and their common build tools like maven, sbt, gradle or pip preinstalled.
Please provide us a link to a repository or an archive.
Let us know how long you spent on it.
**IMPORTANT** to be included in your submission:
Add build.sh which sets up your project (e.g. setup python environment)
Add run.sh which takes care of running your micro service on localhost:8080