<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/tlds/template.tld" prefix="template" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="keywords" content="informatics, i2b2, biomedical, clinical research, research, de-identification, clinical data analysis, analytics, medical research, data analysis tool, clinical database, eureka!, eureka, scientific research, temporal patterns, bioinformatics, ontology, ontologies, ontology editor, data mining, etl, cvrg, CardioVascular Research Grid" />

<title>Eureka! Clinical Analytics</title>

<!--[if lte IE 7]>
<link href="${pageContext.request.contextPath}/css/ie7.css" rel="stylesheet" type="text/css">
<style>
.container { width:1024px; margin: 0 auto;}
ul.nav a { zoom: 1; }
#submit { border:none;}
</style>
<![endif]-->

<!-- Bootstrap CSS Toolkit styles -->
<link rel="stylesheet" href="http://blueimp.github.com/cdn/css/bootstrap.min.css">
<style>body{padding-top:60px;}</style>
<!-- Bootstrap styles for responsive website layout, supporting different screen sizes -->
<link rel="stylesheet" href="http://blueimp.github.com/cdn/css/bootstrap-responsive.min.css">
<!-- Bootstrap CSS fixes for IE6 -->
<!--[if lt IE 7]><link rel="stylesheet" href="http://blueimp.github.com/cdn/css/bootstrap-ie6.min.css"><![endif]-->
<!-- Bootstrap Image Gallery styles -->
<link rel="stylesheet" href="http://blueimp.github.com/Bootstrap-Image-Gallery/css/bootstrap-image-gallery.min.css">
<!-- CSS to style the file input field as button and adjust the Bootstrap progress bars -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/jquery.fileupload-ui.css">
<!-- Shim to make HTML5 elements usable in older Internet Explorer versions -->
<!--[if lt IE 9]><script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script><![endif]-->


<link href="${pageContext.request.contextPath}/css/styles.css" rel="stylesheet" type="text/css" />
<script src="${pageContext.request.contextPath}/js/jquery-1.7.1.min.js"></script>
<script src="${pageContext.request.contextPath}/js/jquery.validate.js"></script>
<script src="${pageContext.request.contextPath}/js/eureka.js"></script>

<script src="//ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
<!-- The jQuery UI widget factory, can be omitted if jQuery UI is already included -->
<script src="${pageContext.request.contextPath}/js/vendor/jquery.ui.widget.js"></script>
<!-- The Templates plugin is included to render the upload/download listings -->
<script src="http://blueimp.github.com/JavaScript-Templates/tmpl.min.js"></script>
<!-- The Load Image plugin is included for the preview images and image resizing functionality -->
<script src="http://blueimp.github.com/JavaScript-Load-Image/load-image.min.js"></script>
<!-- The Canvas to Blob plugin is included for image resizing functionality -->
<script src="http://blueimp.github.com/JavaScript-Canvas-to-Blob/canvas-to-blob.min.js"></script>
<!-- Bootstrap JS and Bootstrap Image Gallery are not required, but included for the demo -->
<script src="http://blueimp.github.com/cdn/js/bootstrap.min.js"></script>
<script src="http://blueimp.github.com/Bootstrap-Image-Gallery/js/bootstrap-image-gallery.min.js"></script>
<!-- The Iframe Transport is required for browsers without support for XHR file uploads -->
<script src="${pageContext.request.contextPath}/js/jquery.iframe-transport.js"></script>
<!-- The basic File Upload plugin -->
<script src="${pageContext.request.contextPath}/js/jquery.fileupload.js"></script>
<!-- The File Upload image processing plugin -->
<script src="${pageContext.request.contextPath}/js/jquery.fileupload-ip.js"></script>
<!-- The File Upload user interface plugin -->
<script src="${pageContext.request.contextPath}/js/jquery.fileupload-ui.js"></script>
<!-- The main application script -->
<script src="${pageContext.request.contextPath}/js/main.js"></script>
<!-- The XDomainRequest Transport is included for cross-domain file deletion for IE8+ -->
<!--[if gte IE 8]><script src="${pageContext.request.contextPath}/js/cors/jquery.xdr-transport.js"></script><![endif]-->

