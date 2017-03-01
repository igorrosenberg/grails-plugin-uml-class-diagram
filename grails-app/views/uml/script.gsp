<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<title>UML Script rendering</title>
        <%-- <g:javascript library="jquery" /> --%>
        <%-- <script src="http://code.jquery.com/jquery-1.11.2.min.js"></script> --%>
        <script>
        function submitFormAjax()
        {
            var xmlhttp= window.XMLHttpRequest ?
                    new XMLHttpRequest() : new ActiveXObject("Microsoft.XMLHTTP");

            xmlhttp.onreadystatechange = function() {
                if (xmlhttp.readyState == 4 && xmlhttp.status == 200)
                    // Here is the response
                    document.getElementById('outputImage').src=xmlhttp.responseText;
            }

            var uml = document.getElementById('umlArea').value  ;

            xmlhttp.open("POST", "${createLink(action: 'renderUmlScript')}", true);
            xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
            xmlhttp.send( uml);

        }
    </script>
	</head>
	<body>
		<div class="content scaffold-create" role="main">
			<h1>UML Script rendering</h1>

            <div>
                <form onsubmit="submitFormAjax(); return false;">
                    <div>
                        <g:textArea name="umlArea" value="${uml}" style="width:100%" rows="10" autofocus="autofocus"/>
                    </div>
                    <div>
                       <button name="Go">Go</button>
                        <a href="http://plantuml.sourceforge.net/">PlantUml Powered</a>
                    </div>
                </form>
            </div>

            <div>
                <img title="Uml diagram" id="outputImage" width="100%"/>
            </div>
		</div>
	</body>
</html>
