(ns clojure-dependencies
  (:import java.io.File)
  (:use
     (clojure.contrib [find-namespaces :only (find-ns-decls-in-dir)]
                      [duck-streams :only (writer reader)])
     clojure-dependencies.graph clojure-dependencies.dot))



(defn is-dependency? [form]
  (and (list? form) (#{:require :use} (first form))))

(defn is-libspec? [spec]
  (or (symbol? spec) (vector? spec)))

(defn is-prefix-list? [spec]
  (list? spec))

(defn libspec-dependency [spec]
  (cond
    (symbol? spec) (str spec)
    (vector? spec) (str (first spec))
    :else "###spec error###"))

(defn prefix-list-dependencies [spec]
  (let [[prefix & libspecs] spec]
    (vec (map #(str prefix "." (libspec-dependency %)) libspecs))))


(defn spec-dependencies [spec]
  (cond
    (is-libspec? spec) [(libspec-dependency spec)]
    (is-prefix-list? spec) (prefix-list-dependencies spec)
    :else ['unknown]))


(defn extract-dependencies [form]
  (when-let [dep-type (and (is-dependency? form) (first form))]
    (mapcat spec-dependencies (next form))))


(defn process-ns [graph form]
  (let [[ns name & forms] form]
    (reduce #(apply add-fan %1 (str name) (extract-dependencies %2)) graph forms)))


(defn dir-dependencies
  [file]
  (reduce process-ns empty-graph (find-ns-decls-in-dir file)))

(defn write-dependency-graph 
  [sourcedir out]
  (with-open [out (writer out)]
    (let [graph (dir-dependencies (File. sourcedir))
          dot (graph-to-dot graph)]
      (. out write dot))))


