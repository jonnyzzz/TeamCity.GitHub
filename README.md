TeamCity.GitHub
===============
Integration of TeamCity and GitHub.

What is supported
=================
The aim to create the plugin was to support GitHub Change Status API in TeamCity.
https://github.com/blog/1227-commit-status-api

Plugin is descibed in one of the following blog posts
- http://blog.jonnyzzz.name/2012/09/reporting-change-status-to-github.html
- http://blog.jonnyzzz.name/2012/09/github-status-api-in-teamcity-update.html
- http://blog.jonnyzzz.name/2013/04/github-change-status-on-branches.html

Download
========
I set up TeamCity build configuration for it. Fresh builds are downloadable from 
http://teamcity.jetbrains.com/viewType.html?buildTypeId=bt398&tab=buildTypeStatusDiv

To install plugin, put downloaded ```.zip``` file into `<TeamCity Data Directory>/plugins` folder and restart the server

**NOTE** Check you downloaded ```.zip``` file (you may be redirected to login page when using `curl` or `wget`).



License
=======
Apache 2.0

Current Status
==============
Implemented most-simpliest approach to update change status to github. 
Only username/password authentication is supported.


Note
====
This is my (Eugene Petrenko) private home project

If you like, you may support this plugin:

[![Donate](https://www.paypalobjects.com/en_US/i/btn/btn_donate_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=AJRXZ9X6ZKXPJ)

