(ns run-tests
  (:use [clojure.test :only (run-tests)]))

(def test-namespaces ['clj-deps.graph-test
                      'clj-deps.dot-test
                      'clj-deps.deps-test
                      'clj-deps.clj-deps-test])

(apply require test-namespaces)

(defn main []
  (println "Running tests...")
  (apply run-tests test-namespaces))

(main)
