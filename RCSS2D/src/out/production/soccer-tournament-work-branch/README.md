## RCSS2D Sim Code
Java code to communicate with the Robocup Soccer Simulator (RCSS) server

[Github with all the resources](https://rcsoccersim.github.io/) 
[Server Manual](https://rcsoccersim.github.io/rcssserver-manual-20030211.pdf)  

## Dependencies  
Use "brew" commands for MacOS, "apt-get" with Debian-based Linux distibutions (such as Ubuntu), ans "dnf install" with rpm-based Linux distributions (like Fedora)


* g++  
* make  
* boost  
    `    sudo apt-get boost`
    `    sudo dnf install boost`
    `    brew install boost`
* flex  
    `    sudo apt-get flex`
    `    sudo dnf install flex`
    `    brew install flex`
* bison  
    `    sudo apt-get bison`
    `    sudo dnf install bison`
    `    brew install bison`
* qt5  
    `    sudo apt-get qt5-default`
    `    sudo dnf install qt5*`
    `    brew install qt5`

## Installing the software
Please follow the instructions for the operating system you use. If any issues arise, consult the readme files of the server and monitor, or read the [manual, section 3](https://rcsoccersim.readthedocs.io/en/latest/gettingstarted.html), for a more in-depth look at the installation.

### Linux Installation
* Install the dependencies, then downloard the latest releases of the [server](https://github.com/rcsoccersim/rcssserver/releases) and the [monitor](https://github.com/rcsoccersim/rcssmonitor/releases)

* Extract the files

* Run `./configure` followed by `make`. These commands will have to be run once from the rcssserver-\[version no\] directory and once from the rcssmonitor-\[version no\] directory

### MAC Installation  
* After installing the dependencies above, download the latest releases of the [server](https://github.com/rcsoccersim/rcssserver/releases) and the [monitor](https://github.com/rcsoccersim/rcssmonitor/releases)

* Set the env, using your path for qt5 and zshrc if zsh and corresponding if bash (you can find these also by running `brew info qt5`):
  * ` export LDFLAGS="-L/opt/homebrew/opt/qt@5/lib"`
  * ` export CPPFLAGS="-I/opt/homebrew/opt/qt@5/include"`
  * ` export PKG_CONFIG_PATH="/opt/homebrew/opt/qt@5/lib/pkgconfig"`
  * ` echo 'export PATH="/opt/homebrew/opt/qt@5/bin:$PATH"' >> ~/.zshrc`

* Run `./configure --with-boost="/opt/homebrew/Cellar/boost/1.76.0"` and `make`, with the respective boost location. These commands will have to be run once from the rcssserver-\[version no\] directory and once from the rcssmonitor-\[version no\] directory

### Windows Installation
* The installation process for windows is a little bit different. [This tutorial by Carleton University](https://carleton.ca/nmai/wp-content/uploads/Lesson-1-Setting-Up-a-Game-of-RoboCup-Soccer-.pdf) covers the installation process.

## Using the program
With all operating systems, to run the program, you must do the following:
* Run the server first
* Then run the monitor (optional but recommended)
* Finally run this project (start the .java file with a main function)

For Windows, run the respective .exe files
For MacOS and Linux, `cd` into `rcssserver-\[version no\]/src` and r`cssmonitor-\[version no\]/src` then type `./rcssserver` and `./rcssmonitor` respectively.
* If you installed the executables in your PATH, you can just run `rcssserver` followed by `rcssmonitor` (or `rcsoccersim` to run both  at the same time)

* Quick note about the code: currently, the players are intentionally slow. However, if the ball hasn't been touched in 100 cycles, the server automatically issues a "drop ball" command to ensure that the ball isn't stuck, which repels the players around the ball. As a consequence, sometimes the players are never able to touch the ball, stopping the game from progressing. So, for the time being, this check should be disabled by running `./rcssserver server::ball_stuck_area=0` instead of just `./rcssserver`.

