#!/usr/bin/env python
# Licensed to Cloudera, Inc. under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  Cloudera, Inc. licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

from desktop.lib.django_util import render
import datetime
from os import listdir
from os.path import join, isfile
import subprocess
import logging
from django.http import HttpResponse
from django.core.servers.basehttp import FileWrapper
import zipfile
import time
import tempfile

LOG = logging.getLogger(__name__)

tail_step = 50

def index(request):
	return render('index.mako', request, data=None)

def log_view(request, path, start_line=0):
	LOG.info("Log view request")
	LOG.info(start_line)
	p = subprocess.Popen(["tail","-n",str(int(start_line)+int(tail_step)),path], stdout=subprocess.PIPE)
	lines = p.stdout.readlines()
	if (len(lines)-int(start_line))<tail_step:
		start_line=len(lines)
	else:
		start_line=int(start_line)+int(tail_step)
	return render(template='log_data.mako', request=request, data=dict(lines=lines,start_line=str(start_line),path=path),force_template=True)

def logs_list(request, path):
	files = [(f,join(path,f)) for f in listdir(path) if isfile(join(path,f))]
	return render(template='logs_list.mako', request=request, data=dict(data=files),force_template=True)


def download_log_view(request, path):
        t = time.time()
	LOG.info("Downloading log file..")
	try:
		tmp = tempfile.NamedTemporaryFile()

		zip = zipfile.ZipFile(tmp, "w", zipfile.ZIP_DEFLATED)
		zip.write(path,"hue-custom-logs/hue-%s.log" % t)
		zip.close()
		length = tmp.tell()
		
		wrapper = FileWrapper(tmp)
		response = HttpResponse(wrapper,content_type="application/zip")
		response['Content-Disposition'] = 'attachment; filename=hue-logs-%s.zip' % t
		response['Content-Length'] = length
		return response
	except Exception, e:
		LOG.info(e)	
	        LOG.exception("Couldn't construct zip file to write logs to.")

