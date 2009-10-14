(ns test.clj-deps-test
  (:import java.io.File)
  (:use clojure.test [clj-deps.dot :only (graph-to-dot)])
  (:require
     [clj-deps :as m]
     [test.clj-deps.graph-test :as gt]))

(def source-tree-dir (File. (.. (File. *file*) (getParent)) "resources"))
(def source-tree-dir-name (.getPath source-tree-dir))

(def dep-map (m/dir-dependency-map source-tree-dir))

(deftest dir-dependency-map
  (is dep-map)
  (is (= {'test.resources.a '(test.resources.b test.resources.dir1.a)
          'test.resources.b '(test.resources.dir2.a test.resources.dir2.b)
          'test.resources.dir1.a '(test.resources.dir1.b)
          'test.resources.dir1.b '()
          'test.resources.dir2.a '()
          'test.resources.dir2.b '(test.resources.dir1.b)}
         dep-map)))



(def dep-graph (m/dir-dependency-graph source-tree-dir))

(deftest dir-dependency-graph
  (is dep-graph)
  (gt/graph-has-edges dep-graph
    'test.resources.a 'test.resources.b
    'test.resources.a 'test.resources.dir1.a
    'test.resources.b 'test.resources.dir2.a
    'test.resources.b 'test.resources.dir2.b
    'test.resources.dir1.a 'test.resources.dir1.b
    'test.resources.dir2.b 'test.resources.dir1.b))

(deftest write-dependency-graph
  (let [file (File/createTempFile "clj-deps-test" "dot")]
    (.deleteOnExit file)
    (m/write-dependency-graph source-tree-dir-name file)
    (is (= (graph-to-dot dep-graph) (slurp (.getPath file))))))
