var FPS = 10;
var isStreamOn = false;
var isArrived = true;

$(document).ready(function() {

	updateContent();
	setInterval(updateContent, 3000);
});

function getStream() {
	
	if (isArrived === true) {
		
		isArrived = false;
		$.ajax({
			url: '/stream/next',
			type: 'GET',
			success: function(data) {
				

				var $imgElem = $('<img>')
				$imgElem.attr({
					src:data,
					class:'img-responsive center-block'
				});
				
				$('#stream').html('');
				$('#stream').append($imgElem);
				
				isArrived = true;
			},
			error: function() {
				isStreamOn = false;
			}
		});
	}
	
	if (isStreamOn === true) {
		setTimeout(getStream, 1000 / FPS);
	}
}

function updateContent() {

	$.ajax({
		url: '/status',
		type: 'GET',
		success: function(json){
			
			if (json.stream === true) {
				
				if (isStreamOn === false) {
					
					isStreamOn = true;
					isArrived = true;
					getStream();
				}

			} else {

				isStreamOn = false;
				$('#stream').html('<span class="text-info">No streaming!</span>');
			}
		}
	});
}