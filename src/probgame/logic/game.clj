(ns probgame.logic.game)

(def initial-deck 
	["AC", "2C", "3C", "4C", "5C", "6C", "7C", "8C", "9C", "10C", "JC", "QC", "KC", 
	 "AE", "2E", "3E", "4E", "5E", "6E", "7E", "8E", "9E", "10E", "JE", "QE", "KE", 
	 "AP", "2P", "3P", "4P", "5P", "6P", "7P", "8P", "9P", "10P", "JP", "QP", "KP", 
	 "AO", "2O", "3O", "4O", "5O", "6O", "7O", "8O", "9O", "10O", "JO", "QO", "KO"])

(defn take-card [deck]
	(first (shuffle deck)))

(defn print-card! [card]
	"
	 ┌─────────┐
 │{}        │
 │         │
 │         │
 │    {}    │
 │         │
 │         │
 │        {}│
 └─────────┘
	")

