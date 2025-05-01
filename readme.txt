README

Intel macOS Installation
1. Install Dependencies
2. Configure Environment Variables
Add the following to your ~/.zshrc or ~/.bash_profile:
export LDFLAGS="-L/usr/local/opt/qt@5/lib"
export CPPFLAGS="-I/usr/local/opt/qt@5/include"
export PKG_CONFIG_PATH="/usr/local/opt/qt@5/lib/pkgconfig"
echo 'export PATH="/usr/local/opt/qt@5/bin:$PATH"' >> ~/.zshrc
3. Build Server & Monitor
To run the serval on local 
cd ~/Desktop/RCSS2D/rcssserver-19.0.0/
./configure --with-boost="/usr/local/opt/boost"
Make
cd ~/Desktop/RCSS2D/rcssserver-19.0.0/src
./rcssserver
To run the monitor
cd ~/Desktop/RCSS2D/rcssmonitor-19.0.1/src
./rcssmonitor
5. Run Your Java Agents
cd ~/RCSS2D
 find . -name '*.java' > sources.txt  
 javac -d ../out @sources.txt 
java -cp ../out RCSS2D.src.teamtester

javac -d bin RCSS2D/src/teamtester/*.java
java  -cp bin RCSS2D.src.teamtester.TeamTester


 Windows Installation
1. Prerequisites
Java Development Kit (JDK) (for agent code)


7-Zip or similar to extract .zip files


2. Download & Extract
Go to SourceForge:


rcssmonitor-14.1.0-win.zip


rcssserver-14.0.3-win.zip


Place both ZIPs into C:\Users\<You>\Desktop\RCSS2D\ and extract them there. You should now have:

 C:\Users\<You>\Desktop\RCSS2D\rcssserver-14.0.3-win\
C:\Users\<You>\Desktop\RCSS2D\rcssmonitor-14.1.0-win\
3. Launch via Batch Script
Inside RCSS2D\rcssserver-14.0.3-win\, double-click build.bat

