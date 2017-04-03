Syncing your fork or clone with the latest version from CloudSim Plus repository
================================================================================

Syncing your fork
-----------------

If you want to contribute to CloudSim Plus, you have to fork the project so that you can make changes at your own copy of the repository.
Once you have forked CloudSim Plus, this quick tutorial shows you how to sync your fork with the official repository in order to get the latest
development version.

Open a terminal inside the directory where you cloned the CloudSim Plus repository and execute the following commands:

1. Add a remote to the official repository in order to track changes on
   it (this step has to be performed just once, if you already have
   execute it and try to execute again, git will just tell you that a
   remote called "upstream" already exists):
   ``git remote add upstream https://github.com/manoelcampos/cloudsim-plus``
2. Get the latest version from the upstream remote (that is pointing to
   the official repository): ``git fetch upstream``
3. Switch to your master branch to merge the changes from the upstream:
   ``git checkout master``
4. Merge the changes from the master branch got from the upstream remote
   into your master: ``git merge upstream/master``
5. As CloudSim Plus is a Maven project, usually it is worth to execute
   ``mvn clean install`` to clean the build directory, re-build the
   project and install the new updated CloudSim Plus jars into your
   local maven repository. This procedure is useful to avoid errors when
   trying to use a new version.
6. Push the changes to your fork at github: ``git push origin master``

More information on syncing forks can be found
`here <https://help.github.com/articles/syncing-a-fork/>`__.

Syncing your clone
------------------

If you just want to download CloudSim Plus source code so that you can use it into your project, 
study its code, documentation and examples, you can simply clone it locally at your computer.
The clone doesn't create a copy of the repository into your GitHub account.
However, if you are planning to make changes and request their inclusion in CloudSim Plus, 
you have to fork the project.

If you have just cloned the repository, to update your local copy with the official repository it is as simple as executing ``git pull`` at the command line.
