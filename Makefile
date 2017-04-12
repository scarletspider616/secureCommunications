CC=gcc

CFLAGS= 
MACSTEP=
MACSTEP2=
CARGS=

MACFLAGS=-c -fPIC -I/System/Library/Frameworks/JavaVM.framework/Versions/A/Headers/ 

LINUXFLAGS=-shared -fpic -o libTEAEncrypt.so -I$(JAVA_HOME)/include/ -I$(JAVA_HOME)/include/linux/ 
LINUXFLAGS2=-shared -fpic -o libTEADecrypt.so -I$(JAVA_HOME)/include/ -I$(JAVA_HOME)/include/linux/

LINUXARGS=encrypt.c
LINUXARGS2=decrypt.c

MACARGS=encrypt.c -o encrypt.o
MACARGS2=decrypt.c -o decrypt.o

UNAME_S:=$(shell uname -s)
ifeq ($(UNAME_S),Linux)
	CFLAGS+=$(LINUXFLAGS)
	CFLAGS2+=$(LINUXFLAGS2)
	CARGS+=$(LINUXARGS)
	CARGS2+=$(LINUXARGS2)
endif
ifeq ($(UNAME_S),Darwin)
	CFLAGS+=$(MACFLAGS)
	CFLAGS2+=$(MACFLAGS)
	MACSTEP+=libtool -dynamic -lSystem encrypt.o -o libTEAEncrypt.dylib 
	MACSTEP2+=libtool -dynamic -lSystem decrypt.o -o libTEADecrypt.dylib
	CARGS+=$(MACARGS)
	CARGS2+=$(MACARGS2)
endif

decryption: all
	java -Djava.library.path=.:TEA/ TEA.TEADecrypt
encryption: all
	java -Djava.library.path=. TEA.TEAEncrypt

all: compilejava createJNIheader makenative linklibrary setjavapath

compilejava: 
	javac TEA/TEAEncrypt.java
	javac TEA/TEADecrypt.java

createJNIheader: 
	javah TEA.TEAEncrypt
	javah TEA.TEADecrypt

makenative: 
	$(CC) $(CFLAGS) $(CARGS)
	$(CC) $(CFLAGS2) $(CARGS2)


linklibrary:  
	$(MACSTEP)
	$(MACSTEP2)


setjavapath: 
	export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:.

clean:
	rm -f *.class libTEAEncrypt.* *.h *.log *.o libTEADecrypt.* 
	rm -f TEA/*.class TEA/*.dylib TEA/*.so TEA/*.h TEA/*.o
