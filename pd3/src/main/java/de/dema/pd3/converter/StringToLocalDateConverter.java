package de.dema.pd3.converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Konvertiert Datumsangaben in Form von Strings zu LocalDate-Objekte. Die Klasse wird von Spring automatisch gefunden
 * und im {@linkplain ConversionService} bei Bedarf verwendet. 
 */
@Component
public class StringToLocalDateConverter implements Converter<String, LocalDate> {

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

	@Override
	public LocalDate convert(String source) {
		return LocalDate.parse(source, FORMATTER);
	}

}
