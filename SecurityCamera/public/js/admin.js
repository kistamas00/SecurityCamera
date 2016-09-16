$(document).ready(function() {

	updateContent();
	var updateInterval = setInterval(updateContent, 3000);

	$('#webserver-switch').click(function() {
		
		if ($('#webserver-switch-cb').prop('checked') && window.confirm("Are you sure?")) {
		
			$('#webserver-switch-cb').prop('checked', !$('#webserver-switch-cb').prop('checked'));
			
			$.ajax({
				url: '/admin/webserver/stop',
				type: 'POST',
			});
		}
	});
	
	$('#camera-switch').click(function() {
		
		$.ajax({
			url: $('#camera-switch-cb').prop('checked') === false ? '/admin/camera/start' : '/admin/camera/stop',
			type: 'POST',
			success: function() {
				$('#camera-switch-cb').prop('checked', !$('#camera-switch-cb').prop('checked'));
				if ($('#camera-switch-cb').prop('checked') === false) {
					$('#streaming-switch-cb').prop('checked', false);
					$('#motion-detection-switch-cb').prop('checked', false);
				}
			}
		});	
	});

	$('#streaming-switch').click(function() {
		
		if ($('#camera-switch-cb').prop('checked') === true) {
		
			$.ajax({
				url: $('#streaming-switch-cb').prop('checked') === false ? '/admin/camera/stream/on' : '/admin/camera/stream/off',
				type: 'POST',
				success: function() {
					$('#streaming-switch-cb').prop('checked', !$('#streaming-switch-cb').prop('checked'));
				}
			});
		}
	});
	
	$('#motion-detection-switch').click(function() {
		
		if ($('#camera-switch-cb').prop('checked') === true) {
		
			$.ajax({
				url: $('#motion-detection-switch-cb').prop('checked') === false ? '/admin/camera/motiondetection/enable' : '/admin/camera/motiondetection/disable',
				type: 'POST',
				success: function() {
					$('#motion-detection-switch-cb').prop('checked', !$('#motion-detection-switch-cb').prop('checked'));
				}
			});
		}
	});
	
	$('#email-set-button').click(function() {
		
		$.ajax({
			url: '/admin/email',
			type: 'POST',
			data: {
				email: $('#email-input').val()
			},
			success: updateContent
		});
	});
});

function updateContent() {
	
	$.ajax({
		url: '/status',
		type: 'GET',
		success: function(json){
			
			
			$('#webserver-switch-cb').prop('checked', true);
			
			$('#camera-switch-cb').prop('checked', json.camera);
			$('#streaming-switch-cb').prop('checked', json.stream);
			$('#motion-detection-switch-cb').prop('checked', json.motionDetection);
			
			if ($('#email-input').val() === '') {
				$('#email-input').val(json.email);
			}
		}
	});
}