jQuery(function($){
    
	if($('#chart')[0]){

        var chart = $('#chart').flash({
            swf        : '/static/plugins/beechart/beechart-line.swf',
            width      : 980,
            height     : 400,
            allowScriptAccess : "always",
            flashvars  : {
                dataUrl : $('#chart').data('url'),
                w:920,
                h:300
            }
        })
        .on("swfReady.flash",function(){

        });

        jQuery('#last-7day').on('click',function(){
            var chart = $('#chart').flash({
                swf        : '/static/plugins/beechart/beechart-line.swf',
                width      : 980,
                height     : 400,
                allowScriptAccess : "always",
                flashvars  : {
                    dataUrl : $(this).data('url'),
                    w:920,
                    h:300
                }
            });
        });
        jQuery('#select-week').change(function(){
            var chart = $('#chart').flash({
                swf        : '/static/plugins/beechart/beechart-line.swf',
                width      : 980,
                height     : 400,
                allowScriptAccess : "always",
                flashvars  : {
                    dataUrl : $(this).val(),
                    w:920,
                    h:300
                }
            });

        });

    }
	
	
});