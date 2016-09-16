$(document).ready(function() {

	var webserverSwitch = $('<input>');
	webserverSwitch.attr({
		type:'checkbox',
		'data-size':'mini',
		id:'webserver-switch-cb'
	});
	$('#webserver-switch-container').append(webserverSwitch);

	webserverSwitch.on('switchChange.bootstrapSwitch', function(event, state) {
		
		if (state === false && window.confirm("Are you sure?")) {
			
			$.ajax({
				url: '/admin/webserver/stop',
				type: 'POST',
			});
			
		} else {

			$('#webserver-switch-cb').bootstrapSwitch('state', true);
		}
	});

	var cameraSwitch = $('<input>');
	cameraSwitch.attr({
		type:'checkbox',
		'data-size':'mini',
		id:'camera-switch-cb'
	});
	$('#camera-switch-container').append(cameraSwitch);

	cameraSwitch.on('switchChange.bootstrapSwitch', function(event, state) {

		$.ajax({
			url: state === true ? '/admin/camera/start' : '/admin/camera/stop',
			type: 'POST'
		});
	});

	var streamingSwitch = $('<input>');
	streamingSwitch.attr({
		type:'checkbox',
		'data-size':'mini',
		id:'streaming-switch-cb'
	});
	$('#streaming-switch-container').append(streamingSwitch);

	streamingSwitch.on('switchChange.bootstrapSwitch', function(event, state) {

		$.ajax({
			url: state === true ? '/admin/camera/stream/on' : '/admin/camera/stream/off',
			type: 'POST'
		});
	});

	var motionDetectionSwitch = $('<input>');
	motionDetectionSwitch.attr({
		type:'checkbox',
		'data-size':'mini',
		id:'motion-detection-switch-cb'
	});
	$('#motion-detection-switch-container').append(motionDetectionSwitch);
	
	motionDetectionSwitch.on('switchChange.bootstrapSwitch', function(event, state) {

		$.ajax({
			url: state === true ? '/admin/camera/motiondetection/enable' : '/admin/camera/motiondetection/disable',
			type: 'POST'
		});
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
	
	webserverSwitch.bootstrapSwitch();
	cameraSwitch.bootstrapSwitch();
	streamingSwitch.bootstrapSwitch();
	motionDetectionSwitch.bootstrapSwitch();

	updateContent();
	setInterval(updateContent, 3000);
});

function updateContent() {

	$.ajax({
		url: '/status',
		type: 'GET',
		success: function(json){
			
			
			$('#webserver-switch-cb').bootstrapSwitch('state', true);
			
			$('#camera-switch-cb').bootstrapSwitch('state', json.camera);
			$('#streaming-switch-cb').bootstrapSwitch('state', json.stream);
			$('#motion-detection-switch-cb').bootstrapSwitch('state', json.motionDetection);
			
			if ($('#email-input').val() === '') {
				$('#email-input').val(json.email);
			}
		}
	});
}