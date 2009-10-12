(ns clj-deps
  (:import java.io.File)
  (:use
     (clojure.contrib [find-namespaces :only (find-ns-decls-in-dir)]
                      [duck-streams :only (writer reader)])
     clj-deps.graph clj-deps.dot clj-deps.deps))


(defn dir-dependency-map
  [file]
  (letfn [(to-map [res-map ns-form]
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

