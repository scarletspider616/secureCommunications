// Adapted from insertion sort in first project

#include <jni.h>
#include <stdio.h>
#include "TEA_TEAEncrypt.h"

void decrypt(int *, int *);
// JNI tutorial used as reference: 
// https://www3.ntu.edu.sg/home/ehchua/programming/java/JavaNativeInterface.html

// JNI func
JNIEXPORT jintArray JNICALL Java_TEA_TEADecrypt_runTEADecrypt ( 
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

	decrypt(in_value, in_key);

	// convert back to java data types and output 
	jintArray output = (*env)->NewIntArray(env, value_length);

	if (output == NULL) {
		return NULL;
	}
	(*env)->SetIntArrayRegion(env, output, 0, value_length, in_value);
	return output;
}




// TEA decryption function from class 
void decrypt (int *v, int *k){
/* TEA decryption routine */
unsigned int n=32, sum, y=v[0], z=v[1];
unsigned int delta=0x9e3779b9l;

	sum = delta<<5;
	while (n-- > 0){
		z -= (y<<4) + k[2] ^ y + sum ^ (y>>5) + k[3];
		y -= (z<<4) + k[0] ^ z + sum ^ (z>>5) + k[1];
		sum -= delta;
	}
	v[0] = y;
	v[1] = z;
}