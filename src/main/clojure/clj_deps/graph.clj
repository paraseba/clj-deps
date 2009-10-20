(ns clj-deps.graph
  (:import clojure.lang.ILookup)
  (:use
     [clojure.set :only (union)]
     (clojure.contrib [def :only (defstruct-)])))

(defstruct- graph :node-data :topology)

(defn- adapt-to-contrib-graph
  [graph]
  (letfn [(dispatch [key, default]
            (condp contains? key
              #{:nodes} (keys (:node-data graph))
              #{:neighbors :topology} (:topology graph)
              #{:node-data} (:node-data graph)
              default))]
    (proxy [ILookup] []
      (valAt
        ([key] (dispatch key, nil))
        ([key, default] (dispatch key, default))))))


(defn new-graph
  ([] (new-graph nil))
  ([node-data] (new-graph node-data {}))
  ([node-data topology] (adapt-to-contrib-graph (struct graph node-data topology))))

(def empty-graph (new-graph))

(defn set-node-data
  [{:keys (node-data topology) as :graph} node-id data]
  (new-graph (assoc node-data node-id data) topology))

(defn add-node-data
  [{:keys (node-data topology) as :graph} node-id data]
  (new-graph (merge-with merge node-data {node-id data}) topology))

(defn get-node-data
  [{:keys (node-data _) as :graph} node-id]
  (node-data node-id))

(defn- add-node
  [graph id]
  (add-node-data graph id nil))

(defn add-edge
  [graph from to]
  (let [added-nodes-graph (-> graph (add-node from) (add-node to))
        new-data (:node-data added-nodes-graph)
        old-topology (:topology added-nodes-graph)
        new-topology (merge-with union old-topology {from #{to}})]
  (new-graph new-data new-topology)))


(defn add-fan [graph from & to]
  (reduce #(add-edge %1 from %2) graph to))

(defn map-to-graph [m]
  (reduce #(apply add-fan %1 (first %2) (second %2)) empty-graph m))

(defn filter-graph
  [graph pred]
  (letfn [(node-filter [nodes] (filter pred nodes))
          (edge-filter [neigh] #(filter pred (neigh %)))]
    (new-graph (node-filter (:nodes graph)) (edge-filter (:neighbors graph)))))

