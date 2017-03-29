package de.dema.pd3;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.security.core.Authentication;

import de.dema.pd3.security.CurrentUser;


public final class Pd3Util {

	private Pd3Util() {
	}

	public static String injectHtmlTags(String text) {
		text = StringEscapeUtils.escapeHtml4(text);
		return text.replaceAll("(\\s|^)(www\\.)", "$1http://$2")
				.replaceAll("((?:https?://|https?://www\\.|www\\.)[^\\s\\.]+(?:\\.[^\\s\\.]+)+)", "<a href=\"$1\" target=\"_blank\">$1</a>")
				.replaceAll("\\n", "<br/>");
	}
	
	public static Long currentUserId(Authentication auth) {
		return ((CurrentUser) auth.getPrincipal()).getId();
	}
	
}
