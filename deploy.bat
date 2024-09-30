set nomAppli=testMVC
set nomSrcTxt=java_files_list.txt
set lib=C:\Users\rohyr\Documents\study\S4\java\winter\lib\
set classpath=C:\Users\rohyr\Documents\study\S4\java\winterTest\lib
set web2=C:\Users\rohyr\Documents\study\S4\java\winterTest\webxml
set temp=C:\Users\rohyr\Documents\study\S4\java\winter\temp
set webxml=C:\Users\rohyr\Documents\study\S4\java\winter\webxml

rmdir /s /q %temp%
mkdir %temp%

for /f "delims=" %%i in (%nomSrcTxt%) do set src=%src% %%i

robocopy %webxml% %web2% /E
javac -parameters -cp  "%lib%\*" %src% -d  %temp%  
cd %temp% && jar cvf %nomAppli%.jar * 
copy %nomAppli%.jar %classpath%
rm %nomAppli%.jar