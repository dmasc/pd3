$(function() {
	$("#room-notifications-form input[type='checkbox']").change(function(event) {
		var $form = $("#room-notifications-form");
		
		$.post($form.attr("action"), $form.serialize());
	});
});
