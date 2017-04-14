package de.dema.pd3;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.security.core.Authentication;

import de.dema.pd3.persistence.User;
import de.dema.pd3.security.CurrentUser;

/**
 *	Utility-Klasse mit statischen Hilfsmethoden, die an mehreren Stellen in der Anwendung benötigt werden.
 */
public final class Pd3Util {

	private Pd3Util() {
	}

	/**
	 * Ersetzt URL's im Text durch funktionsfähige HTML-Links in Form von &lt;a&gt;-Tags. Die Links werden in einem 
	 * separaten Reiter angezeigt.
	 *  
	 * @param text ein beliebiger nicht HTML-codierter Text.
	 * @return ein HTML-codierter Text mit durch &lt;a&gt;-Tags ersetzten URL's. 
	 */
	public static String injectHtmlTags(String text) {
		text = StringEscapeUtils.escapeHtml4(text);
		return text.replaceAll("(\\s|^)(www\\.)", "$1http://$2")
				.replaceAll("((?:https?://|https?://www\\.|www\\.)[^\\s\\.]+(?:\\.[^\\s\\.]+)+)", "<a href=\"$1\" target=\"_blank\">$1</a>")
				.replaceAll("\\n", "<br/>");
	}
	
	/**
	 * Gibt die ID aus der {@linkplain CurrentUser}-Instanz zurück, die sich als Principal im {@code auth}-Objekt befindet.
	 * 
	 * @param auth Spring's {@linkplain Authentication}-Objekt.
	 * @return die ID aus der {@linkplain CurrentUser}-Instanz.
	 */
	public static Long currentUserId(Authentication auth) {
		return ((CurrentUser) auth.getPrincipal()).getId();
	}

	/**
	 * Baut aus dem Vor- und Nachnamen den vollständigen Namen von {@code user} und gibt diesen zurück.
	 *  
	 * @param user Der Benutzer, dessen Name gebildet werden soll.
	 * @return der vollständige Name des Benutzers.
	 */
	public static String username(User user) {
		return user.getForename() + " " + user.getSurname();
	}
	
}
