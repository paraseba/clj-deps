(ns clj-deps.clj-deps-test
  (:import java.io.File)
  (:use clojure.test
        [clojure.contrib.java-utils :only (file)]
        [clj-deps.dot :only (graph-to-dot)]
        [clj-deps.graph-test :only (graph-has-edges)])
  (:require [clj-deps :as m]))

; Do I really have to do all this to get a File pointing to resources dir?
; *file* is a relative path, but not relative in the same sense the File class works
; File uses current working directory to resolve relative paths
(def this-file (File. (.getFile (ClassLoader/getSystemResource *file*))))
(def this-dir  (.getParent this-file))
(def source-tree-dir (file this-dir ".." ".." "resources"))
(def source-tree-dir-name (.getPath source-tree-dir))



(deftest test-dir-dep-map
  (is (= {'a '(b dir1.a)
          'b '(dir2.a dir2.b)
          'dir1.a '(dir1.b)
          'dir1.b '()
          'dir2.a '()
          'dir2.b '(dir1.b)}
         (m/dir-dep-map source-tree-dir))))



(def dep-graph (m/dir-dep-graph source-tree-dir))

(deftest test-dir-dep-graph
  (graph-has-edges dep-graph
    'a 'b
    'a 'dir1.a
    'b 'dir2.a
    'b 'dir2.b
    'dir1.a 'dir1.b
    'dir2.b 'dir1.b))

(deftest test-filter-dep-graph
  (graph-has-edges (m/filter-dep-graph dep-graph :only (constantly true))
    'a 'b
    'a 'dir1.a
    'b 'dir2.a
    'b 'dir2.b
    'dir1.a 'dir1.b
    'dir2.b 'dir1.b)

  (graph-has-edges (m/filter-dep-graph dep-graph :only #(re-find #"^(a|b)|(dir1\..*)$" (name %)))
    'a 'b
    'a 'dir1.a
    'dir1.a 'dir1.b)

  (graph-has-edges (m/filter-dep-graph dep-graph :only #(re-find #"^(a|b)|(dir1\..*)$" (name %)))
    'a 'b
    'a 'dir1.a
    'dir1.a 'dir1.b)

  (graph-has-edges (m/filter-dep-graph dep-graph :except #(re-find #"dir" (name %)))
    'a 'b)

  (graph-has-edges (m/filter-dep-graph dep-graph :only-matching #"dir")
    'dir1.a 'dir1.b
    'dir2.b 'dir1.b)

  (graph-has-edges (m/filter-dep-graph dep-graph :except-matching #"dir2")
    'a 'b
    'a 'dir1.a
    'dir1.a 'dir1.b)

  (deftest test-combined-filters
    (graph-has-edges (m/filter-dep-graph dep-graph :only-matching #"dir" :except-matching #"dir2")
    'dir1.a 'dir1.b)))


(deftest test-save-graph
  (let [file (File/createTempFile "clj-deps-test" "dot")]
    (.deleteOnExit file)
    (m/save-graph dep-graph file)
    (is (= (graph-to-dot dep-graph) (slurp (.getPath file))))))

