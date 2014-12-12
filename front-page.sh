#!/bin/bash

cp head.html_part index.html 

for i in */
do 
	folder=$(echo $i | tr -d '/' )
	echo "<li><a href='$folder'>$folder</a></li>" >> index.html 
done 

cat tail.html_part >> index.html 
	
