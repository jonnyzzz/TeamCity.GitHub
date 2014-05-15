TeamCity.GitHub
===============
Integration of TeamCity and GitHub. Supports TeamCity 7.1 and newer

Branches
========
We maintain two branches `master` and `stable`. Development is done in `master`. 
Plugin from `stable` branch is expected to be more stable and feature-lacking

It is highly recommended to use builds from `master` branch

Try the build from `master` first. 
If you see problems, please report them as issues first. 
If urget fix is required, than try a build from `stable` branch

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
I set up TeamCity build configuration for it [here](http://teamcity.jetbrains.com/viewType.html?buildTypeId=bt398&tab=buildTypeStatusDiv)

To install plugin, put downloaded ```.zip``` file into `<TeamCity Data Directory>/plugins` folder and restart the server

Letst build of the plugin could be downloaded from [TeamCity's build artifact](http://teamcity.jetbrains.com/guestAuth/repository/download/bt398/lastest.lastSuccessful/teamcity.github.zip)

**NOTE** Check you downloaded ```.zip``` file (you may be redirected to login page when using `curl` or `wget`)

Internal Properties
===================
This is the list of [TeamCity Internal Properites](http://confluence.jetbrains.com/display/TCD8/Configuring+TeamCity+Server+Startup+Properties#ConfiguringTeamCityServerStartupProperties-TeamCityinternalproperties) that are supported by the plugin

``teamcity.github.verify.ssl.certificate=true|false`` enable/disable https certificates check. default is false

``teamcity.github.http.timeout`` GitHub connection timeout in milliseconds, default 5 minutes

Properties to configure Proxy server settings used for GitHub conncetions.
``teamcity.github.http.proxy.host``, ``teamcity.github.http.proxy.port`` defines proxy host/port 
``teamcity.github.http.proxy.user``, ``teamcity.github.http.proxy.password`` optionally defines proxy credentials
``teamcity.github.http.proxy.domain``, ``teamcity.github.http.proxy.workstation`` optionally deifnes NT doamin credentials

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

You may support my home projects:
[![Donate](https://www.paypalobjects.com/en_US/i/btn/btn_donate_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=AJRXZ9X6ZKXPJ)

