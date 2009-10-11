(ns clojure-dependencies.dot
  (:use
     (clojure.contrib [graph :only (get-neighbors)]
                      [str-utils2 :only (join)])))

(defn edge-repr [from to]
  (str "\"" from "\" -> \"" to "\""))

(defn- adj-list [graph]
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


