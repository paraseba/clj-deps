(ns clj-deps.clj-deps-test
  (:import java.io.File)
  (:use clojure.test [clj-deps.dot :only (graph-to-dot)]
        [clojure.contrib.java-utils :only (file)])
  (:require
     [clj-deps :as m]
     [clj-deps.graph-test :as gt]))

; Do I really have to do all this to get a File pointing to resources dir?
; *file* is a relative path, but not relative in the same sense the File class works
; File uses current working directory to resolve relative paths
(def this-file (File. (.getFile (ClassLoader/getSystemResource *file*))))
(def this-dir  (.getParent this-file))
(def source-tree-dir (file this-dir ".." ".." "resources"))
(def source-tree-dir-name (.getPath source-tree-dir))


(deftest dir-dependency-map
  (is (= {'a '(b dir1.a)
          'b '(dir2.a dir2.b)
          'dir1.a '(dir1.b)
          'dir1.b '()
          'dir2.a '()
          'dir2.b '(dir1.b)}
         (m/dir-dependency-map source-tree-dir))))



(def dep-graph (m/dir-dependency-graph source-tree-dir))

(deftest dir-dependency-graph
  (gt/graph-has-edges dep-graph
    'a 'b
    'a 'dir1.a
    'b 'dir2.a
    'b 'dir2.b
    'dir1.a 'dir1.b
    'dir2.b 'dir1.b)

  (deftest filters

    (gt/graph-has-edges (m/dir-dependency-graph source-tree-dir :only (constantly true))
      'a 'b
      'a 'dir1.a
      'b 'dir2.a
      'b 'dir2.b
      'dir1.a 'dir1.b
      'dir2.b 'dir1.b)

    (gt/graph-has-edges (m/dir-dependency-graph source-tree-dir :only #(re-find #"^(a|b)|(dir1\..*)$" (name %)))
      'a 'b
      'a 'dir1.a
      'dir1.a 'dir1.b)

    (gt/graph-has-edges (m/dir-dependency-graph source-tree-dir :only #(re-find #"^(a|b)|(dir1\..*)$" (name %)))
      'a 'b
      'a 'dir1.a
      'dir1.a 'dir1.b)

    (gt/graph-has-edges (m/dir-dependency-graph source-tree-dir :except #(re-find #"dir" (name %)))
      'a 'b)

    (gt/graph-has-edges (m/dir-dependency-graph source-tree-dir :only-matching #"dir")
      'dir1.a 'dir1.b
      'dir2.b 'dir1.b)

    (gt/graph-has-edges (m/dir-dependency-graph source-tree-dir :except-matching #"dir2")
      'a 'b
      'a 'dir1.a
      'dir1.a 'dir1.b)

    (deftest combined-filters
      (gt/graph-has-edges (m/dir-dependency-graph source-tree-dir :only-matching #"dir" :except-matching #"dir2")
      'dir1.a 'dir1.b))))



(deftest write-dependency-graph
  (let [file (File/createTempFile "clj-deps-test" "dot")]
    (.deleteOnExit file)
    (m/write-dependency-graph source-tree-dir-name file)
    (is (= (graph-to-dot dep-graph) (slurp (.getPath file)))))

  (deftest accepts-filter-parameters
    (let [file (File/createTempFile "clj-deps-filter-test" "dot")
          graph (m/dir-dependency-graph source-tree-dir :only-matching #"dir" :except-matching #"dir2")]
      (.deleteOnExit file)
      (m/write-dependency-graph source-tree-dir-name file :only-matching #"dir" :except-matching #"dir2")
      (is (= (graph-to-dot graph) (slurp (.getPath file)))))))
