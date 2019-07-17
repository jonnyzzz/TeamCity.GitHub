TeamCity.GitHub (obsolete)
----

This plugin provides GitHub integration for [TeamCity](https://jetbrains.com/teamcity) 7.1 and later releases, automatically reporting the status of a TeamCity build to its corresponding GitHub repository.

Please note that TeamCity 10.0 and later releases now include built-in support for updating GitHub and other platforms through the bundled [Commit Status Publisher](https://www.jetbrains.com/help/teamcity/commit-status-publisher.html), which effectively replaces this TeamCity.GitHub project. No further work is being done on TeamCity.GitHub, and we recommend that users migrate to Commit Status Publisher. 


About the Plugin
================
The purpose of creating this plugin is to support integration with the [GitHub Change Status API](https://github.com/blog/1227-commit-status-api) in TeamCity, which allows TeamCity to automatically attach build statuses to GitHub pull requests.

The plugin is described in more detail in the following blog posts:
- http://jonnyzzz.com/blog/2012/09/06/reporting-change-status-to-github
- http://jonnyzzz.com/blog/2012/09/13/github-status-api-in-teamcity-update
- http://jonnyzzz.com/blog/2013/04/26/github-change-status-on-branches

Other useful links: 
- [GitHub Pull Requests plugin for TeamCity | TeamCity Blog](https://blog.jetbrains.com/teamcity/2018/10/github-pull-requests-plugin-for-teamcity/)
- [Commit Status Publisher - TeamCity - Confluence](https://confluence.jetbrains.com/display/TW/Commit+Status+Publisher)


Installation and Configuration
==============================
First, download the [latest build of the plugin](http://teamcity.jetbrains.com/guestAuth/repository/download/bt398/lastest.lastSuccessful/teamcity.github.zip), which is configured for continuous integration on TeamCity [here](http://teamcity.jetbrains.com/viewType.html?buildTypeId=bt398&tab=buildTypeStatusDiv).

**NOTE** Ensure that your download of the `.zip` file is valid - you may be redirected to the login page when using `curl` or `wget`.

Next, put the downloaded `.zip` file into the `<TeamCity Data Directory>/plugins` folder and restart the TeamCity server. You can also upload the .zip directly by clicking "Upload plugin zip" in the Plugins List section of the Administration settings in TeamCity's web interface.

After restarting the server, the plugin should show up as an external plugin in the Plugins List section of the Administration settings.

To use the plugin with one of your TeamCity projects, ensure that your VCS root branch specification includes pull requests:

`+:refs/pull/(*/head)` (build number will include `/head`)

or 

`+:refs/pull/(*)/head` (build number will not include `/head`)

**Note:** It is also possible to use `+:refs/pull/(*/merge)`, but not recommended. There is some risk that this spec will cause a feedback loop of builds that will bog down your TeamCity server. [See this bug report](http://youtrack.jetbrains.com/issue/TW-33455) for more information.

Finally, add a new Build Feature to your project's configuration. Choose "Report change status to GitHub" from the list, fill in the necessary info in the dialog, and you should be good to go!

Branches
========
We maintain two branches, `master` and `stable`. Development is done in `master`. 
The plugin from the `stable` branch is expected to be more stable and feature-lacking.

It is highly recommended to use builds from the `master` branch.

Try the build from `master` first. If you see problems, please report them as issues first. 
If an urgent fix is required, then try a build from the `stable` branch.

Internal Properties
===================

This is the list of [TeamCity Internal Properites](http://confluence.jetbrains.com/display/TCD8/Configuring+TeamCity+Server+Startup+Properties#ConfiguringTeamCityServerStartupProperties-TeamCityinternalproperties) that are supported by the plugin.

``teamcity.github.verify.ssl.certificate=true|false`` enable/disable https certificates check. default is false
``teamcity.github.http.timeout`` GitHub connection timeout in milliseconds, default 5 minutes

Properties to configure Proxy server settings used for GitHub conncetions.
``teamcity.github.http.proxy.host``, ``teamcity.github.http.proxy.port`` defines proxy host/port 
``teamcity.github.http.proxy.user``, ``teamcity.github.http.proxy.password`` optionally defines proxy credentials
``teamcity.github.http.proxy.domain``, ``teamcity.github.http.proxy.workstation`` optionally deifnes NT doamin credentials

License
=======
Apache 2.0

Current Status: Deprecated
==============
TeamCity.GitHub implements a simple approach to updating change status to github. Only username/password authentication is supported.
No further work is being done on TeamCity.GitHub, and users are advised to migrate to [Commit Status Publisher](https://www.jetbrains.com/help/teamcity/commit-status-publisher.html). 

Note
====
This is my [Eugene Petrenko](https://jonnyzzz.com/) private home project
