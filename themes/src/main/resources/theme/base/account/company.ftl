<h1>Company FreeMarker</h1>
<div>
User Id : ${user.id} <br/>
User name : ${user.username}
</div>
<table>
<thead>
<tr>
    <td>id</td>
    <td>name</td>
</tr>
</thead>
<tbody>
<#list companies as company>
<tr>
    <td><#if company.id?has_content>${company.id}</#if></td>
    <td><#if company.name?has_content>${company.name}</#if></td>
</tr>
</tbody>
</#list>
</table>