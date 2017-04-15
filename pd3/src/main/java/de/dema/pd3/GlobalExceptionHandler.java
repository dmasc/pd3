package de.dema.pd3;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Applikationsweiter globaler ExceptionHandler, der alle Exceptions behandelt, die in der Applikation während der 
 * Verarbeitung eines Requests geworfen und nicht anderweitig behandelt werden.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * Wenn bei einem Dateiupload eine zu große Datei hochgeladen wird, kommt es zu einer {@linkplain MultipartException}
	 * noch bevor der Request im Controller ankommt. Entsprechend muss die Exception hier im globalen Handler
	 * verarbeitet werden.
	 * 
	 * @param req wird von Spring automatisch injiziert.
	 * @param attr wird von Spring automatisch injiziert.
	 * @return Ziel-ViewName.
	 */
    @ExceptionHandler(MultipartException.class)
    public String handleFileUploadSizeExceededLimit(HttpServletRequest req, RedirectAttributes attr) {
    	String viewname = "/public/home"; 
    	if (req.getServletPath().startsWith("/user/upload-profile-picture")) {
    		viewname = "/user/profile";
    	}
    	attr.addAttribute("fileSizeExceededLimit", true);
        return "redirect:" + viewname;
    }
    
}
