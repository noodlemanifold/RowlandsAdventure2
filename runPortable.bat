set cp=;%CLASSPATH%;%cd%\bullet\Libbulletjme-20.2.0.jar;%cd%\libraries\jogl\jogamp-fat.jar;%cd%\libraries\joml\joml-1.10.5.jar;%cd%\libraries\jinput\jinput.jar;%cd%\libraries\jbullet\jbullet.jar;%cd%\libraries\vecmath\vecmath.jar;

java -cp "%cp%" --add-exports java.base/java.lang=ALL-UNNAMED --add-exports java.desktop/sun.awt=ALL-UNNAMED --add-exports java.desktop/sun.java2d=ALL-UNNAMED -Dsun.java2d.d3d=false -Dsun.java2d.uiScale=1 rowlandsAdventure2.MyGame 76.137.198.19 6010 UDP