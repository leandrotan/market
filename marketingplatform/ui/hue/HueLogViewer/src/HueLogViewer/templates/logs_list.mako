<%!
from desktop.views import commonheader, commonfooter 
from django.utils.translation import ugettext as _
%>

<%namespace name="shared" file="shared_components.mako" />
<div id="logs_list" class="overflow: auto">
	<ul>
	% for file in data:
         
              <li><a href="javascript:showLog('${file[1]}')">${file[0]}</a></li>
         
	% endfor
        </ul>
</div>
