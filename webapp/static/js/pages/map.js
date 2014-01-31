
jQuery(function($){

	renderMap();
    function renderMap(){
	
	
        if(!$('#map_container')[0])return;
		
		
        var map = new BMap.Map("map_container");
        map.centerAndZoom(new BMap.Point(108.403765, 36.914850), 5);
		
		
		function getBoundaryDigui(res){
			var data = res.pop();
            var bdary = new BMap.Boundary();
            bdary.get(data.split("-")[0], function(rs){

                var maxNum = -1, maxPly;
                var name = data.split("-")[0];
                var color = data.split("-")[1];
                var idoNum = data.split("-")[2];
                var idoPer = data.split("-")[3];

                var count = rs.boundaries.length;
                for(var i = 0; i < count; i++){
                    var ply = new BMap.Polygon(rs.boundaries[i], {strokeWeight: 1, strokeOpacity:0.5,fillColor:color,strokeColor: "#000000"});
                    map.addOverlay(ply);

                    ply.addEventListener("click", function (e) {

                        var latlng = e.point;
                        var info = new BMap.InfoWindow(name + "订单量是" + idoNum + ", 总占比是" + idoPer+"%", {width:100});
                        map.openInfoWindow(info, latlng);

                        delay = 0;
                        for (var flashTimes = 0; flashTimes < 3; flashTimes++) {
                            delay += 100;
                            setTimeout(function () {
                                ply.setFillColor("#FFFF00");
                            }, delay);

                            delay += 100;
                            setTimeout(function () {
                                ply.setFillColor(color);
                            }, delay);
                        }
                    });
                }
                if(maxPly){
                    map.setViewport(maxPly.getPoints());
                }
				
				if(res.length>0)
				
				getBoundaryDigui(res);
				
            });
        }

        $.ajax({
            url:$('#map_container').data('url'),
            type:'GET',
			success:function(){
				
			}
        }).done(function(res){
				res = jQuery.parseJSON(res);
				if(res&&res.success){
					getBoundaryDigui(res.data);
					map.clearOverlays();
					map.disableDragging();
					map.disableScrollWheelZoom();
					map.disableDoubleClickZoom();
					map.disableKeyboard();
					map.disableContinuousZoom();
					map.disablePinchToZoom();
				
				}	
               
        });
	}

});