function showMovingCommentPanel(parentId, topicId, page) {
	var panel = $("#movingcommentpanel");

	$("#movingcommentpanel input[name='commentId']").val(parentId);
	panel.insertAfter('#comment_' + parentId);
	panel.show();
	$("#movingcommentpanel textarea").focus();
}

function hideMovingCommentPanel() {
	$("#movingcommentpanel textarea").text('');
	$("#movingcommentpanel").hide();
}

function submitReply() {
	$("#movingcommentpanel form").submit();
}