<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<style>
#backLink {
	margin: 10px 0 0 10px;
	display: block;
}
</style>
<!-- Bootstrap Core CSS -->
<link href="/resources/vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">

<!-- MetisMenu CSS -->
<link href="/resources/vendor/metisMenu/metisMenu.min.css" rel="stylesheet">

<!-- DataTables CSS -->
<link href="/resources/vendor/datatables-plugins/dataTables.bootstrap.css" rel="stylesheet">

<!-- DataTables Responsive CSS -->
<link href="/resources/vendor/datatables-responsive/dataTables.responsive.css" rel="stylesheet">

<!-- Custom CSS -->
<link href="/resources/dist/css/sb-admin-2.css" rel="stylesheet">

<!-- Custom Fonts -->
<link href="/resources/vendor/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
</head>
<body>
	<a href="javascript:history.back()" id="backLink">BACK</a>
	<div class="container">
		<div class="row">
			<div class="col-md-4 col-md-offset-4">
				<div class="login-panel panel panel-default">
					<div class="panel-heading">
						<h3 class="panel-title">회원가입</h3>
					</div>
					<div class="panel-body">
						<form id="signUpForm" role="form" method="post">
							<input type="hidden" id="idCheckResult" value="0"> <input type="hidden" id="emailCheckResult" value="0">
							<fieldset>
								<div class="form-group input-group">
									<input class="form-control" placeholder="아이디" type="text" id="userId" name="userId" autofocus> <span class="input-group-btn">
										<button class="btn btn-default" type="button" id="checkUserId">
											<i class="fa fa-search"></i>
										</button>
									</span>
								</div>
								<div class="form-group">
									<input class="form-control" placeholder="비밀번호" type="password" id="userPwd" name="userPwd">
								</div>
								<div class="form-group">
									<input class="form-control" placeholder="이름" type="text" id="userName" name="userName">
								</div>
								<div class="form-group input-group">
									<input class="form-control" placeholder="이메일" type="text" id="userEmail" name="userEmail"> <span class="input-group-btn">
										<button class="btn btn-default" type="button" id="checkUserEmail">
											<i class="fa fa-search"></i>
										</button>
									</span>
								</div>
								<div class="form-group">
									<input class="form-control" placeholder="연락처" type="text" id="userPhone" name="userPhone">
								</div>
								<div class="form-group input-group">
									<input class="form-control" placeholder="주소" type="text" id="userAddr" name="userAddr"> <span class="input-group-btn">
										<button class="btn btn-default" type="button" id="findAddr">
											<i class="fa fa-search"></i>
										</button>
									</span>
								</div>
								<button type="button" id="submitBtn" class="btn btn-success">회원가입</button>

							</fieldset>
						</form>
					</div>
				</div>
			</div>
		</div>
	</div>
<script src="//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
<script type="text/javascript">

	// 아이디/이메일 중복체크 실행 여부 확인값 초기화
	validCheckInit();
	
	function validCheckInit() {
		if (document.getElementById("idCheckResult").value == "1") {
			document.getElementById("idCheckResult").value = "0";
		}
		if (document.getElementById("emailCheckResult").value == "1") {
			document.getElementById("emailCheckResult").value = "0";
		}
	}
	
	$(function() {
		
		$("#submitBtn").click(function(){
			var isSignable = true;
			
				// 폼 입력값 빈칸 및 유효성 체크
				var userId = document.querySelector('#userId');
				var userPwd = document.querySelector('#userPwd');
				var userName = document.querySelector('#userName');
				var userEmail = document.querySelector('#userEmail');
				var userPhone = document.querySelector('#userPhone');
				var userAddr = document.querySelector('#userAddr');
				
				// 빈칸 체크
				var elementArr = [
					userId,
					userPwd,
					userName,
					userEmail,
					userPhone,
					userAddr
				];
				
				elementArr.forEach(function(inputTag) {
					if (inputTag.value == null || inputTag.value == "") {
						alert(inputTag.placeholder + " 입력하세요.");
						isSignable = false;
						return false;
					}
				});
				
				// 이메일 체크
				var emailCheck = /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$/i;
				if (!emailCheck.test(userEmail.value)) {
					alert("이메일 주소는 @가 포함되어야 합니다.");
					userEmail.focus();
					isSignable = false;
					return false;
				}
				
				// 전화번호 체크
				var phoneCheck = /^[0-9]{3}[0-9]{4}[0-9]{4}$/;
				if (!phoneCheck.test(userPhone.value)) {
					alert("전화번호 입력 양식을 지켜주세요.\n - 숫자만 입력 \n - 010-{4자리 숫자}-{4자리 숫자}");
					userPhone.focus();
					isSignable = false;
					return false;
				}
				
				if ($('#idCheckResult').val() == "0") {
					alert("아이디 중복체크를 반드시 해주세요.");
					isSignable = false;
					return false;
				}
				
				if ($('#emailCheckResult').val() == "0") {
					alert("이메일 중복체크를 반드시 해주세요.");
					isSignable = false;
					return false;
				}
				
				isSignable = true;
				
				// 회원가입
				if (isSignable == true) {
					var formData = {
							userId : $('#userId').val(),
							userPwd : $('#userPwd').val(),
							userName : $('#userName').val(),
							userEmail : $('#userEmail').val(),
							userPhone : $('#userPhone').val(),
							userAddr : $('#userAddr').val()
						}
						
					$.ajax({
						type: "post",
						url: "/users",
						data: JSON.stringify(formData),
						contentType: "application/json; charset=utf-8",
						headers: {
							Accept: "text/html; charset=utf-8"
						},
						success: function(data) {
							alert("회원가입 완료");
							location.href="/";
						},
						error: function(e) {
							alert("에러 발생. 다시 요청해주세요.");
						}
					});
					
				}
			
		});
		
		// 아이디 중복 체크
		$('#checkUserId').on('click', function() {
			const userId = $('#userId').val();
			if (userId == null || userId == "") {
				alert("아이디 입력하세요.");
				return;
			}
			$.ajax({
				type: "get",
				url: "/users/signup/id",
				data: {userId:userId},
				success: function(data) {
						alert("사용 가능한 아이디.");
						$('#idCheckResult').val("1");
				},
				error: function(e) {
					alert("이미 존재하는 아이디.");
				}
			});
		});
		
		// 이메일 중복 체크
		$('#checkUserEmail').on('click', function() {
			var userEmail = $('#userEmail').val();
			if (userEmail == null || userEmail == "") {
				alert("이메일 입력하세요.");
				return;
			}
			var emailCheck = /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$/i;
			if (!emailCheck.test(userEmail)) {
				alert("이메일 주소 형식에 맞게 작성해주세요.");
				return;
			}
			$.ajax({
				type: "get",
				url: "/users/signup/email",
				data: {userEmail:userEmail},
				success: function(data) {
					alert("사용 가능한 이메일.");
					$('#emailCheckResult').val("1");
				},
				error: function(e) {
					alert("이미 존재하는 이메일.");
				}
			});
		});
		
		// 주소찾기
		$('#findAddr').on('click', function() {
		    new daum.Postcode({
		        oncomplete: function(data) {
		        	document.getElementById('userAddr').value = data.address;
		        }
		    }).open();
		})
		
	});
	
</script>
</body>
</html>