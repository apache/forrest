The project is written in Scala and I'm currently using Simple Build Tool (SBT) to build/test things.  
My goal has been two-fold - redesign forrest and learn functional programming/message-passing 
concurrency/scala/etc.  As such, it's likely not idiomatic Scala but will hopefully be so as I learn.

sbt is a ridiculously simple tool that can be found here:

http://code.google.com/p/simple-build-tool/wiki/Setup

Following those directions, I basically have ~/bin directory where I added 
a new script 'sbt' with the following content:

#!/bin/sh
java -Xmx512M -jar  `dirname $0`/sbt-launch-0.7.2.jar "$@"

Then, added that to my path, obviously with the jar file in the same directory.

In the root project directory (hint: this directory) type 'sbt' which will get 
you in the sbt interactive shell.  Key tasks are 'compile' and 'run' - mostly, you'll 
just type 'run' at the prompt, which will recompile if necessary and launch 
the f9 interactive shell.

Everything is really rudimentary at this point - I'm trying to carve out some basics
first with a focus on simplicity for the user, then, once happy with that, we can build
in functionality.  So far, once in the f9 shell, you can:

init - to initialize project
build - to transform sources and build static files in the output dir. (well, eventually)
run - to run a webapp at 8080
stop - to stop the webapp at 8080
clean - to clean the output dir

There aren't currently any dependencies between tasks, so you need to, for example, make sure 
you build before you run.

To keep things simple in development, I've been just creating the f9 project right in the same directory.  So, 
I'll do something like:

sbt
> run
f9> project doesn't exist, create? (y)

I then copy the {forrest-seed-sample}/../xdocs/* into ./sources/docs
