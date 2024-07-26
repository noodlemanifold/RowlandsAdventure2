set cp=%CLASSPATH%;%cd%\bullet\Libbulletjme-20.2.0.jar;

javadoc -classpath "%cp%" -d .\javadoc --show-members public -quiet -author -subpackages tage
