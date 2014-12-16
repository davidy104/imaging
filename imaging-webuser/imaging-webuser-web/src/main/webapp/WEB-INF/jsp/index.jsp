<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
<script type="text/javascript" src="static/js/imaging.home.js"></script>
<script type="text/javascript" src="static/js/imaging.form.js"></script>
<script type="text/javascript" src="static/js/imaging.add.js"></script>

<title></title>
</head>
<body>
	<h2>
		<spring:message code="imagingweb.title" />
	</h2>
	<div>
		<form:errors path="searchInfo" cssClass="errorBlock" element="div" />
		<form:form action="/imagingweb/search" cssClass="well" commandName="searchInfo" method="POST">
			<div id="control-group-tag class="control-group">
				<label for="searchInfo-tag"><spring:message
						code="search.label.tag" />:</label>
				<div class="controls">
					<form:input id="searchInfo-tag" path="tag" />
					<form:errors id="error-tag" path="tag"
						cssClass="help-inline" />
				</div>
			</div>
			<div id="control-group-name class="control-group">
				<label for="searchInfo-name"><spring:message
						code="search.label.name" />:</label>
				<div class="controls">
					<form:input id="searchInfo-name" path="name" />
					<form:errors id="error-name" path="name"
						cssClass="help-inline" />
				</div>
			</div>
			<div id="control-group-scalingType class="control-group">
				<label for="searchInfo-scalingType"><spring:message
						code="search.label.scalingType" />:</label>
				<div class="controls">
					<form:input id="searchInfo-scalingType" path="scalingType" />
					<form:errors id="error-scalingType" path="scalingType"
						cssClass="help-inline" />
				</div>
			</div>
			<div class="form-buttons">
				<button id="search-button" type="submit"
					class="btn btn-primary">
					<spring:message code="search.button.label" />
				</button>
			</div>
		</form:form>
		
		<div class="row well">
			 <div class="span11">
			 	 <ul class="thumbnails">
			 	 <c:forEach items="${imageInfos}" var="imageInfo">
			 	 	<li class="span3">
	                    <div class="thumbnail">
	                        <img class="my-square-image" src="${imageInfo.imageUri}" data-src="${imageInfo.imageUri}" />
	
	                        <div class="caption">
	                            <h5><c:out value="${imageInfo.tag}" /></h5>
	
	                            <p><c:out value="${imageInfo.name}" /></p>
	
	                            <p><a href="place_order.jsp?type=<%=item.getName()%>" class="btn btn-primary">Check Details</a></p>
	                        </div>
	                    </div>
               		</li> 
               		</c:forEach>           
			 	 </ul>
			 </div>
		</div>
	</div>
</body>
</html>