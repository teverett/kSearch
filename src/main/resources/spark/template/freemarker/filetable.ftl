 <table class="table table-sm">
			  <thead>
			    <tr>
			      <th scope="col">File</th>
			      <th scope="col">Addition Date</th>
			    </tr>
			  </thead>
			  <tbody>
			   <#list files as file>
			    <tr>
			      <td><a href="/showdoc?doc=${file.file_absolute_path}">${file.file_absolute_path}</a></td>
			      <td>${file.addition_date}</td>
			    </tr>			
			   </#list>
			  </tbody>
			</table>