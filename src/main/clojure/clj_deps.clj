(ns clj-deps
  (:import java.io.File)
  (:use (clojure.contrib [find-namespaces :only (find-ns-decls-in-dir)]
                         [duck-streams :only (writer)]
                         [def :only (defnk)])
     clj-deps.graph clj-deps.dot clj-deps.deps))


(defn dir-dependency-map
  [sourcedir]
  (letfn [(to-map [res-map ns-form]
            (let [deps (process-ns ns-form)
                  [name & deps] deps]
              (assoc res-map name (or deps '()))))]
  (reduce to-map {} (find-ns-decls-in-dir sourcedir))))

(defnk dir-dependency-graph [sourcedir :only nil :except nil :only-matching nil :except-matching nil]
  (letfn [(decorate [graph test filter] (if test (filter-graph graph filter) graph))
          (match-filter [regex] #(re-find regex (name %)))]
    (let [graph (map-to-graph (dir-dependency-map sourcedir))
          graph (decorate graph only only)
          graph (decorate graph except (complement except))
          graph (decorate graph only-matching (match-filter only-matching))
          graph (decorate graph except-matching (complement (match-filter except-matching)))]
      graph)))

(defn write-dependency-graph 
  [sourcedir out & filters]
  (with-open [out (writer out)]
    (let [graph (apply dir-dependency-graph (File. sourcedir) filters)
          dot (graph-to-dot graph)]
      (. out write dot))))

