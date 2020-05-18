 			<table class="table table-sm">
			  <thead>
			    <tr>
			      <th scope="col">File</th>
			      <th scope="col">Type</th>
			      <th scope="col">Size</th>
			      <th scope="col">Addition Date</th>
			    </tr>
			  </thead>
			  <tbody>
			   <#list files as file>
			    <tr>
			      <td><a href="/showdoc?doc=${file.file_absolute_path}">${file.file_absolute_path}</a></td>
			      <td>${file.file_extension}</td>
			      <td>${file.file_size}</td>
			      <td>${file.additionDateString}</td>
			    </tr>			
			   </#list>
			  </tbody>
			</table>