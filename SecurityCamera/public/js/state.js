$(document).ready(function() {

	updateContent();
	setInterval(updateContent, 3000);
});

function updateContent() {
	
	$.ajax({
		url: '/status',
		type: 'GET',
		success: function(json){
			
			if (json.camera) {
				$('#camera-status').html('RUNNING');
				$('#camera-status').attr('class', 'label label-success');
			} else {
				$('#camera-status').html('STOPPED');
				$('#camera-status').attr('class', 'label label-danger');
			}
			if (json.stream) {
				$('#streaming-status').html('ON');
				$('#streaming-status').attr('class', 'label label-success');
			} else {
				$('#streaming-status').html('OFF');
				$('#streaming-status').attr('class', 'label label-danger');
			}
			if (json.motionDetection) {
				$('#motion-detection-status').html('ENABLED');
				$('#motion-detection-status').attr('class', 'label label-success');
			} else {
				$('#motion-detection-status').html('DISABLED');
				$('#motion-detection-status').attr('class', 'label label-danger');
			}
		}
	});
}