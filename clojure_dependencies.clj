(ns clojure-graph
  (:import java.io.File)
  (:use clojure.contrib.find-namespaces))

(defn is-dependency? [form]
  (and (list? form) (#{:require :use} (first form))))

(defn is-libspec? [spec]
  (or (symbol? spec) (vector? spec)))

(defn is-prefix-list? [spec]
  (list? spec))

(defn libspec-dependency [spec]
  (cond
    (symbol? spec) spec
    (vector? spec) (first spec)
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
    {dep-type (mapcat spec-dependencies (next form))}))


(defn process-ns [form]
  (let [[ns name & forms] form]
    {name (apply merge-with conj {} (map extract-dependencies forms))}))


(defn dir-dependencies
  [file]
  (map process-ns (find-ns-decls-in-dir file)))

