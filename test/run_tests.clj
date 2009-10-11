(ns test.run-tests
  (:use [clojure.test :only (run-tests)]))

(load "clojure_dependencies/graph_test" "clojure_dependencies/dot_test")

(defn main []
  (println "Running tests...")
  (run-tests 'test.clojure-dependencies.graph-test
             'test.clojure-dependencies.dot-test))

(main)
