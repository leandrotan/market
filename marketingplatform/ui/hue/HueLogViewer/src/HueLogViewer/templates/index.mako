<%!
from desktop.views import commonheader, commonfooter 
from django.utils.translation import ugettext as _
%>

<%namespace name="shared" file="shared_components.mako" />

${commonheader("HueLogViewer", "HueLogViewer", user, "100px")| n,unicode}

## Use double hashes for a mako template comment
## Main body

<h1>${_('Log entries')}</h1>
  <div class="row-fluid">
    <div class="span3" style="float: left;">
      <div class="well sidebar-nav">
	<input id="logs_path" type="editbox" value="/var/log/hadoop"></input>
	<input id="scan_path" type="button" value="Scan" onclick=loadLogList($("#logs_path").val())></input>
	<div id="logsList">
          Press "Scan" button to list log files in directory
	</div>
      </div>
    </div>

    <%include file="log_data.mako"/>
  </div>

<script>
	function showLog(log_path) {
		$("#logData").load("log_view&path="+log_path);
	}
	function showLogFrom(log_path,s_line) {
		$("#logData").load("log_view&path="+log_path+"&start_line="+s_line);
	}
	function loadLogList(path) {
		$("#logsList").load("logs_list"+path);
	}
</script>

<style>

.script_list_header {
  height:40px;
  position: relative;
}

.script_list {
  overflow-y: auto;
  border: 1px solid #ccc;
  border-radius: 4px;
  background: #fff;
  box-shadow: inset 0 1px 1px rgba(0,0,0,0.05);
  padding: 6px;
  max-height: 290px;
}
.script_list p {
  margin: 0 0 0px;
}
}

</style>

${commonfooter(messages)| n,unicode}

