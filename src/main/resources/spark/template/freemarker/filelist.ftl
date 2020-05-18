<!doctype html>
<html lang="en" class="h-100">
<head>
	<#include "header.ftl">
</head>
	<body class="d-flex flex-column h-100">
		<#include "menu.ftl">
		<main role="main" class="flex-shrink-0">
		  	<div class="container">
		   	<#include "filetable.ftl">
			<nav aria-label="Page Navigation">
				<ul class="pagination">
					<#if showprevious==true>
					<li class="page-item"><a class="page-link" href="/filelist?page=${previouspage}">Previous</a></li>
					</#if>
					<#if shownext==true>	
					<li class="page-item"><a class="page-link" href="/filelist?page=${nextpage}">Next</a></li>
					</#if>
				</ul>
			</nav>
		  	</div>
		</main>

		<#include "footer.ftl">
	</body>
</html>
