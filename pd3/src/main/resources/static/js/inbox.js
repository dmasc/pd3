$(function() {
	$("#room-notifications-form input[type='checkbox']").change(function(event) {
		var $form = $("#room-notifications-form");
		
		$.post($form.attr("action"), $form.serialize());
	});
	
	$("#chatroomNameForm input[type='text']").change(function(event) {
		var $form = $("#chatroomNameForm"), namefield = $form.find("input[type='text']:first"),
				roomChoiceItem = $("div.room-choice-item.selected div.room-choice-item-text span:first");
		
		if (namefield.val().trim().length == 0) {
			namefield.val(roomChoiceItem.text());
			return false;
		}
		$.post($form.attr("action"), $form.serialize(), function(result) {
			if (result) {
				roomChoiceItem.text(namefield.val());
			}
		});
	});
	
	$("div.room-send-panel form").submit(function(event) {
		var $area = $(this).find("textarea");

		if ($area.val().trim().length == 0) {
			event.preventDefault();
			$area.val("");
		}
		$area.focus();
	});
});

function showMoreMessages(roomId, lastMsgId) {
	var link = $("div.room-content a:last");

	$.post("/user/inbox-ajax?roomId=" + roomId + "&lastMsgId="+ lastMsgId, function(data) {
		link.replaceWith(data);
	});
}
