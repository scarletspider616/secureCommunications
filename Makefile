CC=gcc

CFLAGS= 
MACSTEP=
CARGS=

MACFLAGS=-c -fPIC -I/System/Library/Frameworks/JavaVM.framework/Versions/A/Headers/ 

LINUXFLAGS=-shared -fpic -o encrypt.so -I$(JAVA_HOME)/include/ -I$(JAVA_HOME)/include/linux/ 

LINUXARGS=encrypt.c

MACARGS=encrypt.c -o encrypt.o

UNAME_S:=$(shell uname -s)
ifeq ($(UNAME_S),Linux)
	CFLAGS+=$(LINUXFLAGS)
	CARGS+=$(LINUXARGS)
endif
ifeq ($(UNAME_S),Darwin)
	CFLAGS+=$(MACFLAGS)
	MACSTEP+=libtool -dynamic -lSystem encrypt.o -o libTEAEncrypt.dylib 
	CARGS+=$(MACARGS)
endif

runEncryptionTest: all
	java -Djava.library.path=. TEAEncrypt

all: compilejava createJNIheader makenative linklibrary setjavapath

compilejava: 
	javac TEAEncrypt.java

createJNIheader: TEAEncrypt.class
	javah TEAEncrypt

makenative: 
	$(CC) $(CFLAGS) $(CARGS)

linklibrary:  
	$(MACSTEP)


setjavapath: 
	# export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:.

clean:
	rm -f *.class libEncrypt.* encrypt.h *.log *.o
