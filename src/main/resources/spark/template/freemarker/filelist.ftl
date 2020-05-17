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
			      <th scope="col">Indexed Files</th>
			    </tr>
			  </thead>
			  <tbody>
			      <#list files as file>
			    <tr>
			      <td><a href="/showdoc?doc=${file.file_absolute_path}">${file.file_absolute_path}</a></td>
			    </tr>			
			      </#list>
			  </tbody>
			</table>
		  	</div>
		</main>
		<#include "footer.ftl">
	</body>
</html>
