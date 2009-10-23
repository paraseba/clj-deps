(ns generate-samples
  (:refer-clojure :exclude (replace))
  (:use clj-deps
        [clj-deps.graph :only (map-graph)])
  (:use (clojure.contrib
          [str-utils2 :only (replace)]
          [shell-out :only (sh)]
          [java-utils :only (file)])))

(defn color-atts [color] {:color color :fontcolor color})
(defn shape-atts [shape] {:shape shape})
(defn label-atts [label] {:label label})
(defn style-atts [style] {:style style})

(def in-dir "/tmp")
(def out-dir "/tmp")
(def dot-exe "dot")

(def main-color  "#000055")
(def sec-color   "#333333")
(def other-color "#777777")
(def error-color :red)
(def main-shape  :ellipse)
(def sec-shape   :diamond)
(def other-shape :box)
(def main-style  :bold)
(def sec-style   :dashed)
(def other-style :dotted)

(defn clj-deps-node-atts [ns atts]
  (let [ns (str ns)]
    (condp re-find ns
      #"^clojure\.contrib" (merge (color-atts other-color)
                                  (shape-atts other-shape)
                                  (label-atts (replace ns #"^clojure\.contrib" "contr"))
                                  (style-atts other-style))
      #"^clojure\." (merge (color-atts sec-color)
                          (shape-atts sec-shape)
                          (style-atts sec-style))
      (merge (color-atts main-color) (shape-atts main-shape) (style-atts main-style)))))


(defn cascade-node-atts [ns atts]
  (let [ns (str ns)]
    (merge (color-atts main-color)
           (shape-atts main-shape)
           (style-atts main-style)
           (label-atts (replace ns #"^cascade\." "")))))

(defn compojure-node-atts [ns atts]
  (let [ns (str ns)]
    (merge (color-atts main-color)
           (shape-atts main-shape)
           (style-atts main-style)
           (label-atts (replace ns #"^compojure\." "")))))

(defn contrib-node-atts [ns atts]
  (let [ns (str ns)]
    (condp re-find ns
      #"^clojure\.contrib" (merge (color-atts main-color)
                                  (shape-atts main-shape)
                                  (style-atts main-style)
                                  (label-atts (replace ns #"^clojure\.contrib\." "")))
      #"^clojure\." (merge (color-atts sec-color)
                          (shape-atts sec-shape)
                          (style-atts sec-style))
      (merge (color-atts error-color)
             (shape-atts other-shape)
             (style-atts other-style)))))

(defn dot2png
  [dotfile]
  (let [name (.getName dotfile)
        basename (subs name 0 (.lastIndexOf name "."))
        new-name (str basename ".png")
        new-file (file (.getParentFile dotfile) new-name)]
    (sh dot-exe "-Tpng" (.getPath dotfile) "-o" (.getPath new-file))))


(defn generate-clj-deps-graph  []
  (let [indir (file in-dir "clj-deps" "src/main")
        outfile (file out-dir "clj_deps.dot")
        graph (dir-dep-graph indir)]
    (do
      (save-graph (map-graph clj-deps-node-atts graph) outfile)
      (dot2png outfile))))

(defn generate-simple-clj-deps-graph  []
  (let [indir (file in-dir "clj-deps" "src/main")
        outfile (file out-dir "clj_deps_simple.dot")
        graph (dir-dep-graph indir)]
    (save-graph graph outfile)
    (dot2png outfile)))

(defn generate-cascade-graph  []
  (let [indir (file in-dir "cascade" "src/main/clojure")
        outfile (file out-dir "cascade.dot")
        graph (dir-dep-graph indir)
        graph (filter-dep-graph
                graph
                :only-matching #"^cascade\."
                :except-matching #"^cascade\.(internal\.|fail|config)")]
    (save-graph (map-graph cascade-node-atts graph) outfile)
    (dot2png outfile)))

(defn generate-compojure-graph  []
  (let [indir (file in-dir "compojure" "src/compojure")
        outfile (file out-dir "compojure.dot")
        graph (dir-dep-graph indir)
        graph (filter-dep-graph
                graph
                :only-matching #"^compojure\.")]
    (save-graph (map-graph compojure-node-atts graph) outfile)
    (dot2png outfile)))

(defn generate-contrib-graph  []
  (let [indir (file in-dir "clojure-contrib" "src/clojure/contrib")
        outfile (file out-dir "contrib.dot")
        graph (dir-dep-graph indir)
        graph (filter-dep-graph
                graph
                :except-matching #"test|example|def$|seq-utils")]
    (save-graph (map-graph contrib-node-atts graph) outfile)
    (dot2png outfile)))


(generate-clj-deps-graph)
(generate-simple-clj-deps-graph)
(generate-cascade-graph)
(generate-compojure-graph)
(generate-contrib-graph)
