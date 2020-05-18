 <nav class="navbar navbar-expand-lg navbar-light bg-light">
   <a class="navbar-brand" href="/index">${orgname}</a>
   <ul class="navbar-nav mr-auto mt-2 mt-lg-0">
      <li class="nav-item">
        <a class="nav-link" href="/index?indexName=${indexName}">Home</a>
      </li>
    </ul>
     <ul class="navbar-nav ml-auto mt-2 mt-lg-0">

       <li class="nav-item dropdown">
        <a class="nav-link dropdown-toggle" href="#" id="navbarDropdownMenuLink" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
          Search Indices
        </a>
        <div class="dropdown-menu" aria-labelledby="navbarDropdownMenuLink">
        <#list indices as index>
          <a class="dropdown-item" href="/index?indexName=${index}">${index}</a>
        </#list>
        </div>
      </li>
      <li class="nav-item">
        <a class="nav-link" href="/admin?indexName=${indexName}">Admin</a>
      </li>
    </ul>
 </nav>
 <p></p>

 