package de.dema.pd3;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Die Klasse dient als Ersatz für {@linkplain LocalDate} und {@linkplain LocalDateTime}, um virtuelle Zeitsprünge zu ermöglichen. 
 * Anstelle von {@linkplain LocalDate#now()} und {@linkplain LocalDateTime#now()} können die Methoden {@linkplain Clock#today()} und 
 * {@linkplain Clock#now()} verwendet werden, um die virtuelle Systemzeit abzufragen.<br>
 * <br>
 * Um benachrichtigt zu werden, wann immer ein Zeitsprung stattgefunden hat, kann über die Methode {@linkplain Clock#addLeapListener(LeapListener)}
 * eine Instanz von {@linkplain LeapListener} übergeben werden.  
 */
public class Clock {

	private static java.time.Clock javaClock = java.time.Clock.systemDefaultZone();
	
	private static List<LeapListener> listeners = new ArrayList<>();

	/**
	 * Springt zu dem Zeitpunkt, der relativ zur echten Systemzeit den durch {@code offsetDuration} angegebenen Abstand hat.
	 *   
	 * @param offsetDuration die Sprungweite relativ zur echten Systemzeit.
	 */
	public static void leapToTime(Duration offsetDuration) {
		javaClock = java.time.Clock.offset(java.time.Clock.systemDefaultZone(), offsetDuration);
		notifyListeners();
	}
	
	/**
	 * Springt zu dem Zeitpunkt, der relativ zur aktuellen virtuellen Zeit den durch {@code offsetDuration} angegebenen Abstand hat.
	 *   
	 * @param offsetDuration die Sprungweite relativ zur aktuellen virtuellen Zeit.
	 */
	public static void leapRelative(Duration offsetDuration) {
		javaClock = java.time.Clock.offset(javaClock, offsetDuration);
		notifyListeners();
	}

	/**
	 * Gibt die aktuelle virtuelle Zeit zurück.
	 * @return die aktuelle virtuelle Zeit.
	 */
	public static LocalDateTime now() {
		return LocalDateTime.now(javaClock);
	}
	
	/**
	 * Gibt das aktuelle virtuelle Datum zurück.
	 * @return das aktuelle virtuelle Datum.
	 */
	public static LocalDate today() {
		return LocalDate.now(javaClock);		
	}

	/**
	 * Fügt einen Listener hinzu, dessen einzige Methode aufgerufen wird, wann immer ein Zeitsprung stattgefunden hat.
	 * 
	 * @param listener der Listener
	 */
	public static void addLeapListener(LeapListener listener) {
		listeners.add(listener);
	}

	/**
	 * Entfernt einen zurvor hinzugefügten Listener.
	 * 
	 * @param listener der zu entfernende Listener.
	 * @return {@code true} wenn {@code listener} zuvor in der Liste aller Listeners vorhanden war, sonst {@code false}. 
	 */
	public static boolean removeLeapListener(LeapListener listener) {
		return listeners.remove(listener);
	}
	
	private static void notifyListeners() {
		listeners.forEach(l -> l.leapedInTime());
	}

	/**
	 * Listener-Klasse für Benachrichtungen bei Zeitsprüngen über die Klasse {@linkplain Clock}. 
	 */
	public static interface LeapListener {
		
		/**
		 * Wird aufgerufen, nachdem ein Zeitsprung stattgefunden hat.
		 */
		void leapedInTime();
		
	}
	
}
