<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=EUC-KR">
<title>Minute7 Validator</title>
</head>
<body>
	<h1>This is the list of all errors found.</h1>
	<c:if test="${not empty report}">

		<ul>
			<c:forEach var="reportItem" items="${report}">
				<li>${reportItem}</li>
			</c:forEach>
		</ul>

	</c:if>
</body>
</html>