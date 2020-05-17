<!doctype html>
<html lang="en" class="h-100">
<head>
	<#include "header.ftl">
</head>
	<body class="d-flex flex-column h-100">
		<#include "menu.ftl">
		<main role="main" class="flex-shrink-0">
		  	<div class="container">
		    <p>${searchterm}</p>
		    <#include "filetable.ftl">
		  	</div>
		</main>
		<#include "footer.ftl">
	</body>
</html>
