// Adapted from insertion sort in first project

#include <jni.h>
#include <stdio.h>
#include "TEAEncrypt.h"

void encrypt(int *, int *);
// JNI tutorial used as reference: 
// https://www3.ntu.edu.sg/home/ehchua/programming/java/JavaNativeInterface.html

// JNI func
JNIEXPORT jintArray JNICALL Java_InsertionSort_runInsertionSort( 
	JNIEnv * env, jobject thisObj, jintArray value, 
	jintArray key) {
	// convert from java data types to C data types
	jint * in_value = (*env)->GetIntArrayElements(env, value, NULL);
	if (value == NULL) {
		return NULL;
	}
	jsize value_length = (*env)->GetArrayLength(env, value);

	jint * in_key = (*env)->GetIntArrayElements(env, key, NULL);
	if (key == NULL) {
		return NULL;
	}
	jsize key_length = (*env)->GetArrayLength(env, key);

	encrypt(in_value, in_key);

	// convert back to java data types and output 
	jintArray output = (*env)->NewIntArray(env, value_length);

	if (output == NULL) {
		return NULL;
	}
	(*env)->SetIntArrayRegion(env, output, 0, value_length, in_value);
	return output;
}




// TEA encryption function from class 
void encrypt(int *v, int *k){
	/* TEA encryption algorithm */
	unsigned int y = v[0], z=v[1], sum = 0;
	unsigned int delta = 0x9e3779b9, n=32;

	while (n-- > 0){
		sum += delta;
		y += (z<<4) + k[0] ^ z + sum ^ (z>>5) + k[1];
		z += (y<<4) + k[2] ^ y + sum ^ (y>>5) + k[3];
	}

	v[0] = y;
	v[1] = z;
}

