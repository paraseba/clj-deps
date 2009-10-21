(ns clj-deps.dot
  (:use
     (clojure.contrib [graph :only (get-neighbors)]
                      [str-utils2 :only (join)])))

(defn- escape [name] (.replace (str name) "\"" "\\\""))

(defn edge-repr [from to]
  (str "\"" (escape from) "\" -> \"" (escape to) "\""))

(defn- adj-list [graph]
  (letfn [(node-adj-list
            [node]
            (map vector (repeat node) (get-neighbors graph node)))]
    (mapcat node-adj-list (sort (:nodes graph)))))

(defn dot-graph-edges [graph]
  (let [al (adj-list graph)]
    (join "\n" (map #(apply edge-repr %) al))))

(defn graph-to-dot [graph]
  (str
    "digraph G {\n"
    (dot-graph-edges graph)
    "\n}\n"))

