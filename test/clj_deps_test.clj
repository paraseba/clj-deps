(ns test.clj-deps-test
  (:import java.io.File)
  (:use (clojure test))
  (:require
     [clj-deps :as m]))

(def source-tree-dir (File. (.. (File. *file*) (getParent)) "resources"))

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

