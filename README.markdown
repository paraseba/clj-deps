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

* You need Maven 2 installed in your system
* Install `clj-deps` cloning the repository: `git clone git://github.com/paraseba/clj-deps.git`
* Run `mvn install`
* There are some useful goals:
    * `mvn clojure:test` will run all tests.
    * `mvn clojure:repl` will give you a REPL with CLASSPATH set.
* From there you can do `(use 'clj-deps)` and call the functions you need.


Example
========

This call

     (write-dependency-graph "clj-deps/src" "clj-deps.dot")

will generate a file named `clj-deps.dot` with `clj-deps` dependency graph.
If then you do:

     dot -Tpng clj-deps.dot -o clj-deps.png

you turn that `.dot` file into this image (click to enlarge):

<a href="http://cloud.github.com/downloads/paraseba/clj-deps/clj-deps.png" title="clj-deps dependency graph">
  <img src="http://cloud.github.com/downloads/paraseba/clj-deps/clj-deps-thumb.png" style="width=100%;border:1px solid silver;"/>
</a>


ToDo
====

* Document
* Add to Maven repo
* Namespace translation
* Custom node attributes
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

