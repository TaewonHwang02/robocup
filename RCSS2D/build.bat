@echo off
echo [STARTING ROBOCUP SERVER AND MONITOR...]

:: Start Soccer Server
start "" "rcssserver-14.0.3-win\rcssserver.exe" server::ball_stuck_area=0

:: Wait a little for server to be ready
timeout /t 2 > nul

echo [STARTING ROBOCUP MONITOR...]

:: Start Soccer Monitor and force it to connect to server
start "" "rcssmonitor-14.1.0-win\rcssmonitor.exe"

:: Wait for monitor to connect
timeout /t 2 > nul

echo [BUILDING RCSS2D JAVA AGENTS...]

:: Create bin/ directory if it doesn't exist
if not exist bin mkdir bin

:: Compile all Java files into bin/
javac --release 23 -d bin ^
src/common/actions/*.java ^
src/common/goals/*.java ^
src/common/planning/*.java ^
src/common/graphs/*.java ^
src/common/players/*.java ^
src/common/*.java ^
src/teamA/teamA_actions/*.java ^
src/teamA/teamA_goals/*.java ^
src/teamA/teamA_players/*.java ^
src/teamA/*.java ^
src/teamB/goals/*.java ^
src/teamB/planning/*.java ^
src/teamB/players/*.java ^
src/teamB/TeamB.java ^
src/teamtester.java

if %errorlevel% neq 0 (
    echo [BUILD FAILED! Fix errors and try again.]
    pause
    exit /b
)

echo [BUILD COMPLETE! Running teamtester...]

:: Run the main program from bin folder
java -cp bin teamtester

echo [MATCH COMPLETE.]
pause
