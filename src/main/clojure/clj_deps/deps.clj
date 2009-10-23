(ns clj-deps.deps)

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
    :else '--spec-error--))

(defn- prefix-list-dependencies [spec]
  (let [[prefix & libspecs] spec]
    (letfn [(add-prefix [libspec]
              (symbol (str prefix "." (libspec-dependency libspec))))]
      (vec (map add-prefix libspecs)))))

(defn is-flag? [spec] (#{:reload :reload-all :verbose} spec))

(defn- spec-dependencies [spec]
  (cond
    (is-libspec? spec) [(libspec-dependency spec)]
    (is-prefix-list? spec) (prefix-list-dependencies spec)
    (is-flag? spec) []
    :else ['--spec-error--]))

(defn- extract-dependencies [form]
  (when-let [dep-type (and (is-dependency? form) (first form))]
    (mapcat spec-dependencies (next form))))

(defn process-ns [form]
  (let [[ns name & forms] form]
    (cons name (mapcat extract-dependencies forms))))

