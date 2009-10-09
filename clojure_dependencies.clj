(ns clojure-graph
  (:import java.io.File)
  (:use [clojure.set :only (union)])
  (:use (clojure.contrib find-namespaces graph)))


(defn new-graph [nodes neighbors] (struct directed-graph nodes neighbors))

(def empty-graph (new-graph #{} {}))

(defn add-edge [graph from to]
  (let [nodes (:nodes graph)
        neighbors (:neighbors graph)]
    (new-graph
      (conj nodes from to)
      (merge-with union neighbors {from #{to}}))))

(defn add-fan [graph from & to]
  (reduce #(add-edge %1 from %2) graph to))



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


(defn fan-repr [g node]
  (apply str (map #(str "\"" node "\" -> \"" % "\"\n") (get-neighbors g node))))

(defn graph-pairs [graph]
  (apply str (map (partial fan-repr graph) (:nodes graph))))

(defn graph-to-dot [graph]
  (str
    "digraph G {\n"
    (graph-pairs graph)
    "}\n"
    ))


