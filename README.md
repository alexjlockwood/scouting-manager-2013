scouting-manager-2013
=====================

The Girls of Steel FRC scouting application for the 2013 competition.

Obtaining the Source Code
-------------------------

1. Make sure you have the latest version of git installed on your machine. For those of you who are unfamiliar with git, I recommend downloading and installing it from [here](http://git-scm.com/).

2. Open up a terminal (called 'Git BASH' if you installed using the link above) and execute the following command:

        $ git clone https://github.com/alexjlockwood/scoutingmanager-2013.git

   This will create a ``scouting-manager-2013`` directory containing the source code for Scouting Manager.

3. Scouting Manager uses a third-party library called ActionBarSherlock. Before importing the project into Eclipse, you must initialize the library by executing the following commands:

        $ cd scouting-manager-2013/
        $ git submodule init
        $ git submodule update

4. Import the ``scouting-manager-2013/ActionBarSherlock/library`` project into your Eclipse workspace.

5. Import the ``scouting-manager-2013/ScoutingManager`` project into your Eclipse workspace.

6. Done!
