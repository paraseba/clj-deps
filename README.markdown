clj-deps
========

`clj-deps` assists you in understanding the structure and dependencies of
clojure code. It can generate a dependency graph of clojure namespaces and,
optionally, write it to a file in `.dot` format. Then, using
[Graphviz tools](http://www.graphviz.org/) you can turn that file into a
(sometimes) nice image of your dependency graph.

You can filter parts of the graph if you are interested only in
some namespaces of your system or its dependencies.

Usage
=====

* You may want Maven 2 installed in your system
* Install clj-deps cloning the repository: `git clone git://github.com/paraseba/clj-deps.git`
* Run `mvn install`
* There are some useful goals:
    * `mvn test` will run all tests.
    * `mvn clojure:repl` will give you a REPL with CLASSPATH set.
* From there you can do `(use 'clj-deps)` and call the functions you need.
* If you plan to use clj-deps in your project, you can add it in your pom.xml dependencies
  section, and let Maven install it for you. To do this, add to your pom.xml file:

         <repositories>
           <repository>
             <id>ar.com.grantaire</id>
             <url>http://maven.grantaire.com.ar</url>
           </repository>
         </repositories>

         <dependencies>
           <dependency>
             <groupId>ar.com.grantaire</groupId>
             <artifactId>clj-deps</artifactId>
             <version>1.0-SNAPSHOT</version>
           </dependency>
         </dependencies>



Example
========

This call

     (save-graph (dir-dep-graph "clj-deps/src") "clj-deps.dot")

will generate a file named `clj-deps.dot` with clj-deps dependency graph.
If then you do:

     dot -Tpng clj-deps.dot -o clj-deps.png

you turn that .dot file into this image (click to enlarge):

<a href="http://cloud.github.com/downloads/paraseba/clj-deps/clj_deps_simple.png" title="clj-deps dependency graph">
  <img width="900" src="http://cloud.github.com/downloads/paraseba/clj-deps/clj_deps_simple.png"/> 
</a>

Using other functions you can turn that in a nicer image like the following:

<a href="http://cloud.github.com/downloads/paraseba/clj-deps/clj_deps.png" title="clj-deps dependency graph">
  <img width="900" src="http://cloud.github.com/downloads/paraseba/clj-deps/clj_deps.png"/>
</a>

see the
[wiki](http://wiki.github.com/paraseba/clj-deps) for more examples and usage information.


ToDo
====

* Document
* Command line interface
* Automatic image generation

Since we have a graph, we can do pretty interesting stuff like:

* Cycle detection
* Topological order
* What not.


Inspiration
===========

This is my first clojure project and my first clojure code lines. It was fun to code and it helped
me discover the clojure API and some contrib libraries. It was inspired by an image on the [cascade project](http://github.com/hlship/cascade).

