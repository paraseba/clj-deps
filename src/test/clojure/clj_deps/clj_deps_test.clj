(ns clj-deps.clj-deps-test
  (:import java.io.File)
  (:use clojure.test
        [clojure.contrib.java-utils :only (file)]
        [clj-deps.dot :only (graph-to-dot)]
        [clj-deps.deps :only (new-namesp)]
        [clj-deps.graph-test :only (graph-has-edges)])
  (:require [clj-deps :as m]))

; Do I really have to do all this to get a File pointing to resources dir?
; *file* is a relative path, but not relative in the same sense the File class works
; File uses current working directory to resolve relative paths
(def this-file (File. (.getFile (ClassLoader/getSystemResource *file*))))
(def this-dir  (.getParent this-file))
(def source-tree-dir (file this-dir ".." ".." "resources"))
(def source-tree-dir-name (.getPath source-tree-dir))


(defmacro has-deps
  [g & edges]
  `(graph-has-edges ~g ~@(map new-namesp edges)))



(deftest test-dir-dep-map
  (is (= {(new-namesp 'a) (list (new-namesp 'b) (new-namesp 'dir1.a))
          (new-namesp 'b) (list (new-namesp 'dir2.a) (new-namesp 'dir2.b))
          (new-namesp 'dir1.a) (list (new-namesp 'dir1.b))
          (new-namesp 'dir1.b) '()
          (new-namesp 'dir2.a) '()
          (new-namesp 'dir2.b) (list (new-namesp 'dir1.b))}
         (m/dir-dep-map source-tree-dir))))



(def dep-graph (m/dir-dep-graph source-tree-dir))

(deftest test-dir-dep-graph
  (has-deps dep-graph
    'a 'b
    'a 'dir1.a
    'b 'dir2.a
    'b 'dir2.b
    'dir1.a 'dir1.b
    'dir2.b 'dir1.b))

(deftest test-filter-dep-graph
  (has-deps (m/filter-dep-graph dep-graph :only (constantly true))
    'a 'b
    'a 'dir1.a
    'b 'dir2.a
    'b 'dir2.b
    'dir1.a 'dir1.b
    'dir2.b 'dir1.b)

  (has-deps (m/filter-dep-graph dep-graph :only #(re-find #"^(a|b)|(dir1\..*)$" (name (:sym %))))
    'a 'b
    'a 'dir1.a
    'dir1.a 'dir1.b)

  (has-deps (m/filter-dep-graph dep-graph :only #(re-find #"^(a|b)|(dir1\..*)$" (name (:sym %))))
    'a 'b
    'a 'dir1.a
    'dir1.a 'dir1.b)

  (has-deps (m/filter-dep-graph dep-graph :except #(re-find #"dir" (name (:sym %))))
    'a 'b)

  (has-deps (m/filter-dep-graph dep-graph :only-matching #"dir")
    'dir1.a 'dir1.b
    'dir2.b 'dir1.b)

  (has-deps (m/filter-dep-graph dep-graph :except-matching #"dir2")
    'a 'b
    'a 'dir1.a
    'dir1.a 'dir1.b)

  (deftest test-combined-filters
    (has-deps (m/filter-dep-graph dep-graph :only-matching #"dir" :except-matching #"dir2")
    'dir1.a 'dir1.b)))


(deftest test-write-dep-graph
  (let [file (File/createTempFile "clj-deps-test" "dot")]
    (.deleteOnExit file)
    (m/write-dep-graph dep-graph file)
    (is (= (graph-to-dot dep-graph) (slurp (.getPath file))))))

