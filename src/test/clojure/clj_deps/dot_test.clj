(ns clj-deps.dot-test
  (:use (clojure test)
        (clojure.contrib [graph :only (get-neighbors)]))
  (:require
     [clj-deps.graph :as g]
     [clj-deps.dot :as d]))


(deftest test-edge-repr
  (is (= "\"1\" -> \"2\"" (d/edge-repr 1 2)))
  (is (= "\"hi\" -> \"bye\"" (d/edge-repr "hi" "bye"))))

(def eg g/empty-graph)

(let [g12   (g/add-edge eg 1 2)
      g11   (g/add-edge eg 1 1)
      g123  (-> eg (g/add-edge 1 2) (g/add-edge 2 3))
      g1231 (-> eg (g/add-edge 1 2) (g/add-edge 2 3) (g/add-edge 3 1))]

  (deftest test-dot-graph-edges
    (is (= "\"1\" -> \"2\"" (d/dot-graph-edges g12)))
    (is (= "\"1\" -> \"2\"\n\"2\" -> \"3\"" (d/dot-graph-edges g123)))
    (is (= "\"1\" -> \"2\"\n\"2\" -> \"3\"\n\"3\" -> \"1\"" (d/dot-graph-edges g1231))))

  (deftest test-graph-to-dot
    (is (= (str "digraph G {\n" (d/dot-graph-edges g12) "\n}\n") (d/graph-to-dot g12)))
    (is (= (str "digraph G {\n" (d/dot-graph-edges g123) "\n}\n") (d/graph-to-dot g123)))
    (is (= (str "digraph G {\n" (d/dot-graph-edges g1231) "\n}\n") (d/graph-to-dot g1231)))))
