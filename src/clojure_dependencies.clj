(ns clojure-dependencies
  (:import java.io.File)
  (:use
     (clojure.contrib [find-namespaces :only (find-ns-decls-in-dir)]
                      [duck-streams :only (writer reader)])
     clojure-dependencies.graph clojure-dependencies.dot clojure-dependencies.deps))


(defn dir-dependency-map
  [file]
  (let [to-map (fn [res-map ns-form]
                (let [deps (process-ns ns-form)
                      [name & deps] deps]
                  (assoc res-map name deps)))]
  (reduce to-map {} (find-ns-decls-in-dir file))))

(defn dir-dependency-graph
  [file]
  (map-to-graph (dir-dependency-map file)))

(defn write-dependency-graph 
  [sourcedir out]
  (with-open [out (writer out)]
    (let [graph (dir-dependency-graph (File. sourcedir))
          dot (graph-to-dot graph)]
      (. out write dot))))

