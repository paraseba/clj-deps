(ns test.clojure-dependencies.graph-test
  (:use (clojure test [set :only (difference)])
        (clojure.contrib [graph :only (get-neighbors)] 
                         [seq-utils :only (includes?)]))
  (:require
     [clojure-dependencies.graph :as g]))

(def eg g/empty-graph)

(defn size [g] (count (:nodes g)))

(defmacro eq-nodes [nodes, g]
  `(is (= #{} (difference (set ~nodes) (:nodes ~g))))
  `(is (= #{} (difference (:nodes ~g) (set ~nodes)))))

(defmacro has-edges 
  ([graph] true)
  ([graph unmatched] (throw (Exception. "unmatched pair")))
  ([graph from to & rest]
    `(do
      (is (includes? (get-neighbors ~graph ~from) ~to))
      (has-edges ~graph ~@rest))))



(deftest empty-graph
  (is eg)
  (is (= 0 (size eg)))
  (is (= 0 (count (:neighbors eg)))))

(deftest add-edge
  (let [g12   (g/add-edge eg 1 2)
        g11   (g/add-edge eg 1 1)
        g123  (-> eg (g/add-edge 1 2) (g/add-edge 2 3))
        g1231 (-> eg (g/add-edge 1 2) (g/add-edge 2 3) (g/add-edge 3 1))]

    (eq-nodes [1 2] g12)
    (eq-nodes [1] g11)
    (eq-nodes [1 2 3] g123)
    (eq-nodes [1 2 3] g1231)

    (has-edges g12 1 2)
    (has-edges g11 1 1)
    (has-edges g123 1 2 2 3)
    (has-edges g1231 1 2 2 3 3 1)))

(deftest add-fan
  (let [g2   (g/add-fan eg 1 2)
        g23  (g/add-fan eg 1 2 3)
        g231 (g/add-fan eg 1 2 3 1)
        g232 (g/add-fan eg 1 2 3 2)]

    (eq-nodes [1 2] g2)
    (eq-nodes [1 2 3] g23)
    (eq-nodes [1 2 3] g231)
    (eq-nodes [1 2 3] g232)

    (has-edges g2 1 2)
    (has-edges g23 1 2 1 3)
    (has-edges g231 1 2 1 3 1 1)
    (has-edges g232 1 2 1 3 1 2)))
