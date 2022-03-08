<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../includes/header.jsp"%>
<!-- sidebar -->
<%@ include file="../includes/sidebar.jsp"%>
<!-- End of sidebar -->
<div id="page-wrapper">
	<div class="row">
		<div class="col-lg-12">
			<h1 class="page-header">판매중인 상품</h1>
		</div>
		<!-- /.col-lg-12 -->
	</div>
	<!-- /.row -->
	<div class="row">
		<div class="col-lg-12">
			<div class="panel panel-default">
				<!-- /.panel-heading -->
				<div class="panel-body">

					<!-- 판매 상품 리스트 -->
					<c:forEach var="product" items="${products }">
						<input hidden="hidden" type="text" id="prdtId" name="prdtId" value='<c:out value="${product.prdtId }" />'>
						<div class="gallery">
							<a href="/products/${product.prdtId }" id="imageBtn"><img src='${imagePath }${product.imageVO.fileName }' alt="Cinque Terre">
							</a>
							<div class="desc">
								<c:out value="${product.prdtName }" />
							</div>
						</div>
					</c:forEach>
					<!-- End of 판매 상품 리스트 -->

					<!-- 페이징 -->
					<div class="pull-right">
						<ul class="pagination">
							<c:if test="${pageMaker.prev }">
								<li class="paginate_button previous"><a href='<c:out value="${pageMaker.startPage - 1}" />'>Previous</a></li>
							</c:if>

							<c:forEach var="num" begin="${pageMaker.startPage }" end="${pageMaker.endPage }">
								<li class="paginate_button ${pageMaker.cri.pageNum == num ? "active":""} "><a href="${num }">${num }</a></li>
							</c:forEach>

							<c:if test="${pageMaker.next }">
								<li class="paginate_button next"><a href='<c:out value="${pageMaker.endPage - 1}" />'>Next</a></li>
							</c:if>
						</ul>
					</div>
					<!-- End of 페이징 -->

				</div>
				<!-- /.panel-body -->
			</div>
			<!-- /.panel -->
		</div>
		<!-- /.col-lg-12 -->
	</div>
	<!-- /.row -->
	<!-- /.row -->
	<!-- /.row -->
	<!-- /.row -->
</div>
<!-- 페이징 번호 클릭에 따른 페이지 이동 처리를 위한 form -->
<form id="actionForm" action="/main" method="get">
	<input type="hidden" name="pageNum" value='<c:out value="${pageMaker.cri.pageNum }" />'> 
	<input type="hidden" name="amount" value='<c:out value="${pageMaker.cri.amount }" />'>
</form>
<!-- End of 페이징 번호 클릭에 따른 페이지 이동 처리를 위한 form -->
<script type="text/javascript">
	$(document).ready(function() {
		var actionForm = $("#actionForm");
		$(".paginate_button a").on("click", function(e) {
			e.preventDefault(); // a 태그를 클릭해도 페이지 이동이 없도록 처리
			console.log('click');
			console.log($(this));
			actionForm.find("input[name='pageNum']").val($(this).attr("href"));
			actionForm.submit();
		});
	});
</script>
<%@ include file="../includes/footer.jsp"%>