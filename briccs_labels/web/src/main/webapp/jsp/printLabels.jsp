<%@ include file="/jsp/include.jsp" %>
<html>
<head><title>Print Barcode Labels</title></head>
<body>
<h1>Print Barcode Labels</h1>

<form action="<%= request.getRequestURI() %>" method="GET">

<input type="submit" name="PrintLabels" value="Pack"> Prints a full participant pack - bag and specimen labels. 
<p/>
<input type="submit" name="PrintLabels" value="Samples"> Prints only specimen labels.
<p/>
<input type="submit" name="PrintLabels" value="Bags"> Prints only bag labels.
<p/>
<input type="submit" name="PrintLabels" value="ExternalBags"> Prints bag labels for external sites.
<p/>
<input type="submit" name="PrintLabels" value="MERMAID"> Prints labels for MERMAID.

</form>

<p/>
<p/>
<p/>

<%= printer.print(request) %>

</body>
</html>