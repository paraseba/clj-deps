(ns clj-deps.dot
  (:use
     (clojure.contrib [graph :only (get-neighbors)]
                      [str-utils2 :only (join)]
                      [java-utils :only (as-str)])
     [clj-deps.graph :only (get-node-data)]))


(defn- escape [name] (.replace (as-str name) "\"" "\\\""))
(defn- format-atts
  [atts]
  (join ", " (map #(format "\"%s\"=\"%s\"" (escape (first %)) (escape (second %))) atts)))

(defn edge-repr
  [from to]
  (format "\"%s\" -> \"%s\"" (escape from) (escape to)))

(defn node-repr
  [id atts]
  (format "\"%s\" [%s]" (escape id) (format-atts atts)))

(defn- adj-list
  [graph]
  (letfn [(node-adj-list
            [node]
            (map vector (repeat node) (get-neighbors graph node)))]
    (mapcat node-adj-list (sort (:nodes graph)))))

(defn dot-graph-edges
  [graph]
  (let [al (adj-list graph)]
    (join ";\n" (map #(apply edge-repr %) al))))

(defn dot-graph-nodes
  [graph]
  (join ";\n" (map #(node-repr %1 (get-node-data graph %1)) (sort (:nodes graph)))))

(defn graph-to-dot
  [graph]
  (str
    "digraph G {\n"
    (dot-graph-nodes graph)
    ";\n"
    (dot-graph-edges graph)
    "\n}\n"))

