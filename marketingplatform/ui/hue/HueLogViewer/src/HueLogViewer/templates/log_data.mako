<%!
from django.utils.translation import ugettext as _
%>

<style>
  pre {
    margin: 0;
    padding: 2px;
    border: 0;
  }
</style>


<div class="span9" style="float: left; width: 70%;>
    <div class="well" style="padding-top: 10px; padding-bottom: 0">
	<div id="logData" class="overflow: auto">
		% if start_line:
			<input id="start_line" type="editbox" value="${start_line}"></input>
			<input id="load_next" type="button" value="Load next rows" onclick=showLogFrom('${path}',${start_line})></input>
    <div class="pull-right" style="margin:0">
	<span class="btn-group">
		<a href="download_log&path=${path}" class="btn"><i class="icon-download-alt"></i> ${_('Download entire file as zip')}</a>
	</span>
    </div>
		% endif
		% if lines:
			% for l in lines:
				<pre>${l}</pre>
			% endfor
		% else:
			No log file selected
		% endif

	</div>
    </div>
</div>

