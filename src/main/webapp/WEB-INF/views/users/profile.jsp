<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<title>중고거래사이트</title>
<link href="/resources/css/profile.css" rel="stylesheet">
<link href="/resources/vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">
<style>
#backBtn {
	margin: 20px auto auto 20px;
}
</style>
</head>
<body>
<a href="javascript:history.back()" id="backBtn" class="btn btn-primary btn-lg active" role="button" aria-pressed="true">BACK</a>
<div class="container">
    <div class="card">
        <div class="info"><span></span> 
        <button id="savebutton">수정 ON</button> 
		<button id="saveBtn">저장</button>
		<button id="homeBtn">HOME</button>
        </div>
        <div class="forms">
            <div class="inputs"> <span>아이디</span> 
            	<input readonly value='<c:out value="${users.userId }" />' id="userId" name="userId" disabled> 
            </div>
            <div class="inputs"> <span>이메일</span> 
            	<input type="text" readonly value='<c:out value="${users.userEmail }" />' id="userEmail" name="userEmail" disabled title="이메일 주소는 @가 포함되어야 합니다."> 
            </div>
            <div class="inputs"> <span>전화번호</span> 
            	<input type="text" readonly value='<c:out value="${users.userPhone }" />' id="userPhone" name="userPhone" disabled title="전화번호는 숫자만 가능합니다."> 
            </div>
            <div class="inputs"> <span>주소</span> 
            	<input type="text" readonly value='<c:out value="${users.userAddr }" />' id="userAddr" name="userAddr" disabled title="클릭하면 주소 검색이 가능합니다."> 
            </div>
        </div>
    </div>
</div>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
<script src="//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
<script type="text/javascript">
	const savebutton = document.getElementById('savebutton');
	const readonly = true;
	const inputs = document.querySelectorAll('input[type="text"]');
	savebutton.addEventListener('click', function() {
		
		for (var i = 0; i < inputs.length; i++) {
			inputs[i].toggleAttribute('readonly');
			inputs[i].toggleAttribute('disabled');
		}
		if (savebutton.innerHTML == "수정 ON") {
			savebutton.innerHTML = "수정 OFF";
		} else {
			savebutton.innerHTML = "수정 ON";
		}
	});
	
	const homeBtn = document.getElementById('homeBtn');
		homeBtn.addEventListener('click', function() {
		location.href = "/main";
	});
	
	$(function() {
		
		// 주소찾기
		$('#userAddr').on('click', function() {
			    new daum.Postcode({
			        oncomplete: function(data) {
			        	document.getElementById('userAddr').value = data.address;
			        }
			    }).open();
			})
		
		// 수정 내용 저장
		$('#saveBtn').click(function() {
			const userEmail = $('#userEmail').val();
			const userPhone = $('#userPhone').val();
			const userAddr = $('#userAddr').val();
			const userId = $('#userId').val();
			
			const formData = {
					"userEmail" : userEmail,
					"userPhone" : userPhone,
					"userAddr" : userAddr,
					"userId" : userId
			};
			
			if(confirm("변경 내용을 저장하시겠습니까?")) {
				$.ajax({
					type: "post",
					url: "/users/profile",
					data: JSON.stringify(formData),
					contentType: "application/json; charset=utf8",
					success: function(data) {
						alert("수정 완료.");
						location.reload();
					},
					error: function(xhr) {
						alert("에러 발생. 다시 요청해주세요.");
					}
				});
			}
		});
	});
</script>

<%@ include file="../includes/footer.jsp"%>