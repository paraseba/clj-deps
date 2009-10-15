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

* Install `clj-deps`, it depends on `clojure` and `clojure-contrib`.
* In the `bin` directory you will find an executable you can use to set the
  `CLASSPATH`
* Launch the REPL and call the functions you need.

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

* Use Maven
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

