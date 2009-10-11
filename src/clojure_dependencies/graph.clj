(ns clojure-dependencies.graph
  (:use
     [clojure.set :only (union)]
     (clojure.contrib [graph :only (directed-graph)])))


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

