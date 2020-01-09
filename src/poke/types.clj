(ns poke.types
  (:require
   [clojure.math.combinatorics :as combo]
   [poke.util :as util]))

(def data-path "resources/type-effectiveness.csv")

(def all (memoize #(map (comp keyword :Attacking) (util/read-csv-data data-path))))
(def all-dual (memoize #(combo/combinations (all) 2)))

(defn attack-matchups*
  "Get type matchup attack modifier with path [attacking][defending]"
  []
  (->> (util/read-csv-data data-path)
       (map (fn [x] [(keyword (:Attacking x))
                     (util/vals->floats (dissoc x :Attacking))]))
       (into {})))

(def attack-matchups (memoize attack-matchups*))

(defn defense-matchups*
  "Get type matchup attack modifier with path [defending][attacking]"
  []
  (let [matchups (attack-matchups)
        flip     (fn [[atk def]]
                   (map (fn [[d v]] {d {atk v}}) def))]
    (apply merge-with conj (mapcat flip matchups))))

(def defense-matchups (memoize defense-matchups*))

(defn get-best-modifiers
  "Get all the types which are best matchup with given `comparator` which accepts
  args [best-value current-value] to compare modifiers and matchups for type."
  [comparator matchups]
  (reduce (fn [{:keys [best-val types] :as acc} [k v]]
            (cond (or (nil? best-val) (comparator best-val v))
                  {:best-val v :types [k]}

                  (= best-val v) (update acc :types conj k)

                  (not (comparator best-val v)) acc))

          {:best-val nil ; arbitrarily high
           :types   []}
          matchups))

(defn get-best-defender
  "Get all defending types with lowest attack modifier (best defense) against the
  attacking type."
  [attacking]
  (let [single-modifiers (get (attack-matchups) attacking)
        dual-modifiers   (->> (all-dual)
                              (map (fn [dt]
                                     [dt (* (get single-modifiers (first dt))
                                            (get single-modifiers (second dt)))]))
                              (into {}))]
    (:types (get-best-modifiers > (merge single-modifiers dual-modifiers)))))

;; TODO: best defender against the most attacks (max count type best defender).
;; This sort of counts as a team comp thing though so ehhhh

(defn get-best-attacker
  "Get all attacking types with highest attack modifier against the defending type.
  Accepts dual types."
  ([defending]
   (:types (get-best-modifiers < (get (defense-matchups) defending))))
  ([primary secondary]
   (let [matchups  (defense-matchups)
         modifiers (merge-with * (get matchups primary) (get matchups secondary))]
     (:types (get-best-modifiers < modifiers)))))
