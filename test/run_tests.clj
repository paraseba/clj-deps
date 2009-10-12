(ns test.run-tests
  (:use [clojure.test :only (run-tests)]))

(def test-namespaces ['test.clojure-dependencies.graph-test
                      'test.clojure-dependencies.dot-test
                      'test.clojure-dependencies.deps-test])

(apply require test-namespaces)

(defn main []
  (println "Running tests...")
  (apply run-tests test-namespaces))

(main)
