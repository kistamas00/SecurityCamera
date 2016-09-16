var FPS = 25;
var isStreamOn = false;
var isArrived = true;

$(document).ready(function() {

	updateContent();
	var updateInterval = setInterval(updateContent, 3000);
});

function getStream() {
	
	if (isArrived === true) {
		
		isArrived = false;
		$.ajax({
			url: '/stream',
			type: 'GET',
			success: function(data) {
				
				var imgElem = document.createElement('img');
				imgElem.setAttribute('src', data);
				imgElem.setAttribute('style', 'padding: 10px;');
				
				$('#stream').html('');
				$('#stream').append(imgElem);
				
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
			
			if (json.camera) {
				$('#camera-status').html('RUNNING');
			} else {
				$('#camera-status').html('STOPPED');
			}
			if (json.stream) {
				$('#streaming-status').html('ON');
				
				if (isStreamOn === false) {
						
					isStreamOn = true;
					isArrived = true;
					getStream();
				}
			} else {
				$('#streaming-status').html('OFF');
				$('#stream').html('	No streaming');
			}
			if (json.motionDetection) {
				$('#motion-detection-status').html('ENABLED');
			} else {
				$('#motion-detection-status').html('DISABLED');
			}
		}
	});
	
	$.ajax({
		url: '/pictures',
		type: 'GET',
		success: function(json){
			
			json.sort();
			json.reverse();
			
			var $div = $('#pictures');
			
			if (json.length > 0)
				$div.html('');
			else
				$div.html('	No pictures');
			
			for (var i in json) {
				var aElem = document.createElement('a');
				var imgElem = document.createElement('img');
				
				aElem.setAttribute('href', 'public/pictures/'+json[i]);
				imgElem.setAttribute('src', 'public/pictures/'+json[i]);
				imgElem.setAttribute('width', '250px');
				imgElem.setAttribute('style', 'padding: 10px;');
				
				aElem.appendChild(imgElem);
				$div.append(aElem);
			}
			
			var date = new Date();
			var format = {};
			format.h = date.getHours() < 10 ? '0'+date.getHours() : date.getHours();
			format.m = date.getMinutes() < 10 ? '0'+date.getMinutes() : date.getMinutes();
			format.s = date.getSeconds() < 10 ? '0'+date.getSeconds() : date.getSeconds();
			$('#last-update').html(format.h+':'+format.m+':'+format.s);
		}
	});
}