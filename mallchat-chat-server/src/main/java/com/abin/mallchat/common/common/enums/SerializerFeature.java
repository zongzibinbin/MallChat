package com.abin.mallchat.common.common.enums;

/**
 * @Author HuaPai
 * @Email flowercard591@gmail.com
 * @Date 2024/01/22 12:28
 */
public enum SerializerFeature {
	WriteNullListAsEmpty, WriteNullStringAsEmpty, WriteNullNumberAsZero, WriteNullBooleanAsFalse;

	public final int mask;

	SerializerFeature() {
		mask = (1 << ordinal());
	}
}
