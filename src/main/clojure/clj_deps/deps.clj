(ns clj-deps.deps)

(defstruct namesp :sym :atts)

(defn new-namesp
  ([sym] (struct namesp sym))
  ([sym atts] (struct namesp sym atts)))

(defn- is-dependency? [form]
  (and (list? form) (#{:require :use} (first form))))

(defn- is-libspec? [spec]
  (or (symbol? spec) (vector? spec)))

(defn- is-prefix-list? [spec]
  (list? spec))

(defn- libspec-dependency [spec]
  (cond
    (symbol? spec) (new-namesp spec)
    (vector? spec) (new-namesp (first spec))
    :else (new-namesp '---spec-error---)))

(defn- prefix-list-dependencies [spec]
  (let [[prefix & libspecs] spec]
    (letfn [(add-prefix [libspec]
              (let [{:keys [sym atts]} (libspec-dependency libspec)
                    sym (symbol (str prefix "." sym))]
                (new-namesp sym atts)))]
      (vec (map add-prefix libspecs)))))

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
    (cons (new-namesp name) (mapcat extract-dependencies forms))))

