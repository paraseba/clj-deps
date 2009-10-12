(ns test.run-tests
  (:use [clojure.test :only (run-tests)]))

(def test-namespaces ['test.clj-deps.graph-test
                      'test.clj-deps.dot-test
                      'test.clj-deps.deps-test
                      'test.clj-deps-test])

(apply require test-namespaces)

(defn main []
  (println "Running tests...")
  (apply run-tests test-namespaces))

(main)