<link rel="SHORTCUT ICON" href="favicon.ico">
<meta name="Description" content="A Clinical Analysis Tool for Biomedical Informatics and Data" />


</head>

<body>

<div class="container">
<div>
<!--<div id="login">
	   <label>User Name
	   <input id="login_field" type="text" name="textfield" />
	   </label>
	   	   <label>Password
	   <input id="login_field" type="text" name="textfield" />
	   </label>
 	       <input id="submit" type="submit" name="Submit" value="Log In" />
 <br />
 	<span class="sub_text"><a href="forgot_password.html">Login Help</a></span>
<br />
    </div>-->
<div class="header">
    <span><a href="${pageContext.request.contextPath}"><img src="${pageContext.request.contextPath}/images/tag_line.gif" alt="Data Analysis Tool" width="238" align="absmiddle" /></a></span>
</div>

 <div>    
  <ul class="navbarli">
      <li><a href="${pageContext.request.contextPath}/about.jsp"><img src="${pageContext.request.contextPath}/images/about_icon.gif" alt="About" width="30" height="30" align="absmiddle" />About</a></li>
      <c:choose>
        <c:when test="${pageContext.request.remoteUser == null}">
          <img src="${pageContext.request.contextPath}/images/reg_icon.gif" alt="Register" width="30" height="30" align="absmiddle" />
          <li><a href="${pageContext.request.contextPath}/register.jsp">Register</a></li>
        </c:when>
      </c:choose>
      <c:choose>
        <c:when test="${pageContext.request.remoteUser != null}">
          <img src="${pageContext.request.contextPath}/images/acct_icon.gif" alt="Account" width="30" height="30" align="absmiddle" />
          <li><a href="${pageContext.request.contextPath}/user_acct?action=list">Account</a></li>
        </c:when>
      </c:choose>
     
      <img src="${pageContext.request.contextPath}/images/contact_icon.gif" alt="Contact" width="30" height="30" align="absmiddle" />
      <li><a href="${pageContext.request.contextPath}/contact.jsp">Contact</a></li>
      <img src="${pageContext.request.contextPath}/images/help_icon.gif" alt="Help" width="30" height="30" align="absmiddle" />
      <li><a href="${pageContext.request.contextPath}/help.jsp">Help</a></li>
      
      <c:choose>
        <c:when test="${pageContext.request.remoteUser != null }">
          <img src="${pageContext.request.contextPath}/images/admin_icon.gif" alt="Administration" width="30" height="30" align="absmiddle" />
          <li><a href="${pageContext.request.contextPath}/protected/admin?action=list">Administration</a></li>
	  </c:when>
	  </c:choose>
	  
	  <c:choose>
	  	
	  	<c:when test="${pageContext.request.remoteUser != null}">
<div class="fltrt">
	  	  <li>Welcome ${pageContext.request.remoteUser} | <a href="${pageContext.request.contextPath}/logout">Logout</a></li>
      	  <img src="${pageContext.request.contextPath}/images/i2b2_icon.gif" alt="i2b2" width="30" height="30" align="absmiddle" />
	      <li><a href="https://www.i2b2.org/">i2b2</a></li>
	      <img src="${pageContext.request.contextPath}/images/rsch_icon.gif" alt="Upload Data" width="30" height="30" align="absmiddle" />
	      <li><a href="${pageContext.request.contextPath}/protected/jobs">Upload Data</a></li>
</div>
	  	</c:when>
	  	
	  	<c:otherwise>
<div class="fltrt">
	      <img src="${pageContext.request.contextPath}/images/login_icon.gif" alt="Login" width="30" height="30" align="absmiddle" />
	      <li><a href="${pageContext.request.contextPath}/protected/login">Login</a></li>   </div>
	  	
	  	</c:otherwise>
	  </c:choose>
    </ul>
  </div>


  <div class="content-upload">
		<template:get name="content" />
  </div>
  
   <div class="sub-content">
		<template:get name="subcontent" />
  </div>
  
  <div class="footer">
    <p>Copyright 2011 . CCI . Center for Comprehensive Informatics  </p>
  </div>
  <!-- end .container --></div>
</body>
</html>