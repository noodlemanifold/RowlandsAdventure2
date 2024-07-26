@echo off

set /A err = 0
set cp=;%CLASSPATH%;%cd%\bullet\Libbulletjme-20.2.0.jar;%cd%\libraries\jogl\jogamp-fat.jar;%cd%\libraries\joml\joml-1.10.5.jar;%cd%\libraries\jinput\jinput.jar;%cd%\libraries\jbullet\jbullet.jar;%cd%\libraries\vecmath\vecmath.jar;

echo: javac tage\*.java
javac -cp "%cp%" tage\*.java
set /A err = %err% + %ERRORLEVEL%

echo: javac tage\input\*.java
javac -cp "%cp%" tage\input\*.java
set /A err = %err% + %ERRORLEVEL%

echo: javac tage\input\action\*.java
javac -cp "%cp%" tage\input\action\*.java
set /A err = %err% + %ERRORLEVEL%

echo: javac tage\networking\*.java
javac -cp "%cp%" tage\networking\*.java
set /A err = %err% + %ERRORLEVEL%

echo: javac tage\networking\client\*.java
javac -cp "%cp%" tage\networking\client\*.java
set /A err = %err% + %ERRORLEVEL%

echo: javac tage\networking\server\*.java
javac -cp "%cp%" tage\networking\server\*.java
set /A err = %err% + %ERRORLEVEL%

echo: javac tage\nodeControllers\*.java
javac -cp "%cp%" tage\nodeControllers\*.java
set /A err = %err% + %ERRORLEVEL%

echo: javac tage\shapes\*.java
javac -cp "%cp%" tage\shapes\*.java
set /A err = %err% + %ERRORLEVEL%

echo: javac tage\objectRenderers\*.java
javac -cp "%cp%" tage\objectRenderers\*.java
set /A err = %err% + %ERRORLEVEL%

echo: javac tage\JmeBullet\*.java
javac -cp "%cp%" tage\JmeBullet\*.java
set /A err = %err% + %ERRORLEVEL%

echo: javac tage\rml\*.java
javac -cp "%cp%" tage\rml\*.java
set /A err = %err% + %ERRORLEVEL%

echo: javac tage\ai\behaviortrees\*.java
javac -cp "%cp%" tage\ai\behaviortrees\*.java
set /A err = %err% + %ERRORLEVEL%

echo: javac tage\audio\*.java
javac -cp "%cp%" tage\audio\*.java
set /A err = %err% + %ERRORLEVEL%

echo: javac tage\audio\joal\*.java
javac -cp "%cp%" tage\audio\joal\*.java
set /A err = %err% + %ERRORLEVEL%

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