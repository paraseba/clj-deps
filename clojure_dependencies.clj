(ns clojure-graph
  (:import java.io.File)
  (:use
     [clojure.set :only (union)]
     (clojure.contrib [find-namespaces :only (find-ns-decls-in-dir)]
                      [graph :only (get-neighbors directed-graph)]
                      [str-utils2 :only (join)])))


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

(defn edge-repr [from to]
  (str "\"" from "\" -> \"" to "\""))

(defn adj-list [graph]
  (let [node-adj-list (fn [node]
                       (map vector (repeat node) (get-neighbors graph node)))]
    (mapcat node-adj-list (:nodes graph))))

(defn dot-graph-edges [graph]
  (let [al (adj-list graph)]
    (join "\n" (map #(apply edge-repr %) al))))

(defn graph-to-dot [graph]
  (str
    "digraph G {\n"
    (dot-graph-edges graph)
    "\n}\n"))


