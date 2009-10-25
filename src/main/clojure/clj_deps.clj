(ns clj-deps
  (:import java.io.File)
  (:use (clojure.contrib [find-namespaces :only (find-ns-decls-in-dir)]
                         [duck-streams :only (writer)]
                         [def :only (defnk)]
                         [shell-out :only (sh)]
                         [java-utils :only (file as-str)])
     clj-deps.graph clj-deps.dot clj-deps.deps))


(defn dir-dep-map
  [sourcedir]
  (letfn [(to-map [res-map ns-form]
            (let [deps (process-ns ns-form)
                  [from & deps] deps]
              (assoc res-map from (or deps '()))))]
    (reduce to-map {} (find-ns-decls-in-dir (file sourcedir)))))

(defn dir-dep-graph
  [sourcedir]
  (map-to-graph (dir-dep-map sourcedir)))

(defnk filter-dep-graph
  [graph :only nil :except nil :only-matching nil :except-matching nil]
  (letfn [(decorate [graph test filter] (if test (filter-graph filter graph) graph))
          (match-filter [regex] #(re-find regex (name %)))]
    (let [graph (decorate graph only only)
          graph (decorate graph except (complement except))
          graph (decorate graph only-matching (match-filter only-matching))
          graph (decorate graph except-matching (complement (match-filter except-matching)))]
      graph)))

(defn save-graph
  [graph out]
  (with-open [out (writer out)]
    (. out write (graph-to-dot graph))))

(defn dot2image
  ([dotfile] (dot2image dotfile :png))
  ([dotfile format] (dot2image dotfile format "dot"))
  ([dotfile format dotexe]
   (let [dotfile (file dotfile)
         format (as-str format)
         name (.getName dotfile)
         basename (subs name 0 (.lastIndexOf name "."))
         new-name (str basename "." format)
         new-file (file (.getParentFile dotfile) new-name)]
     (sh dotexe "-T" format "-o" (.getPath new-file) (.getPath dotfile)))))
