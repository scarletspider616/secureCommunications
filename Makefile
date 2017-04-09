CC=gcc

CFLAGS= 
MACSTEP=
CARGS=

MACFLAGS=-c -fPIC -I/System/Library/Frameworks/JavaVM.framework/Versions/A/Headers/ 

LINUXFLAGS=-shared -fpic -o libEncrypt.so -I$(JAVA_HOME)/include/ -I$(JAVA_HOME)/include/linux/ 

LINUXARGS=encrypt.c

MACARGS=encrypt.c -o libEncrypt.o

UNAME_S:=$(shell uname -s)
ifeq ($(UNAME_S),Linux)
	CFLAGS+=$(LINUXFLAGS)
	CARGS+=$(LINUXARGS)
endif
ifeq ($(UNAME_S),Darwin)
	CFLAGS+=$(MACFLAGS)
	MACSTEP+=libtool -dynamic -lSystem libEncrypt.o -o libEncrypt.dylib 
	CARGS+=$(MACARGS)
endif

runEncryptionTest: all
	java -Djava.library.path=. Encrypt

all: compilejava createJNIheader makenative linklibrary setjavapath others

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
	rm -f *.class libEncrypt.* encrypt.h *.log
