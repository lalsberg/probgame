(ns probgame.controllers.game
	(:import (com.mchange.v2.c3p0 ComboPooledDataSource))
	(:require [probgame.logic.game :as logic]
						[probgame.db.db :as db]
						[clojure.java.jdbc :as jdbc]))

; tirar isso daqui start
(def db-spec
  {:classname "com.mysql.jdbc.Driver"
   :subprotocol "mysql"
   :subname "//127.0.0.1:3306/prob_game"
   :user "root"
   :password "root"})

(defn pool
  [spec]
  (let [cpds (doto (ComboPooledDataSource.)
               (.setDriverClass (:classname spec))
               (.setJdbcUrl (str "jdbc:" (:subprotocol spec) ":" (:subname spec)))
               (.setUser (:user spec))
               (.setPassword (:password spec))
               ;; expire excess connections after 30 minutes of inactivity:
               (.setMaxIdleTimeExcessConnections (* 30 60))
               ;; expire connections after 3 hours of inactivity:
               (.setMaxIdleTime (* 3 60 60)))]
    {:datasource cpds}))

(def pooled-db (delay (pool db-spec)))

(defn db-connection [] @pooled-db)
; tirar isso daqui end

(defn create-response [card-taken room-id player-id player-name player-points]
	{:cart card-taken
	 :room_id room-id
	 :player {
	   :id player-id
	   :name player-name
	   :points player-points}})

(defn create-room [player-name]
	(let [card-taken (logic/take-card logic/initial-deck)]
		(jdbc/with-db-transaction [db-connection db-spec]
			(let [room-id (db/create-room db-connection)]
				(let [player-id (db/create-player db-connection room-id player-name 20)]
					(db/save-table-card db-connection room-id card-taken)
					(create-response card-taken room-id player-id player-name 20))))))

(defn create-join-room-response [player-id player-points]
	{:player {
	   :id player-id
	   :points player-points}})

(defn join-room [{room-id :room-id player-name :player-name}]
	(let [player-id (db/create-player (db-connection) room-id player-name 20)]
		(create-join-room-response player-id 20)))

(defn remove-first [thelist element]
	(let [[thelist element] (split-with (partial not= element) thelist)] (concat thelist (rest element))))

(defn remove-all [thelist toremove]
	(if (nil? toremove)
		thelist
		(let [[current & others] toremove]
			(remove-all
				(remove-first thelist current) others))))

(defn get-deck [room-id]
	(remove-all logic/initial-deck (db/get-table-cards (db-connection) room-id)))

(defn count-cards [table-card deck]
	(count 
		(filter #(= table-card %) deck)))

(defn calculate-chance [room-id]
	(let [table-card (db/get-table-card (db-connection) room-id)]
		(let [deck (get-deck room-id)]
			(/ (+ 1 (count-cards table-card deck)) (count deck)))))

(defn abs [n] (max n (- n)))

(defn player-didnt-bet-yet? [player-id]
	(println (str "has bet " (db/has-bet (db-connection) player-id)))
	(not (db/has-bet (db-connection) player-id)))

(defn all-players-bet [room-id]
	(println (str "all-players-bet " (db/all-players-bet (db-connection) room-id)))
	(db/all-players-bet (db-connection) room-id))

(defn bet [{player-id :player-id bet :bet}]
	(println (str "player-id " player-id))
	(println (str "bet " bet))

	(if (player-didnt-bet-yet? player-id)
		(let [player (db/get-player (db-connection) player-id)]
			(let [chance (calculate-chance	(:room_id player))]
				(let [error-rate (abs (- bet chance))]
					(let [updated-points (- (:points player) error-rate)]
						(db/save-bet (db-connection) player-id)
						(db/update-player-points (db-connection) player-id updated-points)

						(if (all-players-bet (:room_id player))
							(let [card-taken (logic/take-card (get-deck (:room_id player)))]
								(db/save-table-card (db-connection) (:room_id player) card-taken)
								(db/remove-round-bets (db-connection) (:room_id player))))
							
						{:points updated-points
						 :correct chance
						 :diff error-rate}
					))))))
