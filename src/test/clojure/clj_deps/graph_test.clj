(ns clj-deps.graph-test
  (:use (clojure test [set :only (union)]))
  (:require
     [clj-deps.graph :as g]))

(def eg g/empty-graph)

(defn edge-list-to-map
  [& edges]
  (letfn [(add-edge [m [from to]] (merge-with union m {from #{to}}))]
    (reduce add-edge {} (partition 2 edges))))

(defn eval-map-equal
  [m1 m2]
  (reduce #(and %1 (= (set (m1 %2)) (set (m2 %2)))) true (keys m1)))


(defmacro graph-has-edges
  [graph & edges]
  `(let [edge-map# (edge-list-to-map ~@edges)]
    (is (eval-map-equal edge-map# (:neighbors ~graph)))))



(deftest test-empty-graph
  (is eg)
  (is (= 0 (count (:nodes eg)))
  (is (= 0 (count (:neighbors eg))))))

(deftest test-add-edge
  (let [g12   (g/add-edge eg 1 2)
        g11   (g/add-edge eg 1 1)
        g123  (-> eg (g/add-edge 1 2) (g/add-edge 2 3))
        g1231 (-> eg (g/add-edge 1 2) (g/add-edge 2 3) (g/add-edge 3 1))]

    (graph-has-edges g12 1 2)
    (graph-has-edges g11 1 1)
    (graph-has-edges g123 1 2 2 3)
    (graph-has-edges g1231 1 2 2 3 3 1)))

(deftest test-add-fan
  (let [g2   (g/add-fan eg 1 2)
        g23  (g/add-fan eg 1 2 3)
        g231 (g/add-fan eg 1 2 3 1)
        g232 (g/add-fan eg 1 2 3 2)]

    (graph-has-edges g2 1 2)
    (graph-has-edges g23 1 2 1 3)
    (graph-has-edges g231 1 2 1 3 1 1)
    (graph-has-edges g232 1 2 1 3 1 2)))

(deftest test-node-data
  (let [g12 (g/add-edge eg 1 2)]
    (is (= nil (g/get-node-data g12 1)))
    (is (= nil (g/get-node-data g12 2)))
    (is (= {:foo :bar} (g/get-node-data (g/set-node-data g12 1 {:foo :bar}) 1)))
    (is (= {:fuu :bar} (g/get-node-data (g/set-node-data g12 2 {:fuu :bar}) 2)))
    (is (= {:foo :bar} (g/get-node-data (g/add-node-data g12 1 {:foo :bar}) 1)))
    (is (= {:fuu :bar} (g/get-node-data (g/add-node-data g12 2 {:fuu :bar}) 2)))
    (let [d1 (g/add-node-data g12 1 {:foo :bar})]
      (is (= {:foo :grr} (g/get-node-data (g/add-node-data d1 1 {:foo :grr}) 1)))
      (is (= {:foo :bar} (g/get-node-data (g/add-node-data d1 2 {:bar :foo}) 1)))
      (is (= {:bar :foo :foo :bar} (g/get-node-data (g/add-node-data d1 1 {:bar :foo}) 1)) ))))


(deftest test-map-to-graph
  (let [m12  (g/map-to-graph {1 [2]})
        m123 (g/map-to-graph {1 [2 3]})
        m123-456 (g/map-to-graph {1 [2 3] 4 [5 6]})]

    (graph-has-edges m12 1 2)
    (graph-has-edges m123 1 2 1 3)
    (graph-has-edges m123-456 1 2 1 3 4 5 4 6)))

(deftest test-filter-graph
  (let [gab   (g/add-edge eg 'a 'b)
        gaa   (g/add-edge eg 'a 'a)
        gabc  (-> eg (g/add-edge 'a 'b) (g/add-edge 'b 'c))
        gabca (-> eg (g/add-edge 'a 'b) (g/add-edge 'b 'c) (g/add-edge 'c 'a))]

    (graph-has-edges (g/filter-graph (constantly true) eg))
    (graph-has-edges (g/filter-graph (constantly false) eg))
    (graph-has-edges (g/filter-graph (constantly true) gab) 'a 'b)
    (graph-has-edges (g/filter-graph #(= 'a %) gab))
    (graph-has-edges (g/filter-graph #(= 'b %) gab))
    (graph-has-edges (g/filter-graph (constantly false) gab))
    (graph-has-edges (g/filter-graph (constantly true) gabc) 'a 'b 'b 'c)
    (graph-has-edges (g/filter-graph #(not= 'b %) gabc))))

(deftest test-map-graph
  (let [gab (g/add-edge eg :a :b)]
    (is (= {:name :a} (g/get-node-data (g/map-graph (fn [id atts] {:name id}) gab) :a)))
    (is (= {:name :b} (g/get-node-data (g/map-graph (fn [id atts] {:name id}) gab) :b)))))

