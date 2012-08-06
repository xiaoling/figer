CP=figer.jar
CP=$CP:$(echo lib/*.jar | tr ' ' ':')

echo $CP
java -Xmx24G -cp $CP edu.washington.cs.figer.Main $@ 
