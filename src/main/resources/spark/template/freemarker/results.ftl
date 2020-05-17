<!doctype html>
<html lang="en" class="h-100">
<head>
	<#include "header.ftl">
</head>
	<body class="d-flex flex-column h-100">
		<main role="main" class="flex-shrink-0">
		  	<div class="container">
		    <h1 class="mt-5">Khubla.com Search</h1>
		    <table class="table">
			  <thead>
			    <tr>
			      <th scope="col">Results for '${searchterm}'</th>
			    </tr>
			  </thead>
			  <tbody>
			    <tr>
			      <#list results as result>
			      <th scope="row">${result}</th>
			      </#list>
			    </tr>			
			  </tbody>
			</table>
		  	</div>
		</main>
		<#include "footer.ftl">
	</body>
</html>
