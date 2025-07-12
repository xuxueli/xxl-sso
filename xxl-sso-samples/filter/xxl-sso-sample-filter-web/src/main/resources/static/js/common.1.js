$(function(){

	/**
	 * logout
	 */
	$("#logoutBtn").click(function(){
		layer.confirm( "确认注销登录" , {
			icon: 3,
			title: "系统提示" ,
			btn: [ "确认", "取消" ]
		}, function(index){
			layer.close(index);

			$.post(base_url + "/weblogin/logout", function(data, status) {
				if (data.code == "200") {

					layer.msg( "注销成功" );
					setTimeout(function(){
						window.location.href = base_url + "/";
					}, 500);

				} else {
					layer.open({
						title: "系统提示" ,
						btn: [ "确认" ],
						content: (data.msg || "注销失败"),
						icon: '2'
					});
				}
			});
		});

	});

	// change menu status
	$('.sidebar-toggle').click(function(){
		if ( 'close' == $.cookie('sidebar_status') ) {
			$.cookie('sidebar_status', 'open', { expires: 7 });
		} else {
			$.cookie('sidebar_status', 'close', { expires: 7 });	//$.cookie('the_cookie', '', { expires: -1 });
		}
	});

	// slideToTop
	var slideToTop = $("<div />");
	slideToTop.html('<i class="fa fa-chevron-up"></i>');
	slideToTop.css({
		position: 'fixed',
		bottom: '20px',
		right: '25px',
		width: '40px',
		height: '40px',
		color: '#eee',
		'font-size': '',
		'line-height': '40px',
		'text-align': 'center',
		'background-color': '#222d32',
		cursor: 'pointer',
		'border-radius': '5px',
		'z-index': '99999',
		opacity: '.7',
		'display': 'none'
	});
	slideToTop.on('mouseenter', function () {
		$(this).css('opacity', '1');
	});
	slideToTop.on('mouseout', function () {
		$(this).css('opacity', '.7');
	});
	$('.wrapper').append(slideToTop);
	$(window).scroll(function () {
		if ($(window).scrollTop() >= 150) {
			if (!$(slideToTop).is(':visible')) {
				$(slideToTop).fadeIn(500);
			}
		} else {
			$(slideToTop).fadeOut(500);
		}
	});
	$(slideToTop).click(function () {
		$("html,body").animate({		// firefox ie not support body, chrome support body. but found that new version chrome not support body too.
			scrollTop: 0
		}, 100);
	});


});
