<!doctype html>
<html lang="en" class="h-100">
<head>
	<#include "header.ftl">
</head>
	<body class="d-flex flex-column h-100">
		<#include "menu.ftl">
		<main role="main" class="flex-shrink-0">
		  	<div class="container">
		  	<h4>Search ${documentcount} documents in ${indexName}</h4>
		    <form action="/search" method="get">
		    	<input type="hidden" id="indexName" name="indexName" value="${indexName}">
				<input class="form-control" id="searchterm" name="searchterm" aria-describedby="search">
				<p></p>
				<button type="submit" class="btn btn-primary">Submit</button>
			</form>
		  	</div>
		</main>
		<#include "footer.ftl">
	</body>
</html>
