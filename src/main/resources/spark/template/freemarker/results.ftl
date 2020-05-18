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
			<nav aria-label="Page Navigation">
				<ul class="pagination">
					<#if showprevious==true>
					<li class="page-item"><a class="page-link" href="/search?page=${previouspage}&searchterm=${searchterm}&indexName=${indexName}">Previous</a></li>
					</#if>
					<#if shownext==true>	
					<li class="page-item"><a class="page-link" href="/search?page=${nextpage}&searchterm=${searchterm}&indexName=${indexName}">Next</a></li>
					</#if>
				</ul>
			</nav>
		  	</div>
		</main>
		<#include "footer.ftl">
	</body>
</html>
