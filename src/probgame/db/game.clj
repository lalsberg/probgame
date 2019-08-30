(ns probgame.db.game
	(:require [clojure.java.jdbc :as jdbc]))

(defn create-room [conn]
	(:generated_key
		(first
			(jdbc/insert! conn :room {:created_by "leandro.alsberg"}))))

(defn create-player [conn room-id player-name points]
	(:generated_key
		(first
			(jdbc/insert! conn :player {:room_id room-id :name player-name :points points}))))

(defn save-table-card [conn room-id card]
	(:generated_key
		(first
			(jdbc/insert! conn :table_card {:room_id room-id :card card}))))