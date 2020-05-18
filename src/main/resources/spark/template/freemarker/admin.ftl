<!doctype html>
<html lang="en" class="h-100">
<head>
	<#include "header.ftl">
</head>
	<body class="d-flex flex-column h-100">
		<#include "menu.ftl">
		<main role="main" class="flex-shrink-0">
		  	<div class="container">
		  		<h4>Admin ${indexName}</h4>
		  		<ul>
				<li><a href="/filelist?indexName=${indexName}">File List</a></li>
				<li><a href="/reindex?indexName=${indexName}">Reindex</a></li>
				</ul>
		  	</div>
		</main>
		<#include "footer.ftl">
	</body>
</html>
