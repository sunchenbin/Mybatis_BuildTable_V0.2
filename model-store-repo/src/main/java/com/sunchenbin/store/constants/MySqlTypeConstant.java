package com.sunchenbin.store.constants;

import com.sunchenbin.store.annotation.LengthCount;

public class MySqlTypeConstant {

	@LengthCount
	public static final  String INT = "int";
	
	@LengthCount
	public static final  String VARCHAR = "varchar";
	
	@LengthCount(LengthCount=0)
	public static final  String TEXT = "text";
	
	@LengthCount(LengthCount=0)
	public static final  String DATETIME = "datetime";
	
	@LengthCount(LengthCount=2)
	public static final  String DECIMAL = "decimal";
	
	@LengthCount(LengthCount=2)
	public static final  String DOUBLE = "double";
	
	@LengthCount
	public static final  String CHAR = "char";
}
