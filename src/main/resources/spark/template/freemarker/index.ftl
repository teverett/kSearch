<!doctype html>
<html lang="en" class="h-100">
<head>
	<#include "header.ftl">
</head>
	<body class="d-flex flex-column h-100">
		<main role="main" class="flex-shrink-0">
		  	<div class="container">
		    <h1 class="mt-5">Khubla.com Search</h1>
		    <form action="/search" method="post">
				<input class="form-control" id="searchterm" name="searchterm" aria-describedby="search">
				<button type="submit" class="btn btn-primary">Submit</button>
			</form>
		  	</div>
		</main>
		<#include "footer.ftl">
	</body>
</html>
