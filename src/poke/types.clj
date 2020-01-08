(ns poke.types
  (:require [clojure.data.csv :as csv]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            ))

(defn csv-data->maps [csv-data]
  (map zipmap
       (->> (first csv-data) ;; First row is the header
            (map keyword) ;; Drop if you want string keys instead
            repeat)
       (rest csv-data)))

(defn get-attack-matchups*
  "Get type matchup attack modifier with path [attacking][defending]"
  []
  (with-open [reader (io/reader "resources/type-effectiveness.csv")]
    (->> (csv-data->maps (csv/read-csv reader))
         (map (fn [x] [(keyword (:Attacking x)) (dissoc x :Attacking)]))
         (into {}))))

(def get-attack-matchups (memoize get-attack-matchups*))

(defn get-defense-matchups*
  "Get type matchup attack modifier with path [defending][attacking]"
  []
  (let [matchups (get-attack-matchups)
        flip     (fn [[atk def]]
                   (map (fn [[d v]] {d {atk v}}) def))]
    (apply merge-with conj (mapcat flip matchups))))

(def get-defense-matchups (memoize get-defense-matchups*))

(defn get-best-types
  "Get all the types which are best matchup with given `comparator` which accepts
  args [best-value current-value] to compare modifiers and matchups for type."
  [comparator matchups]
  (:types
   (reduce (fn [{:keys [best-val types] :as acc} [k str-value]]
             (let [value (edn/read-string str-value)]
               (cond (or (nil? best-val) (comparator best-val value))
                     {:best-val value :types [k]}

                     (= best-val value) (update acc :types conj k)

                     (not (comparator best-val value)) acc)))

           {:best-val nil ; arbitrarily high
            :types   []}
           matchups)))

(defn get-best-defender
  "Get all defending types with lowest attack modifier (best defense) against the
  attacking type."
  [attacking]
  (get-best-types > (get (get-attack-matchups) attacking)))

(defn get-best-attacker
  "Get all attacking types with highest attack modifier against the defending type."
  [defending]
  (get-best-types < (get (get-defense-matchups) defending)))

;; TODO: best attacker against dual-types
;; TODO: best defender against the most attacks (max count type best defender)

(comment
  (get-defense-matchups)
  (get-best-defender :Dark)
  (get-best-attacker :Fire)
  )
