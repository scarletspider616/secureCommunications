# secureCommunications
ECE 422 project 2
Joey-Michael Fallone
Secure Communications using TEA Encryption Algorithm. 

This project makes use of multiple stackoverflow references. They are noted in the comments at points where they are used. 

References to stack overflow published before before Feb.1, 2016 are used under CC-BY-SA license available in the legal/ dir. 

References to stack overflow published on or after Feb. 1, 2016 are used under the MIT license. 

This project is licensed under the MIT license, available in the legal/ dir. 

TEA encryption and decryption algorithms were provided in class. 

This makefile is setup for LINUX AND MACOS ONLY. For windows, it shouldn't take much work if you're familiar with JNI, but you'll have to make your own. 

Setting up TEA package: 
	export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:.
	make all

Generating password shadow table: 
	Create a text file "passwords.txt" in the project directory with the format username,password\n
	javac ShadowTableGenerator.java
	java ShadowTableGenerator
	hit enter
	delete passwords.txt
	Shadow table will now be found in .passwords in the root dir

Starting the file server
	export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:.
	make all
	javac *.java
	java FileServer

Starting a client
	export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:.
	make all
	javac *.java
	java Client

After that just follow the on screen prompts! 

*Note that data served by the FileServer must be stored in the data dir/ in the project dir. 
