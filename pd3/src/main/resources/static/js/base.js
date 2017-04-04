$(function() {
	var indicator = $("#new-messages-indicator");

	$( "#user-search-field" ).autocomplete({
		serviceUrl: "/user/find",
	    onSelect: function (suggestion) {
	    	window.location.href = "/user/profile?id=" + suggestion.data;
	    }
	});
	
	if (indicator.length && !indicator.is(":visible")) {
		updateNewMessagesIndicator(indicator);
	}
});


function updateNewMessagesIndicator(indicator) {
	setTimeout(function() {
		$.post("/user/newmsgs", function(result) {
			if (result) {
				indicator.show();
			} else {
				updateNewMessagesIndicator(indicator);
			}
		});
	}, 30000);
}
