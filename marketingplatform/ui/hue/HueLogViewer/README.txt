## Install app:
1. Copy HueLogViewer directory to /usr/lib/hue/apps
2. /usr/lib/hue/build/env/bin/python /usr/lib/hue/tools/app_reg/app_reg.py --install /usr/lib/hue/apps/HueLogViewer
3. Restart Hue (/etc/init.d/hue restart)

## Uninstall app
/usr/lib/hue/build/env/bin/python /usr/lib/hue/tools/app_reg/app_reg.py --remove /usr/lib/hue/apps/HueLogViewer