@echo off

set /A err = 0
set cp=%CLASSPATH%;%cd%\bullet\Libbulletjme-20.2.0.jar;

echo: javac -Xlint:unchecked rowlandsAdventure2Server\*.java
javac -Xlint:unchecked -cp "%cp%" rowlandsAdventure2Server\*.java
set /A err = %err% + %ERRORLEVEL%

echo: javac -Xlint:unchecked rowlandsAdventure2\*.java
javac -Xlint:unchecked -cp "%cp%" rowlandsAdventure2\*.java
set /A err = %err% + %ERRORLEVEL%

echo: javac -Xlint:unchecked rowlandsAdventure2\input\*.java
javac -Xlint:unchecked -cp "%cp%" rowlandsAdventure2\input\*.java
set /A err = %err% + %ERRORLEVEL%

echo: javac -Xlint:unchecked rowlandsAdventure2\character\*.java
javac -Xlint:unchecked -cp "%cp%" rowlandsAdventure2\character\*.java
set /A err = %err% + %ERRORLEVEL%

echo: javac -Xlint:unchecked rowlandsAdventure2\networking\*.java
javac -Xlint:unchecked -cp "%cp%" rowlandsAdventure2\networking\*.java
set /A err = %err% + %ERRORLEVEL%

echo: javac -Xlint:unchecked rowlandsAdventure2\planets\*.java
javac -Xlint:unchecked -cp "%cp%" rowlandsAdventure2\planets\*.java
set /A err = %err% + %ERRORLEVEL%

echo: javac -Xlint:unchecked rowlandsAdventure2\npcs\*.java
javac -Xlint:unchecked -cp "%cp%" rowlandsAdventure2\npcs\*.java
set /A err = %err% + %ERRORLEVEL%

echo: javac -Xlint:unchecked rowlandsAdventure2\builders\*.java
javac -Xlint:unchecked -cp "%cp%" rowlandsAdventure2\builders\*.java
set /A err = %err% + %ERRORLEVEL%

if %err% neq 0 ( 
   pause 
)