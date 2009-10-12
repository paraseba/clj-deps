(ns clojure-dependencies.deps)

(defn- is-dependency? [form]
  (and (list? form) (#{:require :use} (first form))))

(defn- is-libspec? [spec]
  (or (symbol? spec) (vector? spec)))

(defn- is-prefix-list? [spec]
  (list? spec))

(defn- libspec-dependency [spec]
  (cond
    (symbol? spec) spec
    (vector? spec) (first spec)
    :else "###spec error###"))

(defn- prefix-list-dependencies [spec]
  (let [[prefix & libspecs] spec]
    (vec (map #(symbol (str prefix "." (libspec-dependency %))) libspecs))))

(defn- spec-dependencies [spec]
  (cond
    (is-libspec? spec) [(libspec-dependency spec)]
    (is-prefix-list? spec) (prefix-list-dependencies spec)
    :else ['unknown]))

(defn- extract-dependencies [form]
  (when-let [dep-type (and (is-dependency? form) (first form))]
    (mapcat spec-dependencies (next form))))

(defn process-ns [form]
  (let [[ns name & forms] form]
    (cons name (mapcat extract-dependencies forms))))

